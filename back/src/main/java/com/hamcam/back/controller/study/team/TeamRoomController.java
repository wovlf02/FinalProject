package com.hamcam.back.controller.study.team;

import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.dto.study.team.request.*;
import com.hamcam.back.dto.study.team.response.TeamRoomResponse;
import com.hamcam.back.service.study.team.TeamRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * [TeamRoomController]
 * 팀 학습방(문제풀이방, 공부시간 경쟁방) 관련 REST API 컨트롤러
 * - 생성, 상세조회, 목록조회, 상태 전환, 실패 업로드 등
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/team-rooms")
public class TeamRoomController {

    private final TeamRoomService teamRoomService;
    private final SimpMessagingTemplate messagingTemplate; // ✅ WebSocket 전송용

    /** 문제풀이방 생성 */
    @PostMapping("/quiz/create")
    public ResponseEntity<TeamRoomResponse> createQuizRoom(@RequestBody TeamRoomCreateRequest request) {
        return ResponseEntity.ok(teamRoomService.createQuizRoom(request));
    }

    /** 공부시간 경쟁방 생성 */
    @PostMapping("/focus/create")
    public ResponseEntity<TeamRoomResponse> createFocusRoom(@RequestBody TeamRoomCreateRequest request) {
        return ResponseEntity.ok(teamRoomService.createFocusRoom(request));
    }

    /** 방 상세 조회 */
    @PostMapping("/detail")
    public ResponseEntity<TeamRoomResponse> getRoomDetail(@RequestBody TeamRoomDetailRequest request) {
        return ResponseEntity.ok(teamRoomService.getTeamRoomById(request));
    }

    /** 전체 팀방 목록 조회 */
    @PostMapping("/list")
    public ResponseEntity<List<TeamRoomResponse>> getAllRooms(@RequestBody TeamRoomListRequest request) {
        return ResponseEntity.ok(teamRoomService.getAllTeamRooms(request));
    }

    /** 비밀번호 확인 */
    @PostMapping("/password/check")
    public ResponseEntity<MessageResponse> checkPassword(@RequestBody TeamRoomPasswordRequest request) {
        boolean matched = teamRoomService.checkRoomPassword(request);
        String message = matched ? "비밀번호가 일치합니다." : "비밀번호가 일치하지 않습니다.";
        return ResponseEntity.ok(MessageResponse.of(message));
    }

    /** 문제풀이 시작 요청 (방장) */
    @PostMapping("/quiz/start")
    public ResponseEntity<MessageResponse> startQuiz(@RequestBody TeamRoomUserRequest request) {
        teamRoomService.startQuizSession(request);

        // ✅ WebSocket 알림 전송
        messagingTemplate.convertAndSend("/sub/teamroom/" + request.getRoomId() + "/quiz/start",
                MessageResponse.of("문제풀이가 시작되었습니다."));

        return ResponseEntity.ok(MessageResponse.of("문제풀이 세션이 시작되었습니다."));
    }

    /** 문제풀이 종료 */
    @PostMapping("/quiz/terminate")
    public ResponseEntity<MessageResponse> terminateQuiz(@RequestBody TeamRoomUserRequest request) {
        teamRoomService.terminateQuizSession(request);

        // ✅ WebSocket 알림 전송
        messagingTemplate.convertAndSend("/sub/teamroom/" + request.getRoomId() + "/quiz/terminate",
                MessageResponse.of("문제풀이 세션이 종료되었습니다."));

        return ResponseEntity.ok(MessageResponse.of("문제풀이 세션이 종료되었습니다."));
    }

    /** 실패한 문제를 커뮤니티에 업로드 */
    @PostMapping("/quiz/upload-post")
    public ResponseEntity<MessageResponse> uploadFailedQuestion(@RequestBody TeamRoomUnsolvedPostRequest request) {
        teamRoomService.uploadUnsolvedQuestionPost(request);

        // ✅ WebSocket 알림 전송
        messagingTemplate.convertAndSend("/sub/teamroom/" + request.getRoomId() + "/quiz/upload",
                MessageResponse.of("문제가 커뮤니티에 업로드되었습니다."));

        return ResponseEntity.ok(MessageResponse.of("문제가 커뮤니티에 업로드되었습니다."));
    }

    /** 공부시간 경쟁방 종료 및 랭킹 출력 */
    @PostMapping("/focus/complete")
    public ResponseEntity<MessageResponse> completeFocusSession(@RequestBody TeamRoomUserRequest request) {
        teamRoomService.completeFocusSession(request);

        // ✅ WebSocket 알림 전송
        messagingTemplate.convertAndSend("/sub/teamroom/" + request.getRoomId() + "/focus/complete",
                MessageResponse.of("공부시간 경쟁방이 종료되었습니다."));

        return ResponseEntity.ok(MessageResponse.of("공부시간 경쟁방이 종료되었습니다."));
    }
}
