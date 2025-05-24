package com.hamcam.back.service.community.chat;

import com.hamcam.back.dto.community.chat.request.ChatReadRequest;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.chat.ChatMessage;
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

@Service
@RequiredArgsConstructor
public class ChatReadService {

    private final ChatReadRepository chatReadRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final UserRepository userRepository;

    /**
     * ✅ WebSocket 기반 읽음 처리 (userId 직접 전달)
     */
    @Transactional
    public int markReadAsUserId(Long roomId, Long messageId, Long userId) {
        ChatReadRequest request = ChatReadRequest.builder()
                .roomId(roomId)
                .messageId(messageId)
                .build();
        return markAsReadInternal(request, userId);
    }

    /**
     * ✅ 메시지 읽음 처리 및 미읽은 인원 수 반환
     */
    private int markAsReadInternal(ChatReadRequest request, Long userId) {
        ChatMessage message = getMessage(request.getMessageId());

        if (!message.getSender().getId().equals(userId)) {
            User user = getUser(userId);
            boolean alreadyRead = chatReadRepository.existsByMessageAndUser(message, user);
            if (!alreadyRead) {
                ChatRead read = ChatRead.create(message, user);
                chatReadRepository.save(read);
            }
        }

        return calculateUnreadCount(message);
    }

    /**
     * ✅ 메시지의 읽지 않은 인원 수 계산
     */
    @Transactional(readOnly = true)
    public int getUnreadCountForMessage(Long messageId) {
        ChatMessage message = getMessage(messageId);
        return calculateUnreadCount(message);
    }

    // ===== 내부 유틸 =====

    private int calculateUnreadCount(ChatMessage message) {
        Long roomId = message.getChatRoom().getId();
        int totalParticipants = chatParticipantRepository.countByChatRoomId(roomId);
        long readCount = chatReadRepository.countByMessage(message);
        return totalParticipants - (int) readCount;
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
