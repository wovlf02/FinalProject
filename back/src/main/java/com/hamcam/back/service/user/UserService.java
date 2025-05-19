package com.hamcam.back.service.user;

import com.hamcam.back.dto.auth.request.PasswordConfirmRequest;
import com.hamcam.back.dto.auth.response.UserProfileResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.global.security.SecurityUtil;
import com.hamcam.back.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;
    private final PasswordEncoder passwordEncoder;

    /**
     * 마이페이지 조회: 로그인한 사용자 전체 정보 반환
     */
    public UserProfileResponse getMyProfile() {
        User user = securityUtil.getCurrentUser();

        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getNickname(),
                user.getGrade(),
                user.getStudyHabit(),
                user.getProfileImageUrl(),
                user.getCreatedAt() != null
                        ? user.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                        : null
        );
    }

    /**
     * 회원 탈퇴 (비밀번호 확인 → 완전 삭제)
     */
    @Transactional
    public void withdraw(PasswordConfirmRequest request) {
        User user = securityUtil.getCurrentUser();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_PASSWORD_MISMATCH);
        }

        userRepository.delete(user); // ✅ 완전 삭제 (soft delete 아님)
    }

    /**
     * 닉네임 변경
     */
    @Transactional
    public void updateNickname(String newNickname) {
        if (userRepository.existsByNickname(newNickname)) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }

        User user = securityUtil.getCurrentUser();
        user.setNickname(newNickname);
    }

    /**
     * 이메일 변경
     */
    @Transactional
    public void updateEmail(String newEmail) {
        if (userRepository.existsByEmail(newEmail)) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = securityUtil.getCurrentUser();
        user.setEmail(newEmail);
    }

    /**
     * 프로필 이미지 변경
     */
    @Transactional
    public void updateProfileImage(String imageUrl) {
        User user = securityUtil.getCurrentUser();
        user.setProfileImageUrl(imageUrl);
    }

    /**
     * 아이디(username) 변경
     */
    @Transactional
    public void updateUsername(String newUsername) {
        if (userRepository.existsByUsername(newUsername)) {
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
        }

        User user = securityUtil.getCurrentUser();
        user.setUsername(newUsername);
    }
}
