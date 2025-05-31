package com.hamcam.back.controller.livekit;

import com.hamcam.back.dto.livekit.request.LiveKitTokenRequest;
import com.hamcam.back.dto.livekit.response.LiveKitTokenResponse;
import com.hamcam.back.service.livekit.LiveKitService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/livekit")
@RequiredArgsConstructor
public class LiveKitController {

    private final LiveKitService liveKitService;

    @PostMapping("/token")
    public ResponseEntity<LiveKitTokenResponse> getLiveKitToken(
            @RequestBody LiveKitTokenRequest request
    ) {
        String identity = request.getUserId();
        String roomName = request.getRoomName();

        // 디버깅 로그
        System.out.println("요청 userId: " + identity);
        System.out.println("요청 roomName: " + roomName);

        if (identity == null || roomName == null) {
            return ResponseEntity.badRequest().build();  // 요청 누락
        }

        LiveKitTokenResponse response = liveKitService.issueTokenResponse(identity, roomName);
        return ResponseEntity.ok(response);
    }


}
