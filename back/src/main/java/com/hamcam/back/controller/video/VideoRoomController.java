package com.hamcam.back.controller.video;

import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.dto.video.request.*;
import com.hamcam.back.dto.video.response.VideoRoomResponse;
import com.hamcam.back.service.video.VideoRoomService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * [VideoRoomController]
 * WebRTC 화상 채팅방 관련 REST API 컨트롤러 (세션 기반)
 */
@RestController
@RequestMapping("/api/video")
@RequiredArgsConstructor
public class VideoRoomController {

    private final VideoRoomService videoRoomService;

    /** ✅ 화상 채팅방 생성 (세션 기반) */
    @PostMapping("/create")
    public ResponseEntity<VideoRoomResponse> createRoom(
            @RequestBody VideoRoomCreateRequest request,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity.ok(videoRoomService.createRoom(request, httpRequest));
    }

    /** ✅ 팀 기준 화상 채팅방 목록 조회 */
    @PostMapping("/rooms")
    public ResponseEntity<List<VideoRoomResponse>> getRoomsByTeam(
            @RequestBody VideoRoomListRequest request
    ) {
        return ResponseEntity.ok(videoRoomService.getRoomsByTeam(request));
    }

    /** ✅ 화상 채팅방 상세 조회 */
    @PostMapping("/detail")
    public ResponseEntity<VideoRoomResponse> getRoomById(
            @RequestBody VideoRoomDetailRequest request
    ) {
        return ResponseEntity.ok(videoRoomService.getRoomById(request));
    }

    /** ✅ 방 입장 (접속자 수 증가 후 반환) */
    @PostMapping("/join")
    public ResponseEntity<MessageResponse> joinRoom(
            @RequestBody VideoRoomUserRequest request
    ) {
        videoRoomService.increaseUserCount(request);
        Long count = videoRoomService.getUserCount(request);
        return ResponseEntity.ok(MessageResponse.of("방에 입장했습니다.", count));
    }

    /** ✅ 방 퇴장 (접속자 수 감소 후 반환) */
    @PostMapping("/leave")
    public ResponseEntity<MessageResponse> leaveRoom(
            @RequestBody VideoRoomUserRequest request
    ) {
        videoRoomService.decreaseUserCount(request);
        Long count = videoRoomService.getUserCount(request);
        return ResponseEntity.ok(MessageResponse.of("방에서 퇴장했습니다.", count));
    }

    /** ✅ 현재 접속자 수 조회 */
    @PostMapping("/count")
    public ResponseEntity<Long> getUserCount(
            @RequestBody VideoRoomUserRequest request
    ) {
        return ResponseEntity.ok(videoRoomService.getUserCount(request));
    }
}
