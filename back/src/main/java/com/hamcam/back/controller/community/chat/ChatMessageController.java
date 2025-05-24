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
 * - 채팅방 입장 시 메시지 전체 조회
 */
@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    /**
     * ✅ 채팅방 메시지 전체 조회 (초기 로딩용, 세션 기반)
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
