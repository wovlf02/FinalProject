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
import com.hamcam.back.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
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
     * ✅ 읽음 처리 및 읽지 않은 사용자 수 계산 (세션 기반)
     */
    @Transactional
    public int markAsRead(ChatReadRequest request, HttpServletRequest httpRequest) {
        Long userId = SessionUtil.getUserId(httpRequest);
        ChatMessage message = getMessage(request.getMessageId());

        // 본인이 보낸 메시지는 읽음 처리 제외
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
     * ✅ 메시지 ID로 읽지 않은 사용자 수 반환
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

    /**
     * ✅ WebSocket에서 읽음 처리 (userId 직접 전달 필요 시만 사용)
     */
    @Transactional
    public int markReadAsUserId(Long roomId, Long messageId, Long userId) {
        ChatReadRequest request = ChatReadRequest.builder()
                .roomId(roomId)
                .messageId(messageId)
                .build();
        return markAsRead(request, userId);
    }

    // 오버로드 for WebSocket 내부 호출
    private int markAsRead(ChatReadRequest request, Long userId) {
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
}
