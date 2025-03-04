package com.studymate.back.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {

    // 아이디 (userId로 전달받음)
    private String username;
    // 새로운 비밀번호 (password로 전달받음)
    private String newPassword;
}
