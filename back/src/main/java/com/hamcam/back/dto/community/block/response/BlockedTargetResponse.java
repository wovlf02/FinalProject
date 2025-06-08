package com.hamcam.back.dto.community.block.response;

import lombok.*;

/**
 * 차단된 콘텐츠 또는 사용자 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlockedTargetResponse {

    private Long targetId;
    private String targetType; // 예: POST, COMMENT, REPLY, USER
}
