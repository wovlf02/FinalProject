package com.studymate.back.controller;

import com.studymate.back.dto.FriendRequest;
import com.studymate.back.dto.FriendResponse;
import com.studymate.back.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 친구 관리 컨트롤러
 *
 * 친구 요청, 수락, 거절
 * 친구 목록 조회 및 삭제
 * 친구 차단 및 신고
 */
@RestController
@RequestMapping("/api/friend")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    // ================== 친구 요청 관리 API ==================

    /**
     * 친구 요청 전송 API
     * 사용자가 특정 사용자에게 친구 요청을 보낼 수 있음
     * @param request   친구 요청 대상 사용자 ID
     * @return  성공 시 200 OK 반환
     */
    @PostMapping("/request")
    public ResponseEntity<Void> sendFriendRequest(@RequestBody FriendRequest request) {
        friendService.sendFriendRequest(request);
        return ResponseEntity.ok().build();
    }

    /**
     * 수신된 친구 요청 목록 조회 API
     * 사용자가 받은 친구 요청 목록을 시간순으로 조회할 수 있음
     * @return  친구 요청 목록
     */
    @GetMapping("/requests")
    public ResponseEntity<List<FriendResponse>> getReceivedFriendRequests() {
        List<FriendResponse> response = friendService.getReceivedFriendRequests();
        return ResponseEntity.ok(response);
    }

    /**
     * 친구 요청 수락 API
     * 사용자가 받은 친구 요청을 수락할 수 있음
     * @param request   수락할 친구 요청 정보
     * @return  성공 시 200 OK 반환
     */
    @PostMapping("/accept")
    public ResponseEntity<Void> acceptFriendRequest(@RequestBody FriendRequest request) {
        friendService.acceptFriendRequest(request);
        return ResponseEntity.ok().build();
    }

    /**
     * 친구 요청 거절 API
     * 사용자가 받은 친구 요청을 거절할 수 있음
     * @param request   거절할 친구 요청 정보
     * @return  성공 시 200 OK 반환
     */
    @PostMapping("/reject")
    public ResponseEntity<Void> rejectFriendRequest(@RequestBody FriendRequest request) {
        friendService.rejectFriendRequest(request);
        return ResponseEntity.ok().build();
    }

    // =================== 친구 목록 및 삭제 API =====================

    /**
     * 친구 목록 조회 API
     * 사용자가 추가한 친구 목록을 조회할 수 있음
     * @return  친구 목록
     */
    @GetMapping("/list")
    public ResponseEntity<List<FriendResponse>> getFriendList() {
        List<FriendResponse> response = friendService.getFriendList();
        return ResponseEntity.ok(response);
    }

    /**
     * 친구 삭제 API
     * 사용자가 친구 관계를 해제할 수 있음
     * 삭제 즉시 양측 친구 목록에서 제거됨
     * @param friendId  삭제할 친구의 ID
     * @return  성공 시 200 OK 반환
     */
    @DeleteMapping("/{friendId}")
    public ResponseEntity<Void> deleteFriend(@PathVariable Long friendId) {
        friendService.deleteFriend(friendId);
        return ResponseEntity.ok().build();
    }

    // =================== 친구 차단 및 신고 API =====================

    /**
     * 친구 차단 API
     * 사용자가 특정 친구를 차단할 수 있음
     * 차단 즉시 해당 사용자와의 모든 상호작용이 제한됨
     * @param friendId  차단할 친구의 ID
     * @return  성공 시 200 OK 반환
     */
    @PostMapping("/{friendId}/block")
    public ResponseEntity<Void> blockFriend(@PathVariable Long friendId) {
        friendService.blockFriend(friendId);
        return ResponseEntity.ok().build();
    }

    /**
     * 친구 신고 API
     * 사용자가 부적절한 활동을 하는 친구를 신고할 수 있음
     * @param friendId  신고할 친구 ID
     * @param request   신고 요청 데이터
     * @return  성공 시 200 OK 반환
     */
    @PostMapping("/{friendId}/report")
    public ResponseEntity<Void> reportFriend(@PathVariable Long friendId, @RequestBody ReportRequest request) {
        friendService.reportFriend(friendId, request);
        return ResponseEntity.ok().build();
    }
}
