package com.hamcam.back.global.security;

import jakarta.annotation.PostConstruct;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Spring Security의 SecurityContextHolder 전략을 설정하는 클래스입니다.
 * - 기본은 MODE_THREADLOCAL이지만, WebSocket 등 비동기 환경에서는 인증 정보를 공유하지 못하는 문제가 있습니다.
 * - 이를 해결하기 위해 MODE_INHERITABLETHREADLOCAL 전략으로 설정합니다.
 *
 * ✅ 이 설정은 애플리케이션 전체에 영향을 미치며, 반드시 한 번만 초기화해야 합니다.
 */
@Component
public class SecurityContextStrategyConfigurer {

    @PostConstruct
    public void setup() {
        // ✅ 자식 스레드에서도 SecurityContext를 상속할 수 있도록 설정
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
        System.out.println("✅ SecurityContextHolder 전략 설정: MODE_INHERITABLETHREADLOCAL");
    }
}