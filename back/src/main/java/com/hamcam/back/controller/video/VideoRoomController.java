package com.hamcam.back.controller.video;

import com.hamcam.back.dto.video.request.CreateRoomRequest;
import com.hamcam.back.dto.video.request.JoinRoomRequest;
import com.hamcam.back.dto.video.response.VideoRoomInfoResponse;
import com.hamcam.back.service.video.VideoRoomService;
import com.hamcam.back.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ✅ WebRTC 기반 팀 학습방 REST API Controller
 */
@RestController
@RequestMapping("/api/video-room")
@RequiredArgsConstructor
public class VideoRoomController {

    private final VideoRoomService videoRoomService;

    /**
     * ✅ 방 생성
     */
    @PostMapping("/create")
    public ResponseEntity<VideoRoomInfoResponse> createRoom(
            @RequestBody CreateRoomRequest request,
            HttpServletRequest httpRequest
    ) {
        Long userId = SessionUtil.getUserId(httpRequest);
        VideoRoomInfoResponse response = videoRoomService.createRoom(request, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ 방 입장
     */
    @PostMapping("/join")
    public ResponseEntity<VideoRoomInfoResponse> joinRoom(
            @RequestBody JoinRoomRequest request,
            HttpServletRequest httpRequest
    ) {
        Long userId = SessionUtil.getUserId(httpRequest);
        VideoRoomInfoResponse response = videoRoomService.joinRoom(request, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ 방 정보 조회
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<VideoRoomInfoResponse> getRoomInfo(
            @PathVariable Long roomId
    ) {
        VideoRoomInfoResponse response = videoRoomService.getRoomInfo(roomId);
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ 방 종료 (방장만 가능)
     */
    @PostMapping("/{roomId}/end")
    public ResponseEntity<Void> endRoom(
            @PathVariable Long roomId,
            HttpServletRequest httpRequest
    ) {
        Long userId = SessionUtil.getUserId(httpRequest);
        videoRoomService.endRoom(roomId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * ✅ 내가 참여 중인 방 리스트
     */
    @GetMapping("/my")
    public ResponseEntity<List<VideoRoomInfoResponse>> getMyRooms(
            HttpServletRequest httpRequest
    ) {
        Long userId = SessionUtil.getUserId(httpRequest);
        List<VideoRoomInfoResponse> response = videoRoomService.getRoomsByUser(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ 전체 활성화된 방 리스트
     */
    @GetMapping("/all")
    public ResponseEntity<List<VideoRoomInfoResponse>> getAllRooms() {
        List<VideoRoomInfoResponse> response = videoRoomService.getAllActiveRooms();
        return ResponseEntity.ok(response);
    }
}
