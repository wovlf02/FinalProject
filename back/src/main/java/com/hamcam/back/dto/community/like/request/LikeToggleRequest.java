package com.hamcam.back.dto.community.like.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeToggleRequest {

    @NotNull(message = "userId는 필수입니다.")
    private Long userId;
}
