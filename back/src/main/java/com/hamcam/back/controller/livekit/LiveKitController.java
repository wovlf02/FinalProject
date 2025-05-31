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
            @RequestBody LiveKitTokenRequest request,
            HttpSession session
    ) {
        Object userIdObj = session.getAttribute("user_id");

        if (userIdObj == null) {
            return ResponseEntity.status(401).build();  // 인증 실패
        }

        String identity = userIdObj.toString();
        LiveKitTokenResponse response = liveKitService.issueTokenResponse(identity, request.getRoomName());

        return ResponseEntity.ok(response);
    }
}
