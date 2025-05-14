package com.hamcam.back.service.auth;

import com.hamcam.back.dto.auth.request.*;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.dto.auth.response.LoginResponse;
import com.hamcam.back.dto.auth.response.TokenResponse;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.config.auth.JwtProvider;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.global.security.SecurityUtil;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.service.util.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

/**
 * 인증 및 회원가입 관련 로직을 담당하는 AuthService 구현 클래스입니다.
 * 인터페이스 없이 단일 구현체로 사용됩니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final SecurityUtil securityUtil;

    public Boolean checkUsername(UsernameCheckRequest request) {
        return !userRepository.existsByUsername(request.getUsername());
    }

    public Boolean checkNickname(NicknameCheckRequest request) {
        return !userRepository.existsByNickname(request.getNickname());
    }

    public Boolean checkEmail(EmailRequest request) {
        return !userRepository.existsByEmail(request.getEmail());
    }

    public String sendVerificationCode(EmailSendRequest request) {
        String code = String.valueOf((int)(Math.random() * 900000) + 100000); // 6자리 랜덤 코드
        redisTemplate.opsForValue().set("EMAIL:CODE:" + request.getEmail(), code, Duration.ofMinutes(3));
        mailService.sendVerificationCode(request.getEmail(), code, request.getType());
        return "인증코드가 이메일로 발송되었습니다.";
    }

    public Boolean verifyCode(EmailVerifyRequest request) {
        String key = "EMAIL:CODE:" + request.getEmail();
        String stored = redisTemplate.opsForValue().get(key);
        return stored != null && stored.equals(request.getCode());
    }

    public void deleteTempData(EmailRequest request) {
        redisTemplate.delete("EMAIL:CODE:" + request.getEmail());
    }

    /**
     * 회원가입
     */
    public void register(RegisterRequest request) {
        // 아이디/이메일 중복 체크
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException("이미 존재하는 아이디입니다.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException("이미 존재하는 이메일입니다.");
        }

        // User 엔티티 빌드
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .nickname(request.getNickname())
                .profileImageUrl(request.getProfileImageUrl())
                .grade(request.getGrade())
                .subjects(request.getSubjects())
                .studyHabit(request.getStudyHabit())
                .build();

        userRepository.save(user);
    }



    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_PASSWORD_MISMATCH);
        }

        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);

        return new LoginResponse(accessToken, refreshToken);
    }


    public void logout(TokenRequest request) {
        Long userId = securityUtil.getCurrentUserId(); // ✅ 변경
        redisTemplate.delete("RT:" + userId);

        long expiration = jwtProvider.getExpiration(request.getAccessToken());
        redisTemplate.opsForValue().set("BL:" + request.getAccessToken(), "logout", Duration.ofMillis(expiration));
    }

    public TokenResponse reissue(TokenRequest request) {
        if (!jwtProvider.validateToken(request.getRefreshToken())) {
            throw new CustomException("유효하지 않은 refresh token 입니다.");
        }

        Long userId = jwtProvider.getUserIdFromToken(request.getRefreshToken());
        String redisRefresh = redisTemplate.opsForValue().get("RT:" + userId);

        if (redisRefresh == null || !redisRefresh.equals(request.getRefreshToken())) {
            throw new CustomException("저장된 refresh token과 일치하지 않습니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("존재하지 않는 사용자입니다."));

        String newAccess = jwtProvider.generateAccessToken(user);
        String newRefresh = jwtProvider.generateRefreshToken(user);

        redisTemplate.opsForValue().set("RT:" + userId, newRefresh, Duration.ofDays(14));

        return new TokenResponse(newAccess, newRefresh);
    }

    public String sendFindUsernameCode(EmailRequest request) {
        if (!userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException("해당 이메일로 등록된 사용자가 없습니다.");
        }
        return sendVerificationCode(new EmailSendRequest(request.getEmail(), "find-id"));
    }

    public String verifyFindUsernameCode(EmailVerifyRequest request) {
        if (!verifyCode(request)) {
            throw new CustomException("인증코드가 올바르지 않습니다.");
        }
        return userRepository.findByEmail(request.getEmail())
                .map(User::getUsername)
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다."));
    }

    public String requestPasswordReset(PasswordResetRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다."));

        if (!user.getEmail().equals(request.getEmail())) {
            throw new CustomException("입력한 이메일이 일치하지 않습니다.");
        }

        return sendVerificationCode(new EmailSendRequest(request.getEmail(), "reset-pw"));
    }

    public Boolean verifyPasswordResetCode(EmailVerifyRequest request) {
        return verifyCode(request);
    }

    public void updatePassword(PasswordChangeRequest request) {
        User user = securityUtil.getCurrentUser(); // ✅ 현재 로그인한 사용자

        if (!user.getUsername().equals(request.getUsername())) {
            throw new CustomException("로그인된 사용자 정보와 일치하지 않습니다.");
        }

        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
    }

    public void withdraw(PasswordConfirmRequest request) {
        User user = securityUtil.getCurrentUser(); // ✅ 현재 로그인한 사용자

        if (!user.getUsername().equals(request.getUsername())) {
            throw new CustomException("로그인된 사용자 정보와 일치하지 않습니다.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException("비밀번호가 일치하지 않습니다.");
        }

        user.softDelete();
    }
}
