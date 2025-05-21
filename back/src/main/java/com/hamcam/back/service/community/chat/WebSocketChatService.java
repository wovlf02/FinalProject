package com.hamcam.back.service.community.chat;

import com.hamcam.back.dto.community.chat.request.ChatMessageRequest;
import com.hamcam.back.dto.community.chat.response.ChatMessageResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.chat.ChatMessage;
import com.hamcam.back.entity.chat.ChatRoom;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.chat.ChatMessageRepository;
import com.hamcam.back.repository.chat.ChatRoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * [WebSocketChatService]
 *
 * WebSocket 기반 채팅 메시지 처리 서비스
 * - 메시지 저장
 * - 마지막 메시지 갱신
 * - 응답 DTO 생성
 */
@Service
@RequiredArgsConstructor
@Transactional
public class WebSocketChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatReadService chatReadService;

    /**
     * WebSocket을 통해 수신된 채팅 메시지를 저장하고 응답으로 반환
     *
     * @param request 메시지 내용 요청 DTO
     * @param userId  송신자 사용자 ID
     * @return 응답 DTO
     */
    public ChatMessageResponse saveMessage(ChatMessageRequest request, Long userId) {
        ChatRoom room = chatRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ChatMessage message = ChatMessage.builder()
                .chatRoom(room)
                .sender(sender)
                .type(request.getType())
                .content(request.getContent())
                .storedFileName(request.getStoredFileName())
                .sentAt(LocalDateTime.now())
                .build();

        chatMessageRepository.save(message);

        // 마지막 메시지 갱신
        String preview = switch (message.getType()) {
            case FILE, IMAGE -> "[파일]";
            case TEXT -> message.getContent();
        };
        room.setLastMessage(preview);
        room.setLastMessageAt(message.getSentAt());
        chatRoomRepository.save(room);

        int unreadCount = chatReadService.getUnreadCountForMessage(message.getId());

        return ChatMessageResponse.builder()
                .messageId(message.getId())
                .roomId(room.getId())
                .senderId(sender.getId())
                .nickname(sender.getNickname())
                .profileUrl(sender.getProfileImageUrl() != null ? sender.getProfileImageUrl() : "")
                .content(message.getContent())
                .type(message.getType().name())
                .storedFileName(message.getStoredFileName())
                .sentAt(message.getSentAt())
                .unreadCount(unreadCount)
                .build();
    }
}
