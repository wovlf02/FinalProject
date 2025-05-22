package com.hamcam.back.dto.community.post.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 단순 사용자 식별용 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostUserRequest {
    @NotNull(message = "userId는 필수입니다.")
    private Long userId;
}
