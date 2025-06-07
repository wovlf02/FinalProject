package com.hamcam.back.service.community.notice;

import com.hamcam.back.dto.community.notice.response.NoticeResponse;
import com.hamcam.back.dto.community.notice.response.NoticeSummaryResponse;
import com.hamcam.back.entity.community.Notice;
import com.hamcam.back.repository.community.notice.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    /**
     * ✅ 주요 공지사항 (최근 등록 순 상위 3개)
     */
    public List<NoticeSummaryResponse> getMainNotices() {
        List<Notice> topNotices = noticeRepository.findTop3ByOrderByCreatedAtDesc();

        return topNotices.stream()
                .map(n -> NoticeSummaryResponse.builder()
                        .id(n.getId())
                        .title(n.getTitle())
                        .date(n.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .views(n.getViews())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 전체 공지사항 조회 (최신순 정렬)
     */
    public List<NoticeResponse> getAllNotices() {
        return noticeRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(n -> NoticeResponse.builder()
                        .id(n.getId())
                        .title(n.getTitle())
                        .content(n.getContent())
                        .views(n.getViews())
                        .createdAt(n.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
