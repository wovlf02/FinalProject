package com.hamcam.back.controller.community.friend;

import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.dto.community.friend.request.*;
import com.hamcam.back.dto.community.friend.response.*;
import com.hamcam.back.service.community.friend.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * [FriendController]
 * 친구 관련 REST API 컨트롤러
 */
@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    /** ✅ 친구 요청 전송 */
    @PostMapping("/request")
    public ResponseEntity<MessageResponse> sendFriendRequest(@RequestBody FriendRequestSendRequest request) {
        friendService.sendFriendRequest(request);
        return ResponseEntity.ok(MessageResponse.of("친구 요청이 전송되었습니다."));
    }

    /** ✅ 친구 요청 수락 */
    @PostMapping("/request/accept")
    public ResponseEntity<MessageResponse> acceptFriendRequest(@RequestBody FriendAcceptRequest request) {
        friendService.acceptFriendRequest(request);
        return ResponseEntity.ok(MessageResponse.of("친구 요청을 수락했습니다."));
    }

    /** ✅ 친구 요청 거절 */
    @PostMapping("/request/reject")
    public ResponseEntity<MessageResponse> rejectFriendRequest(@RequestBody FriendRejectRequest request) {
        friendService.rejectFriendRequest(request);
        return ResponseEntity.ok(MessageResponse.of("친구 요청을 거절했습니다."));
    }

    /** ✅ 친구 요청 취소 */
    @PostMapping("/request/cancel")
    public ResponseEntity<MessageResponse> cancelSentFriendRequest(@RequestBody FriendCancelRequest request) {
        friendService.cancelSentFriendRequest(request);
        return ResponseEntity.ok(MessageResponse.of("친구 요청을 취소했습니다."));
    }

    /** ✅ 받은 친구 요청 목록 조회 */
    @PostMapping("/requests")
    public ResponseEntity<FriendRequestListResponse> getReceivedRequests(@RequestBody FriendBaseRequest request) {
        return ResponseEntity.ok(friendService.getReceivedFriendRequests(request));
    }

    /** ✅ 보낸 친구 요청 목록 조회 */
    @PostMapping("/requests/sent")
    public ResponseEntity<SentFriendRequestListResponse> getSentRequests(@RequestBody FriendBaseRequest request) {
        return ResponseEntity.ok(friendService.getSentFriendRequests(request));
    }

    /** ✅ 친구 목록 조회 */
    @PostMapping("/list")
    public ResponseEntity<FriendListResponse> getFriendList(@RequestBody FriendBaseRequest request) {
        return ResponseEntity.ok(friendService.getFriendList(request));
    }

    /** ✅ 친구 삭제 */
    @PostMapping("/delete")
    public ResponseEntity<MessageResponse> deleteFriend(@RequestBody FriendDeleteRequest request) {
        friendService.deleteFriend(request);
        return ResponseEntity.ok(MessageResponse.of("친구가 삭제되었습니다."));
    }

    /** ✅ 사용자 차단 */
    @PostMapping("/block")
    public ResponseEntity<MessageResponse> blockUser(@RequestBody FriendBlockRequest request) {
        friendService.blockUser(request);
        return ResponseEntity.ok(MessageResponse.of("해당 사용자를 차단하였습니다."));
    }

    /** ✅ 차단 해제 */
    @PostMapping("/unblock")
    public ResponseEntity<MessageResponse> unblockUser(@RequestBody FriendBlockRequest request) {
        friendService.unblockUser(request);
        return ResponseEntity.ok(MessageResponse.of("차단을 해제하였습니다."));
    }

    /** ✅ 차단 목록 조회 */
    @PostMapping("/blocked")
    public ResponseEntity<BlockedFriendListResponse> getBlockedUsers(@RequestBody FriendBaseRequest request) {
        return ResponseEntity.ok(friendService.getBlockedUsers(request));
    }

    /** ✅ 닉네임으로 사용자 검색 */
    @PostMapping("/search")
    public ResponseEntity<FriendSearchResponse> searchUsersByNickname(@RequestBody FriendSearchRequest request) {
        return ResponseEntity.ok(friendService.searchUsersByNickname(request));
    }

    /** ✅ 사용자 신고 */
    @PostMapping("/report")
    public ResponseEntity<MessageResponse> reportUser(@RequestBody FriendReportRequest request) {
        friendService.reportUser(request);
        return ResponseEntity.ok(MessageResponse.of("해당 사용자가 신고되었습니다."));
    }
}
