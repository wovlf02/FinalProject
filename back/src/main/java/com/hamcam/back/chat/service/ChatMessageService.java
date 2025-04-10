package com.hamcam.back.chat.service;

import com.hamcam.back.auth.entity.User;
import com.hamcam.back.auth.repository.UserRepository;
import com.hamcam.back.chat.dto.request.ChatMessageRequest;
import com.hamcam.back.chat.dto.response.ChatMessageResponse;
import com.hamcam.back.chat.entity.ChatMessage;
import com.hamcam.back.chat.entity.ChatRoom;
import com.hamcam.back.chat.repository.ChatMessageRepository;
import com.hamcam.back.chat.repository.ChatRoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * ChatMessageService
 *
 * 실시간 채팅 메시지 관련 비즈니스 로직을 처리하는 서비스 클래스
 * WebSocket을 통해 전달된 메시지를 데이터베이스에 저장하고,
 * 사용자 및 채팅방 정보를 바탕으로 클라이언트 응답 객체를 생성하는 역할을 수행
 *
 * [주요 기능]
 * 메시지 저장 (채팅방 ID 및 사용자 ID 기반)
 * 파일 첨부 가능 (텍스트 + 바이너리)
 * 저장된 메시지를 클라이언트 응답 형식으로 반환
 *
 * [관련 Entity]
 * ChatMessage: 채팅 메시지 정보 저장 (텍스트, 첨부파일, 시간 등)
 * ChatRoom: 채팅방 정보 (room_id, type 등)
 * User: 메시지 전송자 (sender)
 */
@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatMessageResponse saveMessage(ChatMessageRequest request) {
        // 1. 채팅방 존재 여부 확인
        ChatRoom room = chatRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방이 존재하지 않습니다."));

        // 2. 사용자 존재 여부 확인
        User sender = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        // 3. 첨부파일 업로드 (있을 경우)
        String fileUrl = null;
        MultipartFile file = request.getFile();

        if(file != null && !file.isEmpty()) {
            try {
                // S3 등에 업로드한 후 반환되는 URL
                fileUrl = cloudStorageUtil.upload(file, "chat");
            } catch (IOException e) {
                throw new RuntimeException("파일 업로드 중 오류 발생", e);
            }
        }

        // 4. 메시지 엔티티 생성
        ChatMessage message = ChatMessage.builder()
                .room(room) // 메시지가 속한 채팅방
                .sender(sender) // 메시지를 보낸 사용자
                .content(request.getContent()) // 텍스트 메시지
                .fileUrl(fileUrl) // DB에는 경로만 저장
                .fileType(request.getFileType()) // 첨부파일 MIME 타입 (ex. image/png)
                .sendAt(LocalDateTime.now()) // 전송 시각
                .build();

        // 4. 메시지 DB 저장
        ChatMessage saved = chatMessageRepository.save(message);

        // 5. 응답 DTO 생성 및 반환
        return ChatMessageResponse.builder()
                .messageId(saved.getId())
                .senderNickname(sender.getNickname())
                .content(saved.getContent())
                .fileUrl(fileUrl)
                .fileType(saved.getFileType())
                .sendAt(saved.getSendAt())
                .build();
    }
}
