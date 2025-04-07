package com.hamcam.back.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * [User 엔티티 클래스]
 * 모든 사용자 정보를 저장하는 핵심 테이블
 * 로그인, 회원가입, 커뮤니티 활동, 공부 기록, 채팅, 알림 등 거의 모든 기능과 연계됨
 *
 * [관련 기능]
 * 회원가입, 로그인, 로그아웃
 * 이메일 인증 및 닉네임 중복 체크
 * 사용자 프로필 관리
 * 학습 시간 통계, 활동 점수 및 레벨 시스템
 * 사용자 상태 관리 (경과, 정지 등)
 * Refresh Token 저장 및 인증 흐름
 */
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "nickname")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /**
     * 사용자 고유 ID
     * 자동 증가
     * 기본 키(PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    /**
     * 사용자 로그인 아이디
     * 사용자 식별자
     * 중복 불가
     */
    @Column(name = "user_name", nullable = false, length = 50)
    private String username;

    /**
     * 사용자 비밀번호 (BCrypt로 암호화되어 저장됨)
     * 프론트에서 입력받은 값을 암호화 후 저장
     * 평문 저장 금지
     */
    @Column(name = "user_password", nullable = false, length = 200)
    private String password;

    /**
     * 사용자 이메일
     * 회원가입 및 인증에 사용됨
     * 중복 불가
     */
    @Column(name = "user_email", nullable = false, length = 100)
    private String email;

    /**
     * 사용자 닉네임
     * 커뮤니티, 채팅, 게시판 등에서 사용되는 식별 이름
     * 중복 불가
     */
    @Column(name = "user_nickname", nullable = false, length = 100)
    private String nickname;

    /**
     * 사용자의 학습 목표
     * 마이페이지에서 설정 가능
     * 홈 화면 대시보드 등에 표시
     */
    @Column(name = "user_goal", nullable = false, columnDefinition = "TEXT")
    private String goal;

    /**
     * 누적 공부 시간 (분 단위)
     * 학습 통계, 성장 분석 등에 사용됨
     */
    @Column(name = "user_studytime", nullable = false)
    private int studyTime = 0;

    /**
     * 프로필 이미지 (Binary 파일 저장)
     * 사용자가 등록한 이미지
     * 프론트에서는 base64 또는 multipart로 처리
     */
    @Lob
    @Column(name = "user_profile")
    private byte[] profileImage;

    /**
     * 계정 생성일 (자동 기록)
     */
    @CreationTimestamp
    @Column(name = "user_created", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 계정 정보 최종 수정일 (자동 갱신)
     */
    @UpdateTimestamp
    @Column(name = "user_updated", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Refresh Token (JWT 토큰 재발급용)
     * 로그인 시 생성된 Refresh Token을 암호화하여 저장
     */
    @Column(name = "user_token", length = 500)
    private String refreshToken;

    /**
     * 사용자 활동 점수
     * 게시글, 댓글, 공부 시간 등에 따라 증가
     * 레벨 시스템과 연결 가능
     */
    @Column(name = "user_value")
    private int value = 0;

    /**
     * 사용자 계정 상태
     * ACTIVE: 정상 사용
     * INACTIVE: 비활성 상태 (휴면 계정)
     * BANNED: 정지된 계정
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    private AccountStatus status = AccountStatus.ACTIVE;

    /**
     * 누적 경고 횟수
     * 커뮤니티 신고, 금지어 사용 등으로 누적됨
     */
    @Column(name = "warning_count")
    private int warningCount = 0;

    /**
     * 사용자 레벨
     * 학습 시간, 활동 점수 등을 기반으로 상승
     */
    @Column(name = "user_level", nullable = false)
    private int level = 1;

    @Column(name = "email_verified")
    private boolean emailVerified = false;

    /**
     * 사용자 계정 상태 ENUM
     * ACTIVE: 정상
     * INACTIVE: 비활성화
     * BANNED: 정지
     */
    public enum AccountStatus {
        ACTIVE, INACTIVE, BANNED
    }
}