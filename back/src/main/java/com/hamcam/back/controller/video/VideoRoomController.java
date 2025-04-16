package com.hamcam.back.controller.video;

import com.hamcam.back.dto.video.VideoRoomResponse;
import com.hamcam.back.service.video.VideoRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/video-room")
@RequiredArgsConstructor
public class VideoRoomController {

    private final VideoRoomService videoRoomService;

    @PostMapping("/create")
    public ResponseEntity<VideoRoomResponse> createRoom(@RequestParam String title) {
        VideoRoomResponse response = videoRoomService.createRoom(title);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<List<VideoRoomResponse>> getAllRooms() {
        List<VideoRoomResponse> rooms = videoRoomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }
}
