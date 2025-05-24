package com.hamcam.back.controller.community.chat;

import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.dto.community.chat.request.*;
import com.hamcam.back.dto.community.chat.response.*;
import com.hamcam.back.service.community.chat.ChatRoomService;
import com.hamcam.back.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
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
     * ✅ 채팅방 생성
     * - Multipart 요청 예외 적용
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> createChatRoom(
            @RequestPart("request") ChatRoomCreateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            HttpServletRequest httpRequest
    ) {
        request.setImage(image);
        ChatRoomResponse createdRoom = chatRoomService.createChatRoom(request, httpRequest);
        return ResponseEntity.ok(MessageResponse.of("채팅방이 생성되었습니다.", createdRoom));
    }

    /**
     * ✅ 내 채팅방 목록 조회
     */
    @PostMapping("/my")
    public ResponseEntity<MessageResponse> getMyChatRooms(HttpServletRequest httpRequest) {
        List<ChatRoomListResponse> rooms = chatRoomService.getMyChatRooms(httpRequest);
        return ResponseEntity.ok(MessageResponse.of("채팅방 목록 조회 성공", rooms));
    }

    /**
     * ✅ 채팅방 상세 조회
     */
    @PostMapping("/detail")
    public ResponseEntity<MessageResponse> getChatRoom(@RequestBody ChatRoomDetailRequest request) {
        ChatRoomResponse room = chatRoomService.getChatRoomById(request);
        return ResponseEntity.ok(MessageResponse.of("채팅방 상세 조회 성공", room));
    }

    /**
     * ✅ 채팅방 삭제
     */
    @DeleteMapping
    public ResponseEntity<MessageResponse> deleteChatRoom(
            @RequestBody ChatRoomDeleteRequest request,
            HttpServletRequest httpRequest
    ) {
        chatRoomService.deleteChatRoom(request, httpRequest);
        return ResponseEntity.ok(MessageResponse.of("채팅방이 삭제되었습니다."));
    }
}
