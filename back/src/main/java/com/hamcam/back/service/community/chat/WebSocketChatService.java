package com.hamcam.back.service.community.chat;

import com.hamcam.back.dto.community.chat.request.ChatMessageRequest;
import com.hamcam.back.dto.community.chat.response.ChatMessageResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.chat.ChatMessage;
import com.hamcam.back.entity.chat.ChatMessageType;
import com.hamcam.back.entity.chat.ChatRoom;
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
    private final SecurityUtil securityUtil; // ✅ 인증된 사용자 조회용

    /**
     * 텍스트/파일 메시지를 저장하고 응답으로 변환합니다.
     *
     * @param request 클라이언트 요청 메시지
     * @return 저장된 메시지 응답 DTO
     */
    public ChatMessageResponse saveTextMessage(ChatMessageRequest request) {
        ChatRoom room = chatRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방을 찾을 수 없습니다."));

        // ✅ 인증된 사용자 사용
        User sender = securityUtil.getCurrentUser();

        // 문자열 type → enum 변환 (유효성 포함)
        ChatMessageType messageType;
        try {
            messageType = ChatMessageType.valueOf(request.getType().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("유효하지 않은 메시지 타입입니다: " + request.getType());
        }

        ChatMessage message = ChatMessage.builder()
                .chatRoom(room)
                .sender(sender)
                .type(messageType)
                .content(request.getContent())
                .storedFileName(request.getStoredFileName())
                .sentAt(LocalDateTime.now())
                .build();

        chatMessageRepository.save(message);
        return toResponse(message);
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
