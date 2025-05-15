package com.hamcam.back.controller.community.chat;

import com.hamcam.back.dto.community.chat.request.DirectChatRequest;
import com.hamcam.back.dto.community.chat.response.ChatRoomListResponse;
import com.hamcam.back.dto.community.chat.response.ChatRoomResponse;
import com.hamcam.back.service.community.chat.DirectChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 1:1 채팅 관련 REST 컨트롤러
 * - 채팅방 생성, 조회, 목록 조회 등을 제공합니다.
 */
@RestController
@RequestMapping("/api/chat/direct")
@RequiredArgsConstructor
public class DirectChatController {

    private final DirectChatService directChatService;

    /**
     * 1:1 채팅 시작 또는 기존 채팅방 반환
     *
     * @param request 상대 사용자 ID 포함
     * @return 채팅방 응답 정보
     */
    @PostMapping("/start")
    public ResponseEntity<ChatRoomResponse> startDirectChat(@RequestBody DirectChatRequest request) {
        ChatRoomResponse response = directChatService.startOrGetDirectChat(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 로그인 사용자의 모든 1:1 채팅방 목록 조회
     *
     * @return 채팅방 리스트 응답
     */
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomListResponse>> getMyDirectChatRooms() {
        List<ChatRoomListResponse> response = directChatService.getMyDirectChatRooms();
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 사용자와의 1:1 채팅방 조회
     *
     * @param userId 상대 사용자 ID
     * @return 채팅방 응답 정보 (없으면 404 발생 가능)
     */
    @GetMapping("/with/{userId}")
    public ResponseEntity<ChatRoomResponse> getDirectChatWithUser(@PathVariable Long userId) {
        ChatRoomResponse response = directChatService.getDirectChatWithUser(userId);
        return ResponseEntity.ok(response);
    }
}
