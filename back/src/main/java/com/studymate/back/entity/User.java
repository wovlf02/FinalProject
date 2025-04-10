package com.studymate.back.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * User Entity 클래스
 * users 테이블과 매핑되는 JPA Entity
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
     * 사용자 고유 ID
     * Auto Increment
     * Primary Key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 아이디
     * Not Null
     * 중복 불가
     * 최대 50자
     */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /**
     * 암호화된 비밀번호
     * Not Null
     */
    @Column(nullable = false)
    private String password;

    /**
     * 이름
     * Not Null
     * 최대 100자
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 전화번호
     * Not Null
     * 최대 15자
     */
    @Column(nullable = false, length = 15)
    private String phone;

    /**
     * 이메일
     * Not Null
     * 중복 불가
     * 최대 100자
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * 이메일 인증 여부
     * Default: false
     * Not Null
     */
    @Column(nullable = false, name = "email_verified")
    @Builder.Default
    private boolean emailVerified = false;

    /**
     * Refresh Token -> JWT 재발급용
     * BCrypt 암호화된 상태로 저장 -> 보안 강화
     */
    @Column(name = "refresh_token")
    private String refreshToken;

    /**
     * 계정 생성 시각
     * 회원가입이 완료된 시각으로 자동 설정
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 계정 정보 마지막 수정 시각
     * -> 계정 정보 변경 시 자동 갱신
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Entity 저장 전 실행 -> createdAt 자동 설정
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Entity 업데이트 전 실행 -> updatedAt 자동 설정
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
