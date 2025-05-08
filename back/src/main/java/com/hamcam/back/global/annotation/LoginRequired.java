package com.hamcam.back.global.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;

/**
 * [@LoginRequired]
 *
 * 인증이 필요한 API에서 사용되는 커스텀 어노테이션
 *
 * 이 어노테이션이 붙은 메서드는 인증된 사용자만 접근할 수 있도록 제한되며,
 * 인증 정보가 없을 경우 401 Unauthorized 에러를 발생시킨다.
 *
 * AOP 또는 인터셉터와 함께 사용하여 로그인 검증 로직을 수행한다.
 *
 * 사용 예시:
 * @LoginRequired
 * @GetMapping("/profile")
 * public ResponseEntity<?> getUserProfile() {
 *     ...
 * }
 *
 * 동작 방식:
 * -> 인터셉터 또는 AOP에서 HandlerMethod를 통해 해당 어노테이션을 감지
 * SecurityContextHolder에서 인증 여부 확인
 * 인증이 없을 경우 예외 ('UnauthorizedException') 발생
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoginRequired {
}
