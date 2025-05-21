package com.hamcam.back.controller.community.chat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.dto.community.chat.request.ChatRoomCreateRequest;
import com.hamcam.back.dto.community.chat.response.ChatRoomListResponse;
import com.hamcam.back.dto.community.chat.response.ChatRoomResponse;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.service.community.chat.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * [ChatRoomController]
 * 채팅방 생성, 조회, 삭제 등을 처리하는 컨트롤러 (보안 제거 및 사용자 ID 명시 전달)
 */
@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ObjectMapper objectMapper;

    /**
     * 채팅방 생성 (1:1 또는 그룹)
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> createChatRoom(
            @RequestParam("userId") Long userId,
            @RequestPart("roomName") String roomName,
            @RequestPart("invitedUserIds") String invitedUserIdsJson,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        List<Long> invitedUserIds;
        try {
            invitedUserIds = objectMapper.readValue(invitedUserIdsJson, new TypeReference<>() {});
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        ChatRoomCreateRequest request = ChatRoomCreateRequest.builder()
                .roomName(roomName)
                .creatorId(userId) // ✅ 생성자 명시
                .invitedUserIds(invitedUserIds)
                .image(image)
                .build();

        ChatRoomResponse createdRoom = chatRoomService.createChatRoom(request);
        return ResponseEntity.ok(MessageResponse.of("채팅방이 생성되었습니다.", createdRoom));
    }

    /**
     * 내 채팅방 목록 조회
     */
    @GetMapping
    public ResponseEntity<MessageResponse> getMyChatRooms(@RequestParam("userId") Long userId) {
        List<ChatRoomListResponse> rooms = chatRoomService.getMyChatRooms(userId);
        return ResponseEntity.ok(MessageResponse.of("채팅방 목록 조회 성공", rooms));
    }

    /**
     * 채팅방 상세 조회
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<MessageResponse> getChatRoom(@PathVariable Long roomId) {
        ChatRoomResponse room = chatRoomService.getChatRoomById(roomId);
        return ResponseEntity.ok(MessageResponse.of("채팅방 조회 성공", room));
    }

    /**
     * 채팅방 삭제
     */
    @DeleteMapping("/{roomId}")
    public ResponseEntity<MessageResponse> deleteChatRoom(
            @PathVariable Long roomId,
            @RequestParam("userId") Long userId
    ) {
        chatRoomService.deleteChatRoom(roomId, userId); // ✅ 삭제 요청자 전달
        return ResponseEntity.ok(MessageResponse.of("채팅방이 삭제되었습니다."));
    }
}
