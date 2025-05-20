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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * [ChatMessageService]
 * 채팅 메시지 처리 서비스
 * - WebSocket 및 REST 기반 메시지 저장 및 조회 처리
 */
@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatReadService chatReadService;
    private final SecurityUtil securityUtil;

    /**
     * 채팅 메시지 저장 (REST & WebSocket 공통)
     */
    // 오버로드된 메서드 추가
    public ChatMessageResponse sendMessage(Long roomId, User user, ChatMessageRequest request) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        ChatMessage message = createMessageEntity(room, user, request);
        chatMessageRepository.save(message);

        // 채팅방 마지막 메시지 갱신
        room.setLastMessage(request.getContent());
        room.setLastMessageAt(message.getSentAt());
        chatRoomRepository.save(room);

        return toResponse(message);
    }


    /**
     * 채팅 메시지 조회 (오래된 순)
     */
    public List<ChatMessageResponse> getMessages(Long roomId, int page, int size) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "sentAt"));
        List<ChatMessage> messages = chatMessageRepository.findByChatRoom(room, pageable);

        return messages.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ===== 내부 유틸 =====

    private ChatMessage createMessageEntity(ChatRoom room, User sender, ChatMessageRequest request) {
        ChatMessageType type;
        try {
            type = request.getType();
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        return ChatMessage.builder()
                .chatRoom(room)
                .sender(sender)
                .content(request.getContent())
                .type(type)
                .storedFileName(request.getStoredFileName())
                .sentAt(LocalDateTime.now())
                .build();
    }

    private ChatMessageResponse toResponse(ChatMessage message) {
        User sender = message.getSender();

        return ChatMessageResponse.builder()
                .messageId(message.getId())
                .roomId(message.getChatRoom().getId())
                .senderId(sender.getId())
                .nickname(sender.getNickname())
                .profileUrl(sender.getProfileImageUrl() != null ? sender.getProfileImageUrl() : "")
                .content(message.getContent())
                .type(message.getType().name())
                .storedFileName(message.getStoredFileName())
                .sentAt(message.getSentAt())
                .unreadCount(chatReadService.getUnreadCountForMessage(message.getId()))
                .build();
    }
}
