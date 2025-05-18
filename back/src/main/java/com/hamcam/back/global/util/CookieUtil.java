package com.hamcam.back.global.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;
import org.springframework.http.ResponseCookie;

/**
 * [CookieUtil]
 *
 * HttpOnly 쿠키 생성, 조회, 삭제를 위한 유틸리티 클래스입니다.
 * 주로 JWT 토큰을 HttpOnly 쿠키로 주고받을 때 사용됩니다.
 */
@UtilityClass
public class CookieUtil {

    /**
     * HttpOnly 쿠키 생성 및 추가 (응답에 쿠키 세팅)
     *
     * @param response   HttpServletResponse
     * @param name       쿠키 이름
     * @param value      쿠키 값 (JWT 등)
     * @param maxAge     만료 시간 (초 단위)
     * @param secure     HTTPS 여부
     */
    public void addHttpOnlyCookie(HttpServletResponse response, String name, String value, int maxAge, boolean secure) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .maxAge(maxAge)
                .sameSite("Lax") // 또는 "Strict" / "None" (프론트와 협의 필요)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    /**
     * 요청에서 쿠키 값을 가져옴
     *
     * @param request HttpServletRequest
     * @param name    쿠키 이름
     * @return 쿠키 값 (없으면 null)
     */
    public String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * 쿠키 삭제 (Set-Cookie로 MaxAge=0 전달)
     *
     * @param response HttpServletResponse
     * @param name     삭제할 쿠키 이름
     */
    public void deleteCookie(HttpServletResponse response, String name) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}
