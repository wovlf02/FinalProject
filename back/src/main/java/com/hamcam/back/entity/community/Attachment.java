package com.hamcam.back.entity.community;

import com.hamcam.back.entity.chat.ChatMessage;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 첨부파일 엔티티 (MySQL 호환)
 */
@Entity
@Table(name = "attachment",
        indexes = {
                @Index(name = "idx_attachment_post", columnList = "post_id"),
                @Index(name = "idx_attachment_chat_message", columnList = "chat_message_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attachment {

    /**
     * MySQL에서는 IDENTITY 전략 사용
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 원본 파일명
     */
    @Column(name = "original_file_name", nullable = false, length = 255)
    private String originalFileName;

    /**
     * 저장 파일명 (UUID 포함)
     */
    @Column(name = "stored_file_name", nullable = false, length = 500)
    private String storedFileName;

    /**
     * MIME 타입
     */
    @Column(name = "content_type", length = 100)
    private String contentType;

    /**
     * 미리보기 가능 여부
     */
    @Column(name = "preview_available", nullable = false)
    private boolean previewAvailable;

    /**
     * 파일 크기 (byte 단위)
     */
    @Column(name = "file_size")
    private Long fileSize;

    /**
     * 생성 시각
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 게시글 첨부파일
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    /**
     * 채팅 메시지 첨부파일
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_message_id")
    private ChatMessage chatMessage;

    /**
     * 파일 기본 접근 경로
     */
    public static final String DEFAULT_BASE_URL = "/uploads";

    /**
     * 생성 시 자동 시간 설정
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * 파일 접근 URL 반환
     */
    public String getFileUrl() {
        return (storedFileName != null) ? DEFAULT_BASE_URL + "/" + storedFileName : null;
    }

    /**
     * 유효한 첨부파일인지 검사 (게시글 또는 채팅 메시지 중 하나에만 연결)
     */
    public boolean isValidAttachment() {
        return (post != null && chatMessage == null) || (post == null && chatMessage != null);
    }
}
