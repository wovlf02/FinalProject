package com.hamcam.back.service.auth;

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
                user.getCreatedAt().toString()
        );
    }

    /**
     * 회원 탈퇴 (비밀번호 확인 → 소프트 삭제)
     */
    public void withdraw(PasswordConfirmRequest request) {
        User user = securityUtil.getCurrentUser();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_PASSWORD_MISMATCH);
        }

        user.softDelete(); // isDeleted = true, deletedAt = now 등
    }
}
