package com.hamcam.back.chat.controller;

import com.hamcam.back.chat.dto.request.ChatJoinRequest;
import com.hamcam.back.chat.dto.request.ChatMessageRequest;
import com.hamcam.back.chat.dto.request.ChatRoomCreateRequest;
import com.hamcam.back.chat.dto.response.ChatMessageResponse;
import com.hamcam.back.chat.dto.response.ChatRoomListResponse;
import com.hamcam.back.chat.dto.response.ChatRoomResponse;
import com.hamcam.back.chat.service.ChatMessageService;
import com.hamcam.back.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * [ChatController]
 *
 * 실시간 채팅 기능을 담당하는 REST 컨트롤러 및 WebSocket 엔드포인트
 * 채팅방 생성, 입장/퇴장, 메시지 전송 등을 관리하며,
 * 채팅 관련 엔티티는 CHAT_ROOMS, CHAT_MESSAGES 테이블과 직접 연결됨.
 *
 * 관련 테이블:
 * - CHAT_ROOMS: 채팅방 생성 및 관리
 * - CHAT_MESSAGES: 실시간 채팅 메시지 저장
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * [채팅방 생성 API]
     *
     * - HTTP Method: POST
     * - URI: /api/chat/rooms
     * - 요청 DTO: ChatRoomCreateRequest (roomType, referenceId 등 포함)
     * - 응답 DTO: ChatRoomResponse
     *
     * 기능 설명:
     * - 새로운 채팅방을 생성함
     * - CHAT_ROOMS 테이블에 새로운 row를 추가
     * - 생성된 room_id 및 생성 시각을 반환
     */
    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomResponse> createChatRoom(@RequestBody ChatRoomCreateRequest request) {
        ChatRoomResponse room = chatRoomService.createRoom(request);
        return ResponseEntity.ok(room);
    }

    /**
     * [전체 채팅방 목록 조회 API]
     *
     * - HTTP Method: GET
     * - URI: /api/chat/rooms
     * - 응답 DTO: List<ChatRoomListResponse>
     *
     * 기능 설명:
     * - 모든 채팅방 리스트를 조회
     * - CHAT_ROOMS 테이블에서 전체 row 조회 후 응답
     */
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomListResponse>> getAllChatRooms() {
        List<ChatRoomListResponse> rooms = chatRoomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }

    /**
     * [채팅방 상세 조회 API]
     *
     * - HTTP Method: GET
     * - URI: /api/chat/rooms/{roomId}
     * - 응답 DTO: ChatRoomResponse
     *
     * 기능 설명:
     * - 특정 채팅방의 상세 정보를 조회
     * - CHAT_ROOMS 테이블에서 room_id 기준으로 단일 row 조회
     */
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<ChatRoomResponse> getRoomDetail(@PathVariable Long roomId) {
        ChatRoomResponse response = chatRoomService.getRoomDetail(roomId);
        return ResponseEntity.ok(response);
    }

    /**
     * [채팅방 입장 API]
     *
     * - HTTP Method: POST
     * - URI: /api/chat/rooms/{roomId}/join
     * - 요청 DTO: ChatJoinRequest (userId, nickname 포함)
     *
     * 기능 설명:
     * - 사용자가 특정 채팅방에 입장
     * - 서버에서 입장 상태를 관리 (Redis 또는 세션, DB 캐시 등 활용 가능)
     */
    @PostMapping("/rooms/{roomId}/join")
    public ResponseEntity<String> joinRoom(@PathVariable Long roomId, @RequestBody ChatJoinRequest request) {
        chatRoomService.joinRoom(roomId, request);
        return ResponseEntity.ok("채팅방에 입장하였습니다.");
    }

    /**
     * [채팅방 퇴장 API]
     *
     * - HTTP Method: DELETE
     * - URI: /api/chat/rooms/{roomId}/exit
     * - 요청 DTO: ChatJoinRequest (userId 포함)
     *
     * 기능 설명:
     * - 사용자가 채팅방에서 퇴장
     * - 서버에서 세션 또는 참여자 목록에서 해당 사용자 제거
     * - 사용자가 퇴장하면 클라이언트에서 메시지 수신 중단
     */
    @DeleteMapping("/rooms/{roomId}/exit")
    public ResponseEntity<String> exitRoom(@PathVariable Long roomId, @RequestBody ChatJoinRequest request) {
        chatRoomService.exitRoom(roomId, request);
        return ResponseEntity.ok("채팅방에서 나갔습니다.");
    }

    /**
     * [실시간 채팅 메시지 수신 처리 (WebSocket)]
     *
     * - WebSocket 엔드포인트: /pub/chat/message
     * - 구독 경로: /sub/chat/room/{roomId}
     * - 요청 DTO: ChatMessageRequest
     * - 응답 DTO: ChatMessageResponse
     *
     * 기능 설명:
     * - WebSocket을 통해 수신된 메시지를 DB에 저장 (CHAT_MESSAGES 테이블)
     * - 같은 roomId를 구독 중인 사용자들에게 메시지를 브로드캐스트 전송
     * - 첨부파일이 포함된 경우 file, fileType도 함께 저장됨
     */
    @MessageMapping("/chat/message")
    public void sendMessage(ChatMessageRequest messageRequest) {
        ChatMessageResponse savedMessage = chatMessageService.saveMessage(messageRequest);

        messagingTemplate.convertAndSend(
                "/sub/chat/room/" + messageRequest.getRoomId(),
                savedMessage
        );
    }
}
