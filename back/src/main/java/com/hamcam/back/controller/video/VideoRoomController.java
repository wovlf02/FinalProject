package com.hamcam.back.controller.video;

import com.hamcam.back.dto.video.VideoRoomRequest;
import com.hamcam.back.dto.video.VideoRoomResponse;
import com.hamcam.back.service.video.VideoRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * [VideoRoomController]
 *
 * WebRTC 기반 화상 채팅방(스터디룸) 관리 컨트롤러
 * - 방 생성, 조회
 * - Redis 기반 접속자 수 관리 (입장, 퇴장, 조회)
 */
@RestController
@RequestMapping("/api/video")
@RequiredArgsConstructor
public class VideoRoomController {

    private final VideoRoomService videoRoomService;

    /**
     * [화상 채팅방 생성]
     *
     * @param request 방 생성 요청 정보
     * @return 생성된 방 정보
     */
    @PostMapping("/create")
    public ResponseEntity<VideoRoomResponse> createRoom(@RequestBody VideoRoomRequest request) {
        VideoRoomResponse response = videoRoomService.createRoom(request);
        return ResponseEntity.ok(response);
    }

    /**
     * [팀 기준 방 목록 조회]
     *
     * @param teamId 팀 ID
     * @return 해당 팀의 모든 화상 채팅방 리스트
     */
    @GetMapping("/rooms/{teamId}")
    public ResponseEntity<List<VideoRoomResponse>> getRoomsByTeam(@PathVariable Long teamId) {
        return ResponseEntity.ok(videoRoomService.getRoomsByTeam(teamId));
    }

    /**
     * [단일 방 정보 조회]
     *
     * @param roomId 화상 채팅방 ID
     * @return 해당 방의 상세 정보
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<VideoRoomResponse> getRoomById(@PathVariable Long roomId) {
        return ResponseEntity.ok(videoRoomService.getRoomById(roomId));
    }

    /**
     * [방 입장 시 접속자 수 증가]
     * - Redis에 인원 수 증가 반영
     *
     * @param roomId 대상 방 ID
     * @return 현재 접속자 수
     */
    @PostMapping("/join/{roomId}")
    public ResponseEntity<Long> joinRoom(@PathVariable Long roomId) {
        videoRoomService.increaseUserCount(roomId);
        return ResponseEntity.ok(videoRoomService.getUserCount(roomId));
    }

    /**
     * [방 퇴장 시 접속자 수 감소]
     * - Redis에 인원 수 감소 반영
     *
     * @param roomId 대상 방 ID
     * @return 현재 접속자 수
     */
    @PostMapping("/leave/{roomId}")
    public ResponseEntity<Long> leaveRoom(@PathVariable Long roomId) {
        videoRoomService.decreaseUserCount(roomId);
        return ResponseEntity.ok(videoRoomService.getUserCount(roomId));
    }

    /**
     * [현재 접속자 수 조회]
     *
     * @param roomId 화상 채팅방 ID
     * @return Redis 기준 실시간 접속자 수
     */
    @GetMapping("/count/{roomId}")
    public ResponseEntity<Long> getUserCount(@PathVariable Long roomId) {
        return ResponseEntity.ok(videoRoomService.getUserCount(roomId));
    }
}
