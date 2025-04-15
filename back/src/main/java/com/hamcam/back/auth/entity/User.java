package com.hamcam.back.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

/**
 * [User 엔티티 클래스 + UserDetails 구현]
 * 모든 사용자 정보를 저장하는 핵심 테이블
 * 로그인, 회원가입, 커뮤니티 활동, 공부 기록, 채팅, 알림 등 거의 모든 기능과 연계됨
 * Spring Security에서 사용자 인증 정보를 직접 제공하는 도메인 모델
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
public class User implements UserDetails {

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

    // ------------------------ UserDetails 인터페이스 구현 ---------------------------

    /**
     * 현재 사용자에게 부여된 권한(역할) 목록을 반환하는 메서드
     * ex) ROLE_USER, ROLE_ADMIN 등
     * -> 현재 시스템에서는 사용자 권한 시스템이 따로 없으므로 빈 리스트 반환
     * -> 향후 역할 기반 접근 제어를 도입하면 여기에 권한을 추가
     * @return 사용자의 GrantAuthority 목록 (현재는 비어있음)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    /**
     * 사용자 계정의 만료 여부를 반환
     * -> true: 계정이 유효함 (로그인 가능)
     * -> false: 계정이 만료되어 더 이상 사용할 수 없음
     * -> 일반적으로 정기적인 계정 검증/갱신이 필요한 서비스에서 사용
     * -> 현재 서비스에서는 만료 제도 없음 -> 항상 true 반환
     * @return 계정 잠김 여부 (정지 상태가 아닌 경우 true)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 사용자 계정이 잠겨있는지 여부
     * -> true: 계정이 잠겨있지 않음 (로그인 가능)
     * -> false: 계정이 잠겨있어 로그인 불가 (ex. 정지된 계정)
     * -> 계정 상태가 BANNED인 경우, 잠긴 상태로 간주
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        return this.status != AccountStatus.BANNED;
    }

    /**
     * 사용자 비밀번호(자격 증명)가 만료되었는지 여부
     * -> true: 비밀번호 유효 (로그인 가능)
     * -> false: 비밀번호가 오래되어 만료됨 -> 변경 필요
     * -> 현재 서비스는 비밀번호 만료 정책 없음 -> 항상 true 반환
     * @return 자격 증명이 유효한지 여부 (항상 true)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 사용자 계정이 사용 가능한 상태인지 여부
     * -> true: 계정이 활성화됨 (로그인 가능)
     * -> false: 계정이 비활성화됨 (INACTIVE로 설정된 경우)
     * -> 주의: 여기서 false가 반환되면 Spring Security에서 로그인 자체가 막힘
     * -> status가 ACTIVE가 아닌 경우 사용 불가 처리
     * @return 계정 활성화 여부 (ACTIVE일 때만 true)
     */
    @Override
    public boolean isEnabled() {
        return this.status != AccountStatus.ACTIVE;
    }
}