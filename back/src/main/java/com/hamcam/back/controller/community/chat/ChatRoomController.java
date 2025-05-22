package com.hamcam.back.controller.community.chat;

import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.dto.community.chat.request.*;
import com.hamcam.back.dto.community.chat.response.*;
import com.hamcam.back.service.community.chat.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    /**
     * ✅ 채팅방 생성 (1:1 또는 그룹)
     * - userId, roomName, invitedUserIds는 JSON
     * - image는 Multipart
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> createChatRoom(
            @RequestPart("request") ChatRoomCreateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        request.setImage(image); // DTO에 이미지 주입
        ChatRoomResponse createdRoom = chatRoomService.createChatRoom(request);
        return ResponseEntity.ok(MessageResponse.of("채팅방이 생성되었습니다.", createdRoom));
    }

    /**
     * ✅ 내 채팅방 목록 조회
     */
    @PostMapping("/my")
    public ResponseEntity<MessageResponse> getMyChatRooms(@RequestBody ChatRoomListRequest request) {
        List<ChatRoomListResponse> rooms = chatRoomService.getMyChatRooms(request.getUserId());
        return ResponseEntity.ok(MessageResponse.of("채팅방 목록 조회 성공", rooms));
    }

    /**
     * ✅ 채팅방 상세 조회
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<MessageResponse> getChatRoom(@PathVariable Long roomId) {
        ChatRoomResponse room = chatRoomService.getChatRoomById(roomId);
        return ResponseEntity.ok(MessageResponse.of("채팅방 조회 성공", room));
    }

    /**
     * ✅ 채팅방 삭제
     */
    @DeleteMapping("/{roomId}")
    public ResponseEntity<MessageResponse> deleteChatRoom(
            @PathVariable Long roomId,
            @RequestBody ChatRoomDeleteRequest request
    ) {
        chatRoomService.deleteChatRoom(roomId, request.getUserId());
        return ResponseEntity.ok(MessageResponse.of("채팅방이 삭제되었습니다."));
    }
}
