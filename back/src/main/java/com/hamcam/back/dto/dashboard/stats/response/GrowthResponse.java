package com.hamcam.back.dto.dashboard.stats.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GrowthResponse {
    private String subject;
    private int rate; // 증가율 (%)
}