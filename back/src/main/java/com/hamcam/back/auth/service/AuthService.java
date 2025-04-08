package com.hamcam.back.auth.service;

import com.hamcam.back.auth.dto.*;
import com.hamcam.back.auth.entity.User;
import com.hamcam.back.auth.repository.UserRepository;
import com.hamcam.back.auth.util.EmailUtil;
import com.hamcam.back.auth.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * AuthService
 *
 * 사용자 인증 및 계정 관리 관련 핵심 비즈니스 로직을 처리하는 서비스 클래스
 * 회원가입, 로그인, 이메일 인증, 아이디/비밀번호 찾기 기능 포함
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
     * 아이디(username) 중복 여부 확인
     * @param username 아이디
     * @return 중복이면 true, 사용 가능하면 false
     */
    public boolean checkUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * 닉네임 중복 여부 확인
     * @param nickname 닉네임
     * @return 중복이면 true
     */
    public boolean checkNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    /**
     * 이메일 인증번호 발송
     * 인증번호를 생성 후 Redis에 5분 간 저장
     * 이메일로 인증번호 전송
     * @param email 사용자 이메일
     */
    public void sendVerificationEmail(String email) {
        String code = emailUtil.generateVerificationCode();
        redisTemplate.opsForValue().set(email, code, 5, TimeUnit.MINUTES);

        try {
            emailUtil.sendVerificationEmail(email, code);
        } catch (Exception e) {
            throw new RuntimeException("이메일 전송 실패: ", e);
        }
    }

    /**
     * 이메일 인증번호 검증
     * Redis에서 인증번호를 불러와 비교
     * @param request 이메일과 인증번호
     */
    public void verifyEmail(EmailVerificationRequest request) {
        String savedCode = redisTemplate.opsForValue().get(request.getEmail());

        if(savedCode == null || !savedCode.equals(request.getCode())) {
            throw new IllegalArgumentException("인증번호가 유효하지 않습니다.");
        }

        // 인증된 이메일일 경우 사용자에게 반영 (선택사항)
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        userOpt.ifPresent(user -> {
            user.setEmailVerified(true);
            userRepository.save(user);
        });
    }

    /**
     * 회원가입 처리
     * 이메일 인증 여부는 프론트에서 사전 확인 완료
     * 비밀번호는 BCrypt로 암호화하여 저장
     * @param request 회원가입 요청 DTO
     */
    public void register(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setNickname(request.getNickname());
        user.setGoal(request.getGoal());

        // 프로필 이미지 파일 저장 (MultipartFile -> byte[] 변환)
        MultipartFile file = request.getProfileImage();
        if(file != null && !file.isEmpty()) {
            try {
                user.setProfileImage(file.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("프로필 이미지 저장 중 오류 발생", e);
            }
        }

        userRepository.save(user);
    }

    /**
     * 로그인 처리
     * 아이디, 비밀번호 검증
     * Access Token, Refresh Token 발급 및 저장
     * @param request 로그인 요청 DTO
     * @return 로그인 응답 DTO (JWT 토큰 포함)
     */
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtProvider.generateAccessToken(user.getUsername());
        String refreshToken = jwtProvider.generateRefreshToken(user.getUsername());

        // Access Token은 Redis에 1시간 저장
        redisTemplate.opsForValue().set(user.getUsername(), accessToken, 1, TimeUnit.HOURS);

        // Refresh Token은 암호화하여 MySQL에 저장
        user.setRefreshToken(passwordEncoder.encode(refreshToken));
        userRepository.save(user);

        return new LoginResponse(accessToken, refreshToken, user.getUsername(), user.getEmail(), user.getNickname());
    }

    /**
     * 아이디 찾기 (이메일로 조회)
     * @param request 이메일 기반 요청 DTO
     * @return 아이디 응답 DTO
     */
    public UsernameFindResponse findUsername(UsernameFindRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일로 가입된 계정이 없습니다."));

        return new UsernameFindResponse(true, user.getUsername(), "아이디가 성공적으로 조회되었습니다.");
    }

    /**
     * 비밀번호 초기화 처리
     * 아이디를 기준으로 비밀번호 변경
     * @param request 비밀번호 변경 요청
     * @return 처리 결과
     */
    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디를 찾을 수 없습니다."));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return new ResetPasswordResponse(true, "비밀번호가 성공적으로 변경되었습니다.");
    }
}
