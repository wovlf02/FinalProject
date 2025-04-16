package com.hamcam.back.controller.chat;

import com.hamcam.back.dto.chat.request.ChatRoomCreateRequest;
import com.hamcam.back.dto.chat.request.ChatJoinRequest;
import com.hamcam.back.dto.chat.response.ChatRoomListResponse;
import com.hamcam.back.dto.chat.response.ChatRoomResponse;
import com.hamcam.back.service.chat.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.hamcam.back.dto.common.MessageResponse;

import java.util.List;

@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    /**
     * 채팅방 생성
     *
     * @param request 채팅방 생성 요청 정보
     * @return 생성된 채팅방 정보
     */
    @PostMapping
    public ResponseEntity<ChatRoomResponse> createChatRoom(@RequestBody ChatRoomCreateRequest request) {
        ChatRoomResponse response = chatRoomService.createChatRoom(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 전체 채팅방 목록 조회 (관리자 or 전체 공개 대상자)
     *
     * @return 채팅방 리스트
     */
    @GetMapping
    public ResponseEntity<List<ChatRoomListResponse>> getAllChatRooms() {
        List<ChatRoomListResponse> response = chatRoomService.getAllChatRooms();
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 채팅방 상세 조회
     *
     * @param roomId 채팅방 ID
     * @return 채팅방 상세 정보
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<ChatRoomResponse> getChatRoom(@PathVariable Long roomId) {
        ChatRoomResponse response = chatRoomService.getChatRoomById(roomId);
        return ResponseEntity.ok(response);
    }

    /**
     * 채팅방 삭제 (내부 로직 또는 빈 채팅방 자동 삭제용)
     *
     * @param roomId 채팅방 ID
     * @return 삭제 결과 메시지
     */
    @DeleteMapping("/{roomId}")
    public ResponseEntity<MessageResponse> deleteChatRoom(@PathVariable Long roomId) {
        chatRoomService.deleteChatRoom(roomId);
        return ResponseEntity.ok(new MessageResponse("채팅방이 삭제되었습니다."));
    }

    /**
     * 채팅방 입장
     *
     * @param roomId 채팅방 ID
     * @param request 입장 요청 정보 (사용자 ID 등)
     * @return 입장 결과 메시지
     */
    @PostMapping("/{roomId}/join")
    public ResponseEntity<MessageResponse> joinChatRoom(
            @PathVariable Long roomId,
            @RequestBody ChatJoinRequest request
    ) {
        chatRoomService.joinChatRoom(roomId, request);
        return ResponseEntity.ok(new MessageResponse("채팅방에 입장하였습니다."));
    }

    /**
     * 채팅방 퇴장
     *
     * @param roomId 채팅방 ID
     * @param request 퇴장 요청 정보
     * @return 퇴장 결과 메시지
     */
    @DeleteMapping("/{roomId}/exit")
    public ResponseEntity<MessageResponse> exitChatRoom(
            @PathVariable Long roomId,
            @RequestBody ChatJoinRequest request
    ) {
        chatRoomService.exitChatRoom(roomId, request);
        return ResponseEntity.ok(new MessageResponse("채팅방에서 나갔습니다."));
    }
}
