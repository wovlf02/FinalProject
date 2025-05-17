package com.hamcam.back.entity.friend;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 친구 관계 엔티티 (Oracle Express 기반)
 */
@Entity
@Table(
        name = "FRIEND",
        uniqueConstraints = @UniqueConstraint(name = "UK_USER_FRIEND", columnNames = {"USER_ID", "FRIEND_ID"}),
        indexes = {
                @Index(name = "IDX_FRIEND_USER", columnList = "USER_ID"),
                @Index(name = "IDX_FRIEND_FRIEND", columnList = "FRIEND_ID"),
                @Index(name = "IDX_FRIEND_IS_DELETED", columnList = "IS_DELETED")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "friend_seq_generator")
    @SequenceGenerator(
            name = "friend_seq_generator",
            sequenceName = "FRIEND_SEQ",
            allocationSize = 1
    )
    private Long id;

    /** 친구 요청 보낸 사용자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    /** 친구 요청 받은 사용자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FRIEND_ID", nullable = false)
    private User friend;

    /** 친구 등록 일시 */
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 논리 삭제 여부 */
    @Builder.Default
    @Column(name = "IS_DELETED", nullable = false)
    private boolean isDeleted = false;

    /** 친구 삭제 일시 */
    @Column(name = "DELETED_AT")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (!this.isDeleted) this.isDeleted = false;
    }

    // ===== 비즈니스 로직 =====

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public void restore() {
        this.isDeleted = false;
        this.deletedAt = null;
    }
}
