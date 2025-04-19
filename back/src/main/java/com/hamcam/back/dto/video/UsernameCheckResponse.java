package com.studymate.back.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 아이디 중복 확인 결과를 반환하는 DTO
 */
@Getter
@Setter
@AllArgsConstructor
public class UsernameCheckResponse {

    /**
     * 아이디 사용 가능 여부
     * true: 사용 가능
     * false: 중복됨
     */
    private boolean available;
}
