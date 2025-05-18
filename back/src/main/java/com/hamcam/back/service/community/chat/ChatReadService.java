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
     * 사용자가 특정 메시지를 읽었음을 기록하고,
     * 해당 메시지를 아직 읽지 않은 사용자 수를 반환합니다.
     */
    @Transactional
    public int markAsRead(User reader, Long roomId, Long messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (!message.getSender().getId().equals(reader.getId())) {
            boolean alreadyRead = chatReadRepository.existsByMessageAndUser(message, reader);
            if (!alreadyRead) {
                chatReadRepository.save(ChatRead.create(message, reader));
            }
        }

        int totalParticipants = chatParticipantRepository.countByChatRoomId(roomId);
        long readCount = chatReadRepository.countByMessage(message);

        return (int) (totalParticipants - readCount - 1); // 보낸 사람 제외
    }

    /**
     * 메시지를 아직 읽지 않은 사용자 수를 반환합니다.
     */
    @Transactional(readOnly = true)
    public int getUnreadCountForMessage(Long messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        Long roomId = message.getChatRoom().getId();
        int totalParticipants = chatParticipantRepository.countByChatRoomId(roomId);
        long readCount = chatReadRepository.countByMessage(message);

        return (int) (totalParticipants - readCount - 1); // 보낸 사람 제외
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
     * SecurityUtil 기반 유저로 바로 읽음 처리 (WebSocket 등에서 활용 가능)
     */
    @Transactional
    public int markAsReadByAuth(Long roomId, Long messageId) {
        User reader = securityUtil.getCurrentUser();
        return markAsRead(reader, roomId, messageId);
    }

    @Transactional
    public void updateLastReadMessageByAuth(Long roomId) {
        Long userId = securityUtil.getCurrentUserId();
        updateLastReadMessage(roomId, userId);
    }
}
