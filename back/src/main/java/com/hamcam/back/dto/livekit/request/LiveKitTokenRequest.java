package com.hamcam.back.dto.livekit.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LiveKitTokenRequest {

    @NotBlank(message = "roomName은 필수입니다.")
    private String roomName;

    // ✅ 발표자 여부 (video publish 권한 부여 기준)
    private boolean presenter;
}
