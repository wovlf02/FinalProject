package com.hamcam.back.entity.community;

import jakarta.persistence.*;
import lombok.*;

/**
 * 첨부파일(Attachment) 엔티티
 */
@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 업로드된 파일의 원본 이름
     */
    private String originalFileName;

    /**
     * 서버에 저장된 파일명 (UUID 등)
     */
    private String storedFileName;

    /**
     * MIME 타입 (image/png, application/pdf 등)
     */
    private String contentType;

    /**
     * 이미지 미리보기 가능 여부
     */
    private boolean previewAvailable;

    /**
     * 게시글 연관 (nullable)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    /**
     * 댓글 연관 (nullable)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    /**
     * 대댓글 연관 (nullable)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_id")
    private Reply reply;

    /**
     * 첨부파일 다운로드 URL 반환
     * (예시 경로: /api/community/attachments/{id}/download)
     */
    public String getFileUrl() {
        return "/api/community/attachments/" + this.id + "/download";
    }
}
