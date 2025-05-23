package com.hamcam.back.service.community.chat;

import com.hamcam.back.dto.community.chat.request.ChatEnterRequest;
import com.hamcam.back.dto.community.chat.request.ChatReadRequest;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.chat.ChatMessage;
import com.hamcam.back.entity.chat.ChatParticipant;
import com.hamcam.back.entity.chat.ChatRead;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.chat.ChatMessageRepository;
import com.hamcam.back.repository.chat.ChatParticipantRepository;
import com.hamcam.back.repository.chat.ChatReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * [ChatReadService]
 * 채팅 메시지의 읽음 처리 및 읽지 않은 사용자 수 계산 서비스
 */
@Service
@RequiredArgsConstructor
public class ChatReadService {

    private final ChatReadRepository chatReadRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final UserRepository userRepository;

    /**
     * ✅ 특정 메시지에 대해 읽지 않은 사용자 수 계산
     */
    @Transactional(readOnly = true)
    public int getUnreadCount(ChatReadRequest request) {
        ChatMessage message = getMessage(request.getMessageId());
        return calculateUnreadCount(message);
    }

    /**
     * ✅ 채팅방 입장 시 마지막 메시지 ID를 기준으로 읽음 처리
     */
    @Transactional
    public void updateLastReadMessage(ChatEnterRequest request) {
        ChatParticipant participant = chatParticipantRepository.findByChatRoomIdAndUserId(
                        request.getRoomId(), request.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.ACCESS_DENIED));

        ChatMessage lastMessage = chatMessageRepository.findTopByChatRoomOrderBySentAtDesc(participant.getChatRoom());
        if (lastMessage != null) {
            participant.setLastReadMessageId(lastMessage.getId());
            chatParticipantRepository.save(participant);
        }
    }

    /**
     * ✅ 읽음 처리 및 읽지 않은 사용자 수 계산 (DTO 기반)
     */
    @Transactional
    public int markAsRead(ChatReadRequest request) {
        ChatMessage message = getMessage(request.getMessageId());

        if (!message.getSender().getId().equals(request.getUserId())) {
            User user = getUser(request.getUserId());

            boolean alreadyRead = chatReadRepository.existsByMessageAndUser(message, user);
            if (!alreadyRead) {
                ChatRead read = ChatRead.create(message, user);
                chatReadRepository.save(read);
            }
        }

        return calculateUnreadCount(message);
    }

    /**
     * ✅ WebSocket 등에서 간단한 파라미터 기반 읽음 처리
     */
    @Transactional
    public int markReadAsUserId(Long roomId, Long messageId, Long userId) {
        ChatReadRequest request = ChatReadRequest.builder()
                .roomId(roomId)
                .messageId(messageId)
                .userId(userId)
                .build();
        return markAsRead(request);
    }

    // ===== 내부 유틸 =====

    private int calculateUnreadCount(ChatMessage message) {
        Long roomId = message.getChatRoom().getId();
        int totalParticipants = chatParticipantRepository.countByChatRoomId(roomId);
        long readCount = chatReadRepository.countByMessage(message);
        return (int) (totalParticipants - readCount - 1); // 보낸 사람은 읽은 것으로 간주
    }

    private ChatMessage getMessage(Long messageId) {
        return chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new CustomException(ErrorCode.MESSAGE_NOT_FOUND));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
