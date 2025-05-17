package com.hamcam.back.controller.community.chat;

import com.hamcam.back.dto.community.chat.request.ChatMessageRequest;
import com.hamcam.back.dto.community.chat.response.ChatMessageResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.global.security.SecurityUtil;
import com.hamcam.back.service.community.chat.ChatMessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 채팅 메시지 컨트롤러
 * - REST 방식 메시지 처리 전용
 */
@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final SecurityUtil securityUtil;

    /**
     * 채팅방 메시지 목록 조회 (페이지네이션 기반)
     *
     * @param roomId 채팅방 ID
     * @param page 페이지 번호 (기본값: 0)
     * @param size 한 페이지당 메시지 수 (기본값: 30)
     * @return 채팅 메시지 목록
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
     * REST 방식 메시지 전송 (텍스트 또는 파일 메시지 전용)
     *
     * @param roomId 채팅방 ID
     * @param request 채팅 메시지 요청
     * @return 저장된 메시지 응답
     */
    @PostMapping("/{roomId}/messages")
    public ResponseEntity<ChatMessageResponse> sendRestMessage(
            @PathVariable Long roomId,
            @RequestBody @Valid ChatMessageRequest request
    ) {
        User sender = securityUtil.getCurrentUser(); // ✅ 인증 사용자 추출
        ChatMessageResponse response = chatMessageService.sendMessage(roomId, sender, request);
        return ResponseEntity.ok(response);
    }
}
