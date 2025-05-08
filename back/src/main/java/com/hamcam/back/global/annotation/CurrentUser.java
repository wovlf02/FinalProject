package com.hamcam.back.global.annotation;

import java.lang.annotation.*;

/**
 * [@CurrentUser]
 *
 * 현재 인증된 사용자 객체(UserDetails 또는 사용자 엔티티 등)를 컨트롤러 파라미터에 자동 주입하기 위해
 * 사용하는 커스텀 어노테이션
 *
 * Spring Security + HandlerMethodArgumentResolver와 함께 사용됨
 *
 * 사용 예시:
 * @GetMapping("/me")
 * public ResponseEntity<UserProfileResponse> getMyProfile(@CurrentUser User user) {}
 *
 * -> 내부적으로 인증된 사용자 정보를 SecurityContextHolder에서 가져오며
 * 해당 사용자 객체를 직접 메서드에 주입할 수 있다.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {
}
