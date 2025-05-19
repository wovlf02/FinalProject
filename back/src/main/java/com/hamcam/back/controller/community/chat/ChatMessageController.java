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
    private final SecurityUtil securityUtil;

    /**
     * [채팅 메시지 목록 조회]
     *
     * - 채팅방 ID와 페이지 정보를 기반으로 채팅 메시지를 조회
     * - 가장 최근 메시지부터 내림차순으로 조회
     *
     * @param roomId 채팅방 ID
     * @param page   페이지 번호 (0부터 시작)
     * @param size   한 페이지당 메시지 수
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
     * [REST 메시지 전송]
     *
     * - 텍스트 또는 파일 메시지를 REST 방식으로 전송 (WebSocket 미사용 시)
     * - 인증된 사용자 정보는 SecurityContext에서 추출
     *
     * @param roomId  채팅방 ID
     * @param request 메시지 요청 본문
     * @return 저장된 메시지 응답
     */
    @PostMapping("/{roomId}/messages")
    public ResponseEntity<ChatMessageResponse> sendRestMessage(
            @PathVariable Long roomId,
            @RequestBody @Valid ChatMessageRequest request
    ) {
        User sender = securityUtil.getCurrentUser(); // JWT 기반 인증 사용자 추출
        ChatMessageResponse response = chatMessageService.sendMessage(roomId, sender, request);
        return ResponseEntity.ok(response);
    }
}
