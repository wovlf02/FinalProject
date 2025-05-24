package com.hamcam.back.dto.community.block.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 게시글, 댓글, 대댓글, 사용자 차단 해제 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnblockTargetRequest {

    @NotNull(message = "차단 해제 대상 ID는 필수입니다.")
    private Long targetId;
}
