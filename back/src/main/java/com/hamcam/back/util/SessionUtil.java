package com.hamcam.back.util;

import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Map;

/**
 * [SessionUtil]
 * 세션 기반 사용자 인증 처리 유틸 클래스
 */
public class SessionUtil {

    private static final String USER_ID_ATTR = "userId";

    /**
     * ✅ HttpServletRequest 기반 userId 추출 (일반 HTTP 컨트롤러용)
     */
    public static Long getUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        Object userIdAttr = session.getAttribute(USER_ID_ATTR);
        return parseUserId(userIdAttr);
    }

    /**
     * ✅ WebSocket 세션 속성 Map에서 userId 추출 (STOMP용)
     */
    public static Long getUserIdFromSession(Map<String, Object> sessionAttributes) {
        Object userIdAttr = sessionAttributes.get(USER_ID_ATTR);
        return parseUserId(userIdAttr);
    }

    /**
     * ✅ 공통 userId 파싱 로직
     */
    private static Long parseUserId(Object userIdAttr) {
        if (userIdAttr instanceof Long l) return l;
        if (userIdAttr instanceof Integer i) return Long.valueOf(i);
        if (userIdAttr instanceof String s) {
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException e) {
                throw new CustomException(ErrorCode.USER_NOT_FOUND);
            }
        }

        throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }
}
