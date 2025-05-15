package com.hamcam.back.controller.community.chat;

import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.dto.community.chat.request.ChatRoomCreateRequest;
import com.hamcam.back.dto.community.chat.request.ChatJoinRequest;
import com.hamcam.back.dto.community.chat.response.ChatRoomListResponse;
import com.hamcam.back.dto.community.chat.response.ChatRoomResponse;
import com.hamcam.back.service.community.chat.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 채팅방 관리 컨트롤러
 * - 채팅방 생성, 입장, 퇴장, 조회, 삭제 기능 제공
 */
@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    /**
     * 채팅방 생성
     *
     * @param request 채팅방 생성 요청
     * @return 생성된 채팅방 정보
     */
    @PostMapping
    public ResponseEntity<ChatRoomResponse> createChatRoom(@RequestBody ChatRoomCreateRequest request) {
        ChatRoomResponse response = chatRoomService.createChatRoom(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 로그인한 사용자의 채팅방 목록 조회
     *
     * @param userId 사용자 ID (✅ 추후 JWT 기반 인증으로 대체 예정)
     * @return 참여 중인 채팅방 목록
     */
    @GetMapping
    public ResponseEntity<List<ChatRoomListResponse>> getMyChatRooms(@RequestParam Long userId) {
        List<ChatRoomListResponse> response = chatRoomService.getChatRoomsByUserId(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 채팅방 상세 정보 조회
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
     * 채팅방 삭제 (빈 방 또는 관리자가 삭제할 때 사용)
     *
     * @param roomId 삭제할 채팅방 ID
     * @return 삭제 완료 메시지
     */
    @DeleteMapping("/{roomId}")
    public ResponseEntity<MessageResponse> deleteChatRoom(@PathVariable Long roomId) {
        chatRoomService.deleteChatRoom(roomId);
        return ResponseEntity.ok(new MessageResponse("채팅방이 삭제되었습니다."));
    }

    /**
     * 채팅방 입장
     *
     * @param roomId 입장할 채팅방 ID
     * @param request 입장 요청 (사용자 ID 포함)
     * @return 입장 완료 메시지
     */
    @PostMapping("/{roomId}/join")
    public ResponseEntity<MessageResponse> joinChatRoom(
            @PathVariable Long roomId,
            @RequestBody ChatJoinRequest request
    ) {
        chatRoomService.joinChatRoom(roomId, request);
        return ResponseEntity.ok(new MessageResponse("채팅방에 입장했습니다."));
    }

    /**
     * 채팅방 퇴장
     *
     * @param roomId 퇴장할 채팅방 ID
     * @param request 퇴장 요청 (사용자 ID 포함)
     * @return 퇴장 완료 메시지
     */
    @DeleteMapping("/{roomId}/exit")
    public ResponseEntity<MessageResponse> exitChatRoom(
            @PathVariable Long roomId,
            @RequestBody ChatJoinRequest request
    ) {
        chatRoomService.exitChatRoom(roomId, request);
        return ResponseEntity.ok(new MessageResponse("채팅방에서 퇴장했습니다."));
    }
}
