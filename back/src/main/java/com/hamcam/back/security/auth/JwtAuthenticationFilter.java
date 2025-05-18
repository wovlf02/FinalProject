package com.hamcam.back.security.auth;

import com.hamcam.back.config.auth.JwtProvider;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.repository.auth.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 인증 필터 (HttpOnly 쿠키 기반 + Redis 검증 포함)
 * - accessToken 쿠키에서 JWT 추출
 * - Redis에 저장된 토큰과 비교하여 유효성 확인
 * - 사용자 인증 컨텍스트 설정
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getTokenFromCookie(request);

            if (token != null && jwtProvider.validateAccessTokenWithRedis(token)) {
                Long userId = jwtProvider.getUserIdFromToken(token);

                // 사용자 정보 조회
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다."));

                UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

                // 인증 객체 생성 및 SecurityContext 등록
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (ExpiredJwtException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access Token이 만료되었습니다.");
            return;
        } catch (JwtException | CustomException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Token이 유효하지 않습니다.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 요청에서 HttpOnly 쿠키로부터 accessToken 추출
     */
    private String getTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if (JwtProvider.ACCESS_COOKIE.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
