package com.hamcam.back.controller.video;

import com.hamcam.back.service.video.PresenterService;
import com.hamcam.back.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ✅ 발표자 컨트롤러
 */
@RestController
@RequestMapping("/api/video-room/presenter")
@RequiredArgsConstructor
public class PresenterController {

    private final PresenterService presenterService;

    /**
     * ✅ 손들기 → 발표자 등록
     */
    @PostMapping("/select")
    public ResponseEntity<Void> selectPresenter(
            @RequestParam Long roomId,
            HttpServletRequest request
    ) {
        Long userId = SessionUtil.getUserId(request);
        presenterService.selectPresenter(userId, roomId);
        return ResponseEntity.ok().build();
    }

    /**
     * ✅ 발표 종료 → 발표자 해제
     */
    @PostMapping("/end")
    public ResponseEntity<Void> endPresentation(
            @RequestParam Long roomId
    ) {
        presenterService.endPresentation(roomId);
        return ResponseEntity.ok().build();
    }

    /**
     * ✅ 현재 발표자 조회
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<Long> getCurrentPresenter(
            @PathVariable Long roomId
    ) {
        Long presenterId = presenterService.getCurrentPresenter(roomId);
        return ResponseEntity.ok(presenterId);
    }
}
