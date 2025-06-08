package com.hamcam.back.controller.community.notice;

import com.hamcam.back.dto.community.notice.response.NoticeResponse;
import com.hamcam.back.dto.community.notice.response.NoticeSummaryResponse;
import com.hamcam.back.service.community.notice.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    /**
     * 전체 공지사항 조회
     */
    @GetMapping
    public List<NoticeResponse> getAllNotices() {
        return noticeService.getAllNotices();
    }

    /**
     * ✅ 주요 공지사항 조회 (커뮤니티 첫 화면용)
     */
    @GetMapping("/main")
    public List<NoticeSummaryResponse> getMainNotices() {
        return noticeService.getMainNotices();
    }
}
