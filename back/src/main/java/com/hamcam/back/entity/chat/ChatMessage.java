package com.hamcam.back.entity.chat;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 채팅 메시지 엔티티 (TEXT, IMAGE, FILE 지원)
 */
@Entity
@Table(name = "chat_message",
        indexes = {
                @Index(name = "idx_chat_room_sent_at", columnList = "chat_room_id, sent_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 소속 채팅방
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    /**
     * 메시지 전송자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    /**
     * 메시지 내용 (TEXT만 필수)
     */
    @Column(length = 2000)
    private String content;

    /**
     * 메시지 타입 (TEXT, IMAGE, FILE)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ChatMessageType type;

    /**
     * 저장된 첨부파일명
     */
    @Column(name = "stored_file_name", length = 500)
    private String storedFileName;

    /**
     * 첨부파일 MIME 타입
     */
    @Column(name = "content_type", length = 100)
    private String contentType;

    /**
     * 전송 시각
     */
    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;

    // ===== 콜백 =====

    @PrePersist
    protected void onSend() {
        this.sentAt = LocalDateTime.now();
    }

    // ===== 유틸 =====

    public boolean isFileMessage() {
        return type == ChatMessageType.IMAGE || type == ChatMessageType.FILE;
    }

    public String getFileUrl() {
        return storedFileName != null ? "/uploads/chat/" + storedFileName : null;
    }
}
