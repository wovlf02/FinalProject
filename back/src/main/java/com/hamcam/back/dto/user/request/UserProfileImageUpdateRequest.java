package com.hamcam.back.dto.user.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * [UserProfileImageUpdateRequest]
 *
 * 프로필 이미지 변경 요청 DTO입니다.
 * - MultipartFile과 userId를 함께 전달합니다.
 * - @ModelAttribute 기반으로 전송되며, controller에서만 사용됩니다.
 */
@Getter
@Setter
@NoArgsConstructor
public class UserProfileImageUpdateRequest {

    /**
     * 사용자 ID
     */
    @NotNull(message = "userId는 필수입니다.")
    private Long userId;

    /**
     * 프로필 이미지 파일
     */
    @NotNull(message = "profileImage는 필수입니다.")
    private MultipartFile profileImage;
}
