package com.hamcam.back.service.community.friend;

import com.hamcam.back.dto.community.friend.request.FriendAcceptRequest;
import com.hamcam.back.dto.community.friend.request.FriendRejectRequest;
import com.hamcam.back.dto.community.friend.request.FriendRequestSendRequest;
import com.hamcam.back.dto.community.friend.response.*;
import com.hamcam.back.dto.community.report.request.ReportRequest;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.friend.*;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.friend.FriendBlockRepository;
import com.hamcam.back.repository.friend.FriendReportRepository;
import com.hamcam.back.repository.friend.FriendRepository;
import com.hamcam.back.repository.friend.FriendRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final FriendBlockRepository friendBlockRepository;
    private final FriendReportRepository friendReportRepository;
    private final UserRepository userRepository;

    public void sendFriendRequest(FriendRequestSendRequest request) {
        User sender = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        User receiver = userRepository.findById(request.getTargetUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (sender.getId().equals(receiver.getId())) {
            throw new CustomException("자기 자신에게 친구 요청을 보낼 수 없습니다.");
        }

        if (friendRepository.existsByUserAndFriend(sender, receiver) ||
                friendRepository.existsByUserAndFriend(receiver, sender)) {
            throw new CustomException("이미 친구 관계입니다.");
        }

        if (friendRequestRepository.existsBySenderAndReceiver(sender, receiver)) {
            throw new CustomException("이미 친구 요청을 보냈습니다.");
        }

        friendRequestRepository.save(FriendRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .build());
    }

    public void acceptFriendRequest(FriendAcceptRequest request) {
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        User sender = userRepository.findById(request.getRequestId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        FriendRequest friendRequest = friendRequestRepository
                .findBySenderAndReceiver(sender, receiver)
                .orElseThrow(() -> new CustomException("친구 요청이 존재하지 않습니다."));

        friendRepository.save(Friend.builder().user(sender).friend(receiver).build());
        friendRepository.save(Friend.builder().user(receiver).friend(sender).build());
        friendRequestRepository.delete(friendRequest);
    }

    public void rejectFriendRequest(FriendRejectRequest request) {
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        FriendRequest friendRequest = friendRequestRepository.findById(request.getRequestId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!friendRequest.getReceiver().getId().equals(receiver.getId())) {
            throw new CustomException("요청 수신자와 사용자 정보가 일치하지 않습니다.");
        }

        friendRequestRepository.delete(friendRequest);
    }

    public FriendListResponse getFriendList(Long userId) {
        User me = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        List<Friend> friends = friendRepository.findAllFriendsOfUser(me);

        return new FriendListResponse(
                friends.stream()
                        .map(f -> f.getUser().equals(me) ? f.getFriend() : f.getUser())
                        .distinct()
                        .map(FriendListResponse.FriendDto::from)
                        .collect(Collectors.toList())
        );
    }

    public FriendRequestListResponse getReceivedFriendRequests(Long userId) {
        User me = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        List<FriendRequest> requests = friendRequestRepository.findByReceiver(me);
        return new FriendRequestListResponse(
                requests.stream()
                        .map(FriendRequestListResponse.FriendRequestDto::from)
                        .collect(Collectors.toList())
        );
    }

    public SentFriendRequestListResponse getSentFriendRequests(Long userId) {
        User me = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        List<FriendRequest> requests = friendRequestRepository.findBySender(me);

        List<SentFriendRequestListResponse.SentFriendRequestDto> result = requests.stream()
                .map(req -> SentFriendRequestListResponse.SentFriendRequestDto.builder()
                        .requestId(req.getId())
                        .receiverId(req.getReceiver().getId())
                        .receiverNickname(req.getReceiver().getNickname())
                        .receiverProfileImageUrl(req.getReceiver().getProfileImageUrl())
                        .requestedAt(req.getRequestedAt())
                        .status(req.getStatus().name())
                        .build())
                .collect(Collectors.toList());

        return new SentFriendRequestListResponse(result);
    }

    public void cancelSentFriendRequest(Long requestId, Long senderId) {
        User me = userRepository.findById(senderId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new CustomException("요청이 존재하지 않습니다."));

        if (!request.getSender().getId().equals(me.getId())) {
            throw new CustomException("해당 요청을 취소할 권한이 없습니다.");
        }

        friendRequestRepository.delete(request);
    }

    public FriendSearchResponse searchUsersByNickname(String nickname, Long userId) {
        User me = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        List<User> users = userRepository.findByNicknameContaining(nickname);

        return new FriendSearchResponse(
                users.stream()
                        .filter(user -> !user.getId().equals(me.getId()))
                        .map(user -> {
                            boolean alreadyFriend = friendRepository.existsByUserAndFriend(me, user)
                                    || friendRepository.existsByUserAndFriend(user, me);
                            boolean alreadyRequested = friendRequestRepository.existsBySenderAndReceiver(me, user);
                            boolean isBlocked = friendBlockRepository.existsByBlockerAndBlocked(me, user);

                            return new FriendSearchResponse.UserSearchResult(
                                    user.getId(),
                                    user.getNickname(),
                                    user.getProfileImageUrl(),
                                    alreadyFriend,
                                    alreadyRequested,
                                    isBlocked
                            );
                        })
                        .toList()
        );
    }

    public void deleteFriend(Long userId, Long friendId) {
        User me = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        User target = userRepository.findById(friendId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        friendRepository.findByUserAndFriend(me, target).ifPresent(friendRepository::delete);
        friendRepository.findByUserAndFriend(target, me).ifPresent(friendRepository::delete);
    }

    public void blockUser(Long userId, Long targetId) {
        User me = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        User target = userRepository.findById(targetId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (me.getId().equals(target.getId())) {
            throw new CustomException("자기 자신은 차단할 수 없습니다.");
        }

        if (friendBlockRepository.findByBlockerAndBlocked(me, target).isEmpty()) {
            friendBlockRepository.save(FriendBlock.builder()
                    .blocker(me)
                    .blocked(target)
                    .build());
        }

        deleteFriend(userId, targetId);
    }

    public void unblockUser(Long userId, Long targetId) {
        User me = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        User target = userRepository.findById(targetId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        friendBlockRepository.findByBlockerAndBlocked(me, target)
                .ifPresent(friendBlockRepository::delete);
    }

    public BlockedFriendListResponse getBlockedUsers(Long userId) {
        User me = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        List<FriendBlock> blocks = friendBlockRepository.findByBlocker(me);
        return new BlockedFriendListResponse(
                blocks.stream()
                        .map(block -> BlockedFriendListResponse.BlockedUserDto.from(block.getBlocked()))
                        .collect(Collectors.toList())
        );
    }

    public void reportUser(Long reporterId, Long reportedUserId, ReportRequest request) {
        if (reporterId.equals(reportedUserId)) {
            throw new CustomException("자기 자신은 신고할 수 없습니다.");
        }

        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        User reported = userRepository.findById(reportedUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        boolean alreadyReported = friendReportRepository
                .findByReporterAndReported(reporter, reported)
                .isPresent();

        if (alreadyReported) {
            throw new CustomException(ErrorCode.ALREADY_REPORTED);
        }

        FriendReport report = FriendReport.builder()
                .reporter(reporter)
                .reported(reported)
                .reason(request.getReason())
                .status(FriendReportStatus.PENDING)
                .reportedAt(LocalDateTime.now())
                .build();

        friendReportRepository.save(report);
    }
}