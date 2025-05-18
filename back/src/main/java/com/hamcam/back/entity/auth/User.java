package com.hamcam.back.entity.auth;

import com.hamcam.back.entity.chat.ChatMessage;
import com.hamcam.back.entity.chat.ChatParticipant;
import com.hamcam.back.entity.community.*;
import com.hamcam.back.entity.friend.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 사용자 엔티티 (MySQL 기반, 하드 삭제 적용)
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /**
     * MySQL에서는 AUTO_INCREMENT를 사용하는 IDENTITY 전략이 일반적입니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 100)
    private String nickname;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Column(nullable = false)
    private Integer grade;

    /**
     * ElementCollection은 MySQL에서도 지원되며, user_subjects 테이블로 매핑됩니다.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_subjects", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "subject")
    private List<String> subjects;

    @Column(nullable = false, length = 50)
    private String studyHabit;

    /**
     * DATETIME 타입으로 자동 처리됩니다.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ===== 연관관계 =====

    @Builder.Default
    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostFavorite> favorites = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "reporter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reportsSent = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "targetUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reportsReceived = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FriendRequest> friendRequestsSent = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FriendRequest> friendRequestsReceived = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Friend> friends = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "friend", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Friend> friendOf = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "blocker", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FriendBlock> blockedUsers = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "blocked", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FriendBlock> blockedBy = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "reporter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FriendReport> friendReportsSent = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "reported", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FriendReport> friendReportsReceived = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> chatMessages = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatParticipant> chatParticipations = new ArrayList<>();

    // ===== 콜백 =====

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 비밀번호 변경
     */
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }
}
