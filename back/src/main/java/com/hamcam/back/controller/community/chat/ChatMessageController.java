package com.hamcam.back.controller.community.chat;

import com.hamcam.back.dto.community.chat.request.ChatMessageRequest;
import com.hamcam.back.dto.community.chat.response.ChatMessageResponse;
import com.hamcam.back.service.community.chat.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * [ChatMessageController]
 * 채팅 메시지 REST API 컨트롤러 (보안 제거 버전)
 */
@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    /**
     * 채팅 메시지 목록 조회
     */
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getChatMessages(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size
    ) {
        return ResponseEntity.ok(chatMessageService.getMessages(roomId, page, size));
    }

    /**
     * REST 메시지 전송 (보안 제거 버전)
     * - 사용자 ID를 프론트에서 직접 전달받음
     */
    @PostMapping("/{roomId}/messages")
    public ResponseEntity<ChatMessageResponse> sendRestMessage(
            @PathVariable Long roomId,
            @RequestParam("userId") Long userId,
            @RequestBody @Valid ChatMessageRequest request
    ) {
        return ResponseEntity.ok(chatMessageService.sendMessage(roomId, userId, request));
    }
}
