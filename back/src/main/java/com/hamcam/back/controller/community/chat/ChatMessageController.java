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
 *
 * 채팅 메시지 REST API 컨트롤러
 * WebSocket 외 REST 방식으로도 채팅 메시지를 처리할 수 있도록 지원
 */
@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    /**
     * [채팅 메시지 목록 조회]
     *
     * - 채팅방 ID와 페이지 정보를 기반으로 채팅 메시지를 조회
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
     * [REST 메시지 전송]
     *
     * - 텍스트 또는 파일 메시지를 REST 방식으로 전송
     * - 인증된 사용자 정보는 서비스 내에서 추출
     */
    @PostMapping("/{roomId}/messages")
    public ResponseEntity<ChatMessageResponse> sendRestMessage(
            @PathVariable Long roomId,
            @RequestBody @Valid ChatMessageRequest request
    ) {
        return ResponseEntity.ok(chatMessageService.sendMessage(roomId, request));
    }
}
