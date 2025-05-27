// src/main/java/com/hamcam/back/controller/video/VideoRoomController.java
package com.hamcam.back.controller.video;

import com.hamcam.back.dto.video.request.VideoRoomCreateRequest;
import com.hamcam.back.dto.video.request.VideoRoomUserRequest;
import com.hamcam.back.dto.video.response.VideoRoomResponse;
import com.hamcam.back.entity.video.VideoRoom;
import com.hamcam.back.service.video.VideoRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/video")
@RequiredArgsConstructor
public class VideoRoomController {
    private final VideoRoomService svc;

    /** 1) 방 생성 **/
    @PostMapping("/rooms")
    public ResponseEntity<VideoRoomResponse> create(
            @RequestBody VideoRoomCreateRequest req
    ) {
        VideoRoom room = svc.createRoom(
            req.getHostId(),
            req.getTeamId(),
            req.getTitle(),
            req.getType(),
            req.getMaxParticipants(),
            req.getPassword(),
            req.getTargetTime()
        );
        return ResponseEntity.ok(VideoRoomResponse.fromEntity(room));
    }

    /** 2) 팀별 방 목록 조회 **/
    @GetMapping("/rooms")
    public ResponseEntity<List<VideoRoomResponse>> listByTeam(
            @RequestParam Long teamId
    ) {
        List<VideoRoom> rooms = svc.getRoomsByTeam(teamId);
        List<VideoRoomResponse> dtoList = rooms.stream()
                .map(VideoRoomResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    /** 3) 방 참가 **/
    @PostMapping("/rooms/join")
    public ResponseEntity<Void> join(
            @RequestBody VideoRoomUserRequest req
    ) {
        svc.joinRoom(req.getRoomId(), req.getUserId());
        return ResponseEntity.ok().build();
    }

    /** 4) 방 나가기 **/
    @PostMapping("/rooms/leave")
    public ResponseEntity<Void> leave(
            @RequestBody VideoRoomUserRequest req
    ) {
        svc.leaveRoom(req.getRoomId(), req.getUserId());
        return ResponseEntity.ok().build();
    }

    /** 5) 참여자 수 조회 **/
    @GetMapping("/rooms/{roomId}/count")
    public ResponseEntity<Long> count(@PathVariable Integer roomId) {
        return ResponseEntity.ok(svc.getParticipantCount(roomId));
    }
}
