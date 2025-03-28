package com.studymate.back.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * User Entity
 * user 테이블과 매핑되는 JPA 엔티티
 * 사용자 정보 관리 (로그인, 인증, 프로필 관리)
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /**
     * 사용자 고유 ID (Primary Key)
     * 자동 증가 (IDENTITY 전략)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 아이디 (username)
     * 로그인 시 사용하는 고유한 ID
     * 중복 불가 (unique)
     * 최대 50자 제한
     */
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    /**
     * 비밀번호 (password)
     * BCrypt 해시로 암호화 저장
     * Not Null (필수 값)
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * 닉네임 (nickname)
     * 사용자 별명 (중복 불가)
     * 최대 100자 제한
     */
    @Column(name = "nickname", nullable = false, unique = true, length = 100)
    private String nickname;

    /**
     * 전화번호 (phone)
     * Not Null (필수 값)
     * 최대 20자 제한
     */
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    /**
     * 이메일 (email)
     * 중복 불가 (unique)
     * Not Null (필수 값)
     * 최대 100자 제한
     */
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    /**
     * 사용자 역할 (role)
     * Default: user
     * 관리자(admin) 또는 일반 사용자(user)로 구분됨
     */
    @Column(name = "role", nullable = false, length = 10)
    @Builder.Default
    private String role = "user";

    /**
     * Refresh Token (JWT 재발급용)
     * 로그인 유지 및 보안 강화를 위한 토큰 저장
     * Default: null
     */
    @Column(name = "refresh_token", length = 500)
    private String refreshToken;

    /**
     * 프로필 이미지 (profile_image)
     * 2진 데이터(BLOB) 형태로 저장
     * Default: null (선택 사항)
     */
    @Lob
    @Column(name = "profile_image")
    private byte[] profileImage;

    /**
     * 계정 생성 시각 (created_at)
     * Default: 현재 시각
     * 계정이 생성될 때 자동 설정
     * 수정 불가 (updateable = false)
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 계정 정보 마지막 수정 시각 (updated_at)
     * 계정 정보가 변경될 때 자동 갱신
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
