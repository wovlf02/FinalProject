package com.hamcam.back.controller.video;

import com.hamcam.back.dto.video.request.VoteRequest;
import com.hamcam.back.dto.video.response.VoteResultResponse;
import com.hamcam.back.service.video.VoteService;
import com.hamcam.back.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ✅ 발표자 투표 컨트롤러
 */
@RestController
@RequestMapping("/api/video-room/vote")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    /**
     * ✅ 투표 제출 (찬성/반대)
     */
    @PostMapping
    public ResponseEntity<Void> submitVote(
            @RequestBody VoteRequest request,
            HttpServletRequest httpRequest
    ) {
        Long voterId = SessionUtil.getUserId(httpRequest);
        voteService.submitVote(voterId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * ✅ 투표 결과 계산
     */
    @GetMapping("/result")
    public ResponseEntity<VoteResultResponse> getVoteResult(
            @RequestParam Long roomId,
            @RequestParam Long presenterId
    ) {
        VoteResultResponse response = voteService.calculateVoteResult(roomId, presenterId);
        return ResponseEntity.ok(response);
    }
}
