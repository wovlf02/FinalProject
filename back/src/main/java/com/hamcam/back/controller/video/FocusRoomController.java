package com.hamcam.back.controller.video;

import com.hamcam.back.dto.video.response.RankingResponse;
import com.hamcam.back.service.video.FocusRankingService;
import com.hamcam.back.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ✅ 집중 경쟁방 컨트롤러
 */
@RestController
@RequestMapping("/api/video-room/focus")
@RequiredArgsConstructor
public class FocusRoomController {

    private final FocusRankingService focusRankingService;

    /**
     * ✅ 집중 시간 업데이트
     */
    @PostMapping("/update-time")
    public ResponseEntity<Void> updateFocusTime(
            @RequestParam Long roomId,
            @RequestParam int seconds,
            HttpServletRequest request
    ) {
        Long userId = SessionUtil.getUserId(request);
        focusRankingService.updateFocusTime(userId, roomId, seconds);
        return ResponseEntity.ok().build();
    }

    /**
     * ✅ 실시간 랭킹 조회
     */
    @GetMapping("/ranking/{roomId}")
    public ResponseEntity<RankingResponse> getRanking(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "3600") int targetTime // 초 단위 목표 시간 (예: 60분)
    ) {
        RankingResponse response = focusRankingService.getRanking(roomId, targetTime);
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ 1등 도달 여부 체크 + 방 종료 처리
     */
    @PostMapping("/check-winner/{roomId}")
    public ResponseEntity<Long> checkForWinner(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "3600") int targetTime
    ) {
        Long winnerId = focusRankingService.checkForWinnerAndEndRoom(roomId, targetTime);
        return ResponseEntity.ok(winnerId);
    }
}
