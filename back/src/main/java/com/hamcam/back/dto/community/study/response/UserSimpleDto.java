package com.hamcam.back.dto.community.study.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSimpleDto {
    private Long userId;
    private String nickname;
}
