package com.hamcam.back.controller.study.team;

import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.dto.study.team.response.*;
import com.hamcam.back.dto.study.team.rest.request.TeamRoomCreateRequest;
import com.hamcam.back.dto.study.team.rest.request.TeamRoomDeleteRequest;
import com.hamcam.back.dto.study.team.rest.request.TeamRoomDetailRequest;
import com.hamcam.back.dto.study.team.rest.request.TeamRoomUserRequest;
import com.hamcam.back.dto.study.team.rest.response.TeamRoomDetailResponse;
import com.hamcam.back.service.study.team.TeamStudyService;
import com.hamcam.back.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/study/team")
@RequiredArgsConstructor
public class TeamStudyRestController {

    private final TeamStudyService teamStudyService;

    /** ✅ 팀방 생성 (Quiz / Focus 공통) */
    @PostMapping("/create")
    public ResponseEntity<MessageResponse> createRoom(
            @RequestBody TeamRoomCreateRequest request,
            HttpServletRequest httpRequest
    ) {
        Long userId = SessionUtil.getUserId(httpRequest);
        Long roomId = teamStudyService.createRoom(request, userId);
        return ResponseEntity.ok(MessageResponse.of("✅ 팀방이 생성되었습니다.", roomId));
    }

    /** ✅ 팀방 입장 */
    @PostMapping("/enter")
    public ResponseEntity<MessageResponse> enterRoom(
            @RequestBody TeamRoomUserRequest request,
            HttpServletRequest httpRequest
    ) {
        Long userId = SessionUtil.getUserId(httpRequest);
        teamStudyService.enterRoom(request.getRoomId(), userId);
        return ResponseEntity.ok(MessageResponse.of("✅ 팀방에 입장했습니다."));
    }

    /** ✅ 팀방 나가기 */
    @PostMapping("/leave")
    public ResponseEntity<MessageResponse> leaveRoom(
            @RequestBody TeamRoomUserRequest request,
            HttpServletRequest httpRequest
    ) {
        Long userId = SessionUtil.getUserId(httpRequest);
        teamStudyService.leaveRoom(request.getRoomId(), userId);
        return ResponseEntity.ok(MessageResponse.of("👋 팀방에서 퇴장했습니다."));
    }

    /** ✅ 팀방 삭제 (종료) */
    @PostMapping("/delete")
    public ResponseEntity<MessageResponse> deleteRoom(
            @RequestBody TeamRoomDeleteRequest request,
            HttpServletRequest httpRequest
    ) {
        Long userId = SessionUtil.getUserId(httpRequest);
        teamStudyService.deleteRoom(request.getRoomId(), userId);
        return ResponseEntity.ok(MessageResponse.of("🗑️ 팀방이 삭제되었습니다."));
    }

    /** ✅ 팀방 상세 조회 */
    @PostMapping("/detail")
    public ResponseEntity<TeamRoomDetailResponse> getRoomDetail(
            @RequestBody TeamRoomDetailRequest request,
            HttpServletRequest httpRequest
    ) {
        Long userId = SessionUtil.getUserId(httpRequest);
        TeamRoomDetailResponse response = teamStudyService.getRoomDetail(request.getRoomId(), userId);
        return ResponseEntity.ok(response);
    }

    /** ✅ 나의 팀방 목록 조회 */
    @PostMapping("/my")
    public ResponseEntity<TeamRoomListResponse> getMyRooms(HttpServletRequest httpRequest) {
        Long userId = SessionUtil.getUserId(httpRequest);
        TeamRoomListResponse response = teamStudyService.getMyRoomList(userId);
        return ResponseEntity.ok(response);
    }

    /** ✅ 전체 활성화된 팀방 목록 조회 */
    @PostMapping("/list")
    public ResponseEntity<TeamRoomListResponse> getAllActiveRooms() {
        TeamRoomListResponse response = teamStudyService.getAllActiveRoomList();
        return ResponseEntity.ok(response);
    }

}
