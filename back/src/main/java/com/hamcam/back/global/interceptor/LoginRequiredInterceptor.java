package com.hamcam.back.global.interceptor;

import com.hamcam.back.global.annotation.LoginRequired;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod handlerMethod) {
            boolean methodHasAnnotation = handlerMethod.hasMethodAnnotation(LoginRequired.class);
            boolean classHasAnnotation = handlerMethod.getBeanType().isAnnotationPresent(LoginRequired.class);

            if (methodHasAnnotation || classHasAnnotation) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication == null || !authentication.isAuthenticated()
                        || authentication.getPrincipal().equals("anonymousUser")) {
                    throw new CustomException(ErrorCode.UNAUTHORIZED);
                }
            }
        }

        return true;
    }
}
