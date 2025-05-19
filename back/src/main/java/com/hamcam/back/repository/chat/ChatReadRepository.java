package com.hamcam.back.repository.chat;

import com.hamcam.back.entity.chat.ChatRead;
import com.hamcam.back.entity.chat.ChatMessage;
import com.hamcam.back.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * [ChatReadRepository]
 *
 * 메시지 읽음 상태(ChatRead) 관련 JPA Repository입니다.
 * - 누가 어떤 메시지를 읽었는지 추적합니다.
 * - 메시지별 읽음 수, 사용자별 읽음 여부 확인 등에 사용됩니다.
 */
public interface ChatReadRepository extends JpaRepository<ChatRead, Long> {

    /**
     * [메시지 읽은 사용자 수 조회]
     *
     * @param message 대상 메시지
     * @return 해당 메시지를 읽은 사용자 수
     */
    long countByMessage(ChatMessage message);

    /**
     * [사용자의 메시지 읽음 여부 확인]
     *
     * @param message 대상 메시지
     * @param user 확인할 사용자
     * @return 읽었으면 true, 읽지 않았으면 false
     */
    boolean existsByMessageAndUser(ChatMessage message, User user);
}
