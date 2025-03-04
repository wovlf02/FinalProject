package com.studymate.back.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * PostLike Entity (게시글 추천)
 * 특정사용자가 특정 게시글에 추천을 누른 정보를 저장
 * 동일 사용자가 한 게시글에 여러 번 좋아요를 누를 수 없도록 복합 키 적용
 * users 테이블과 연관 (추천을 누를 사용자의 정보를 저장)
 * posts 테이블과 연관 (추천이 눌려진 게시글 저장)
 */
@Entity
@Table(name = "post_likes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostLike {

    /**
     * 추천을 누른 사용자 (User)
     * Many-to-One 관계 (한 사용자는 여러 게시글에 좋아요 가능)
     * 같은 사용자가 동일한 게시글에 여러 번 추천을 누르지 못하도록 복합 키 설정
     */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_like_user"))
    private User user;

    /**
     * 추천이 눌린 게시글 (Post)
     * Many-to-One 관계 (한 게시글은 여러 사용자에게 추천을 받을 수 있음)
     * 동일한 사용자가 같은 게시글에 여러 번 추천을 누르지 못하도록 복합 키 설정
     */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, foreignKey = @ForeignKey(name = "fk_like_post"))
    private Post post;

    /**
     * 추천을 누른 시각 (created_at)
     * Default: 현재 시각
     * 사용자가 추천을 누른시각을 저장
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
