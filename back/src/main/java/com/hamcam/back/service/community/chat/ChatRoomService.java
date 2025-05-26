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
import com.hamcam.back.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
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

    private static final String DEFAULT_PROFILE_URL = "/icons/base_profile.png";

    public ChatRoomResponse createChatRoom(ChatRoomCreateRequest request, HttpServletRequest httpRequest) {
        Long userId = SessionUtil.getUserId(httpRequest);
        User creator = getUser(userId);

        List<Long> inviteeIds = request.getInvitedUserIds();
        if (inviteeIds == null || inviteeIds.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_CHATROOM_INVITEE);
        }

        ChatRoomType type = (inviteeIds.size() == 1) ? ChatRoomType.DIRECT : ChatRoomType.GROUP;
        String imageUrl = request.getImage() != null && !request.getImage().isEmpty()
                ? fileUploadService.storeChatRoomImage(request.getImage())
                : null;

        ChatRoom room = ChatRoom.builder()
                .name(request.getRoomName())
                .type(type)
                .representativeImageUrl(imageUrl)
                .createdAt(LocalDateTime.now())
                .build();

        chatRoomRepository.save(room);

        List<Long> participantIds = Stream.concat(Stream.of(userId), inviteeIds.stream()).distinct().toList();
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

    public List<ChatRoomListResponse> getMyChatRooms(HttpServletRequest httpRequest) {
        Long userId = SessionUtil.getUserId(httpRequest);
        User user = getUser(userId);
        List<ChatParticipant> participants = chatParticipantRepository.findByUser(user);

        return participants.stream().map(participant -> {
            ChatRoom room = participant.getChatRoom();
            ChatMessage lastMessage = chatMessageRepository.findTopByChatRoomOrderBySentAtDesc(room);

            int unreadCount = chatMessageRepository.countUnreadMessagesByReadTable(room, userId);
            int totalMessageCount = chatMessageRepository.countByChatRoom(room);

            return ChatRoomListResponse.builder()
                    .roomId(room.getId())
                    .roomName(room.getName())
                    .roomType(room.getType().name())
                    .lastMessage(lastMessage != null ? generatePreview(lastMessage) : null)
                    .lastMessageAt(lastMessage != null ? lastMessage.getSentAt() : null)
                    .lastSenderNickname(lastMessage != null && lastMessage.getSender() != null ? lastMessage.getSender().getNickname() : null)
                    .lastSenderProfileImageUrl(lastMessage != null && lastMessage.getSender() != null
                            ? (convertToWebProfileUrl(lastMessage.getSender().getProfileImageUrl()))
                            : DEFAULT_PROFILE_URL)
                    .lastMessageType(lastMessage != null ? lastMessage.getType().name() : null)
                    .participantCount(chatParticipantRepository.countByChatRoom(room))
                    .unreadCount(unreadCount)
                    .totalMessageCount(totalMessageCount)
                    .profileImageUrl(convertToWebProfileUrl(room.getRepresentativeImageUrl()))
                    .build();
        }).toList();
    }

    public ChatRoomResponse getChatRoomById(ChatRoomDetailRequest request) {
        ChatRoom room = getRoom(request.getRoomId());
        return toResponse(room);
    }

    public void deleteChatRoom(ChatRoomDeleteRequest request, HttpServletRequest httpRequest) {
        SessionUtil.getUserId(httpRequest); // 유효성 확인만
        chatRoomRepository.delete(getRoom(request.getRoomId()));
    }

    @Transactional
    public void joinChatRoom(ChatEnterRequest request, HttpServletRequest httpRequest) {
        Long userId = SessionUtil.getUserId(httpRequest);
        ChatRoom room = getRoom(request.getRoomId());
        User user = getUser(userId);

        chatParticipantRepository.findByChatRoomAndUser(room, user)
                .orElseGet(() -> chatParticipantRepository.save(ChatParticipant.builder()
                        .chatRoom(room)
                        .user(user)
                        .joinedAt(LocalDateTime.now())
                        .build()));
    }

    @Transactional
    public void exitChatRoom(ChatEnterRequest request, HttpServletRequest httpRequest) {
        Long userId = SessionUtil.getUserId(httpRequest);
        ChatRoom room = getRoom(request.getRoomId());
        User user = getUser(userId);

        ChatParticipant participant = chatParticipantRepository.findByChatRoomAndUser(room, user)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCESS_DENIED));

        chatParticipantRepository.delete(participant);
        if (chatParticipantRepository.findByChatRoom(room).isEmpty()) {
            chatRoomRepository.delete(room);
        }
    }

    @Transactional
    public void deleteChatRoomById(Long roomId, HttpServletRequest httpRequest) {
        Long userId = SessionUtil.getUserId(httpRequest);
        ChatRoom room = getRoom(roomId);
        User user = getUser(userId);

        ChatParticipant participant = chatParticipantRepository.findByChatRoomAndUser(room, user)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCESS_DENIED));

        chatParticipantRepository.delete(participant);

        if (chatParticipantRepository.findByChatRoom(room).isEmpty()) {
            chatRoomRepository.delete(room);
        }
    }

    // =======================
    // 핵심: 프로필 이미지 경로 변환
    // =======================
    private ChatRoomResponse toResponse(ChatRoom room) {
        List<ChatParticipantDto> participants = chatParticipantRepository.findByChatRoom(room).stream()
                .map(p -> new ChatParticipantDto(
                        p.getUser().getId(),
                        p.getUser().getNickname(),
                        convertToWebProfileUrl(p.getUser().getProfileImageUrl())
                ))
                .collect(Collectors.toList());

        return ChatRoomResponse.builder()
                .roomId(room.getId())
                .roomName(room.getName())
                .roomType(room.getType().name())
                .createdAt(room.getCreatedAt())
                .representativeImageUrl(convertToWebProfileUrl(room.getRepresentativeImageUrl()))
                .participants(participants)
                .build();
    }

    private String convertToWebProfileUrl(String url) {
        if (url == null || url.isEmpty() || url.equals("프로필 사진이 없습니다")) {
            return DEFAULT_PROFILE_URL;
        }
        if (url.startsWith("C:\\FinalProject")) {
            String webUrl = url.replace("C:\\FinalProject", "").replace("\\", "/");
            return webUrl.startsWith("/") ? webUrl : "/" + webUrl;
        }
        return url;
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private ChatRoom getRoom(Long id) {
        return chatRoomRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
    }

    private String generatePreview(ChatMessage message) {
        return switch (message.getType()) {
            case TEXT -> message.getContent();
            case FILE, IMAGE -> "[파일]";
            case ENTER -> message.getContent();
            case READ_ACK -> "";
        };
    }
}
