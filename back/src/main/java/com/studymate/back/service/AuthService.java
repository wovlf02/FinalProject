package com.studymate.back.service;

import com.studymate.back.config.JwtProvider;
import com.studymate.back.dto.EmailVerificationRequest;
import com.studymate.back.dto.LoginRequest;
import com.studymate.back.dto.RegisterRequest;
import com.studymate.back.dto.LoginResponse;
import com.studymate.back.entity.User;
import com.studymate.back.repository.UserRepository;
import com.studymate.back.utils.EmailUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 회원가입, 로그인, 이메일 인증 관련 로직 처리
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final EmailUtil emailUtil;

    /**
     * 아이디 중복 확인 메서드
     * @param username 아이디
     * @return 중복확인 결과
     */
    public boolean checkUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * 이메일 인증번호 전송 메서드 -> 이메일 인증을 위한 6자리 코드 생성 후 Redis에 저장 -> 5분 후 만료
     * @param email 이메일
     */
    public void sendVerificationEmail(String email) {
        String verificationCode = emailUtil.generateVerificationCode();
        redisTemplate.opsForValue().set(email, verificationCode, 5, TimeUnit.MINUTES);
        try {
            emailUtil.sendVerificationEmail(email, verificationCode);
        } catch (Exception e) {
            throw new RuntimeException("이메일 전송 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 이메일 인증번호 검증 메서드
     * -> Redis에서 이메일 인증번호 검증 후 emailVerified 값을 true로 업데이트
     * @param request 인증번호 검증 DTO
     */
    public void verifyEmail(EmailVerificationRequest request) {
        String storedCode = redisTemplate.opsForValue().get(request.getEmail());
        if(storedCode == null || !storedCode.equals(request.getCode())) {
            throw new IllegalArgumentException("유효하지 않은 인증번호입니다.");
        }
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if(userOpt.isPresent()) {
            User user = userOpt.get();
            user.setEmailVerified(true);
            userRepository.save(user);
        }
    }

    /**
     * 회원가입 메서드
     * -> 이메일 인증이 완료된 경우에만 회원가입 가능
     * -> 비밀번호는 BCrypt 암호화 후 저장
     * @param request 회원가입 요청 DTO
     */
    public void register(RegisterRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 인증이 완료되지 않았습니다."));

        if(!user.isEmailVerified()) {
            throw new IllegalArgumentException("이메일 인증 후 회원가입을 완료할 수 있습니다.");
        }

        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        userRepository.save(user);
    }

    /**
     * 로그인 메서드
     * 아이디 및 비밀번호 검증 후 JWT 발급
     * Access Token: 1시간 동안 유효 (Redis 저장)
     * Refresh Token: 30일 동안 유효 (BCrypt 암호화 후 MySQL에 저장)
     * @param request 로그인 DTO
     * @return Access Token, Refresh Token, 사용자 정보 (username, email, name)
     */
    public LoginResponse login(LoginRequest request) {
        // 사용자 조회
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 비밀번호 검증
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호를 잘못 입력하셨습니다.");
        }

        // JWT Access Token 및 Refresh Token 생성
        String accessToken = jwtProvider.generateAccessToken(user.getUsername());
        String refreshToken = jwtProvider.generateRefreshToken(user.getUsername());

        // Access Token을 Redis에 저장 -> 1시간 만료
        redisTemplate.opsForValue().set(user.getUsername(), accessToken, 1, TimeUnit.HOURS);

        // Refresh Token을 BCrypt 암호화하여 MySQL에 저장 -> 30일 만료
        user.setRefreshToken(passwordEncoder.encode(refreshToken));
        userRepository.save(user);

        // 로그인 성공 시 Access Token, Refresh Token, 사용자 정보 반환
        return new LoginResponse(accessToken, refreshToken, user.getUsername(), user.getEmail(), user.getName());
    }
}
