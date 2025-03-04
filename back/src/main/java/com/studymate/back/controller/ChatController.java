package com.studymate.back.controller;

import com.studymate.back.dto.*;
import com.studymate.back.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 채팅 컨트롤러
 *
 * 1:1 및 단체 채팅방 생성
 * 채팅방 초대, 강퇴, 관리자 설정
 * 채팅방 고정, 알림 설정, 비밀번호 보호
 * 메시지 전송 (텍스트, 이미지, 파일)
 * 메시지 삭제 및 신고
 * 채팅 암호화 적용
 * 입력 중 표시 (Typing Indicator)
 * 투표 생성
 * 음성 메시지 전송
 * 채팅 메시지 자동 번역
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // =============== 채팅방 관리 API ==================

    /**
     * 채팅방 생성 API
     * 1:1 채팅 또는 단체 채팅방을 생성할 수 있음
     * 단체 채팅방 생성 시 참가자 목록이 필요함
     * @param request   채팅방 생성 요청 데이터 (참가자 목록 포함)
     * @return  생성된 채팅방 ID
     */
    @PostMapping("/create")
    public ResponseEntity<ChatRoomResponse> createChatRoom(@RequestBody ChatRoomRequest request) {
        ChatRoomResponse response = chatService.createChatRoom(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 채팅방 목록 조회 API
     * 사용자가 참여 중인 모든 채팅방을 조회할 수 있음
     * 채팅방은 최신 메시지 기준으로 정렬함
     * @return  사용자의 채팅방 목록
     */
    @GetMapping("/list")
    public ResponseEntity<List<ChatRoomResponse>> getChatRooms() {
        List<ChatRoomResponse> response = chatService.getChatRooms();
        return ResponseEntity.ok(response);
    }

    /**
     * 채팅방 초대 API
     * 단체 채팅방에 새로운 사용자를 초대할 수 있음
     * 관리자만 초대 가능
     * @param chatId    채팅방 ID
     * @param request   초대할 사용자 ID 목록
     * @return  성공 시 200 OK 반환
     */
    @PostMapping("/{chatId}/invite")
    public ResponseEntity<Void> inviteUsers(@PathVariable Long chatId, @RequestBody InviteRequest request) {
        chatService.inviteUsers(chatId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 채팅방 나가기 API
     * 사용자가 특정 채팅방에서 나갈 수 있음
     * 단체 채팅방에서는 관리자가 나가면 자동 승격됨
     * @param chatId    나갈 채팅방 ID
     * @return  성공 시 200 OK 반환
     */
    @DeleteMapping("/{chatId}/leave")
    public ResponseEntity<Void> leaveChatRoom(@PathVariable Long chatId) {
        chatService.leaveChatRoom(chatId);
        return ResponseEntity.ok().build();
    }

    /**
     * 채팅방 강퇴 API
     * 관리자가 특정 사용자를 채팅방에서 강제 퇴장시킬 수 있음
     * @param chatId    채팅방 ID
     * @param userId    강퇴할 사용자 ID
     * @return  성공 시 200 OK 반환
     */
    @DeleteMapping("/{chatId}/kick/{userId}")
    public ResponseEntity<Void> kickUser(@PathVariable Long chatId, @PathVariable Long userId) {
        chatService.kickUser(chatId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 채팅방 관리자 설정 API
     * 특정 사용자를 단체 채팅방의 관리자로 설정할 수 있음
     * @param chatId    채팅방 ID
     * @param userId    관리자 역할을 부여할 사용자 ID
     * @return  성공 시 200 OK 반환
     */
    @PostMapping("/{chatId}/set-admin/{userId}")
    public ResponseEntity<Void> setAdmin(@PathVariable Long chatId, @PathVariable Long userId) {
        chatService.setAdmin(chatId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 채팅방 고정 API
     * 사용자가 특정 채팅방을 상단에 고정할 수 있음
     * @param chatId    채팅방 ID
     * @return  성공 시 200 OK 반환
     */
    @PostMapping("/{chatId}/pin")
    public ResponseEntity<Void> pinChatRoom(@PathVariable Long chatId) {
        chatService.pinChatRoom(chatId);
        return ResponseEntity.ok().build();
    }

    /**
     * 채팅방 알림 설정 API
     * 사용자가 특정 채팅방의 알림을 켜거나 끌 수 있음
     * @param chatId    채팅방 ID
     * @return  성공 시 200 OK 반환
     */
    @PostMapping("/{chatId}/mute")
    public ResponseEntity<Void> muteChatRoom(@PathVariable Long chatId) {
        chatService.muteChatRoom(chatId);
        return ResponseEntity.ok().build();
    }

    /**
     * 비밀번호 보호 채팅방 설정 API
     * 특정 채팅방을 비밀번호 보호 모드로 설정할 수 있음
     * 사용자가 입장 시 비밀번호를 입력해야 함
     * @param chatId    채팅방 ID
     * @param request   비밀번호 설정 요청 데이터
     * @return  성공 시 200 OK 반환
     */
    @PostMapping("/{chatId}/lock")
    public ResponseEntity<Void> lockChatRoom(@PathVariable Long chatId, @RequestBody ChatRoomLockRequest request) {
        chatService.lockChatRoom(chatId, request);
        return ResponseEntity.ok().build();
    }

    // =============== 채팅 메시지 관리 API ==================

    /**
     * 메시지 전송 API
     * 사용자가 채팅방에 텍스트, 이미지, 파일 등의 메시지를 전송할 수 있음
     * 메시지는 AES 암호화를 적용하여 저장됨
     * @param chatId    메시지를 보낼 채팅방 ID
     * @param request   메시지 전송 요청 데이터
     * @return  메시지 전송 결과
     */
    @PostMapping("/{chatId}/send")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @PathVariable Long chatId,
            @RequestBody ChatMessageRequest request
    ) {
        ChatMessageResponse response = chatService.sendMessage(chatId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 채팅 메시지 조회 API
     * 사용자가 특정 채팅방의 메시지를 조회할 수 있음
     * 페이지네이션을 적용하여 일정 개수씩 불러올 수 있음
     * @param chatId    채팅방 ID
     * @param page  페이지 번호 (기본갑: 0)
     * @param size  메시지 개수 (기본값: 20)
     * @return  메시지 목록
     */
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
            @PathVariable Long chatId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        List<ChatMessageResponse> response = chatService.getMessages(chatId, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * 메시지 삭제 API
     * 사용자는자신이 보낸 메시지를 삭제할 수 있음
     * 삭제된 메시지는 '삭제된 메시지입니다.'로 표시됨
     * @param messageId 삭제할 메시지 ID
     * @return  성공 시 200 OK 반환
     */
    @DeleteMapping("/message/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId) {
        chatService.deleteMessage(messageId);
        return ResponseEntity.ok().build();
    }

    /**
     * 실시간 타이핑 표시 API
     * 상대방 또는 단체 채팅방에서 사용자가 메시지를 입력 중인지 확인할 수 있음
     * @param chatId    채팅방 ID
     * @return  현재 입력 중인 사용자 목록
     */
    @GetMapping("/{chatId}/typing")
    public ResponseEntity<List<String>> getTypingUsers(@PathVariable Long chatId) {
        List<String> typingUsers = chatService.getTypingUsers(chatId);
        return ResponseEntity.ok(typingUsers);
    }

    /**
     * 메시지 암호화 API
     * 채팅 메시지를 AES 암호화하여 보안 강화
     * @param chatId    채팅방 ID
     * @return  성공 시 200 OK 반환
     */
    @PostMapping("/{chatId}/encrypt")
    public ResponseEntity<Void> encryptChat(@PathVariable Long chatId) {
        chatService.encryptChat(chatId);
        return ResponseEntity.ok().build();
    }

    /**
     * 메시지 신고 API
     * 사용자가 특정 메시지를 신고할 수 있음
     * @param chatId    채팅방 ID
     * @param request   신고 요청 데이터
     * @return  성공 시 200 OK 반환
     */
    @PostMapping("/{chatId}/report")
    public ResponseEntity<Void> reportMessage(@PathVariable Long chatId, @RequestBody ReportRequest request) {
        chatService.reportMessage(chatId, request);
        return ResponseEntity.ok().build();
    }

    // =============== 부가적인 기능 API ==================

    /**
     * 채팅방 투표 생성 API
     * 단체 채팅방에서 특정 주제에 대해 투표를 생성할 수 있음
     * @param chatId    채팅방 ID
     * @param request   투표 생성 요청 데이터
     * @return  성공 시 200 OK 반환
     */
    @PostMapping("/{chatId}/vote")
    public ResponseEntity<Void> createVote(@PathVariable Long chatId, @RequestBody VoteRequest request) {
        chatService.createVote(chatId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 음성 메시지 전송 API
     * 사용자가 음성 메시지를 녹음하여 전송할 수 있음
     * @param chatId    채팅방 ID
     * @param request   음성 메시지 데이터
     * @return  성공 시 200 OK 반환
     */
    @PostMapping("/{chatId}/voice-message")
    public ResponseEntity<Void> sendVoiceMessage(@PathVariable Long chatId, @RequestBody VoiceMessageRequest request) {
        chatService.sendVoiceMessage(chatId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 채팅 메시지 번역 API
     * 사용자가 채팅 메시지를 선택한 언어로 번역할 수 있음
     * @param chatId    채팅방 ID
     * @param request   번역 요청 데이터 (타겟 언어 포함)
     * @return  번역된 메시지
     */
    @PostMapping("/{chatId}/translate")
    public ResponseEntity<String> translateMessage(@PathVariable Long chatId, @RequestBody TranslateRequest request) {
        String translatedText = chatService.translateMessage(chatId, request);
        return ResponseEntity.ok(translatedText);
    }
}
