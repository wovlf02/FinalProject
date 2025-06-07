package com.hamcam.back.controller.livekit;

import com.hamcam.back.dto.livekit.request.LiveKitTokenRequest;
import com.hamcam.back.dto.livekit.response.LiveKitTokenResponse;
import com.hamcam.back.service.livekit.LiveKitService;
import com.hamcam.back.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Slf4j
@ConditionalOnProperty(name = "livekit.enabled", havingValue = "true", matchIfMissing = false)
@RestController
@RequestMapping("/api/livekit")
@RequiredArgsConstructor
public class LiveKitController {

    private final LiveKitService liveKitService;

    /**
     * ✅ LiveKit 접속용 JWT 토큰 발급
     *
     * @param request roomName, isPresenter 포함
     * @param httpRequest 세션에서 userId 추출
     * @return token + wsUrl
     */
    @PostMapping("/token")
    public ResponseEntity<LiveKitTokenResponse> getLiveKitToken(
            @Valid @RequestBody LiveKitTokenRequest request,
            HttpServletRequest httpRequest
    ) {
        Long userId = SessionUtil.getUserId(httpRequest);
        String roomName = request.getRoomName();
        boolean isPresenter = request.isPresenter();

        log.info("🔑 LiveKit 토큰 요청: userId={}, roomName={}, isPresenter={}", userId, roomName, isPresenter);

        try {
            LiveKitTokenResponse response = liveKitService.issueTokenResponse(userId.toString(), roomName, isPresenter);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("🔥 LiveKit 토큰 발급 실패: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
