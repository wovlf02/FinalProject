package com.hamcam.back.service.chat;

import com.hamcam.back.dto.chat.request.DirectChatRequest;
import com.hamcam.back.dto.chat.response.ChatRoomListResponse;
import com.hamcam.back.dto.chat.response.ChatRoomResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.chat.ChatParticipant;
import com.hamcam.back.entity.chat.ChatRoom;
import com.hamcam.back.repository.chat.ChatParticipantRepository;
import com.hamcam.back.repository.chat.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 1:1 채팅 서비스
 * <p>
 * 사용자 간의 Direct(1:1) 채팅방 생성, 조회, 목록 반환을 담당합니다.
 * 동일한 두 사용자 간의 중복 방 생성을 방지하고, 항상 하나의 채팅방을 유지합니다.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class DirectChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;

    /**
     * 1:1 채팅 시작 또는 기존 채팅방 반환
     *
     * @param request 상대 사용자 ID 포함
     * @return 기존 또는 새로 생성된 채팅방 정보
     */
    public ChatRoomResponse startOrGetDirectChat(DirectChatRequest request) {
        Long myId = getCurrentUserId();  // 실제 서비스에서는 인증 기반 사용자 ID 추출
        Long otherId = request.getTargetUserId();

        // 기존 1:1 채팅방 조회
        List<ChatRoom> myRooms = chatParticipantRepository.findByUser(User.builder().id(myId).build())
                .stream().map(ChatParticipant::getChatRoom).collect(Collectors.toList());

        for (ChatRoom room : myRooms) {
            if ("DIRECT".equals(room.getType())) {
                List<ChatParticipant> participants = chatParticipantRepository.findByChatRoom(room);
                if (participants.size() == 2 &&
                        participants.stream().anyMatch(p -> p.getUser().getId().equals(otherId))) {
                    return toResponse(room);  // 기존 방 반환
                }
            }
        }

        // 없으면 새로 생성
        ChatRoom newRoom = ChatRoom.builder()
                .name("DirectChat")
                .type("DIRECT")
                .referenceId(null)
                .createdAt(LocalDateTime.now())
                .build();
        chatRoomRepository.save(newRoom);

        chatParticipantRepository.save(ChatParticipant.builder()
                .chatRoom(newRoom)
                .user(User.builder().id(myId).build())
                .joinedAt(LocalDateTime.now())
                .build());

        chatParticipantRepository.save(ChatParticipant.builder()
                .chatRoom(newRoom)
                .user(User.builder().id(otherId).build())
                .joinedAt(LocalDateTime.now())
                .build());

        return toResponse(newRoom);
    }

    /**
     * 현재 로그인 사용자가 속한 모든 1:1 채팅방 조회
     */
    public List<ChatRoomListResponse> getMyDirectChatRooms() {
        Long myId = getCurrentUserId();
        User me = User.builder().id(myId).build();

        return chatParticipantRepository.findByUser(me).stream()
                .map(ChatParticipant::getChatRoom)
                .filter(room -> "DIRECT".equals(room.getType()))
                .map(room -> ChatRoomListResponse.builder()
                        .roomId(room.getId())
                        .name(room.getName())
                        .type(room.getType())
                        .participantCount(
                                chatParticipantRepository.findByChatRoom(room).size()
                        )
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 특정 사용자와의 1:1 채팅방 조회
     *
     * @param userId 상대 사용자 ID
     * @return 채팅방 응답 DTO
     */
    public ChatRoomResponse getDirectChatWithUser(Long userId) {
        Long myId = getCurrentUserId();

        List<ChatRoom> myRooms = chatParticipantRepository.findByUser(User.builder().id(myId).build())
                .stream().map(ChatParticipant::getChatRoom).collect(Collectors.toList());

        for (ChatRoom room : myRooms) {
            if ("DIRECT".equals(room.getType())) {
                List<ChatParticipant> participants = chatParticipantRepository.findByChatRoom(room);
                if (participants.size() == 2 &&
                        participants.stream().anyMatch(p -> p.getUser().getId().equals(userId))) {
                    return toResponse(room);
                }
            }
        }

        throw new IllegalArgumentException("상대방과의 1:1 채팅방이 존재하지 않습니다.");
    }

    // ===== DTO 변환 =====

    private ChatRoomResponse toResponse(ChatRoom room) {
        return ChatRoomResponse.builder()
                .roomId(room.getId())
                .name(room.getName())
                .type(room.getType())
                .referenceId(room.getReferenceId())
                .createdAt(room.getCreatedAt())
                .build();
    }

    // ===== 인증 사용자 식별자 추출 (mock) =====
    private Long getCurrentUserId() {
        // ⚠ 실제 서비스에서는 Spring Security 기반 사용자 ID 반환으로 대체
        return 1L;  // mock user ID
    }
}
