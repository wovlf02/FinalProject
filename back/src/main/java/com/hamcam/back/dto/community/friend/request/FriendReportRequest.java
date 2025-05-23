package com.hamcam.back.dto.community.friend.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * 친구 신고 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendReportRequest {

    @NotNull(message = "신고자 ID는 필수입니다.")
    private Long userId;

    @NotNull(message = "신고 대상 사용자 ID는 필수입니다.")
    private Long targetUserId;

    @NotBlank(message = "신고 사유는 필수입니다.")
    private String reason;
}
