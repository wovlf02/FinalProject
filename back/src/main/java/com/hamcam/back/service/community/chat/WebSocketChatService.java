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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * [WebSocketChatService]
 * WebSocket 기반 실시간 채팅 메시지 저장 및 응답 서비스
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
     * ✅ 채팅 메시지 저장 및 응답 생성
     * - TEXT, IMAGE, FILE, ENTER 타입만 저장
     * - READ_ACK는 저장되지 않음
     *
     * @param request 클라이언트로부터 받은 메시지
     * @param userId  세션에서 추출한 전송자 ID
     * @return ChatMessageResponse
     */
    public ChatMessageResponse saveMessage(ChatMessageRequest request, Long userId) {
        if (request.getType() == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        if (request.getType() == ChatMessageType.READ_ACK) {
            return null; // 저장 안함
        }

        ChatRoom room = chatRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // ✅ 메시지 엔티티 생성 및 저장
        ChatMessage message = ChatMessage.builder()
                .chatRoom(room)
                .sender(sender)
                .type(request.getType())
                .content(request.getContent())
                .storedFileName(request.getStoredFileName())
                .sentAt(LocalDateTime.now())
                .build();

        chatMessageRepository.save(message);

        // ✅ 미리보기 메시지 및 최종 메시지 갱신
        if (request.getType().isPreviewType()) {
            room.setLastMessage(generatePreview(message));
            room.setLastMessageAt(message.getSentAt());
            chatRoomRepository.save(room);
        }

        // ✅ 미읽음 인원 수 계산
        int unreadCount = chatReadService.getUnreadCountForMessage(message.getId());

        return ChatMessageResponse.builder()
                .messageId(message.getId())
                .roomId(room.getId())
                .senderId(sender.getId())
                .nickname(sender.getNickname())
                .profileUrl(Optional.ofNullable(sender.getProfileImageUrl()).orElse(""))
                .content(message.getContent())
                .type(message.getType())
                .storedFileName(message.getStoredFileName())
                .sentAt(message.getSentAt())
                .unreadCount(unreadCount)
                .build();
    }

    /**
     * ✅ 미리보기 텍스트 생성
     *
     * @param message 메시지 객체
     * @return 미리보기 문자열
     */
    private String generatePreview(ChatMessage message) {
        if (message == null || message.getType() == null) return "";

        return switch (message.getType()) {
            case FILE, IMAGE -> "[파일]";
            case TEXT, ENTER -> message.getContent();
            default -> "";
        };
    }
}
