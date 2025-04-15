package com.hamcam.back.entity.chat;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 채팅방 엔티티
 * <p>
 * 게시글, 그룹, 스터디, 1:1 채팅 등 다양한 타입의 채팅방을 표현합니다.
 * </p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 채팅방 이름
     */
    private String name;

    /**
     * 채팅방 유형 (POST, GROUP, STUDY, DIRECT 등)
     */
    private String type;

    /**
     * 연동된 리소스 ID (예: 게시글 ID, 그룹 ID 등)
     */
    private Long referenceId;

    /**
     * 채팅방 생성 시각
     */
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatParticipant> participants = new ArrayList<>();
}
