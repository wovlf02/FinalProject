package com.hamcam.back.service.chat;

import com.hamcam.back.entity.chat.ChatMessage;
import com.hamcam.back.entity.chat.ChatRoom;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.dto.chat.request.ChatMessageRequest;
import com.hamcam.back.dto.chat.response.ChatMessageResponse;
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
     * 텍스트 메시지를 저장하고 응답으로 반환합니다.
     *
     * @param request 클라이언트에서 수신한 메시지
     * @return 저장된 메시지 응답
     */
    public ChatMessageResponse saveTextMessage(ChatMessageRequest request) {
        ChatRoom room = chatRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        ChatMessage message = ChatMessage.builder()
                .chatRoom(room)
                .sender(User.builder().id(request.getSenderId()).build())
                .type(request.getType())
                .content(request.getContent())
                .sentAt(LocalDateTime.now())
                .build();

        chatMessageRepository.save(message);

        return ChatMessageResponse.builder()
                .messageId(message.getId())
                .roomId(room.getId())
                .senderId(message.getSender().getId())
                .content(message.getContent())
                .type(message.getType())
                .sentAt(message.getSentAt())
                .build();
    }
}
