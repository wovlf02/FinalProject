package com.hamcam.back.global.annotation;

import java.lang.annotation.*;

/**
 * [@CurrentUser]
 *
 * 현재 인증된 사용자 객체(UserDetails 또는 사용자 엔티티 등)를 컨트롤러 파라미터에 자동 주입하기 위해
 * 사용하는 커스텀 어노테이션
 *
 * 사용 예시:
 * @GetMapping("/me")
 * public ResponseEntity<UserProfileResponse> getMyProfile(@CurrentUser User user) {}
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {
}
