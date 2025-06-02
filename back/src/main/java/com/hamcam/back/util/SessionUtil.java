package com.hamcam.back.util;

import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * [SessionUtil]
 * 세션 기반 사용자 인증 처리 유틸 클래스
 */
public class SessionUtil {

    private static final String USER_ID_ATTR = "userId";

    /**
     * 세션에서 userId를 꺼내 반환
     *
     * @param request HttpServletRequest
     * @return userId (Long)
     * @throws CustomException USER_NOT_FOUND
     */
    public static Long getUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        Object userIdAttr = session.getAttribute(USER_ID_ATTR);

        if (userIdAttr instanceof Long) {
            return (Long) userIdAttr;
        }

        throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }
}
