package com.hamcam.back.dto.user.response;

import com.hamcam.back.entity.auth.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * 사용자 프로필 응답 DTO
 * - 마이페이지, 사용자 정보 조회 시 사용됨
 */
@Getter
@AllArgsConstructor
@Builder // 🔥 builder 사용을 위해 추가
public class UserProfileResponse {

    private Long userId;
    private String username;
    private String email;
    private String nickname;
    private int grade;
    private String studyHabit;
    private String profileImageUrl;
    private String createdAt;

    /**
     * User 엔티티를 기반으로 UserProfileResponse 생성
     *
     * @param user 사용자 엔티티
     * @return 변환된 응답 객체
     */
    public static UserProfileResponse from(User user) {
        return UserProfileResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .grade(user.getGrade())
                .studyHabit(user.getStudyHabit())
                .profileImageUrl(Optional.ofNullable(user.getProfileImageUrl()).orElse(""))
                .createdAt(user.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .build();
    }
}
