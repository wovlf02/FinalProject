package com.hamcam.back.dto.community.post.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagListResponse {
    private List<String> tags;
}
