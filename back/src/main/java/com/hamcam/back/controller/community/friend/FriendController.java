package com.hamcam.back.controller.community.friend;

import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.dto.community.friend.request.*;
import com.hamcam.back.dto.community.friend.response.*;
import com.hamcam.back.service.community.friend.FriendService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @PostMapping("/request")
    public ResponseEntity<MessageResponse> sendFriendRequest(
            @RequestBody FriendRequestSendRequest request,
            HttpServletRequest httpRequest
    ) {
        friendService.sendFriendRequest(request, httpRequest);
        return ResponseEntity.ok(MessageResponse.of("친구 요청이 전송되었습니다."));
    }

    @PostMapping("/request/accept")
    public ResponseEntity<MessageResponse> acceptFriendRequest(
            @RequestBody FriendAcceptRequest request,
            HttpServletRequest httpRequest
    ) {
        friendService.acceptFriendRequest(request, httpRequest);
        return ResponseEntity.ok(MessageResponse.of("친구 요청을 수락했습니다."));
    }

    @PostMapping("/request/reject")
    public ResponseEntity<MessageResponse> rejectFriendRequest(
            @RequestBody FriendRejectRequest request,
            HttpServletRequest httpRequest
    ) {
        friendService.rejectFriendRequest(request, httpRequest);
        return ResponseEntity.ok(MessageResponse.of("친구 요청을 거절했습니다."));
    }

    @PostMapping("/request/cancel")
    public ResponseEntity<MessageResponse> cancelSentFriendRequest(
            @RequestBody FriendCancelRequest request,
            HttpServletRequest httpRequest
    ) {
        friendService.cancelSentFriendRequest(request, httpRequest);
        return ResponseEntity.ok(MessageResponse.of("친구 요청을 취소했습니다."));
    }

    @GetMapping("/requests")
    public ResponseEntity<FriendRequestListResponse> getReceivedRequests(HttpServletRequest httpRequest) {
        return ResponseEntity.ok(friendService.getReceivedFriendRequests(httpRequest));
    }

    @PostMapping("/requests/sent")
    public ResponseEntity<SentFriendRequestListResponse> getSentRequests(HttpServletRequest httpRequest) {
        return ResponseEntity.ok(friendService.getSentFriendRequests(httpRequest));
    }

    @GetMapping("/list")
    public ResponseEntity<FriendListResponse> getFriendList(HttpServletRequest httpRequest) {
        return ResponseEntity.ok(friendService.getOnlineOfflineFriendList(httpRequest));
    }

    @PostMapping("/delete")
    public ResponseEntity<MessageResponse> deleteFriend(
            @RequestBody FriendDeleteRequest request,
            HttpServletRequest httpRequest
    ) {
        friendService.deleteFriend(request, httpRequest);
        return ResponseEntity.ok(MessageResponse.of("친구가 삭제되었습니다."));
    }

    @PostMapping("/block")
    public ResponseEntity<MessageResponse> blockUser(
            @RequestBody FriendBlockRequest request,
            HttpServletRequest httpRequest
    ) {
        friendService.blockUser(request, httpRequest);
        return ResponseEntity.ok(MessageResponse.of("해당 사용자를 차단하였습니다."));
    }

    @PostMapping("/unblock")
    public ResponseEntity<MessageResponse> unblockUser(
            @RequestBody FriendBlockRequest request,
            HttpServletRequest httpRequest
    ) {
        friendService.unblockUser(request, httpRequest);
        return ResponseEntity.ok(MessageResponse.of("차단을 해제하였습니다."));
    }

    @GetMapping("/blocked")
    public ResponseEntity<BlockedFriendListResponse> getBlockedUsers(HttpServletRequest httpRequest) {
        return ResponseEntity.ok(friendService.getBlockedUsers(httpRequest));
    }

    @PostMapping("/search")
    public ResponseEntity<FriendSearchResponse> searchUsersByNickname(
            @RequestBody FriendSearchRequest request,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity.ok(friendService.searchUsersByNickname(request, httpRequest));
    }

    @PostMapping("/report")
    public ResponseEntity<MessageResponse> reportUser(
            @RequestBody FriendReportRequest request,
            HttpServletRequest httpRequest
    ) {
        friendService.reportUser(request, httpRequest);
        return ResponseEntity.ok(MessageResponse.of("해당 사용자가 신고되었습니다."));
    }
}
