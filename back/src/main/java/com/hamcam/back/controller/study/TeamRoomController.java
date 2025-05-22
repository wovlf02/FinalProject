package com.hamcam.back.controller.study;

import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.dto.study.team.request.FocusRoomCreateRequest;
import com.hamcam.back.dto.study.team.request.QuizRoomCreateRequest;
import com.hamcam.back.dto.study.team.request.RoomPasswordCheckRequest;
import com.hamcam.back.dto.study.team.response.TeamRoomResponse;
import com.hamcam.back.service.study.TeamRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * [TeamRoomController]
 * 팀 학습 모드 방 (문제풀이/공부시간 경쟁) 생성 및 조회 API 컨트롤러
 */
@RestController
@RequestMapping("/api/study/team/rooms")
@RequiredArgsConstructor
public class TeamRoomController {

    private final TeamRoomService teamRoomService;

    /**
     * ✅ 문제풀이방(Quiz Mode) 생성
     */
    @PostMapping("/quiz/create")
    public ResponseEntity<TeamRoomResponse> createQuizRoom(
            @RequestBody QuizRoomCreateRequest request
    ) {
        TeamRoomResponse response = teamRoomService.createQuizRoom(request);
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ 공부시간 경쟁방(Focus Mode) 생성
     */
    @PostMapping("/focus/create")
    public ResponseEntity<TeamRoomResponse> createFocusRoom(
            @RequestBody FocusRoomCreateRequest request
    ) {
        TeamRoomResponse response = teamRoomService.createFocusRoom(request);
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ 방 단건 조회 (공통)
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<TeamRoomResponse> getRoomById(@PathVariable Long roomId) {
        TeamRoomResponse response = teamRoomService.getTeamRoomById(roomId);
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ 전체 팀 스터디방 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<TeamRoomResponse>> getAllRooms() {
        return ResponseEntity.ok(teamRoomService.getAllTeamRooms());
    }

    /**
     * ✅ 방 비밀번호 확인 (입장 전)
     */
    @PostMapping("/{roomId}/check-password")
    public ResponseEntity<MessageResponse> checkRoomPassword(
            @PathVariable Long roomId,
            @RequestBody RoomPasswordCheckRequest request
    ) {
        boolean success = teamRoomService.checkRoomPassword(roomId, request.getPassword());
        return ResponseEntity.ok(
                MessageResponse.of(success ? "입장 가능" : "비밀번호가 일치하지 않습니다.", success)
        );
    }

    /**
     * ✅ 방 삭제 (방장 전용)
     */
    @DeleteMapping("/{roomId}")
    public ResponseEntity<MessageResponse> deleteRoom(
            @PathVariable Long roomId,
            @RequestParam("userId") Long userId
    ) {
        teamRoomService.deleteRoom(roomId, userId);
        return ResponseEntity.ok(MessageResponse.of("스터디 방이 삭제되었습니다."));
    }
}
