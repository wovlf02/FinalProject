package com.hamcam.back.entity.chat;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 채팅방 참여자 엔티티 - Oracle Express 호환
 */
@Entity
@Table(name = "CHAT_PARTICIPANT",
        indexes = {
                @Index(name = "IDX_PARTICIPANT_USER_ROOM", columnList = "USER_ID, CHAT_ROOM_ID")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_participant_seq_generator")
    @SequenceGenerator(
            name = "chat_participant_seq_generator",
            sequenceName = "CHAT_PARTICIPANT_SEQ",
            allocationSize = 1
    )
    private Long id;

    /** 참여한 채팅방 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHAT_ROOM_ID", nullable = false)
    private ChatRoom chatRoom;

    /** 참여자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    /** 마지막 읽은 메시지 ID (미리보기/안읽음 처리용) */
    @Column(name = "LAST_READ_MESSAGE_ID")
    private Long lastReadMessageId;

    /** 채팅방 알림 꺼짐 여부 (false = 알림 O) */
    @Column(name = "IS_MUTED", nullable = false)
    @Builder.Default
    private boolean isMuted = false;

    /** 채팅방 상단 고정 여부 */
    @Column(name = "IS_PINNED", nullable = false)
    @Builder.Default
    private boolean isPinned = false;

    /** 입장 시각 */
    @Column(name = "JOINED_AT", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    // ===== 입장 시각 설정 =====
    @PrePersist
    protected void onJoin() {
        this.joinedAt = LocalDateTime.now();
    }
}
