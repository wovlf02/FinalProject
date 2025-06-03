package com.hamcam.back.dto.study.problem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProblemSummary {
    private Long problemId;
    private String passageTitle;
    private String subject;
}
