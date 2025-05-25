package com.hamcam.back.service.community.friend;

import com.hamcam.back.dto.community.friend.request.*;
import com.hamcam.back.dto.community.friend.response.*;
import com.hamcam.back.dto.community.report.request.ReportRequest;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.friend.*;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.friend.*;
import com.hamcam.back.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final FriendBlockRepository friendBlockRepository;
    private final FriendReportRepository friendReportRepository;
    private final UserRepository userRepository;

    // 🔥 수정: 어떤 RedisTemplate을 쓸지 명확히 지정
    private final @Qualifier("redisTemplate") RedisTemplate<String, String> redisTemplate;

    public void sendFriendRequest(FriendRequestSendRequest request, HttpServletRequest httpRequest) {
        User sender = getSessionUser(httpRequest);
        User receiver = getUser(request.getTargetUserId());

        validateNotSelf(sender, receiver);
        validateNotAlreadyFriend(sender, receiver);
        validateNotAlreadyRequested(sender, receiver);

        friendRequestRepository.save(FriendRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .build());
    }

    public void acceptFriendRequest(FriendAcceptRequest request, HttpServletRequest httpRequest) {
        User receiver = getSessionUser(httpRequest);
        FriendRequest fr = getFriendRequestById(request.getRequestId());

        if (!fr.getReceiver().equals(receiver)) {
            throw new CustomException("요청 대상 불일치");
        }

        friendRepository.save(Friend.of(fr.getSender(), receiver));
        friendRepository.save(Friend.of(receiver, fr.getSender()));
        friendRequestRepository.delete(fr);
    }

    public void rejectFriendRequest(FriendRejectRequest request, HttpServletRequest httpRequest) {
        User receiver = getSessionUser(httpRequest);
        FriendRequest fr = getFriendRequestById(request.getRequestId());

        if (!fr.getReceiver().equals(receiver)) {
            throw new CustomException("요청 대상 불일치");
        }

        friendRequestRepository.delete(fr);
    }

    public void cancelSentFriendRequest(FriendCancelRequest request, HttpServletRequest httpRequest) {
        User sender = getSessionUser(httpRequest);
        FriendRequest fr = getFriendRequestById(request.getRequestId());

        if (!fr.getSender().equals(sender)) {
            throw new CustomException("취소 권한 없음");
        }

        friendRequestRepository.delete(fr);
    }

    public FriendRequestListResponse getReceivedFriendRequests(HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        return new FriendRequestListResponse(friendRequestRepository.findByReceiver(user).stream()
                .map(FriendRequestListResponse.FriendRequestDto::from)
                .toList());
    }

    public SentFriendRequestListResponse getSentFriendRequests(HttpServletRequest httpRequest) {
        User sender = getSessionUser(httpRequest);
        return new SentFriendRequestListResponse(friendRequestRepository.findBySender(sender).stream()
                .map(req -> SentFriendRequestListResponse.SentFriendRequestDto.builder()
                        .requestId(req.getId())
                        .receiverId(req.getReceiver().getId())
                        .receiverNickname(req.getReceiver().getNickname())
                        .receiverProfileImageUrl(req.getReceiver().getProfileImageUrl())
                        .requestedAt(req.getRequestedAt())
                        .status(req.getStatus().name())
                        .build())
                .toList());
    }

    public void deleteFriend(FriendDeleteRequest request, HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        User target = getUser(request.getTargetUserId());

        friendRepository.findByUserAndFriend(user, target).ifPresent(friendRepository::delete);
        friendRepository.findByUserAndFriend(target, user).ifPresent(friendRepository::delete);
    }

    public void blockUser(FriendBlockRequest request, HttpServletRequest httpRequest) {
        User me = getSessionUser(httpRequest);
        User target = getUser(request.getTargetUserId());

        validateNotSelf(me, target);

        if (friendBlockRepository.findByBlockerAndBlocked(me, target).isEmpty()) {
            friendBlockRepository.save(FriendBlock.builder().blocker(me).blocked(target).build());
        }

        deleteFriend(FriendDeleteRequest.builder()
                .targetUserId(target.getId())
                .build(), httpRequest);
    }

    public void unblockUser(FriendBlockRequest request, HttpServletRequest httpRequest) {
        User me = getSessionUser(httpRequest);
        User target = getUser(request.getTargetUserId());

        friendBlockRepository.findByBlockerAndBlocked(me, target).ifPresent(friendBlockRepository::delete);
    }

    public BlockedFriendListResponse getBlockedUsers(HttpServletRequest httpRequest) {
        User me = getSessionUser(httpRequest);
        return new BlockedFriendListResponse(friendBlockRepository.findByBlocker(me).stream()
                .map(block -> BlockedFriendListResponse.BlockedUserDto.from(block.getBlocked()))
                .toList());
    }

    public FriendSearchResponse searchUsersByNickname(FriendSearchRequest request, HttpServletRequest httpRequest) {
        User me = getSessionUser(httpRequest);
        List<User> users = userRepository.findByNicknameContaining(request.getNickname());

        return new FriendSearchResponse(users.stream()
                .filter(u -> !u.equals(me))
                .map(u -> new FriendSearchResponse.UserSearchResult(
                        u.getId(),
                        u.getNickname(),
                        u.getProfileImageUrl(),
                        isFriend(me, u),
                        hasSentRequest(me, u),
                        isBlocked(me, u)
                )).toList());
    }

    public void reportUser(FriendReportRequest request, HttpServletRequest httpRequest) {
        User reporter = getSessionUser(httpRequest);
        User reported = getUser(request.getTargetUserId());

        if (reporter.getId().equals(reported.getId())) {
            throw new CustomException("자기 자신은 신고할 수 없습니다.");
        }

        if (friendReportRepository.findByReporterAndReported(reporter, reported).isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_REPORTED);
        }

        friendReportRepository.save(FriendReport.builder()
                .reporter(reporter)
                .reported(reported)
                .reason(request.getReason())
                .status(FriendReportStatus.PENDING)
                .reportedAt(LocalDateTime.now())
                .build());
    }

    public FriendListResponse getOnlineOfflineFriendList(HttpServletRequest request) {
        User user = getSessionUser(request);
        List<Friend> relations = friendRepository.findAllFriendsOfUser(user);

        // 중복 제거를 위한 Set<Long> 사용
        Set<Long> seenUserIds = new HashSet<>();
        List<FriendListResponse.FriendDto> online = new ArrayList<>();
        List<FriendListResponse.FriendDto> offline = new ArrayList<>();

        for (Friend relation : relations) {
            User friend = relation.getUser().equals(user) ? relation.getFriend() : relation.getUser();

            // userId 중복 체크
            if (!seenUserIds.add(friend.getId())) continue;

            FriendListResponse.FriendDto dto = FriendListResponse.FriendDto.builder()
                    .userId(friend.getId())
                    .nickname(friend.getNickname())
                    .profileImageUrl(friend.getProfileImageUrl())
                    .build();

            boolean isOnline = redisTemplate.hasKey("ws:connected:" + friend.getId());
            if (isOnline) {
                online.add(dto);
            } else {
                offline.add(dto);
            }
        }

        return FriendListResponse.builder()
                .onlineFriends(online)
                .offlineFriends(offline)
                .build();
    }

    // ===== 내부 유틸 =====

    private User getSessionUser(HttpServletRequest request) {
        Long userId = SessionUtil.getUserId(request);
        return getUser(userId);
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private FriendRequest getFriendRequestById(Long id) {
        return friendRequestRepository.findById(id)
                .orElseThrow(() -> new CustomException("요청이 존재하지 않습니다."));
    }

    private boolean isFriend(User me, User target) {
        return friendRepository.existsByUserAndFriend(me, target) ||
                friendRepository.existsByUserAndFriend(target, me);
    }

    private boolean hasSentRequest(User me, User target) {
        return friendRequestRepository.existsBySenderAndReceiver(me, target);
    }

    private boolean isBlocked(User me, User target) {
        return friendBlockRepository.existsByBlockerAndBlocked(me, target);
    }

    private void validateNotSelf(User user, User target) {
        if (user.equals(target)) {
            throw new CustomException("자기 자신에 대해 수행할 수 없습니다.");
        }
    }

    private void validateNotAlreadyFriend(User user, User target) {
        if (isFriend(user, target)) {
            throw new CustomException("이미 친구입니다.");
        }
    }

    private void validateNotAlreadyRequested(User sender, User receiver) {
        if (hasSentRequest(sender, receiver)) {
            throw new CustomException("이미 친구 요청을 보냈습니다.");
        }
    }
}
