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

    public ChatRoomResponse createChatRoom(ChatRoomCreateRequest request) {
        User creator = userRepository.findById(request.getCreatorId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

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

    public List<ChatRoomListResponse> getMyChatRooms(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
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

    public ChatRoomResponse getChatRoomById(Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
        return toResponse(room);
    }

    public void deleteChatRoom(Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
        chatRoomRepository.delete(room);
    }

    @Transactional
    public void joinChatRoom(ChatJoinRequest request) {
        ChatRoom room = chatRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        boolean alreadyJoined = chatParticipantRepository.findByChatRoomAndUser(room, user).isPresent();
        if (!alreadyJoined) {
            chatParticipantRepository.save(ChatParticipant.builder()
                    .chatRoom(room)
                    .user(user)
                    .joinedAt(LocalDateTime.now())
                    .build());
        }
    }

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