package com.hamcam.back.controller.community.chat;

import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.dto.community.chat.request.*;
import com.hamcam.back.dto.community.chat.response.*;
import com.hamcam.back.service.community.chat.DirectChatService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * [DirectChatController]
 * 1:1 채팅 관련 REST API (세션 기반)
 */
@RestController
@RequestMapping("/api/chat/direct")
@RequiredArgsConstructor
public class DirectChatController {

    private final DirectChatService directChatService;

    /**
     * ✅ 1:1 채팅 시작 또는 기존 채팅방 반환
     */
    @PostMapping("/start")
    public ResponseEntity<MessageResponse> startDirectChat(
            @RequestBody DirectChatRequest request,
            HttpServletRequest httpRequest
    ) {
        ChatRoomResponse chatRoom = directChatService.startOrGetDirectChat(request, httpRequest);
        return ResponseEntity.ok(MessageResponse.of("1:1 채팅방 생성 또는 조회 완료", chatRoom));
    }

    /**
     * ✅ 내 1:1 채팅방 목록 조회
     */
    @PostMapping("/rooms")
    public ResponseEntity<MessageResponse> getMyDirectChatRooms(HttpServletRequest httpRequest) {
        List<ChatRoomListResponse> roomList = directChatService.getMyDirectChatRooms(httpRequest);
        return ResponseEntity.ok(MessageResponse.of("1:1 채팅방 목록 조회 성공", roomList));
    }

    /**
     * ✅ 특정 사용자와의 1:1 채팅방 조회
     */
    @PostMapping("/with")
    public ResponseEntity<MessageResponse> getDirectChatWithUser(
            @RequestBody DirectChatLookupRequest request,
            HttpServletRequest httpRequest
    ) {
        ChatRoomResponse chatRoom = directChatService.getDirectChatWithUser(
                request.getTargetUserId(), httpRequest
        );
        return ResponseEntity.ok(MessageResponse.of("상대 사용자와의 채팅방 조회 성공", chatRoom));
    }
}
