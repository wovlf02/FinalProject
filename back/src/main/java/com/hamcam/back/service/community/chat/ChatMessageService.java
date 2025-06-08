package com.hamcam.back.service.community.chat;

import com.hamcam.back.dto.community.chat.request.ChatMessageRequest;
import com.hamcam.back.dto.community.chat.response.ChatMessageResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.chat.ChatMessage;
import com.hamcam.back.entity.chat.ChatMessageType;
import com.hamcam.back.entity.chat.ChatRoom;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.chat.ChatMessageRepository;
import com.hamcam.back.repository.chat.ChatRoomRepository;
import com.hamcam.back.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ChatReadService chatReadService;

    /**
     * ✅ 채팅 메시지 저장 (WebSocket & REST 공통)
     */
    public ChatMessageResponse sendMessage(Long roomId, Long senderId, ChatMessageRequest request) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ChatMessage message = createMessageEntity(room, sender, request);
        chatMessageRepository.save(message);

        // 마지막 메시지 갱신 (READ_ACK 제외)
        if (message.getType() != ChatMessageType.READ_ACK) {
            room.setLastMessage(generatePreview(message));
            room.setLastMessageAt(message.getSentAt());
            chatRoomRepository.save(room);
        }

        // ✅ WebSocket에서도 미읽음 인원 수 포함 응답
        int unreadCount = chatReadService.getUnreadCountForMessage(message.getId());

        return toResponse(message, unreadCount);
    }

    /**
     * ✅ 채팅방 전체 메시지 조회 (세션 기반)
     */
    public List<ChatMessageResponse> getAllMessages(Long roomId, HttpServletRequest request) {
        Long userId = SessionUtil.getUserId(request);

        // 채팅방 및 유저 유효성 검증
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 최신순 정렬된 메시지 최대 100건 조회
        PageRequest pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.ASC, "sentAt"));
        List<ChatMessage> messages = chatMessageRepository.findByChatRoom(room, pageable);

        // 각 메시지에 대해 unreadCount 포함 응답 생성
        return messages.stream()
                .map(msg -> {
                    int unreadCount = chatReadService.getUnreadCountForMessage(msg.getId());
                    return ChatMessageResponse.builder()
                            .messageId(msg.getId())
                            .roomId(roomId)
                            .senderId(msg.getSender().getId())
                            .nickname(msg.getSender().getNickname())
                            .profileUrl(msg.getSender().getProfileImageUrl())
                            .content(msg.getContent())
                            .type(msg.getType())
                            .sentAt(msg.getSentAt())
                            .storedFileName(msg.getStoredFileName())
                            .unreadCount(unreadCount) // ✅ 핵심 필드
                            .build();
                })
                .collect(Collectors.toList());
    }


    // ===== 내부 유틸 =====

    private ChatMessage createMessageEntity(ChatRoom room, User sender, ChatMessageRequest request) {
        if (request.getType() == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        return ChatMessage.builder()
                .chatRoom(room)
                .sender(sender)
                .content(request.getContent())
                .type(request.getType())
                .storedFileName(request.getStoredFileName())
                .sentAt(LocalDateTime.now())
                .build();
    }

    private ChatMessageResponse toResponse(ChatMessage message, int unreadCount) {
        User sender = message.getSender();
        return ChatMessageResponse.builder()
                .messageId(message.getId())
                .roomId(message.getChatRoom().getId())
                .senderId(sender != null ? sender.getId() : null)
                .nickname(sender != null ? sender.getNickname() : null)
                .profileUrl(sender != null && sender.getProfileImageUrl() != null
                        ? sender.getProfileImageUrl()
                        : "")
                .content(generatePreview(message))
                .type(message.getType())
                .storedFileName(message.getStoredFileName())
                .sentAt(message.getSentAt())
                .unreadCount(unreadCount)
                .build();
    }

    /**
     * ✅ 메시지 유형에 따른 표시 문자열 생성
     */
    private String generatePreview(ChatMessage message) {
        return switch (message.getType()) {
            case FILE, IMAGE -> "[파일]";
            case TEXT -> message.getContent();
            case ENTER -> message.getContent();
            case READ_ACK -> "";
        };
    }
}
