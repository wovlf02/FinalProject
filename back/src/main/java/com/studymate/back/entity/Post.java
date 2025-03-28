package com.studymate.back.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Post Entity
 * 게시판의 개별 게시글 정보를 관리하는 JPA 엔티티
 * users 테이블과 관계를 맺어 작성자 정보 저장
 * 댓글, 좋아요, 첨부파일 등의 연관 과계 포함
 */
@Entity
@Table(name = "posts")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    /**
     * 게시글 ID (Primary Key)
     * 자동 증가 (IDENTITY 전략)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    /**
     * 작성자 (User) ID (Foreign Key)
     * users 테이블의 id와 연결
     * 한 사용자가 여러 게시글을 작성할 수 있음 (Many-to-One)
     * 사용자 삭제 시, 해당 사용자의 게시글은 남아있도록 설정 (SET NULL)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true, foreignKey = @ForeignKey(name = "fk_post_user"))
    private User user;

    /**
     * 게시글 제목 (title)
     * Not Null (필수 값)
     * 최대 200자 제한
     */
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * 게시글 내용 (content)
     * Not Null (필수 값)
     * 긴 텍스트 저장을 위해 CLOB 타입 사용
     */
    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    /**
     * 게시글 생성 시각 (created_at)
     * Default: 현재 시각
     * 게시글이 생성될 때 자동 설정
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 게시글 수정 시각 (updated_at)
     * 게시글이 수정될 때 자동 갱신
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
