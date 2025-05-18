package com.hamcam.back.dto.user.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 프로필 이미지 수정 요청 DTO
 * - URL 형태로 전달됨
 */
@Getter
@NoArgsConstructor
public class UpdateProfileImageRequest {

    private String profileImageUrl; // null 또는 빈 문자열일 경우 기본 이미지로 처리 가능
}
