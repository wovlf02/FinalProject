package com.hamcam.back.entity.community;

import jakarta.persistence.*;
import lombok.*;

/**
 * 첨부파일(Attachment) 엔티티
 * <p>
 * 게시글(Post), 댓글(Comment), 대댓글(Reply)에 첨부된 파일 정보를 저장합니다.
 * 하나의 첨부파일은 하나의 대상(Post, Comment, Reply)에만 연결됩니다.
 * </p>
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
     * 첨부된 원본 파일명 (사용자 업로드 기준)
     */
    private String originalFileName;

    /**
     * 서버에 저장된 실제 파일명 (UUID 등으로 식별)
     */
    private String storedFileName;

    /**
     * MIME 타입 (예: image/png, application/pdf)
     */
    private String contentType;

    /**
     * 이미지 미리보기 가능 여부
     */
    private boolean previewAvailable;

    /**
     * 게시글에 첨부된 경우 (nullable)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    /**
     * 댓글에 첨부된 경우
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    /**
     * 대댓글에 첨부된 경우
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_id")
    private Reply reply;
}
