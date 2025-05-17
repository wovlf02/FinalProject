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
 * 채팅방 관리 컨트롤러
 * - 채팅방 생성, 입장, 퇴장, 조회, 삭제 기능 제공
 */
@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 채팅방 생성
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ChatRoomResponse> createChatRoom(
            @RequestPart("roomName") String roomName,
            @RequestPart("invitedUserIds") String invitedUserIdsJson,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        List<Long> invitedUserIds;
        try {
            invitedUserIds = objectMapper.readValue(
                    invitedUserIdsJson, new TypeReference<List<Long>>() {});
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        ChatRoomCreateRequest request = ChatRoomCreateRequest.builder()
                .roomName(roomName)
                .invitedUserIds(invitedUserIds)
                .image(image)
                .build();

        ChatRoomResponse response = chatRoomService.createChatRoom(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 로그인한 사용자의 채팅방 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<ChatRoomListResponse>> getMyChatRooms(@RequestParam Long userId) {
        List<ChatRoomListResponse> response = chatRoomService.getChatRoomsByUserId(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 채팅방 상세 정보 조회
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<ChatRoomResponse> getChatRoom(@PathVariable Long roomId) {
        ChatRoomResponse response = chatRoomService.getChatRoomById(roomId);
        return ResponseEntity.ok(response);
    }

    /**
     * 채팅방 삭제
     */
    @DeleteMapping("/{roomId}")
    public ResponseEntity<MessageResponse> deleteChatRoom(@PathVariable Long roomId) {
        chatRoomService.deleteChatRoom(roomId);
        return ResponseEntity.ok(new MessageResponse("채팅방이 삭제되었습니다."));
    }

}
