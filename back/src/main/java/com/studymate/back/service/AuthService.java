package com.studymate.back.service;

import com.studymate.back.dto.AuthRequest;
import com.studymate.back.dto.AuthResponse;
import com.studymate.back.entity.UserEntity;
import com.studymate.back.repository.UserRepository;
import com.studymate.back.security.JwtUtil;
import com.studymate.back.util.EmailUtil;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 인증 서비스 -> 회원가입, 로그인, 이메일 인증, JWT 발급
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailUtil emailUtil;
    private final StringRedisTemplate redisTemplate;

    /**
     * 아이디 중복확인
     * @param username 아이디
     * @return 중복확인 결과 -> true: 중복되는 결과 없음, false: 중복된 결과
     */
    public boolean checkUsernameAvailability(String username) {
        // 아이디 중복확인
        if(userRepository.findByUsername(username).isPresent()) {
            return false;   // 아이디 중복됨
        }

        // Redis에 인증 상태 저장 -> 10분 유지
        redisTemplate.opsForValue().set("username_available:" + username, "true", 10, TimeUnit.MINUTES);
        return true;
    }

    /**
     * 이메일 중복 확인 및 인증번호 발송
     * @param email
     */
    public void sendVerificationEmail(String email) {
        if(userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }

        String verificationCode = String.valueOf((int) (Math.random() * 9000000 + 1000000));
        redisTemplate.opsForValue().set(email, verificationCode, 5, TimeUnit.MINUTES);
        emailUtil.sendEmail(email, "StudyMate 이메일 인증번호입니다.", "아래의 인증번호를 정확히 입력해주세요.\n\n인증번호: " + verificationCode);
    }

    /**
     * 이메일 인증번호 검증 -> 성공 시 Redis에 인증 여부 저장
     * @param email 이메일
     * @param code 인증번호
     * @return 인증 성공 시 true, 실패 시 false
     */
    public boolean verifyEmail(String email, String code) {
        String storedCode = redisTemplate.opsForValue().get(email);
        if(storedCode != null && storedCode.equals(code)) {
            redisTemplate.opsForValue().set("verified:" + email, "true", 10, TimeUnit.MINUTES);
            redisTemplate.delete(email);
            return true;
        }
        return false;
    }

    /**
     * 회원가입 처리 -> 아이디 중복확인 & 이메일 인증 후 가능
     * @param request
     */
    public void register(AuthRequest.RegisterRequest request) {
        // 아이디 중복 확인 여부 검증
        String usernameAvailable = redisTemplate.opsForValue().get("username_available:" + request.getUsername());
        boolean isUsernameAvailable = usernameAvailable != null && usernameAvailable.equals("true");

        // 이메일 인증 여부 검증
        String emailVerified = redisTemplate.opsForValue().get("verified:" + request.getEmail());
        boolean isEmailVerified = emailVerified != null && emailVerified.equals("true");

        // 두 조건 모두 만족해야 회원가입 가능
        if(!isUsernameAvailable) {
            throw new RuntimeException("아이디 중복 확인이 완료되지 않았습니다.");
        }

        if(!isEmailVerified) {
            throw new RuntimeException("이메일 인증이 완료되지 않았습니다.");
        }

        // 데이터베이스 중복 검사 -> 보안 강화
        if(userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("이미 사용 중인 아이디입니다.");
        }

        if(userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }

        // 사용자 정보 저장
        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .emailVerified(true)    // 인증된 이메일만 저장됨
                .build();

        userRepository.save(user);

        // Redis에 저장된 검증 상태 삭제
        redisTemplate.delete("verified:" + request.getEmail());
        redisTemplate.delete("username_available:" + request.getUsername());
    }

    public AuthResponse.JwtResponse login(AuthRequest.LoginRequest request) {
        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("잘못된 사용자 정보입니다."));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return new AuthResponse.JwtResponse(accessToken, refreshToken, "Bearer");
    }
}
