package com.hamcam.back.controller.community.friend;

import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.dto.community.friend.request.*;
import com.hamcam.back.dto.community.friend.response.*;
import com.hamcam.back.dto.community.report.request.ReportRequest;
import com.hamcam.back.service.community.friend.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 친구 관련 API 컨트롤러 (userId는 모든 요청 DTO에 포함)
 */
@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    /** ✅ 친구 요청 전송 */
    @PostMapping("/request")
    public ResponseEntity<MessageResponse> sendFriendRequest(@RequestBody FriendRequestSendRequest request) {
        friendService.sendFriendRequest(request.getUserId(), request);
        return ResponseEntity.ok(MessageResponse.of("친구 요청이 전송되었습니다."));
    }

    /** ✅ 친구 요청 수락 */
    @PostMapping("/request/{requestId}/accept")
    public ResponseEntity<MessageResponse> acceptFriendRequest(
            @PathVariable Long requestId,
            @RequestBody FriendAcceptRequest request
    ) {
        friendService.acceptFriendRequest(request.getUserId(), request);
        return ResponseEntity.ok(MessageResponse.of("친구 요청을 수락했습니다."));
    }

    /** ✅ 친구 요청 거절 */
    @PostMapping("/request/{requestId}/reject")
    public ResponseEntity<MessageResponse> rejectFriendRequest(
            @PathVariable Long requestId,
            @RequestBody FriendRejectRequest request
    ) {
        friendService.rejectFriendRequest(request.getUserId(), request);
        return ResponseEntity.ok(MessageResponse.of("친구 요청을 거절했습니다."));
    }

    /** ✅ 친구 요청 취소 */
    @PostMapping("/request/{requestId}/cancel")
    public ResponseEntity<MessageResponse> cancelSentFriendRequest(
            @PathVariable Long requestId,
            @RequestBody FriendCancelRequest request
    ) {
        friendService.cancelSentFriendRequest(request.getUserId(), requestId);
        return ResponseEntity.ok(MessageResponse.of("친구 요청을 취소했습니다."));
    }

    /** ✅ 받은 친구 요청 목록 조회 */
    @PostMapping("/requests")
    public ResponseEntity<FriendRequestListResponse> getReceivedRequests(@RequestBody FriendBaseRequest request) {
        return ResponseEntity.ok(friendService.getReceivedFriendRequests(request.getUserId()));
    }

    /** ✅ 보낸 친구 요청 목록 조회 */
    @PostMapping("/requests/sent")
    public ResponseEntity<SentFriendRequestListResponse> getSentRequests(@RequestBody FriendBaseRequest request) {
        return ResponseEntity.ok(friendService.getSentFriendRequests(request.getUserId()));
    }

    /** ✅ 친구 목록 조회 */
    @PostMapping("/list")
    public ResponseEntity<FriendListResponse> getFriendList(@RequestBody FriendBaseRequest request) {
        return ResponseEntity.ok(friendService.getFriendList(request.getUserId()));
    }

    /** ✅ 친구 삭제 */
    @PostMapping("/delete/{friendId}")
    public ResponseEntity<MessageResponse> deleteFriend(
            @PathVariable Long friendId,
            @RequestBody FriendBaseRequest request
    ) {
        friendService.deleteFriend(request.getUserId(), friendId);
        return ResponseEntity.ok(MessageResponse.of("친구가 삭제되었습니다."));
    }

    /** ✅ 사용자 차단 */
    @PostMapping("/block/{targetUserId}")
    public ResponseEntity<MessageResponse> blockUser(
            @PathVariable Long targetUserId,
            @RequestBody FriendBaseRequest request
    ) {
        friendService.blockUser(request.getUserId(), targetUserId);
        return ResponseEntity.ok(MessageResponse.of("해당 사용자를 차단하였습니다."));
    }

    /** ✅ 차단 해제 */
    @PostMapping("/unblock/{targetUserId}")
    public ResponseEntity<MessageResponse> unblockUser(
            @PathVariable Long targetUserId,
            @RequestBody FriendBaseRequest request
    ) {
        friendService.unblockUser(request.getUserId(), targetUserId);
        return ResponseEntity.ok(MessageResponse.of("차단을 해제하였습니다."));
    }

    /** ✅ 차단 목록 조회 */
    @PostMapping("/blocked")
    public ResponseEntity<BlockedFriendListResponse> getBlockedUsers(@RequestBody FriendBaseRequest request) {
        return ResponseEntity.ok(friendService.getBlockedUsers(request.getUserId()));
    }

    /** ✅ 닉네임으로 사용자 검색 */
    @PostMapping("/search")
    public ResponseEntity<FriendSearchResponse> searchUsersByNickname(@RequestBody FriendSearchRequest request) {
        return ResponseEntity.ok(friendService.searchUsersByNickname(request.getUserId(), request.getNickname()));
    }

    /** ✅ 사용자 신고 */
    @PostMapping("/report/{targetUserId}")
    public ResponseEntity<MessageResponse> reportUser(
            @PathVariable Long targetUserId,
            @RequestBody FriendReportRequest request
    ) {
        friendService.reportUser(request.getUserId(), targetUserId, request.getReport());
        return ResponseEntity.ok(MessageResponse.of("해당 사용자가 신고되었습니다."));
    }
}
