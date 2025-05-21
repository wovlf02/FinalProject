package com.hamcam.back.service.auth;

import com.hamcam.back.dto.auth.request.*;
import com.hamcam.back.dto.user.request.UpdatePasswordRequest;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.service.util.FileService;
import com.hamcam.back.service.util.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final MailService mailService;
    private final FileService fileService;

    // ==== 중복 체크 ====

    public Boolean checkUsername(UsernameCheckRequest request) {
        return !userRepository.existsByUsername(request.getUsername());
    }

    public Boolean checkNickname(NicknameCheckRequest request) {
        return !userRepository.existsByNickname(request.getNickname());
    }

    public Boolean checkEmail(EmailRequest request) {
        return !userRepository.existsByEmail(request.getEmail());
    }

    // ==== 이메일 인증 (mock) ====

    public String sendVerificationCode(EmailSendRequest request) {
        String code = String.valueOf((int) (Math.random() * 900000) + 100000);
        mailService.sendVerificationCode(request.getEmail(), code, request.getType());
        // 실제 인증코드 검증은 프론트에서 저장하고 비교하도록 위임
        return "인증코드가 이메일로 발송되었습니다. (코드: " + code + ")";
    }

    public Boolean verifyCode(EmailVerifyRequest request) {
        return request.getCode() != null && request.getCode().matches("\\d{6}");
    }

    public void deleteTempData(EmailRequest request) {
        // Redis 제거 → 비워두기
    }

    // ==== 회원가입 ====

    public void register(RegisterRequest request, MultipartFile profileImage) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        String profileImageUrl = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            profileImageUrl = fileService.saveProfileImage(profileImage);
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())  // 실제 비밀번호 암호화 생략
                .email(request.getEmail())
                .name(request.getName())
                .nickname(request.getNickname())
                .grade(request.getGrade())
                .studyHabit(request.getStudyHabit())
                .phone(request.getPhone())
                .profileImageUrl(profileImageUrl)
                .subjects(Optional.ofNullable(request.getSubjects())
                        .orElseGet(ArrayList::new))
                .build();

        userRepository.save(user);
    }

    // ==== 로그인 ====

    public User login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_USER_NOT_FOUND));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_PASSWORD_MISMATCH);
        }

        return user;
    }

    public void logout(String dummyToken) {
        // 로그아웃 로직 없음
    }

    // ==== 아이디/비밀번호 찾기 ====

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
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.updatePassword(request.getNewPassword());
    }
}
