package com.hamcam.back.service.community.chat;

import com.hamcam.back.dto.community.chat.request.DirectChatRequest;
import com.hamcam.back.dto.community.chat.response.ChatParticipantDto;
import com.hamcam.back.dto.community.chat.response.ChatRoomListResponse;
import com.hamcam.back.dto.community.chat.response.ChatRoomResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.chat.*;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.chat.ChatParticipantRepository;
import com.hamcam.back.repository.chat.ChatRoomRepository;
import com.hamcam.back.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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

    /**
     * ✅ 상대방과의 1:1 채팅방이 존재하면 반환, 없으면 새로 생성
     */
    public ChatRoomResponse startOrGetDirectChat(DirectChatRequest request, HttpServletRequest httpRequest) {
        Long userId = SessionUtil.getUserId(httpRequest);
        User user = getUserById(userId);
        User target = getUserById(request.getTargetUserId());

        if (user.getId().equals(target.getId())) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        return findExistingDirectRoom(user, target)
                .map(this::toResponse)
                .orElseGet(() -> createNewDirectChat(user, target));
    }

    /**
     * ✅ 내가 참여 중인 모든 1:1 채팅방 목록 조회
     */
    public List<ChatRoomListResponse> getMyDirectChatRooms(HttpServletRequest httpRequest) {
        Long userId = SessionUtil.getUserId(httpRequest);
        User user = getUserById(userId);

        return chatParticipantRepository.findByUser(user).stream()
                .map(ChatParticipant::getChatRoom)
                .filter(room -> room.getType() == ChatRoomType.DIRECT)
                .map(room -> ChatRoomListResponse.builder()
                        .roomId(room.getId())
                        .roomName(room.getName())
                        .roomType(room.getType().name())
                        .participantCount(chatParticipantRepository.countByChatRoom(room))
                        .profileImageUrl(room.getRepresentativeImageUrl())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * ✅ 특정 사용자와의 1:1 채팅방 조회 (없으면 예외)
     */
    public ChatRoomResponse getDirectChatWithUser(Long targetUserId, HttpServletRequest httpRequest) {
        Long userId = SessionUtil.getUserId(httpRequest);
        User user = getUserById(userId);
        User target = getUserById(targetUserId);

        return findExistingDirectRoom(user, target)
                .map(this::toResponse)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
    }

    // ===== 내부 유틸 =====

    private Optional<ChatRoom> findExistingDirectRoom(User userA, User userB) {
        List<ChatRoom> directRooms = chatParticipantRepository.findByUser(userA).stream()
                .map(ChatParticipant::getChatRoom)
                .filter(room -> room.getType() == ChatRoomType.DIRECT)
                .toList();

        return directRooms.stream()
                .filter(room -> {
                    List<ChatParticipant> participants = chatParticipantRepository.findByChatRoom(room);
                    return participants.size() == 2 &&
                            participants.stream().anyMatch(p -> p.getUser().getId().equals(userB.getId()));
                })
                .findFirst();
    }

    private ChatRoomResponse createNewDirectChat(User user, User target) {
        String roomName = String.format("%s ↔ %s", user.getNickname(), target.getNickname());

        ChatRoom room = ChatRoom.builder()
                .name(roomName)
                .type(ChatRoomType.DIRECT)
                .createdAt(LocalDateTime.now())
                .build();
        chatRoomRepository.save(room);

        List<ChatParticipant> participants = List.of(
                ChatParticipant.builder().chatRoom(room).user(user).joinedAt(LocalDateTime.now()).build(),
                ChatParticipant.builder().chatRoom(room).user(target).joinedAt(LocalDateTime.now()).build()
        );

        chatParticipantRepository.saveAll(participants);
        return toResponse(room);
    }

    private ChatRoomResponse toResponse(ChatRoom room) {
        List<ChatParticipantDto> participants = chatParticipantRepository.findByChatRoom(room).stream()
                .map(p -> new ChatParticipantDto(
                        p.getUser().getId(),
                        p.getUser().getNickname(),
                        p.getUser().getProfileImageUrl()
                ))
                .toList();

        return ChatRoomResponse.builder()
                .roomId(room.getId())
                .roomName(room.getName())
                .roomType(room.getType().name())
                .createdAt(room.getCreatedAt())
                .representativeImageUrl(room.getRepresentativeImageUrl())
                .participants(participants)
                .build();
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
