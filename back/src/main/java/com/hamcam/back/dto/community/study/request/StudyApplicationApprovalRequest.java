package com.hamcam.back.dto.community.study.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudyApplicationApprovalRequest {
    private Long studyId;
    private Long applicantId;
    private boolean approve;
}
