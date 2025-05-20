package com.hamcam.back.service.community.chat;

import com.hamcam.back.dto.community.chat.request.ChatMessageRequest;
import com.hamcam.back.dto.community.chat.response.ChatMessageResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.chat.ChatMessage;
import com.hamcam.back.entity.chat.ChatMessageType;
import com.hamcam.back.entity.chat.ChatRoom;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.global.security.SecurityUtil;
import com.hamcam.back.repository.chat.ChatMessageRepository;
import com.hamcam.back.repository.chat.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WebSocketChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final SecurityUtil securityUtil;

    /**
     * WebSocket을 통해 수신된 채팅 메시지를 저장하고 응답으로 반환합니다.
     *
     * @param request 클라이언트 요청 DTO
     * @return 저장된 메시지 DTO
     */
    public ChatMessageResponse saveMessage(ChatMessageRequest request) {
        ChatRoom room = chatRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        User sender = securityUtil.getCurrentUser();
        ChatMessageType messageType = parseMessageType(request.getType());

        ChatMessage message = ChatMessage.builder()
                .chatRoom(room)
                .sender(sender)
                .type(messageType)
                .content(request.getContent())
                .storedFileName(request.getStoredFileName())
                .sentAt(LocalDateTime.now())
                .build();

        chatMessageRepository.save(message);

        // 🔁 필요 시 채팅방의 마지막 메시지 갱신 로직 추가
        room.setLastMessage(message.getContent());
        room.setLastMessageAt(message.getSentAt());
        chatRoomRepository.save(room);

        return toResponse(message);
    }

    /**
     * 메시지 타입 문자열을 안전하게 enum으로 변환
     */
    private ChatMessageType parseMessageType(String type) {
        try {
            return ChatMessageType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new CustomException(ErrorCode.INVALID_INPUT, "유효하지 않은 메시지 타입입니다: " + type);
        }
    }

    /**
     * ChatMessage → ChatMessageResponse 변환
     */
    private ChatMessageResponse toResponse(ChatMessage message) {
        User sender = message.getSender();

        return ChatMessageResponse.builder()
                .messageId(message.getId())
                .roomId(message.getChatRoom().getId())
                .senderId(sender.getId())
                .nickname(sender.getNickname() != null ? sender.getNickname() : "")
                .profileUrl(sender.getProfileImageUrl() != null ? sender.getProfileImageUrl() : "")
                .type(message.getType().name())
                .content(message.getContent())
                .storedFileName(message.getStoredFileName())
                .sentAt(message.getSentAt())
                .build();
    }
}
