package com.hamcam.back.dto.community.study.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserListResponse {
    private List<UserSimpleDto> data;
}
