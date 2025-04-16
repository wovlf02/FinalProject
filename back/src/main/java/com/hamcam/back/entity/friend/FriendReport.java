package com.hamcam.back.entity.friend;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 사용자 신고(FriendReport) 엔티티
 * <p>
 * 사용자가 친구 또는 일반 사용자를 신고한 내역을 저장합니다.
 * 신고 사유, 신고자-피신고자 관계, 처리 상태 등을 포함하며,
 * 관리자에 의해 처리될 수 있습니다.
 * </p>
 */
@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"reporter_id", "reported_id"})
)
public class FriendReport {

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
     * 신고당한 사용자 (피신고자)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_id", nullable = false)
    private User reported;

    /**
     * 신고 사유
     */
    @Column(nullable = false)
    private String reason;

    /**
     * 신고 시각
     */
    private LocalDateTime reportedAt;

    /**
     * 신고 처리 상태 (예: PENDING, RESOLVED)
     */
    private String status;
}
