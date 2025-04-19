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
        name = "report",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"reporter_id", "post_id", "comment_id", "reply_id", "target_user_id"}
        )
)
public class Report {

    /**
     * 신고 고유 ID (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 신고자 (User)
     * 실제 FK는 생성되지 않도록 설정 (제약 오류 방지)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User reporter;

    /**
     * 신고 대상 게시글 (nullable)
     * 외래키 제약 없음
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Post post;

    /**
     * 신고 대상 댓글 (nullable)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Comment comment;

    /**
     * 신고 대상 대댓글 (nullable)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Reply reply;

    /**
     * 신고 대상 사용자 (nullable)
     * 외래키 제약 없음 → users.id가 PK/UNIQUE가 아니더라도 오류 없이 생성 가능
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User targetUser;

    /**
     * 신고 사유 (필수)
     */
    @Column(nullable = false)
    private String reason;

    /**
     * 신고 상태 (예: PENDING, RESOLVED)
     */
    @Column(length = 20)
    private String status;

    /**
     * 신고 접수 시간
     */
    private LocalDateTime reportedAt;

    /**
     * 신고 시간 자동 세팅
     */
    @PrePersist
    protected void onReport() {
        this.reportedAt = LocalDateTime.now();
    }
}
