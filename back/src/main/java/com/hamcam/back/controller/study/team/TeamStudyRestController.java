package com.hamcam.back.controller.study.team;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hamcam.back.dto.study.team.rest.request.TeamRoomCreateRequest;
import com.hamcam.back.dto.study.team.rest.request.TeamRoomDetailRequest;
import com.hamcam.back.dto.study.team.rest.request.TeamRoomPostFailureRequest;
import com.hamcam.back.dto.study.team.rest.response.ProblemSummary;
import com.hamcam.back.dto.study.team.rest.response.TeamRoomDetailResponse;
import com.hamcam.back.dto.study.team.rest.response.TeamRoomSimpleInfo;
import com.hamcam.back.dto.study.team.rest.response.LiveKitTokenResponse;
import com.hamcam.back.service.study.team.rest.TeamStudyRestService;
import com.hamcam.back.service.livekit.LiveKitService;
import com.hamcam.back.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/study/team")
@RequiredArgsConstructor
public class TeamStudyRestController {

    private final TeamStudyRestService teamStudyRestService;
    private final LiveKitService liveKitService;

    /**
     * ✅ 팀방 생성 (문제풀이방 or 공부시간 경쟁방)
     */

    @PostMapping("/create")
    public ResponseEntity<Long> createRoom(@RequestBody TeamRoomCreateRequest request, HttpServletRequest httpRequest) {
        Long userId = SessionUtil.getUserId(httpRequest);
        Long roomId = teamStudyRestService.createRoom(request, userId);
        return ResponseEntity.ok(roomId);
    }


    /**
     * ✅ 나의 팀방 목록 조회
     */
    @PostMapping("/my")
    public ResponseEntity<List<TeamRoomSimpleInfo>> getMyRooms(HttpServletRequest httpRequest) {
        Long userId = SessionUtil.getUserId(httpRequest);
        List<TeamRoomSimpleInfo> rooms = teamStudyRestService.getMyRooms(userId);
        return ResponseEntity.ok(rooms);
    }

    /**
     * ✅ 전체 팀방 목록 조회
     */
    @GetMapping("/all")
    public ResponseEntity<List<TeamRoomSimpleInfo>> getAllRooms(HttpServletRequest httpRequest) {
        Long userId = SessionUtil.getUserId(httpRequest);
        List<TeamRoomSimpleInfo> rooms = teamStudyRestService.getAllRooms(userId);
        return ResponseEntity.ok(rooms);
    }

    /**
     * ✅ 특정 유형(QUIZ / FOCUS) 팀방 목록 조회
     */
    @GetMapping("/type")
    public ResponseEntity<List<TeamRoomSimpleInfo>> getRoomsByType(
            @RequestParam String roomType,
            HttpServletRequest httpRequest
    ) {
        Long userId = SessionUtil.getUserId(httpRequest);
        List<TeamRoomSimpleInfo> filtered = teamStudyRestService.getRoomsByType(userId, roomType);
        return ResponseEntity.ok(filtered);
    }

    /**
     * ✅ 내가 속한 팀방 중 특정 유형 필터링
     */
    @GetMapping("/my/type")
    public ResponseEntity<List<TeamRoomSimpleInfo>> getMyRoomsByType(
            @RequestParam String roomType,
            HttpServletRequest httpRequest
    ) {
        Long userId = SessionUtil.getUserId(httpRequest);
        List<TeamRoomSimpleInfo> myFiltered = teamStudyRestService.getMyRoomsByType(userId, roomType);
        return ResponseEntity.ok(myFiltered);
    }


    /**
     * ✅ 특정 팀방 상세 조회
     */
    @PostMapping("/detail")
    public ResponseEntity<TeamRoomDetailResponse> getRoomDetail(@RequestBody TeamRoomDetailRequest request, HttpServletRequest httpRequest) {
        Long userId = SessionUtil.getUserId(httpRequest);
        TeamRoomDetailResponse detail = teamStudyRestService.getRoomDetail(request.getRoomId(), userId);
        return ResponseEntity.ok(detail);
    }

    /**
     * ✅ 파일 업로드 (로컬 저장)
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestPart MultipartFile file, HttpServletRequest httpRequest) {
        Long userId = SessionUtil.getUserId(httpRequest);
        String path = teamStudyRestService.saveFile(file, userId);
        return ResponseEntity.ok(path);
    }

    /**
     * ✅ 실패한 문제를 커뮤니티 게시글로 등록
     */
    @PostMapping("/post-failure")
    public ResponseEntity<Void> postFailureQuestion(@RequestBody TeamRoomPostFailureRequest request, HttpServletRequest httpRequest) {
        Long userId = SessionUtil.getUserId(httpRequest);
        teamStudyRestService.postFailureToCommunity(request, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * ✅ LiveKit 접속용 JWT 토큰 발급
     */
    @GetMapping("/livekit-token")
    public ResponseEntity<LiveKitTokenResponse> getLivekitToken(@RequestParam String roomName, HttpServletRequest httpRequest) {
        Long userId = SessionUtil.getUserId(httpRequest);
        String identity = String.valueOf(userId);
        String token = liveKitService.createAccessToken(identity, roomName);
        return ResponseEntity.ok(new LiveKitTokenResponse(token));
    }

    /**
     * ✅ 팀방 삭제 (방장만 가능)
     */
    @DeleteMapping("/delete/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long roomId, HttpServletRequest request) {
        Long userId = SessionUtil.getUserId(request);
        teamStudyRestService.deleteRoom(roomId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * ✅ 팀방 입장 처리 (participant insert)
     */
    @PostMapping("/enter")
    public ResponseEntity<Void> enterRoom(@RequestParam Long roomId, HttpServletRequest request) {
        Long userId = SessionUtil.getUserId(request);
        teamStudyRestService.enterRoom(roomId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * ✅ 파일 목록 조회 (해당 팀방의 업로드된 파일 리스트)
     */
    @GetMapping("/files")
    public ResponseEntity<List<String>> getUploadedFiles(@RequestParam Long roomId) {
        List<String> fileList = teamStudyRestService.getUploadedFiles(roomId);
        return ResponseEntity.ok(fileList);
    }

    /**
     * ✅ 문제 목록 필터링 조회 (QUIZ 모드에서 문제 선택 시)
     */
    @GetMapping("/problems")
    public ResponseEntity<List<ProblemSummary>> getProblems(
            @RequestParam String subject,
            @RequestParam Integer grade,
            @RequestParam Integer month,
            @RequestParam String difficulty
    ) {
        List<ProblemSummary> problems = teamStudyRestService.getFilteredProblems(subject, grade, month, difficulty);
        return ResponseEntity.ok(problems);
    }
}
