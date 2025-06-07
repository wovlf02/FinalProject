package com.hamcam.back.controller.study.team;

import com.hamcam.back.dto.livekit.response.LiveKitTokenResponse;
import com.hamcam.back.dto.study.team.rest.request.*;
import com.hamcam.back.dto.study.team.rest.response.*;
import com.hamcam.back.service.study.team.rest.TeamStudyRestService;
import com.hamcam.back.service.livekit.LiveKitService;
import com.hamcam.back.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.List;

@Slf4j
@ConditionalOnProperty(name = "livekit.enabled", havingValue = "true", matchIfMissing = false)
@RestController
@RequestMapping("/api/study/team")
@RequiredArgsConstructor
public class TeamStudyRestController {

    private final TeamStudyRestService teamStudyRestService;
    private final LiveKitService liveKitService;

    /** ✅ 팀방 생성 */
    @PostMapping("/create")
    public ResponseEntity<Long> createRoom(@RequestBody TeamRoomCreateRequest request, HttpServletRequest httpRequest) {
        Long userId = extractUserId(httpRequest);
        Long roomId = teamStudyRestService.createRoom(request, userId);
        return ResponseEntity.ok(roomId);
    }

    /** ✅ 팀방 입장 */
    @PostMapping("/enter")
    public ResponseEntity<Void> enterRoom(@RequestParam Long roomId, HttpServletRequest request) {
        Long userId = extractUserId(request);
        teamStudyRestService.enterRoom(roomId, userId);
        return ResponseEntity.ok().build();
    }

    /** ✅ 팀방 삭제 (방장만 가능) */
    @DeleteMapping("/delete/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long roomId, HttpServletRequest request) {
        Long userId = extractUserId(request);
        teamStudyRestService.deleteRoom(roomId, userId);
        return ResponseEntity.ok().build();
    }

    /** ✅ 나의 팀방 목록 */
    @PostMapping("/my")
    public ResponseEntity<List<TeamRoomSimpleInfo>> getMyRooms(HttpServletRequest httpRequest) {
        Long userId = extractUserId(httpRequest);
        return ResponseEntity.ok(teamStudyRestService.getMyRooms(userId));
    }

    /** ✅ 전체 팀방 목록 */
    @GetMapping("/all")
    public ResponseEntity<List<TeamRoomSimpleInfo>> getAllRooms(HttpServletRequest httpRequest) {
        Long userId = extractUserId(httpRequest);
        return ResponseEntity.ok(teamStudyRestService.getAllRooms(userId));
    }

    /** ✅ 유형별 팀방 목록 */
    @GetMapping("/type")
    public ResponseEntity<List<TeamRoomSimpleInfo>> getRoomsByType(@RequestParam String roomType, HttpServletRequest httpRequest) {
        Long userId = extractUserId(httpRequest);
        return ResponseEntity.ok(teamStudyRestService.getRoomsByType(userId, roomType));
    }

    /** ✅ 내가 속한 팀방 중 유형 필터링 */
    @GetMapping("/my/type")
    public ResponseEntity<List<TeamRoomSimpleInfo>> getMyRoomsByType(@RequestParam String roomType, HttpServletRequest httpRequest) {
        Long userId = extractUserId(httpRequest);
        return ResponseEntity.ok(teamStudyRestService.getMyRoomsByType(userId, roomType));
    }

    /** ✅ 팀방 상세 조회 */
    @PostMapping("/detail")
    public ResponseEntity<TeamRoomDetailResponse> getRoomDetail(@RequestBody TeamRoomDetailRequest request, HttpServletRequest httpRequest) {
        Long userId = extractUserId(httpRequest);
        return ResponseEntity.ok(teamStudyRestService.getRoomDetail(request.getRoomId(), userId));
    }

    /** ✅ 파일 업로드 (로컬 저장) */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestPart MultipartFile file, HttpServletRequest httpRequest) {
        Long userId = extractUserId(httpRequest);
        return ResponseEntity.ok(teamStudyRestService.saveFile(file, userId));
    }

    /** ✅ 업로드된 파일 목록 조회 */
    @GetMapping("/files")
    public ResponseEntity<List<String>> getUploadedFiles(@RequestParam Long roomId) {
        return ResponseEntity.ok(teamStudyRestService.getUploadedFiles(roomId));
    }

    /** ✅ 실패한 문제를 커뮤니티 게시글로 등록 */
    @PostMapping("/post-failure")
    public ResponseEntity<Void> postFailureQuestion(@RequestBody TeamRoomPostFailureRequest request, HttpServletRequest httpRequest) {
        Long userId = extractUserId(httpRequest);
        teamStudyRestService.postFailureToCommunity(request, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * ✅ LiveKit 토큰 발급 (기본: 발표자 아님)
     * - roomName을 기준으로 토큰을 생성하고, presenter 권한은 false로 설정됨
     * - JWT + WebSocket URL + presenter 여부 + 만료 시각 반환
     */
    @GetMapping("/livekit-token")
    public ResponseEntity<LiveKitTokenResponse> getLivekitToken(
            @RequestParam String roomName,
            HttpServletRequest httpRequest
    ) {
        Long userId = extractUserId(httpRequest);
        String identity = String.valueOf(userId);

        // ✅ 발표자 권한 없이 토큰 발급
        boolean isPresenter = false;
        String token = liveKitService.createAccessToken(identity, roomName, isPresenter);

        long expiresAt = System.currentTimeMillis() + (60 * 60 * 1000); // 1시간 TTL
        String wsUrl = liveKitService.getWsUrl();

        return ResponseEntity.ok(new LiveKitTokenResponse(token, wsUrl, isPresenter, expiresAt));
    }

    /** ✅ 세션 유저 ID 추출 공통 메서드 */
    private Long extractUserId(HttpServletRequest request) {
        return SessionUtil.getUserId(request);
    }
}
