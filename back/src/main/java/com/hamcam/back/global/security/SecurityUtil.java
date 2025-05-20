package com.hamcam.back.global.security;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.security.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 인증된 사용자 정보를 가져오는 유틸리티 클래스
 * - SecurityContextHolder에서 현재 로그인한 사용자의 ID 또는 엔티티 조회
 * - 인증되지 않은 경우 예외 발생
 */
@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final UserRepository userRepository;

    /**
     * 현재 인증된 사용자의 ID 반환
     * WebSocket 인증 시 principal이 Long이나 String일 수 있으므로 유연하게 처리
     *
     * @return 사용자 ID
     * @throws CustomException 인증되지 않은 경우
     */
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("🔍 SecurityUtil 내부 인증 객체: " + authentication);

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof CustomUserDetails userDetails) {
                return userDetails.getUserId();
            }

            // WebSocket 인증 시 Principal이 문자열 userId로 전달될 수 있음
            if (principal instanceof String str && str.matches("\\d+")) {
                return Long.parseLong(str);
            }

            // WebSocket 연결 중 principal이 Long 타입인 경우도 대응
            if (principal instanceof Long id) {
                return id;
            }
        }

        throw new CustomException(ErrorCode.UNAUTHORIZED);
    }

    /**
     * 현재 인증된 사용자 엔티티 반환
     *
     * @return User 객체
     * @throws CustomException 인증 실패 또는 사용자 조회 실패
     */
    public User getCurrentUser() {
        Long userId = getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * 사용자 ID로 사용자 엔티티 직접 조회
     *
     * @param userId 조회할 사용자 ID
     * @return User 객체
     * @throws CustomException 사용자 존재하지 않을 경우
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
