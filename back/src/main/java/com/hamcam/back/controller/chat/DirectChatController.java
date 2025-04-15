package com.hamcam.back.controller.chat;

import com.hamcam.back.dto.chat.request.DirectChatRequest;
import com.hamcam.back.dto.chat.response.ChatRoomListResponse;
import com.hamcam.back.dto.chat.response.ChatRoomResponse;
import com.hamcam.back.service.chat.DirectChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat/direct")
@RequiredArgsConstructor
public class DirectChatController {

    private final DirectChatService directChatService;

    /**
     * 1:1 채팅 시작 또는 기존 채팅방 반환
     *
     * @param request 상대 사용자 정보
     * @return 생성된 또는 기존 채팅방 정보
     */
    @PostMapping("/start")
    public ResponseEntity<ChatRoomResponse> startDirectChat(@RequestBody DirectChatRequest request) {
        ChatRoomResponse response = directChatService.startOrGetDirectChat(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 내가 속한 모든 1:1 채팅방 목록 조회
     *
     * @return 1:1 채팅방 리스트
     */
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomListResponse>> getMyDirectChatRooms() {
        List<ChatRoomListResponse> response = directChatService.getMyDirectChatRooms();
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 사용자와의 1:1 채팅방 정보 조회
     *
     * @param userId 상대 사용자 ID
     * @return 채팅방 정보 (없으면 404)
     */
    @GetMapping("/with/{userId}")
    public ResponseEntity<ChatRoomResponse> getChatRoomWithUser(@PathVariable Long userId) {
        ChatRoomResponse response = directChatService.getDirectChatWithUser(userId);
        return ResponseEntity.ok(response);
    }
}
