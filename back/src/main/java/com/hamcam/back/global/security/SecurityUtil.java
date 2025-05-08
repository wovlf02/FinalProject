package com.hamcam.back.global.security;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.global.exception.CustomException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    /**
     * 현재 인증된 사용자의 ID 반환
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException("인증된 사용자가 존재하지 않습니다.");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User user) {
            return user.getId();
        }

        throw new CustomException("사용자 정보가 유효하지 않습니다.");
    }

    /**
     * 현재 인증된 사용자 객체 반환
     */
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException("인증된 사용자가 존재하지 않습니다.");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User user) {
            return user;
        }

        throw new CustomException("사용자 정보가 유효하지 않습니다.");
    }
}
