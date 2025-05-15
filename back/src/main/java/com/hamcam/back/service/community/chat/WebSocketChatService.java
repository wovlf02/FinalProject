package com.hamcam.back.service.community.chat;

import com.hamcam.back.dto.community.chat.request.ChatMessageRequest;
import com.hamcam.back.dto.community.chat.response.ChatMessageResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.chat.ChatMessage;
import com.hamcam.back.entity.chat.ChatRoom;
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

    /**
     * 텍스트 메시지를 저장하고 응답으로 변환합니다.
     *
     * @param request 클라이언트 요청 메시지
     * @return 저장된 메시지 응답 DTO
     */
    public ChatMessageResponse saveTextMessage(ChatMessageRequest request) {
        ChatRoom room = chatRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방을 찾을 수 없습니다."));

        // 실제 인증 사용자가 있다면 아래 UserService 등에서 가져올 수 있음
        User sender = User.builder().id(request.getSenderId()).build();

        ChatMessage message = ChatMessage.builder()
                .chatRoom(room)
                .sender(sender)
                .type(request.getType())
                .content(request.getContent())
                .sentAt(LocalDateTime.now())
                .build();

        chatMessageRepository.save(message);
        return toResponse(message);
    }

    /**
     * ChatMessage → ChatMessageResponse 변환
     */
    private ChatMessageResponse toResponse(ChatMessage message) {
        return ChatMessageResponse.builder()
                .messageId(message.getId())
                .roomId(message.getChatRoom().getId())
                .senderId(message.getSender().getId())
                .nickname(message.getSender().getNickname()) // Optional: sender에 nickname 포함된 경우
                .profileUrl(message.getSender().getProfileImageUrl()) // Optional
                .type(message.getType())
                .content(message.getContent())
                .storedFileName(message.getStoredFileName()) // Optional: FILE 메시지 대응
                .sentAt(message.getSentAt())
                .build();
    }
}
