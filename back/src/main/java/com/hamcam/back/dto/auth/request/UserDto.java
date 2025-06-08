package com.hamcam.back.dto.auth.request;

import lombok.Getter;
import lombok.Setter;

/**
 * [UserDto]
 *
 * 사용자 기본 정보 DTO입니다.
 * 사용자 목록 조회, 친구 정보 노출 등 다양한 조회 응답에서 사용됩니다.
 */
@Getter
@Setter
public class UserDto {

    /** 사용자 아이디 (로그인 ID) */
    private String username;

    /** 사용자 닉네임 */
    private String nickname;

    /** 사용자 실명 */
    private String name;

    /** 이메일 주소 */
    private String email;

    /** 프로필 이미지 URL */
    private String profileImageUrl;

    /** 학년 정보 (1~3) */
    private Integer grade;

    /** 공부 습관 정보 (집중형, 분산형 등) */
    private String studyHabit;

    /** 가입 일시 (yyyy-MM-dd HH:mm:ss 형태) */
    private String createdAt;

    /**
     * 전체 필드를 포함한 생성자
     *
     * @param username 로그인 ID
     * @param nickname 닉네임
     * @param name 실명
     * @param email 이메일
     * @param profileImageUrl 프로필 이미지 URL
     * @param grade 학년
     * @param studyHabit 공부 습관
     * @param createdAt 가입 일시
     */
    public UserDto(
            String username,
            String nickname,
            String name,
            String email,
            String profileImageUrl,
            Integer grade,
            String studyHabit,
            String createdAt
    ) {
        this.username = username;
        this.nickname = nickname;
        this.name = name;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.grade = grade;
        this.studyHabit = studyHabit;
        this.createdAt = createdAt;
    }
}
