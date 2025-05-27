package com.hamcam.back.entity.video;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ✅ WebRTC 기반 영상 팀방 Entity
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class VideoRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 방 제목 */
    @Column(nullable = false, length = 100)
    private String title;

    /** 초대 코드 */
    @Column(nullable = false, unique = true, length = 10)
    private String inviteCode;

    /** 방 비밀번호 (선택 입력) */
    @Column(length = 30)
    private String password;

    /** 최대 인원 수 */
    @Column(nullable = false)
    private int maxParticipants;

    /** QUIZ or FOCUS */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType roomType;

    /** 활성화 상태 */
    @Column(nullable = false)
    private boolean isActive;

    /** 생성 시간 */
    private LocalDateTime createdAt;

    /** 생성 직전 값 자동 세팅 */
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }

    /** 방장 정보 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_user_id")
    private User host;

    /** 참가자 목록 */
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants = new ArrayList<>();

    /** 발표 기록 목록 */
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Presentation> presentations = new ArrayList<>();

    /** 활성화 상태 변경 (종료 시 사용) */
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
}
