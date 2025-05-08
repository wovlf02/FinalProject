package com.hamcam.back.service.chat;

import com.hamcam.back.dto.chat.request.ChatJoinRequest;
import com.hamcam.back.dto.chat.request.ChatRoomCreateRequest;
import com.hamcam.back.dto.chat.response.ChatParticipantDto;
import com.hamcam.back.dto.chat.response.ChatRoomListResponse;
import com.hamcam.back.dto.chat.response.ChatRoomResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.chat.ChatParticipant;
import com.hamcam.back.entity.chat.ChatRoom;
import com.hamcam.back.repository.chat.ChatParticipantRepository;
import com.hamcam.back.repository.chat.ChatRoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 채팅방 서비스
 * <p>
 * 채팅방 생성, 삭제, 입장, 퇴장, 목록 조회, 상세 조회 등을 처리합니다.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;

    /**
     * 채팅방 생성
     *
     * @param request 채팅방 생성 요청 정보
     * @return 생성된 채팅방 정보
     */
    public ChatRoomResponse createChatRoom(ChatRoomCreateRequest request) {
        ChatRoom room = ChatRoom.builder()
                .name(request.getRoomName())
                .type(request.getRoomType())
                .referenceId(request.getReferenceId())
                .createdAt(LocalDateTime.now())
                .build();

        chatRoomRepository.save(room);
        return toResponse(room);
    }

    /**
     * 전체 채팅방 목록 조회 (관리자 or 전체 공개)
     */
    public List<ChatRoomListResponse> getAllChatRooms() {
        return chatRoomRepository.findAll().stream()
                .map(this::toListResponse)
                .collect(Collectors.toList());
    }

    /**
     * 채팅방 상세 조회
     *
     * @param roomId 채팅방 ID
     * @return 채팅방 상세 정보
     */
    public ChatRoomResponse getChatRoomById(Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));
        return toResponse(room);
    }

    /**
     * 채팅방 삭제
     *
     * @param roomId 삭제할 채팅방 ID
     */
    public void deleteChatRoom(Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));
        chatRoomRepository.delete(room);
    }

    /**
     * 채팅방 입장
     *
     * @param roomId  채팅방 ID
     * @param request 입장 요청 정보
     */
    @Transactional
    public void joinChatRoom(Long roomId, ChatJoinRequest request) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        User user = User.builder().id(request.getUserId()).build();

        boolean alreadyJoined = chatParticipantRepository.findByChatRoomAndUser(room, user).isPresent();
        if (!alreadyJoined) {
            ChatParticipant participant = ChatParticipant.builder()
                    .chatRoom(room)
                    .user(user)
                    .joinedAt(LocalDateTime.now())
                    .build();
            chatParticipantRepository.save(participant);
        }
    }

    /**
     * 채팅방 퇴장
     *
     * @param roomId  채팅방 ID
     * @param request 퇴장 요청 정보
     */
    @Transactional
    public void exitChatRoom(Long roomId, ChatJoinRequest request) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        User user = User.builder().id(request.getUserId()).build();
        ChatParticipant participant = chatParticipantRepository.findByChatRoomAndUser(room, user)
                .orElseThrow(() -> new IllegalArgumentException("입장한 사용자가 아닙니다."));

        chatParticipantRepository.delete(participant);

        // 채팅방에 참여자가 없다면 자동 삭제
        boolean empty = chatParticipantRepository.findByChatRoom(room).isEmpty();
        if (empty) {
            chatRoomRepository.delete(room);
        }
    }

    // ===== DTO 변환 =====

    private ChatRoomResponse toResponse(ChatRoom room) {
        List<ChatParticipantDto> participantDtos = chatParticipantRepository.findByChatRoom(room)
                .stream()
                .map(p -> new ChatParticipantDto(
                        p.getUser().getId(),
                        p.getUser().getNickname(),
                        p.getUser().getProfileImageUrl()))
                .toList();

        return ChatRoomResponse.builder()
                .roomId(room.getId())
                .roomName(room.getName())
                .roomType(room.getType())
                .referenceId(room.getReferenceId())
                .createdAt(room.getCreatedAt())
                .participants(participantDtos)
                .build();
    }


    private ChatRoomListResponse toListResponse(ChatRoom room) {
        return ChatRoomListResponse.builder()
                .roomId(room.getId())
                .roomName(room.getName())
                .roomType(room.getType())
                .participantCount(
                        chatParticipantRepository.findByChatRoom(room).size()
                )
                .build();
    }
}
