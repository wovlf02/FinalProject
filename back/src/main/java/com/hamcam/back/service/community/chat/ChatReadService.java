package com.hamcam.back.service.community.chat;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.chat.ChatMessage;
import com.hamcam.back.entity.chat.ChatParticipant;
import com.hamcam.back.entity.chat.ChatRead;
import com.hamcam.back.global.exception.CustomException;
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

    /**
     * 사용자가 특정 메시지를 읽었음을 기록하고,
     * 해당 메시지를 아직 읽지 않은 사용자 수를 반환합니다.
     *
     * @param reader    읽은 사용자
     * @param roomId    채팅방 ID
     * @param messageId 읽은 메시지 ID
     * @return 읽지 않은 사람 수 (보낸 사람 제외)
     */
    @Transactional
    public int markAsRead(User reader, Long roomId, Long messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        // 보낸 사람 본인이면 읽음 처리할 필요 없음
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
     * 특정 메시지를 아직 읽지 않은 사용자 수를 반환합니다.
     *
     * @param messageId 메시지 ID
     * @return 읽지 않은 사용자 수
     */
    @Transactional(readOnly = true)
    public int getUnreadCountForMessage(Long messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        Long roomId = message.getChatRoom().getId();
        int totalParticipants = chatParticipantRepository.countByChatRoomId(roomId);
        long readCount = chatReadRepository.countByMessage(message);

        return (int) (totalParticipants - readCount - 1); // 보낸 사람 제외
    }

    @Transactional
    public void updateLastReadMessage(Long roomId, Long userId) {
        ChatParticipant participant = chatParticipantRepository.findByChatRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new CustomException("채팅방 참여 정보를 찾을 수 없습니다."));

        ChatMessage lastMessage = chatMessageRepository.findTopByChatRoomOrderBySentAtDesc(participant.getChatRoom());
        if (lastMessage != null) {
            participant.setLastReadMessageId(lastMessage.getId());
            chatParticipantRepository.save(participant);
        }
    }

}
