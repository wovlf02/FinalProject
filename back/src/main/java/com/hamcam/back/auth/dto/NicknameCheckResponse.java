package com.hamcam.back.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 닉네임 중복 확인 응답을 위한 DTO 클래스
 * 
 * [응답 형태]
 * {
 *     "available": true
 * }
 * 
 * [필드 설명]
 * available: true인 경우 사용 가능한 닉네임, false인 경우 중복된 닉니엠
 * 
 * [사용 위치]
 * AuthController의 /check-nickname API에서 사용
 */
@Getter
@AllArgsConstructor
public class NicknameCheckResponse {

    /**
     * 닉네임 사용 가능 여부
     * true: 사용 가능한 닉네임
     * false: 이미 사용 중인 닉네임
     */
    private boolean available;
}
