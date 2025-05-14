package com.hamcam.back.service.community.chat;

import com.hamcam.back.dto.community.chat.request.ChatMessageRequest;
import com.hamcam.back.dto.community.chat.response.ChatMessageResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.chat.ChatMessage;
import com.hamcam.back.entity.chat.ChatRoom;
import com.hamcam.back.global.security.SecurityUtil;
import com.hamcam.back.repository.auth.UserRepository;
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
 * 채팅 메시지 처리 서비스
 * - WebSocket 및 REST 기반 메시지 저장 및 조회 처리
 */
@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SecurityUtil securityUtil;

    /**
     * 채팅 메시지 저장 (WebSocket / REST 공통)
     *
     * @param roomId  채팅방 ID
     * @param request 채팅 메시지 요청
     * @return 저장된 메시지 응답
     */
    public ChatMessageResponse sendMessage(Long roomId, ChatMessageRequest request) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        // ✅ SecurityContext에서 인증된 사용자 조회
        User sender = securityUtil.getCurrentUser();

        ChatMessage message = createChatMessage(room, sender, request);
        chatMessageRepository.save(message);

        // ✅ 채팅방 마지막 메시지 갱신
        room.setLastMessage(request.getContent());
        room.setLastMessageAt(message.getSentAt());
        chatRoomRepository.save(room);

        return toResponse(message);
    }


    /**
     * 채팅 메시지 목록 조회 (오래된 순)
     *
     * @param roomId 채팅방 ID
     * @param page 페이지 번호
     * @param size 한 페이지당 메시지 수
     * @return 메시지 응답 리스트
     */
    public List<ChatMessageResponse> getMessages(Long roomId, int page, int size) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "sentAt"));
        List<ChatMessage> messages = chatMessageRepository.findByChatRoom(room, pageable);

        return messages.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 채팅 메시지 생성
     */
    private ChatMessage createChatMessage(ChatRoom room, User sender, ChatMessageRequest request) {
        return ChatMessage.builder()
                .chatRoom(room)
                .sender(sender)
                .content(request.getContent())
                .type(request.getType())
                .storedFileName(request.getStoredFileName())
                .sentAt(LocalDateTime.now())
                .build();
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
                .nickname(sender.getNickname())
                .profileUrl(sender.getProfileImageUrl())
                .content(message.getContent())
                .type(message.getType())
                .storedFileName(message.getStoredFileName())
                .sentAt(message.getSentAt())
                .build();
    }
}
