package com.hamcam.back.controller.community.chat;

import com.hamcam.back.dto.community.chat.request.RoomAccessRequest;
import com.hamcam.back.dto.community.chat.response.ChatMessageResponse;
import com.hamcam.back.service.community.chat.ChatMessageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * [ChatMessageController]
 * 채팅 메시지 REST API 컨트롤러 (세션 기반)
 *
 * 사용자가 채팅방에 입장할 때 전체 메시지를 초기 로딩하며,
 * 각 메시지에는 senderId, nickname, profileUrl, sentAt, unreadCount가 포함되어야 한다.
 */
@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    /**
     * ✅ 채팅방 메시지 전체 조회 (초기 로딩)
     * - senderId, unreadCount 포함
     * - 프론트에서는 senderId == userId를 비교하여 isMe 판단
     */
    @PostMapping("/messages")
    public ResponseEntity<List<ChatMessageResponse>> getAllMessages(
            @RequestBody RoomAccessRequest request,
            HttpServletRequest httpRequest
    ) {
        List<ChatMessageResponse> messages =
                chatMessageService.getAllMessages(request.getRoomId(), httpRequest);
        return ResponseEntity.ok(messages);
    }
}
