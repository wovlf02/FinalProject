package com.hamcam.back.dto.community.block.response;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlockedCommentListResponse {
    private List<BlockedTargetResponse> blockedComments;
}
