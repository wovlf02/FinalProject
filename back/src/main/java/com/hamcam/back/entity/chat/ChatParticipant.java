package com.hamcam.back.entity.chat;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 채팅방 참여자 엔티티 (MySQL 기반)
 */
@Entity
@Table(name = "chat_participant", // ✅ 소문자 테이블명
        indexes = {
                @Index(name = "idx_participant_user_room", columnList = "user_id, chat_room_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ MySQL용 기본 키 전략
    private Long id;

    /**
     * 참여한 채팅방
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    /**
     * 참여자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 입장 시각
     */
    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    /**
     * 퇴장 시각 (null이면 현재 참여 중)
     */
    @Column(name = "left_at")
    private LocalDateTime leftAt;

    /**
     * 마지막 읽은 메시지 ID
     */
    @Column(name = "last_read_message_id")
    private Long lastReadMessageId;

    /**
     * 채팅방 알림 끔 여부
     */
    @Column(name = "is_muted", nullable = false)
    @Builder.Default
    private boolean isMuted = false;

    /**
     * 채팅방 상단 고정 여부
     */
    @Column(name = "is_pinned", nullable = false)
    @Builder.Default
    private boolean isPinned = false;

    /**
     * 입장 시각 및 상태 초기화
     */
    @PrePersist
    protected void onJoin() {
        this.joinedAt = LocalDateTime.now();
    }

    /**
     * 퇴장 처리
     */
    public void leave() {
        this.leftAt = LocalDateTime.now();
    }

    /**
     * 알림 설정 토글
     */
    public void toggleMute() {
        this.isMuted = !this.isMuted;
    }

    /**
     * 상단 고정 토글
     */
    public void togglePin() {
        this.isPinned = !this.isPinned;
    }
}
