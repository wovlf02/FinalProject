package com.hamcam.back.service.community.chat;

import com.hamcam.back.dto.community.chat.request.DirectChatRequest;
import com.hamcam.back.dto.community.chat.response.ChatRoomListResponse;
import com.hamcam.back.dto.community.chat.response.ChatRoomResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.chat.ChatParticipant;
import com.hamcam.back.entity.chat.ChatRoom;
import com.hamcam.back.entity.chat.ChatRoomType;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.chat.ChatParticipantRepository;
import com.hamcam.back.repository.chat.ChatRoomRepository;
import com.hamcam.back.security.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DirectChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final UserRepository userRepository;

    public ChatRoomResponse startOrGetDirectChat(DirectChatRequest request) {
        Long myId = getCurrentUserId();
        Long otherId = request.getTargetUserId();

        return findExistingDirectRoom(myId, otherId)
                .map(this::toResponse)
                .orElseGet(() -> createNewDirectChat(myId, otherId));
    }

    public List<ChatRoomListResponse> getMyDirectChatRooms() {
        Long myId = getCurrentUserId();
        User me = User.builder().id(myId).build();

        return chatParticipantRepository.findByUser(me).stream()
                .map(ChatParticipant::getChatRoom)
                .filter(room -> room.getType() == ChatRoomType.DIRECT)
                .map(room -> ChatRoomListResponse.builder()
                        .roomId(room.getId())
                        .roomName(room.getName())
                        .roomType(room.getType().name())
                        .participantCount(chatParticipantRepository.countByChatRoom(room))
                        .build())
                .collect(Collectors.toList());
    }

    public ChatRoomResponse getDirectChatWithUser(Long userId) {
        Long myId = getCurrentUserId();
        return findExistingDirectRoom(myId, userId)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("상대방과의 1:1 채팅방이 존재하지 않습니다."));
    }

    private Optional<ChatRoom> findExistingDirectRoom(Long userA, Long userB) {
        User user = User.builder().id(userA).build();
        List<ChatRoom> myRooms = chatParticipantRepository.findByUser(user)
                .stream()
                .map(ChatParticipant::getChatRoom)
                .filter(room -> room.getType() == ChatRoomType.DIRECT)
                .toList();

        for (ChatRoom room : myRooms) {
            List<ChatParticipant> participants = chatParticipantRepository.findByChatRoom(room);
            if (participants.size() == 2 &&
                    participants.stream().anyMatch(p -> p.getUser().getId().equals(userB))) {
                return Optional.of(room);
            }
        }
        return Optional.empty();
    }

    private ChatRoomResponse createNewDirectChat(Long myId, Long otherId) {
        ChatRoom newRoom = ChatRoom.builder()
                .name("DirectChat")
                .type(ChatRoomType.DIRECT)
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

    private ChatRoomResponse toResponse(ChatRoom room) {
        return ChatRoomResponse.builder()
                .roomId(room.getId())
                .roomName(room.getName())
                .roomType(room.getType().name())
                .referenceId(room.getReferenceId())
                .createdAt(room.getCreatedAt())
                .build();
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) throw new CustomException("로그인 정보가 없습니다.");

        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getUserId();
        }

        throw new CustomException("사용자 정보를 불러올 수 없습니다.");
    }
}
