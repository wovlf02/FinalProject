package com.hamcam.back.global.security;

import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.security.auth.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 인증된 사용자 정보를 가져오는 유틸리티 클래스
 */
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
        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getUserId();
        }

        throw new CustomException("사용자 정보가 유효하지 않습니다.");
    }

    /**
     * 현재 인증된 사용자 정보(CustomUserDetails) 반환
     */
    public static CustomUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException("인증된 사용자가 존재하지 않습니다.");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails;
        }

        throw new CustomException("사용자 정보가 유효하지 않습니다.");
    }
}
