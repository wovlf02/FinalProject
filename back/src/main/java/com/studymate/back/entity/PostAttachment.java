package com.studymate.back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * PostAttachment Entity (게시글 첨부파일)
 * 게시글에 첨부된 파일을 관리하는 JPA 엔티티
 * posts 테이블과 연관 (어떤 게시글의 첨부파일인지 저장
 */
@Entity
@Table(name = "post_attachments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostAttachment {

    /**
     * 첨부파일 ID (Primary Key)
     * 자동 증가 (IDENTITY 전략)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attachment_id")
    private Long attachmentId;

    /**
     * 해당 첨부파일이 속한 게시글 (Post)
     * Many-to-One 관계 (하나의 게시글에 여러 개의 첨부파일 가능)
     * 부모 게시글 삭제 시, 첨부파일도 삭제됨 (CASCADE 설정)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, foreignKey = @ForeignKey(name = "fk_attachment_post"))
    private Post post;

    /**
     * 파일 데이터 (Binary Large Object - BLOB)
     * 이미지, 문서, 동영상 등 다양한 파일 유형 저장 가능
     */
    @Lob
    @Column(name = "file_data", nullable = false)
    private byte[] fileData;

    /**
     * 파일 유형 (예: 이미지, 문서 등)
     * 최대 50자 제한
     * Not Null (필수 값)
     */
    @Column(name = "file_type", nullable = false, length = 50)
    private String fileType;

    /**
     * 파일 업로드 시각 (created_at)
     * Default: 현재시각
     * 파일이 업로드될 때 자동 설정
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
