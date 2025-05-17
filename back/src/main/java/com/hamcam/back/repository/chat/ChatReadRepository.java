package com.hamcam.back.repository.chat;

import com.hamcam.back.entity.chat.ChatRead;
import com.hamcam.back.entity.chat.ChatMessage;
import com.hamcam.back.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatReadRepository extends JpaRepository<ChatRead, Long> {

    /**
     * 특정 메시지를 읽은 사용자 수 조회
     */
    long countByMessage(ChatMessage message);

    /**
     * 특정 메시지에 대해 특정 유저가 읽었는지 여부
     */
    boolean existsByMessageAndUser(ChatMessage message, User user);
}
