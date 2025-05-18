package com.hamcam.back.entity.chat;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 채팅 메시지 엔티티 (MySQL 기반)
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

    /**
     * MySQL에서는 SEQUENCE 대신 IDENTITY 사용
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 해당 메시지가 속한 채팅방
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    /**
     * 메시지를 보낸 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    /**
     * 텍스트 내용 (최대 2000자)
     */
    @Column(length = 2000)
    private String content;

    /**
     * 메시지 타입 (TEXT, IMAGE, FILE 등)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ChatMessageType type;

    /**
     * 첨부 파일 저장 이름
     */
    @Column(name = "stored_file_name", length = 500)
    private String storedFileName;

    /**
     * 첨부 파일의 MIME 타입
     */
    @Column(name = "content_type", length = 100)
    private String contentType;

    /**
     * 전송 시각
     */
    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;

    /**
     * 메시지를 읽은 사용자 목록
     */
    @Builder.Default
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRead> reads = new ArrayList<>();

    /**
     * 전송 시각 자동 설정
     */
    @PrePersist
    protected void onSend() {
        this.sentAt = LocalDateTime.now();
    }

    /**
     * 파일 메시지 여부 확인
     */
    public boolean isFileMessage() {
        return type == ChatMessageType.IMAGE || type == ChatMessageType.FILE;
    }

    /**
     * 첨부 파일 접근 경로 반환
     */
    public String getFileUrl() {
        return storedFileName != null ? "/uploads/chat/" + storedFileName : null;
    }
}
