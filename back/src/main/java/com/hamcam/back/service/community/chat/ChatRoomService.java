package com.hamcam.back.service.community.chat;

import com.hamcam.back.dto.community.chat.request.*;
import com.hamcam.back.dto.community.chat.response.ChatParticipantDto;
import com.hamcam.back.dto.community.chat.response.ChatRoomListResponse;
import com.hamcam.back.dto.community.chat.response.ChatRoomResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.chat.*;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
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

    /**
     * ✅ 채팅방 생성
     */
    public ChatRoomResponse createChatRoom(ChatRoomCreateRequest request) {
        User creator = getUser(request.getCreatorId());

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
                .distinct().toList();

        List<User> members = userRepository.findAllById(participantIds);
        List<ChatParticipant> participants = members.stream()
                .map(user -> ChatParticipant.builder()
                        .chatRoom(room)
                        .user(user)
                        .joinedAt(LocalDateTime.now())
                        .build())
                .toList();

        chatParticipantRepository.saveAll(participants);
        return toResponse(room);
    }

    /**
     * ✅ 채팅방 목록 조회
     */
    public List<ChatRoomListResponse> getMyChatRooms(ChatRoomListRequest request) {
        User user = getUser(request.getUserId());
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
     * ✅ 채팅방 상세 조회
     */
    public ChatRoomResponse getChatRoomById(ChatRoomDetailRequest request) {
        getUser(request.getUserId()); // 유효성만 확인
        ChatRoom room = getRoom(request.getRoomId());
        return toResponse(room);
    }

    /**
     * ✅ 채팅방 삭제
     */
    public void deleteChatRoom(ChatRoomDeleteRequest request) {
        getUser(request.getUserId());
        ChatRoom room = getRoom(request.getRoomId());
        chatRoomRepository.delete(room);
    }

    /**
     * ✅ 채팅방 입장
     */
    @Transactional
    public void joinChatRoom(ChatEnterRequest request) {
        ChatRoom room = getRoom(request.getRoomId());
        User user = getUser(request.getUserId());

        chatParticipantRepository.findByChatRoomAndUser(room, user)
                .orElseGet(() -> chatParticipantRepository.save(ChatParticipant.builder()
                        .chatRoom(room)
                        .user(user)
                        .joinedAt(LocalDateTime.now())
                        .build()));
    }

    /**
     * ✅ 채팅방 나가기
     */
    @Transactional
    public void exitChatRoom(ChatEnterRequest request) {
        ChatRoom room = getRoom(request.getRoomId());
        User user = getUser(request.getUserId());

        ChatParticipant participant = chatParticipantRepository.findByChatRoomAndUser(room, user)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCESS_DENIED));

        chatParticipantRepository.delete(participant);

        if (chatParticipantRepository.findByChatRoom(room).isEmpty()) {
            chatRoomRepository.delete(room);
        }
    }

    // ========== 내부 유틸 ==========

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private ChatRoom getRoom(Long id) {
        return chatRoomRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
    }

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
