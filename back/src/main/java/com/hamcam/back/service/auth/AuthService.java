package com.hamcam.back.service.auth;

import com.hamcam.back.config.auth.JwtProvider;
import com.hamcam.back.dto.auth.request.*;
import com.hamcam.back.dto.auth.response.TokenResponse;
import com.hamcam.back.dto.user.request.UpdatePasswordRequest;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.global.exception.CustomException;
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
        String code = String.valueOf((int) (Math.random() * 900000) + 100000);
        String key = "EMAIL:CODE:" + request.getEmail();
        redisTemplate.opsForValue().set(key, code, Duration.ofMinutes(3));
        mailService.sendVerificationCode(request.getEmail(), code, request.getType());
        return "인증코드가 이메일로 발송되었습니다.";
    }

    public Boolean verifyCode(EmailVerifyRequest request) {
        String key = "EMAIL:CODE:" + request.getEmail();
        String stored = redisTemplate.opsForValue().get(key);
        if (stored != null && stored.equals(request.getCode())) {
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }

    public void deleteTempData(EmailRequest request) {
        redisTemplate.delete("EMAIL:CODE:" + request.getEmail());
    }

    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        String phone = request.getPhone();
        if (phone != null && !isValidPhone(phone)) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .name(request.getName())
                .nickname(request.getNickname())
                .grade(request.getGrade())
                .subjects(request.getSubjects())
                .studyHabit(request.getStudyHabit())
                .phone(phone)
                .profileImageUrl(request.getProfileImageUrl())
                .build();

        userRepository.save(user);
    }

    /**
     * 로그인: ID/PW 검증 수행 후 User 반환
     */
    public User login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_PASSWORD_MISMATCH);
        }

        return user;
    }

    public void logout(String accessToken) {
        Long userId = jwtProvider.getUserIdFromToken(accessToken);

        // RefreshToken 삭제
        redisTemplate.delete("RT:" + userId);

        // AccessToken 블랙리스트 처리
        long expiration = jwtProvider.getExpiration(accessToken);
        redisTemplate.opsForValue().set("BL:" + accessToken, "logout", Duration.ofMillis(expiration));
    }


    public TokenResponse reissue(String refreshToken) {
        // 1. refreshToken 자체 유효성 검사 (만료 여부, 서명 등)
        if (!jwtProvider.validateTokenWithoutRedis(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // 2. 사용자 ID 추출
        Long userId = jwtProvider.getUserIdFromToken(refreshToken);

        // 3. Redis에서 refreshToken 조회 후 비교
        String stored = redisTemplate.opsForValue().get("RT:" + userId);
        if (stored == null || !stored.equals(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // 4. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 5. accessToken 새로 발급
        String newAccess = jwtProvider.generateAccessToken(user);

        // 6. refreshToken도 새로 생성하고 Redis에 저장
        String newRefresh = jwtProvider.generateRefreshToken(user);
        redisTemplate.opsForValue().set("RT:" + userId, newRefresh, Duration.ofDays(14));

        // 7. accessToken과 새 refreshToken 반환
        return new TokenResponse(newAccess, newRefresh, user.getUsername(), user.getName());

    }


    public String sendFindUsernameCode(EmailRequest request) {
        if (!userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        return sendVerificationCode(new EmailSendRequest(request.getEmail(), "find-id"));
    }

    public String verifyFindUsernameCode(EmailVerifyRequest request) {
        if (!verifyCode(request)) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        return userRepository.findByEmail(request.getEmail())
                .map(User::getUsername)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    public String requestPasswordReset(PasswordResetRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!user.getEmail().equals(request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_MISMATCH);
        }

        return sendVerificationCode(new EmailSendRequest(request.getEmail(), "reset-pw"));
    }

    public void updatePassword(UpdatePasswordRequest request) {
        User user = securityUtil.getCurrentUser();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_PASSWORD_MISMATCH);
        }

        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("^\\d{10,15}$");
    }
}
