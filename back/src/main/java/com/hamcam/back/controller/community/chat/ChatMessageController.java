package com.hamcam.back.controller.community.chat;

import com.hamcam.back.dto.community.chat.response.ChatMessageResponse;
import com.hamcam.back.service.community.chat.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * [ChatMessageController]
 * 채팅 메시지 REST API 컨트롤러
 * - 채팅방 입장 시 메시지 전체 조회 (WebSocket은 실시간 처리)
 */
@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    /**
     * ✅ 채팅방 메시지 전체 조회
     * - 채팅방 입장 시 한번에 불러오는 초기 로딩 용도
     */
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getAllMessages(@PathVariable Long roomId) {
        List<ChatMessageResponse> messages = chatMessageService.getAllMessages(roomId);
        return ResponseEntity.ok(messages);
    }
}
