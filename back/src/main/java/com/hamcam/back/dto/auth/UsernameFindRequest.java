package com.hamcam.back.dto.auth;

import lombok.Data;

@Data
public class UsernameFindRequest {
    
    // 유저 이메일
    private String email;
}
