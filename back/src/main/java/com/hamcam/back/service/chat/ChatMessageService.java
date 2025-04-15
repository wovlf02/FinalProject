package com.hamcam.back.service.chat;

import com.hamcam.back.dto.chat.request.ChatMessageRequest;
import com.hamcam.back.dto.chat.response.ChatMessageResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.chat.ChatMessage;
import com.hamcam.back.entity.chat.ChatRoom;
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
 * 채팅 메시지 서비스
 * <p>
 * 채팅방 내 메시지 저장, 조회, 페이징 처리 등을 담당합니다.
 * WebSocket이 아닌 REST 방식 호출도 지원됩니다.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    /**
     * 채팅 메시지 전송
     *
     * @param roomId  채팅방 ID
     * @param request 메시지 전송 요청
     * @return 전송된 메시지 응답
     */
    public ChatMessageResponse sendMessage(Long roomId, ChatMessageRequest request) {
        // 채팅방 존재 여부 확인
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        // 메시지 저장
        ChatMessage message = ChatMessage.builder()
                .chatRoom(room)
                .sender(User.builder().id(request.getSenderId()).build())  // 실제 구현에서는 인증 정보로 교체
                .content(request.getContent())
                .type(request.getType())
                .storedFileName(request.getStoredFileName())  // 파일 메시지의 경우 포함
                .sentAt(LocalDateTime.now())
                .build();

        chatMessageRepository.save(message);

        return toResponse(message);
    }

    /**
     * 채팅 메시지 페이징 조회 (최신순)
     *
     * @param roomId 채팅방 ID
     * @param page   페이지 번호 (0부터)
     * @param size   페이지 크기
     * @return 메시지 응답 리스트
     */
    public List<ChatMessageResponse> getMessages(Long roomId, int page, int size) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        // 최신순으로 조회
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sentAt"));
        List<ChatMessage> messages = chatMessageRepository.findByChatRoomOrderBySentAtDesc(room, pageable);

        return messages.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 메시지 → 응답 DTO 변환
     */
    private ChatMessageResponse toResponse(ChatMessage message) {
        return ChatMessageResponse.builder()
                .messageId(message.getId())
                .senderId(message.getSender().getId())
                .content(message.getContent())
                .type(message.getType())
                .sentAt(message.getSentAt())
                .storedFileName(message.getStoredFileName())
                .build();
    }
}
