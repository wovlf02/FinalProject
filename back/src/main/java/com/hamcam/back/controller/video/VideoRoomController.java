package com.hamcam.back.controller.video;

import com.hamcam.back.dto.video.request.*;
import com.hamcam.back.dto.video.response.VideoRoomResponse;
import com.hamcam.back.service.video.VideoRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/video")
@RequiredArgsConstructor
public class VideoRoomController {

    private final VideoRoomService videoRoomService;

    /** ✅ 화상 채팅방 생성 */
    @PostMapping("/create")
    public ResponseEntity<VideoRoomResponse> createRoom(@RequestBody VideoRoomCreateRequest request) {
        return ResponseEntity.ok(videoRoomService.createRoom(request));
    }

    /** ✅ 팀 기준 방 목록 조회 */
    @PostMapping("/rooms")
    public ResponseEntity<List<VideoRoomResponse>> getRoomsByTeam(@RequestBody VideoRoomListRequest request) {
        return ResponseEntity.ok(videoRoomService.getRoomsByTeam(request));
    }

    /** ✅ 단일 방 정보 조회 */
    @PostMapping("/detail")
    public ResponseEntity<VideoRoomResponse> getRoomById(@RequestBody VideoRoomDetailRequest request) {
        return ResponseEntity.ok(videoRoomService.getRoomById(request));
    }

    /** ✅ 방 입장 (접속자 수 증가) */
    @PostMapping("/join")
    public ResponseEntity<Long> joinRoom(@RequestBody VideoRoomUserRequest request) {
        videoRoomService.increaseUserCount(request);
        return ResponseEntity.ok(videoRoomService.getUserCount(request));
    }

    /** ✅ 방 퇴장 (접속자 수 감소) */
    @PostMapping("/leave")
    public ResponseEntity<Long> leaveRoom(@RequestBody VideoRoomUserRequest request) {
        videoRoomService.decreaseUserCount(request);
        return ResponseEntity.ok(videoRoomService.getUserCount(request));
    }

    /** ✅ 현재 접속자 수 조회 */
    @PostMapping("/count")
    public ResponseEntity<Long> getUserCount(@RequestBody VideoRoomUserRequest request) {
        return ResponseEntity.ok(videoRoomService.getUserCount(request));
    }
}
