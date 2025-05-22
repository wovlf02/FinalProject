package com.hamcam.back.service.user;

import com.hamcam.back.dto.auth.response.UserProfileResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
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
    private final PasswordEncoder passwordEncoder;

    /**
     * 사용자 프로필 조회
     */
    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

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
    public void withdraw(Long userId, PasswordConfirmRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_PASSWORD_MISMATCH);
        }

        userRepository.delete(user);
    }

    /**
     * 닉네임 변경
     */
    @Transactional
    public void updateNickname(Long userId, String newNickname) {
        if (userRepository.existsByNickname(newNickname)) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.setNickname(newNickname);
    }

    /**
     * 이메일 변경
     */
    @Transactional
    public void updateEmail(Long userId, String newEmail) {
        if (userRepository.existsByEmail(newEmail)) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.setEmail(newEmail);
    }

    /**
     * 프로필 이미지 변경
     */
    @Transactional
    public void updateProfileImage(Long userId, String imageUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.setProfileImageUrl(imageUrl);
    }

    /**
     * 아이디(username) 변경
     */
    @Transactional
    public void updateUsername(Long userId, String newUsername) {
        if (userRepository.existsByUsername(newUsername)) {
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.setUsername(newUsername);
    }
}
