package com.hamcam.back.dto.study.team.rest.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProblemSummary {
    private Long problemId;
    private String title;
    private String subject;
    private String difficulty;
}
