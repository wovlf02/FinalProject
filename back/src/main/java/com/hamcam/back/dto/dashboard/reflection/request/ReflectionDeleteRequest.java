package com.hamcam.back.dto.dashboard.reflection.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * [ReflectionDeleteRequest]
 *
 * 저장된 회고 결과를 삭제할 때 사용하는 요청 DTO입니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReflectionDeleteRequest {

    @NotNull(message = "삭제할 회고 ID는 필수입니다.")
    private Long reflectionId;
}
