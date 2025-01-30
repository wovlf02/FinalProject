package com.studymate.back.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 사용자 정보 DTO
 */
@Getter
@Setter
@AllArgsConstructor
public class UserDto {

    private String username;
    private String name;
    private String phone;
    private String email;
    private boolean emailVerified;
}
