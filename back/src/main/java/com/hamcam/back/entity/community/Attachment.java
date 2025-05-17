package com.hamcam.back.entity.community;

import com.hamcam.back.entity.chat.ChatMessage;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 첨부파일 엔티티 (Oracle Express 호환)
 */
@Entity
@Table(name = "ATTACHMENT",
        indexes = {
                @Index(name = "IDX_ATTACHMENT_POST", columnList = "POST_ID"),
                @Index(name = "IDX_ATTACHMENT_CHAT_MESSAGE", columnList = "CHAT_MESSAGE_ID")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attachment_seq_generator")
    @SequenceGenerator(
            name = "attachment_seq_generator",
            sequenceName = "ATTACHMENT_SEQ",
            allocationSize = 1
    )
    private Long id;

    /** 원본 파일명 */
    @Column(name = "ORIGINAL_FILE_NAME", nullable = false, length = 255)
    private String originalFileName;

    /** 저장 파일명 (UUID 포함) */
    @Column(name = "STORED_FILE_NAME", nullable = false, length = 500)
    private String storedFileName;

    /** MIME 타입 (예: image/png, application/pdf) */
    @Column(name = "CONTENT_TYPE", length = 100)
    private String contentType;

    /** 미리보기 가능 여부 (이미지 등) */
    @Column(name = "PREVIEW_AVAILABLE", nullable = false)
    private boolean previewAvailable;

    /** 파일 크기 (byte 단위) */
    @Column(name = "FILE_SIZE")
    private Long fileSize;

    /** 생성 시각 */
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 게시글 첨부파일 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID")
    private Post post;

    /** 채팅 첨부파일 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHAT_MESSAGE_ID")
    private ChatMessage chatMessage;

    /** 업로드 기본 경로 */
    public static final String DEFAULT_BASE_URL = "/uploads";

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /** 파일 접근 경로 반환 */
    public String getFileUrl() {
        return (storedFileName != null) ? DEFAULT_BASE_URL + "/" + storedFileName : null;
    }

    /** 게시글 또는 채팅 중 하나에만 연결되었는지 유효성 확인 */
    public boolean isValidAttachment() {
        return (post != null && chatMessage == null) || (post == null && chatMessage != null);
    }
}
