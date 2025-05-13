package com.hamcam.back.entity.chat;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 채팅 메시지 엔티티 (MySQL 기반)
 */
@Entity
@Table(name = "chat_message", // ✅ 테이블명 소문자 권장
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
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ MySQL에서는 IDENTITY 사용
    private Long id;

    /**
     * 채팅방
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    /**
     * 메시지 보낸 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    /**
     * 메시지 내용
     */
    @Column(nullable = false, length = 2000)
    private String content;

    /**
     * 메시지 타입 (TEXT, IMAGE, FILE)
     */
    @Column(nullable = false, length = 50)
    private String type;

    /**
     * 첨부파일 저장명 (파일 메시지인 경우)
     */
    @Column(name = "stored_file_name", length = 500)
    private String storedFileName;

    /**
     * MIME 타입 (파일 메시지인 경우)
     */
    @Column(name = "content_type", length = 100)
    private String contentType;

    /**
     * 전송 시각
     */
    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;

    /**
     * 삭제 여부 (소프트 삭제)
     */
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    /**
     * 삭제된 시각
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * 전송 시각 자동 설정
     */
    @PrePersist
    protected void onSend() {
        this.sentAt = LocalDateTime.now();
        this.isDeleted = false;
    }

    /**
     * 소프트 삭제 처리
     */
    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * 첨부파일 메시지일 경우 다운로드 URL 반환
     */
    public String getFileUrl() {
        return this.storedFileName != null
                ? "/uploads/chat/" + this.storedFileName
                : null;
    }
}
