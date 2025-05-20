package com.hamcam.back.service.community.chat;

import com.hamcam.back.dto.community.chat.request.ChatJoinRequest;
import com.hamcam.back.dto.community.chat.request.ChatRoomCreateRequest;
import com.hamcam.back.dto.community.chat.response.ChatParticipantDto;
import com.hamcam.back.dto.community.chat.response.ChatRoomListResponse;
import com.hamcam.back.dto.community.chat.response.ChatRoomResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.chat.*;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.global.security.SecurityUtil;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.chat.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;
    private final SecurityUtil securityUtil;

    /**
     * 채팅방 생성 (1:1 또는 그룹)
     */
    public ChatRoomResponse createChatRoom(ChatRoomCreateRequest request) {
        User creator = securityUtil.getCurrentUser();

        List<Long> inviteeIds = request.getInvitedUserIds();
        if (inviteeIds == null || inviteeIds.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_CHATROOM_INVITEE);
        }

        ChatRoomType type = (inviteeIds.size() == 1) ? ChatRoomType.DIRECT : ChatRoomType.GROUP;

        String imageUrl = null;
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            imageUrl = fileUploadService.storeChatRoomImage(request.getImage());
        }

        ChatRoom room = ChatRoom.builder()
                .name(request.getRoomName())
                .type(type)
                .representativeImageUrl(imageUrl)
                .createdAt(LocalDateTime.now())
                .build();

        chatRoomRepository.save(room);

        List<Long> participantIds = Stream.concat(Stream.of(creator.getId()), inviteeIds.stream())
                .distinct()
                .toList();

        List<User> members = userRepository.findAllById(participantIds);
        List<ChatParticipant> chatMembers = members.stream()
                .map(user -> ChatParticipant.builder()
                        .chatRoom(room)
                        .user(user)
                        .joinedAt(LocalDateTime.now())
                        .build())
                .toList();

        chatParticipantRepository.saveAll(chatMembers);
        return toResponse(room);
    }

    /**
     * 내 채팅방 목록 조회
     */
    public List<ChatRoomListResponse> getMyChatRooms() {
        User user = securityUtil.getCurrentUser();
        List<ChatParticipant> participants = chatParticipantRepository.findByUser(user);

        return participants.stream().map(participant -> {
            ChatRoom room = participant.getChatRoom();
            ChatMessage lastMessage = chatMessageRepository.findTopByChatRoomOrderBySentAtDesc(room);
            int unreadCount = chatMessageRepository.countUnreadMessages(room, user, participant.getLastReadMessageId());
            int totalMessageCount = chatMessageRepository.countByChatRoom(room);

            return ChatRoomListResponse.builder()
                    .roomId(room.getId())
                    .roomName(room.getName())
                    .roomType(room.getType().name())
                    .lastMessage(lastMessage != null ? lastMessage.getContent() : null)
                    .lastMessageAt(lastMessage != null ? lastMessage.getSentAt() : null)
                    .lastSenderNickname(lastMessage != null && lastMessage.getSender() != null ? lastMessage.getSender().getNickname() : null)
                    .lastSenderProfileImageUrl(lastMessage != null && lastMessage.getSender() != null ? lastMessage.getSender().getProfileImageUrl() : null)
                    .lastMessageType(lastMessage != null ? lastMessage.getType().name() : null)
                    .participantCount(chatParticipantRepository.countByChatRoom(room))
                    .unreadCount(unreadCount)
                    .totalMessageCount(totalMessageCount)
                    .profileImageUrl(room.getRepresentativeImageUrl())
                    .build();
        }).toList();
    }

    /**
     * 채팅방 상세 조회
     */
    public ChatRoomResponse getChatRoomById(Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
        return toResponse(room);
    }

    /**
     * 채팅방 삭제
     */
    public void deleteChatRoom(Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
        chatRoomRepository.delete(room);
    }

    /**
     * 채팅방 참여 (권한 확인 후)
     */
    @Transactional
    public void joinChatRoom(ChatJoinRequest request) {
        ChatRoom room = chatRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        chatParticipantRepository.findByChatRoomAndUser(room, user)
                .or(() -> {
                    chatParticipantRepository.save(ChatParticipant.builder()
                            .chatRoom(room)
                            .user(user)
                            .joinedAt(LocalDateTime.now())
                            .build());
                    return null;
                });
    }

    /**
     * 채팅방 나가기 (마지막 사용자면 방도 삭제)
     */
    @Transactional
    public void exitChatRoom(ChatJoinRequest request) {
        ChatRoom room = chatRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ChatParticipant participant = chatParticipantRepository.findByChatRoomAndUser(room, user)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCESS_DENIED));

        chatParticipantRepository.delete(participant);

        boolean noParticipantsLeft = chatParticipantRepository.findByChatRoom(room).isEmpty();
        if (noParticipantsLeft) {
            chatRoomRepository.delete(room);
        }
    }

    // ===== 내부 변환 유틸 =====

    private ChatRoomResponse toResponse(ChatRoom room) {
        List<ChatParticipantDto> participants = chatParticipantRepository.findByChatRoom(room).stream()
                .map(p -> new ChatParticipantDto(
                        p.getUser().getId(),
                        p.getUser().getNickname(),
                        p.getUser().getProfileImageUrl()))
                .collect(Collectors.toList());

        return ChatRoomResponse.builder()
                .roomId(room.getId())
                .roomName(room.getName())
                .roomType(room.getType().name())
                .createdAt(room.getCreatedAt())
                .representativeImageUrl(room.getRepresentativeImageUrl())
                .participants(participants)
                .build();
    }
}
