package com.hamcam.back.dto.dashboard.notice.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoticeResponse {
    private String type;
    private String text;
    private String date;
}
