package com.hamcam.back.service.community.friend;

import com.hamcam.back.dto.community.friend.request.FriendAcceptRequest;
import com.hamcam.back.dto.community.friend.request.FriendRejectRequest;
import com.hamcam.back.dto.community.friend.request.FriendRequestSendRequest;
import com.hamcam.back.dto.community.friend.response.BlockedFriendListResponse;
import com.hamcam.back.dto.community.friend.response.FriendListResponse;
import com.hamcam.back.dto.community.friend.response.FriendRequestListResponse;
import com.hamcam.back.dto.community.friend.response.FriendSearchResponse;
import com.hamcam.back.dto.community.report.request.ReportRequest;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.friend.*;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.global.security.SecurityUtil;
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
    private final SecurityUtil securityUtil;

    private User getCurrentUser() {
        return securityUtil.getCurrentUser();
    }

    private Long getCurrentUserId() {
        return securityUtil.getCurrentUserId();
    }

    public void sendFriendRequest(FriendRequestSendRequest request) {
        User sender = getCurrentUser();
        User receiver = userRepository.findById(request.getTargetUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (sender.getId().equals(receiver.getId())) {
            throw new CustomException("자기 자신에게 친구 요청을 보낼 수 없습니다.");
        }

        if (friendRepository.existsByUserAndFriend(sender, receiver) ||
                friendRepository.existsByUserAndFriend(receiver, sender)) {
            throw new CustomException("이미 친구 관계입니다.");
        }

        if (friendRequestRepository.findBySenderAndReceiver(sender, receiver).isPresent()) {
            throw new CustomException("이미 친구 요청을 보냈습니다.");
        }

        friendRequestRepository.save(FriendRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .build());
    }

    public void acceptFriendRequest(FriendAcceptRequest request) {
        User receiver = getCurrentUser();
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
        User receiver = getCurrentUser();

        // 요청 ID로 친구 요청 엔티티 조회
        FriendRequest friendRequest = friendRequestRepository.findById(request.getRequestId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)); // 요청이 없을 경우 예외

        // 요청 수신자가 현재 로그인 사용자와 일치하는지 확인
        if (!friendRequest.getReceiver().getId().equals(receiver.getId())) {
            throw new CustomException("요청 수신자와 로그인 사용자가 일치하지 않습니다.");
        }

        // 요청 삭제 (거절)
        friendRequestRepository.delete(friendRequest);
    }

    public FriendListResponse getFriendList() {
        User me = getCurrentUser();
        List<Friend> friends = friendRepository.findAllFriendsOfUser(me);

        return new FriendListResponse(
                friends.stream()
                        .map(f -> f.getUser().equals(me) ? f.getFriend() : f.getUser())
                        .distinct()
                        .map(FriendListResponse.FriendDto::from)
                        .collect(Collectors.toList())
        );
    }

    public FriendRequestListResponse getReceivedFriendRequests() {
        User me = getCurrentUser();
        List<FriendRequest> requests = friendRequestRepository.findByReceiver(me);
        return new FriendRequestListResponse(
                requests.stream()
                        .map(FriendRequestListResponse.FriendRequestDto::from)
                        .collect(Collectors.toList())
        );
    }

    public FriendSearchResponse searchUsersByNickname(String nickname) {
        User me = getCurrentUser();
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

    public void deleteFriend(Long friendId) {
        User me = getCurrentUser();
        User target = userRepository.findById(friendId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        friendRepository.findByUserAndFriend(me, target).ifPresent(friendRepository::delete);
        friendRepository.findByUserAndFriend(target, me).ifPresent(friendRepository::delete);
    }

    public void blockUser(Long userId) {
        User me = getCurrentUser();
        User target = userRepository.findById(userId)
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

        deleteFriend(userId);
    }

    public void unblockUser(Long userId) {
        User me = getCurrentUser();
        User target = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        friendBlockRepository.findByBlockerAndBlocked(me, target)
                .ifPresent(friendBlockRepository::delete);
    }

    public BlockedFriendListResponse getBlockedUsers() {
        User me = getCurrentUser();
        List<FriendBlock> blocks = friendBlockRepository.findByBlocker(me);
        return new BlockedFriendListResponse(
                blocks.stream()
                        .map(block -> BlockedFriendListResponse.BlockedUserDto.from(block.getBlocked()))
                        .collect(Collectors.toList())
        );
    }

    public void reportUser(Long reportedUserId, ReportRequest request) {
        User reporter = getCurrentUser();

        if (reporter.getId().equals(reportedUserId)) {
            throw new CustomException("자기 자신은 신고할 수 없습니다.");
        }

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
