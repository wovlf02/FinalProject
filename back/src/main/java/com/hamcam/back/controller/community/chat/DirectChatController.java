package com.hamcam.back.controller.community.chat;

import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.dto.community.chat.request.DirectChatRequest;
import com.hamcam.back.dto.community.chat.response.ChatRoomListResponse;
import com.hamcam.back.dto.community.chat.response.ChatRoomResponse;
import com.hamcam.back.service.community.chat.DirectChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * [DirectChatController]
 * 1:1 채팅 관련 REST 컨트롤러 (보안 제거 및 userId 명시적 전달 방식 확장)
 */
@RestController
@RequestMapping("/api/chat/direct")
@RequiredArgsConstructor
public class DirectChatController {

    private final DirectChatService directChatService;

    /**
     * 1:1 채팅 시작 또는 기존 채팅방 반환
     */
    @PostMapping("/start")
    public ResponseEntity<MessageResponse> startDirectChat(@RequestBody DirectChatRequest request) {
        ChatRoomResponse response = directChatService.startOrGetDirectChat(request);
        return ResponseEntity.ok(MessageResponse.of("1:1 채팅방 생성 또는 조회 완료", response));
    }

    /**
     * 내 1:1 채팅방 목록 조회 (userId 명시적 전달)
     */
    @GetMapping("/rooms")
    public ResponseEntity<MessageResponse> getMyDirectChatRooms(@RequestParam("userId") Long userId) {
        List<ChatRoomListResponse> rooms = directChatService.getMyDirectChatRooms(userId);
        return ResponseEntity.ok(MessageResponse.of("1:1 채팅방 목록 조회 성공", rooms));
    }

    /**
     * 특정 사용자와의 1:1 채팅방 조회
     * - 내 userId + 상대방 userId 모두 전달
     */
    @GetMapping("/with/{targetUserId}")
    public ResponseEntity<MessageResponse> getDirectChatWithUser(
            @RequestParam("userId") Long myUserId,
            @PathVariable Long targetUserId
    ) {
        ChatRoomResponse response = directChatService.getDirectChatWithUser(myUserId, targetUserId);
        return ResponseEntity.ok(MessageResponse.of("상대 사용자와의 채팅방 조회 성공", response));
    }
}
