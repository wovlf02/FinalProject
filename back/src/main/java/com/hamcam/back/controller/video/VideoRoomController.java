package com.hamcam.back.controller.video;

import com.hamcam.back.dto.video.VideoRoomRequest;
import com.hamcam.back.dto.video.VideoRoomResponse;
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

    // 화상 채팅방 생성
    @PostMapping("/create")
    public ResponseEntity<VideoRoomResponse> createRoom(@RequestBody VideoRoomRequest request) {
        return ResponseEntity.ok(videoRoomService.createRoom(request));
    }

    // 특정 팀의 화상 채팅방 목록 조회
    @GetMapping("/rooms/{teamId}")
    public ResponseEntity<List<VideoRoomResponse>> getRoomsByTeam(@PathVariable Long teamId) {
        return ResponseEntity.ok(videoRoomService.getRoomsByTeam(teamId));
    }

    // 특정 방 정보 조회
    @GetMapping("/{roomId}")
    public ResponseEntity<VideoRoomResponse> getRoomById(@PathVariable Long roomId) {
        return ResponseEntity.ok(videoRoomService.getRoomById(roomId));
    }

    // ✅ Redis: 접속자 수 증가 (입장 시 호출)
    @PostMapping("/join/{roomId}")
    public ResponseEntity<Long> joinRoom(@PathVariable Long roomId) {
        videoRoomService.increaseUserCount(roomId);
        return ResponseEntity.ok(videoRoomService.getUserCount(roomId));
    }

    // ✅ Redis: 접속자 수 감소 (퇴장 시 호출)
    @PostMapping("/leave/{roomId}")
    public ResponseEntity<Long> leaveRoom(@PathVariable Long roomId) {
        videoRoomService.decreaseUserCount(roomId);
        return ResponseEntity.ok(videoRoomService.getUserCount(roomId));
    }

    // ✅ Redis: 현재 접속자 수 조회
    @GetMapping("/count/{roomId}")
    public ResponseEntity<Long> getUserCount(@PathVariable Long roomId) {
        return ResponseEntity.ok(videoRoomService.getUserCount(roomId));
    }
}
