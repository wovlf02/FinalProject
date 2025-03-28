package com.studymate.back.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * ChatMember Entity (채팅방 참여자)
 * 사용자와 채팅방 간의 관계를 저장하는 JPA 엔티티
 * users 테이블과 연관 (참여자 정보 저장)
 * chat_rooms 테이블과 연관 (어떤 채팅방에 속해 있는지 저장)
 * 참여자의 역할 (일반 멤버 or 관리자) 저장
 */
@Entity
@Table(name = "chat_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMember {

    /**
     * 채팅방 (ChatRoom)
     * Many-to-One 관계 (한 채팅방에는 여러 명이 참여 가능)
     * 채팅방 삭제 시, 참여 정보도 삭제됨 (CASCADE 설정)
     */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false, foreignKey = @ForeignKey(name = "fk_member_chat"))
    private  ChatRoom chatRoom;

    /**
     * 참여자 (User)
     * Many-to-One 관계 (한 사용자는 여러 채팅방에 참여 가능)
     * 사용자 삭제 시, 참여 정보도 삭제됨 (CASCADE 설정)
     */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_member_user"))
    private User user;

    /**
     * 사용자 역할 (role)
     * member -> 일반 사용자 / admin -> 관리자 값만 허용
     * Default: member
     */
    @Column(name = "role", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ChatMemberRole role = ChatMemberRole.MEMBER;

    /**
     * 채팅방 참여 시각 (joined_at)
     * Default: 현재 시각
     * 사용자가 채팅방에 참여한 시각 저장
     */
    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;
}
