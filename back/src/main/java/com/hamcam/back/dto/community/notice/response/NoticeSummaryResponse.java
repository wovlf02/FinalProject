package com.hamcam.back.dto.community.notice.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class NoticeSummaryResponse {
    private Long id;
    private String title;
    private String date;
    private int views;
}
