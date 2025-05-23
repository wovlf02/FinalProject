package com.hamcam.back.service.user;

import com.hamcam.back.dto.user.response.UserProfileResponse;
import com.hamcam.back.dto.user.request.UserRequest;
import com.hamcam.back.dto.user.request.UserProfileImageUpdateRequest;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * ✅ 내 정보 조회
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getMyInfo(UserRequest request) {
        User user = getUserOrThrow(request.getUserId());

        return UserProfileResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .grade(user.getGrade())
                .studyHabit(user.getStudyHabit())
                .profileImageUrl(user.getProfileImageUrl())
                .createdAt(user.getCreatedAt() != null
                        ? user.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                        : null)
                .build();
    }

    /**
     * ✅ 회원 탈퇴
     */
    @Transactional
    public void withdraw(UserRequest request) {
        User user = getUserOrThrow(request.getUserId());
        userRepository.delete(user);
    }

    /**
     * ✅ 다른 사용자 프로필 조회
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(UserRequest request) {
        return getMyInfo(request); // 사용자 ID로 동일하게 처리
    }

    /**
     * ✅ 닉네임 변경
     */
    @Transactional
    public void updateNickname(UserRequest request) {
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }

        User user = getUserOrThrow(request.getUserId());
        user.setNickname(request.getNickname());
    }

    /**
     * ✅ 이메일 변경
     */
    @Transactional
    public void updateEmail(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = getUserOrThrow(request.getUserId());
        user.setEmail(request.getEmail());
    }

    /**
     * ✅ 아이디(username) 변경
     */
    @Transactional
    public void updateUsername(UserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
        }

        User user = getUserOrThrow(request.getUserId());
        user.setUsername(request.getUsername());
    }

    /**
     * ✅ 프로필 이미지 변경
     */
    @Transactional
    public String updateProfileImage(UserProfileImageUpdateRequest request) {
        User user = getUserOrThrow(request.getUserId());

        try {
            String storedFileName = UUID.randomUUID() + "_" + request.getProfileImage().getOriginalFilename();
            Path uploadDir = Paths.get("uploads/profile/" + user.getId());
            Files.createDirectories(uploadDir);

            Path filePath = uploadDir.resolve(storedFileName);
            Files.copy(request.getProfileImage().getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String imageUrl = "/uploads/profile/" + user.getId() + "/" + storedFileName;
            user.setProfileImageUrl(imageUrl);
            return imageUrl;

        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * ✅ 사용자 조회 공통 메서드
     */
    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
