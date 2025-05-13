package com.hamcam.back.controller.community.view;

import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.service.community.view.ViewCountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 게시글 조회수(View Count) 컨트롤러
 * - 게시글 상세 진입 시 클라이언트에서 호출되어 조회수를 1 증가시킵니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community/posts")
public class ViewCountController {

    private final ViewCountService viewCountService;

    /**
     * 게시글 조회수 증가 요청
     *
     * @param postId 게시글 ID
     * @return 처리 메시지
     */
    @PostMapping("/{postId}/view")
    public ResponseEntity<MessageResponse> increaseViewCount(@PathVariable Long postId) {
        viewCountService.increaseViewCount(postId);
        return ResponseEntity.ok(new MessageResponse("게시글 조회 수가 증가했습니다."));
    }
}
