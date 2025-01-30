package com.studymate.back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 사용자 Entity Class
 * users 테이블 매핑
 * 사용자 정보 저장
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class UserEntity {

    /**
     * 사용자 고유 ID
     * 중복 X
     * Primary Key
     * Auto Increment
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 아이디
     * 중복 X
     * Not Null
     * 최대 50자
     */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /**
     * 비밀번호 -> 암호화된 상태로 저장
     * 최대 255자
     * Not Null
     */
    @Column(nullable = false, length = 255)
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
     * 이메일 주소
     * 중복 X
     * 최대 100자
     * Not Null
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * 이메일 인증 여부
     * Default: false -> 미인증 상태
     * Not Null
     */
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean emailVerified;

    /**
     * 계정 생성 시각
     * Default: 현재 시각 -> CURRENT_TIMESTAMP
     * 수정 불가
     */
    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * 마지막 수정 시각
     * Default: 현재 시각 -> CURRENT_TIMESTAMP
     * 데이터 변경 시 자동 갱신
     */
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt = LocalDateTime.now();

    /**
     * Refresh Token -> JWT Refresh Token 저장
     * 토큰 재발급을 위한 필드
     * 500자 제한
     */
    @Column(length = 500)
    private String refreshToken;
}
