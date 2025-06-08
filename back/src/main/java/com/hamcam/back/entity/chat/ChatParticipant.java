package com.hamcam.back.entity.chat;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 채팅방 참여자 엔티티 - MySQL 호환
 */
@Entity
@Table(name = "chat_participant",
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

    /**
     * MySQL에서는 IDENTITY 전략 사용
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
     * 마지막으로 읽은 메시지 ID
     */
    @Column(name = "last_read_message_id")
    private Long lastReadMessageId;

    /**
     * 알림 꺼짐 여부 (기본값: false)
     */
    @Column(name = "is_muted", nullable = false)
    @Builder.Default
    private boolean isMuted = false;

    /**
     * 상단 고정 여부 (기본값: false)
     */
    @Column(name = "is_pinned", nullable = false)
    @Builder.Default
    private boolean isPinned = false;

    /**
     * 입장 시각
     */
    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    /**
     * 입장 시각 자동 설정
     */
    @PrePersist
    protected void onJoin() {
        this.joinedAt = LocalDateTime.now();
    }
}
