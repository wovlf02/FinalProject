package com.hamcam.back.controller.community.chat;

import com.hamcam.back.global.security.SecurityUtil;
import com.hamcam.back.service.community.chat.ChatReadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * [ChatReadController]
 * 채팅 메시지 읽음 관련 REST API
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat/rooms")
public class ChatReadController {

    private final ChatReadService chatReadService;
    private final SecurityUtil securityUtil;

    /**
     * ✅ 채팅방 입장 시 사용자의 마지막 읽은 메시지를 갱신
     * 채팅방 내 모든 메시지를 읽은 것으로 처리
     *
     * @param roomId 채팅방 ID
     * @return 200 OK
     */
    @PostMapping("/{roomId}/read/all")
    public ResponseEntity<Void> markAllMessagesAsRead(@PathVariable Long roomId) {
        Long userId = securityUtil.getCurrentUserId();
        log.info("📩 [읽음 처리 요청] roomId={}, userId={}", roomId, userId);

        chatReadService.updateLastReadMessage(roomId, userId);
        return ResponseEntity.ok().build();
    }
}
