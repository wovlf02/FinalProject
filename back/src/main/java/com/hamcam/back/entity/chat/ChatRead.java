package com.hamcam.back.entity.chat;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * [ChatRead]
 * 채팅 메시지 읽음 처리 정보
 * - 한 사용자가 한 메시지를 읽었는지 여부 저장
 */
@Entity
@Table(
        name = "chat_read",
        uniqueConstraints = @UniqueConstraint(columnNames = {"message_id", "user_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRead {

    /**
     * PK: 읽음 ID (MySQL용 AUTO_INCREMENT)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ✅ 읽은 메시지 (ManyToOne)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ChatMessage message;

    /**
     * ✅ 읽은 사용자 (ManyToOne)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = true;


    /**
     * ✅ 읽은 시각
     */
    @Column(name = "read_at", nullable = false)
    private LocalDateTime readAt;

    public static ChatRead create(ChatMessage message, User user) {
        return ChatRead.builder()
                .message(message)
                .user(user)
                .readAt(LocalDateTime.now())
                .isRead(true) // ✅ 필수
                .build();
    }

}
