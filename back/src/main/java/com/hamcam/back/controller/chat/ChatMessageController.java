package com.hamcam.back.controller.chat;

import com.hamcam.back.dto.chat.request.ChatMessageRequest;
import com.hamcam.back.dto.chat.response.ChatMessageResponse;
import com.hamcam.back.service.chat.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    /**
     * 채팅방 메시지 목록 조회 (무한 스크롤 페이징 기반)
     *
     * @param roomId 채팅방 ID
     * @param page   페이지 번호 (0부터 시작)
     * @param size   한 페이지 당 메시지 수
     * @return 최신순 메시지 리스트
     */
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getChatMessages(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size
    ) {
        List<ChatMessageResponse> messages = chatMessageService.getMessages(roomId, page, size);
        return ResponseEntity.ok(messages);
    }

    /**
     * (선택적) REST 방식으로 메시지 전송 - 일반적으로 WebSocket 사용 권장
     *
     * @param roomId 채팅방 ID
     * @param request 메시지 전송 요청 (텍스트, 파일 등)
     * @return 전송된 메시지 정보
     */
    @PostMapping("/{roomId}/message")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @PathVariable Long roomId,
            @RequestBody ChatMessageRequest request
    ) {
        ChatMessageResponse response = chatMessageService.sendMessage(roomId, request);
        return ResponseEntity.ok(response);
    }
}
