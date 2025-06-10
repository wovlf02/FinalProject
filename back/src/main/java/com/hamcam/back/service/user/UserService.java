package com.hamcam.back.service.user;

import com.hamcam.back.dto.user.response.UserProfileResponse;
import com.hamcam.back.dto.user.request.UserProfileImageUpdateRequest;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
     * ✅ 현재 인증된 사용자 조회
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * ✅ 내 정보 조회
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getMyInfo(HttpServletRequest request) {
        User user = getSessionUser(request);

        return UserProfileResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getName()) // ✅ 추가된 name 필드
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
    public void withdraw(HttpServletRequest request) {
        User user = getSessionUser(request);
        userRepository.delete(user);
    }

    /**
     * ✅ 다른 사용자 프로필 조회 (직접 조회하려면 별도 구현 필요)
     * => 현재는 자기 자신의 정보와 동일 처리
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(HttpServletRequest request) {
        return getMyInfo(request);
    }

    /**
     * ✅ 프로필 이미지 변경
     */
    @Transactional
    public String updateProfileImage(UserProfileImageUpdateRequest request, HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);

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
     * ✅ 세션에서 사용자 조회
     */
    private User getSessionUser(HttpServletRequest request) {
        Long userId = SessionUtil.getUserId(request);
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
