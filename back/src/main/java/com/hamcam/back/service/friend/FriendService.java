package com.hamcam.back.service.friend;

import com.hamcam.back.dto.friend.request.*;
import com.hamcam.back.dto.friend.response.*;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.friend.Friend;
import com.hamcam.back.entity.friend.FriendBlock;
import com.hamcam.back.entity.friend.FriendRequest;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.friend.FriendBlockRepository;
import com.hamcam.back.repository.friend.FriendRepository;
import com.hamcam.back.repository.friend.FriendRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 친구 관련 서비스
 * <p>
 * 친구 요청/수락/삭제, 차단, 검색, 목록 조회 등 모든 기능을 처리합니다.
 */
@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final FriendBlockRepository friendBlockRepository;
    private final UserRepository userRepository;

    private Long getCurrentUserId() {
        return 1L; // 인증 시스템 연동 시 변경
    }

    private User getCurrentUser() {
        return User.builder().id(getCurrentUserId()).build();
    }

    /**
     * 친구 요청 전송
     */
    public void sendFriendRequest(FriendRequest request) {
        User sender = getCurrentUser();
        User receiver = userRepository.findById(request.getReceiver().getId())
                .orElseThrow(() -> new IllegalArgumentException("대상 사용자가 존재하지 않습니다."));

        // 자신에게 요청 불가
        if (sender.getId().equals(receiver.getId()))
            throw new IllegalArgumentException("자기 자신에게 친구 요청을 보낼 수 없습니다.");

        // 이미 친구인 경우
        boolean alreadyFriend = friendRepository.existsByUserAndFriend(sender, receiver) ||
                friendRepository.existsByUserAndFriend(receiver, sender);
        if (alreadyFriend)
            throw new IllegalStateException("이미 친구인 사용자입니다.");

        // 중복 요청 방지
        boolean alreadyRequested = friendRequestRepository
                .findBySenderAndReceiver(sender, receiver)
                .isPresent();
        if (alreadyRequested)
            throw new IllegalStateException("이미 친구 요청을 보냈습니다.");

        FriendRequest friendRequest = FriendRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .build();
        friendRequestRepository.save(friendRequest);
    }

    /**
     * 친구 요청 수락
     */
    public void acceptFriendRequest(FriendAcceptRequest request) {
        User receiver = getCurrentUser();
        User sender = userRepository.findById(request.getRequestId())
                .orElseThrow(() -> new IllegalArgumentException("보낸 사용자가 존재하지 않습니다."));

        FriendRequest friendRequest = friendRequestRepository
                .findBySenderAndReceiver(sender, receiver)
                .orElseThrow(() -> new IllegalArgumentException("친구 요청이 존재하지 않습니다."));

        friendRepository.save(Friend.builder().user(sender).friend(receiver).build());
        friendRepository.save(Friend.builder().user(receiver).friend(sender).build());
        friendRequestRepository.delete(friendRequest);
    }

    /**
     * 친구 요청 거절
     */
    public void rejectFriendRequest(FriendRejectRequest request) {
        User receiver = getCurrentUser();
        User sender = userRepository.findById(request.getRequestId())
                .orElseThrow(() -> new IllegalArgumentException("보낸 사용자가 존재하지 않습니다."));

        FriendRequest friendRequest = friendRequestRepository
                .findBySenderAndReceiver(sender, receiver)
                .orElseThrow(() -> new IllegalArgumentException("친구 요청이 존재하지 않습니다."));

        friendRequestRepository.delete(friendRequest);
    }

    /**
     * 친구 목록 조회
     */
    public FriendListResponse getFriendList() {
        User me = getCurrentUser();
        List<Friend> friends = friendRepository.findAllFriendsOfUser(me);

        return new FriendListResponse(
                friends.stream()
                        .map(f -> f.getUser().equals(me) ? f.getFriend() : f.getUser())
                        .map(FriendListResponse.FriendDto::from)
                        .collect(Collectors.toList())
        );
    }


    /**
     * 받은 친구 요청 목록 조회
     */
    public FriendRequestListResponse getReceivedFriendRequests() {
        User me = getCurrentUser();
        List<FriendRequest> requests = friendRequestRepository.findByReceiver(me);
        return new FriendRequestListResponse(
                requests.stream()
                        .map(FriendRequestListResponse.FriendRequestDto::from)
                        .collect(Collectors.toList())
        );
    }

    /**
     * 사용자 닉네임으로 검색
     */
    public FriendSearchResponse searchUsersByNickname(String nickname) {
        List<User> users = userRepository.findByNicknameContaining(nickname);
        return new FriendSearchResponse(
                users.stream()
                        .map(FriendSearchResponse.UserSearchResult::from)
                        .collect(Collectors.toList())
        );
    }

    /**
     * 친구 삭제
     */
    public void deleteFriend(Long friendId) {
        User me = getCurrentUser();
        User target = userRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Optional<Friend> friend1 = friendRepository.findByUserAndFriend(me, target);
        Optional<Friend> friend2 = friendRepository.findByUserAndFriend(target, me);

        friend1.ifPresent(friendRepository::delete);
        friend2.ifPresent(friendRepository::delete);
    }

    /**
     * 사용자 차단
     */
    public void blockUser(Long userId) {
        User me = getCurrentUser();
        User target = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("차단 대상 사용자가 존재하지 않습니다."));

        if (me.getId().equals(userId))
            throw new IllegalArgumentException("자기 자신은 차단할 수 없습니다.");

        boolean alreadyBlocked = friendBlockRepository.findByBlockerAndBlocked(me, target).isPresent();
        if (!alreadyBlocked) {
            friendBlockRepository.save(FriendBlock.builder()
                    .blocker(me)
                    .blocked(target)
                    .build());
        }

        // 친구 상태였다면 삭제
        deleteFriend(userId);
    }

    /**
     * 차단 해제
     */
    public void unblockUser(Long userId) {
        User me = getCurrentUser();
        User target = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        friendBlockRepository.findByBlockerAndBlocked(me, target)
                .ifPresent(friendBlockRepository::delete);
    }

    /**
     * 내가 차단한 사용자 목록
     */
    public BlockedFriendListResponse getBlockedUsers() {
        User me = getCurrentUser();
        List<FriendBlock> blocks = friendBlockRepository.findByBlocker(me);
        return new BlockedFriendListResponse(
                blocks.stream()
                        .map(block -> BlockedFriendListResponse.BlockedUserDto.from(block.getBlocked()))
                        .collect(Collectors.toList())
        );
    }
}
