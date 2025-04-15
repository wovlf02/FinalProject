package com.hamcam.back.entity.community;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 신고(Report) 엔티티
 * <p>
 * 게시글, 댓글, 대댓글 또는 사용자에 대한 신고 내역을 저장합니다.
 * 하나의 신고는 하나의 사용자(User)가 하나의 대상(Post, Comment, Reply, User)을 신고한 내용으로 구성됩니다.
 * </p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"reporter_id", "post_id", "comment_id", "reply_id", "target_user_id"})
)
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 신고한 사용자 (신고자)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    /**
     * 신고 대상 게시글 (nullable)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    /**
     * 신고 대상 댓글
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    /**
     * 신고 대상 대댓글
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_id")
    private Reply reply;

    /**
     * 신고 대상 사용자 (User)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id")
    private User targetUser;

    /**
     * 신고 사유
     */
    @Column(nullable = false)
    private String reason;

    /**
     * 신고 상태 (예: PENDING, RESOLVED)
     */
    private String status;

    /**
     * 신고 시각
     */
    private LocalDateTime reportedAt;
}
