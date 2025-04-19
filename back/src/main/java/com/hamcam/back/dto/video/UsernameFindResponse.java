package com.studymate.back.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsernameFindResponse {

    // 성공 여부
    private boolean success;
    // 찾은 아이디
    private String username;
    // 응답 메시지
    private String message;
}