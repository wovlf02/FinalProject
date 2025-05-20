package com.hamcam.back.service.auth;

import com.hamcam.back.config.auth.JwtProvider;
import com.hamcam.back.dto.auth.request.*;
import com.hamcam.back.dto.auth.response.TokenResponse;
import com.hamcam.back.dto.user.request.UpdatePasswordRequest;
import com.hamcam.back.entity.auth.Subjects;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.global.security.SecurityUtil;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.service.util.FileService;
import com.hamcam.back.service.util.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final FileService fileService;
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

    /**
     * 회원가입: 요청 + 파일 업로드 처리 포함
     */
    public void register(RegisterRequest request, MultipartFile profileImage) {
        log.info("📥 [회원가입 요청] username={}, email={}, nickname={}",
                request.getUsername(), request.getEmail(), request.getNickname());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        if (request.getPhone() != null && !isValidPhone(request.getPhone())) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        // 1. 프로필 이미지 저장
        String profileImageUrl = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            profileImageUrl = fileService.saveProfileImage(profileImage);
        }

        // 2. 사용자 엔티티 생성
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .name(request.getName())
                .nickname(request.getNickname())
                .grade(request.getGrade())
                .studyHabit(request.getStudyHabit())
                .phone(request.getPhone())
                .profileImageUrl(profileImageUrl)
                .build();

        // 3. 과목 등록
        List<Subjects> subjects = request.getSubjects().stream()
                .map(subject -> Subjects.builder().name(subject).user(user).build())
                .toList();
        user.setSubjects(subjects);

        try {
            userRepository.save(user);
            log.info("✅ [회원가입 성공] {}", user.getUsername());
        } catch (Exception e) {
            log.error("🔥 [회원가입 예외]", e);
            throw new CustomException("회원가입 처리 중 오류가 발생했습니다.");
        }
    }

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

        redisTemplate.delete("RT:" + userId); // RefreshToken 제거
        long exp = jwtProvider.getExpiration(accessToken);
        redisTemplate.opsForValue().set("BL:" + accessToken, "logout", Duration.ofMillis(exp));
    }

    public TokenResponse reissue(String refreshToken) {
        if (!jwtProvider.validateTokenWithoutRedis(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        Long userId = jwtProvider.getUserIdFromToken(refreshToken);
        String stored = redisTemplate.opsForValue().get("RT:" + userId);

        if (stored == null || !stored.equals(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String newAccess = jwtProvider.generateAccessToken(user);
        String newRefresh = jwtProvider.generateRefreshToken(user);

        redisTemplate.opsForValue().set("RT:" + userId, newRefresh, Duration.ofDays(14));

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
