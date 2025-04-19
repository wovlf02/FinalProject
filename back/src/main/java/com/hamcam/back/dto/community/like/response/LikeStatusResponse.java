package com.hamcam.back.dto.community.like.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 좋아요 여부 응답 DTO
 * <p>
 * 현재 로그인 사용자가 특정 리소스에 좋아요를 눌렀는지 여부를 반환합니다.
 * </p>
 */
@Data
@AllArgsConstructor
public class LikeStatusResponse {

    /**
     * 좋아요 여부 (true: 눌렀음, false: 안 눌렀음)
     */
    private boolean liked;
}
