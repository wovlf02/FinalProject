package com.hamcam.back.service.community.chat;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.chat.ChatMessage;
import com.hamcam.back.entity.chat.ChatParticipant;
import com.hamcam.back.entity.chat.ChatRead;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.global.security.SecurityUtil;
import com.hamcam.back.repository.chat.ChatMessageRepository;
import com.hamcam.back.repository.chat.ChatParticipantRepository;
import com.hamcam.back.repository.chat.ChatReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * [ChatReadService]
 * 사용자의 메시지 읽음 처리 및 읽지 않은 참여자 수 계산을 담당하는 서비스
 */
@Service
@RequiredArgsConstructor
public class ChatReadService {

    private final ChatReadRepository chatReadRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final SecurityUtil securityUtil;

    /**
     * 메시지를 아직 읽지 않은 사용자 수를 반환합니다.
     */
    @Transactional(readOnly = true)
    public int getUnreadCountForMessage(Long messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new CustomException(ErrorCode.MESSAGE_NOT_FOUND));
        return calculateUnreadCount(message);
    }

    /**
     * 사용자가 방에 입장했을 때 마지막 읽은 메시지 갱신
     */
    @Transactional
    public void updateLastReadMessage(Long roomId, Long userId) {
        ChatParticipant participant = chatParticipantRepository.findByChatRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ChatMessage lastMessage = chatMessageRepository.findTopByChatRoomOrderBySentAtDesc(participant.getChatRoom());
        if (lastMessage != null) {
            participant.setLastReadMessageId(lastMessage.getId());
            chatParticipantRepository.save(participant);
        }
    }

    /**
     * 인증된 사용자 기준 마지막 메시지 갱신 (SecurityUtil 사용)
     */
    @Transactional
    public void updateLastReadMessageByAuthenticatedUser(Long roomId) {
        Long userId = securityUtil.getCurrentUserId();
        updateLastReadMessage(roomId, userId);
    }

    /**
     * ✅ 명시적 userId를 전달받아 읽음 처리 수행
     */
    @Transactional
    public int markReadAsUserId(Long roomId, Long messageId, Long userId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new CustomException(ErrorCode.MESSAGE_NOT_FOUND));

        if (!message.getSender().getId().equals(userId)) {
            User reader = new User();
            reader.setId(userId);

            boolean alreadyRead = chatReadRepository.existsByMessageAndUser(message, reader);
            if (!alreadyRead) {
                chatReadRepository.save(ChatRead.create(message, reader));
            }
        }

        return calculateUnreadCount(message);
    }

    /**
     * 📌 이전 버전: 인증된 사용자 기준으로 읽음 처리 (호환용)
     */
    @Transactional
    public int markReadAsAuthenticatedUser(Long roomId, Long messageId) {
        Long userId = securityUtil.getCurrentUserId();
        return markReadAsUserId(roomId, messageId, userId);
    }

    /**
     * 내부 공통 로직: 읽지 않은 사용자 수 계산
     */
    private int calculateUnreadCount(ChatMessage message) {
        Long roomId = message.getChatRoom().getId();
        int totalParticipants = chatParticipantRepository.countByChatRoomId(roomId);
        long readCount = chatReadRepository.countByMessage(message);
        return (int) (totalParticipants - readCount - 1); // 보낸 사람 제외
    }
}
