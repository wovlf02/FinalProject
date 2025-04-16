<<<<<<<< HEAD:back/src/main/java/com/hamcam/back/auth/controller/AuthController.java
package com.hamcam.back.auth.controller;

import com.hamcam.back.auth.dto.request.*;
import com.hamcam.back.auth.dto.response.*;
import com.hamcam.back.auth.service.AuthService;
========
package com.hamcam.back.controller.auth;

import com.hamcam.back.dto.auth.*;
import com.hamcam.back.service.auth.AuthService;
>>>>>>>> wovlf:back/src/main/java/com/hamcam/back/controller/auth/AuthController.java
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
<<<<<<<< HEAD:back/src/main/java/com/hamcam/back/auth/controller/AuthController.java
import org.springframework.web.bind.annotation.*;
========
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
>>>>>>>> wovlf:back/src/main/java/com/hamcam/back/controller/auth/AuthController.java

/**
 * 인증 관련 API를 처리하는 컨트롤러 클래스
 * 회원가입, 로그인, 이메일 인증, 중복 확인, 아이디/비밀번호 찾기 등
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 닉네임 중복 확인
     * @param request 닉네임 요청 DTO
     * @return 사용 가능 여부 반환
     */
    @PostMapping("/check-nickname")
    public ResponseEntity<NicknameCheckResponse> checkNickname(@RequestBody NicknameCheckRequest request) {
        boolean isAvailable = authService.checkNickname(request.getNickname());
        return ResponseEntity.ok(new NicknameCheckResponse(isAvailable));
    }

    /**
     * 아이디 중복확인
     * @param request 아이디 요청 DTO
     * @return 사용 가능 여부 반환
     */
    @PostMapping("/check-username")
    public ResponseEntity<UsernameCheckResponse> checkUsername(@RequestBody UsernameCheckRequest request) {
        boolean isAvailable = authService.checkUsername(request.getUsername());
        return ResponseEntity.ok(new UsernameCheckResponse(isAvailable));
    }

    /**
     * 이메일 인증번호 전송
     * @param request 이메일 인증 요청 DTO
     * @return 성공 메시지 반환
     */
    @PostMapping("/send-email-code")
    public ResponseEntity<EmailVerificationResponse> sendVerificationEmail(@RequestBody EmailVerificationRequest request) {
        authService.sendVerificationEmail(request.getEmail());
        return ResponseEntity.ok(new EmailVerificationResponse("인증번호가 이메일로 전송되었습니다."));
    }

    /**
     * 이메일 인증번호 검증
     * @param request 이메일 인증 요청 DTO
     * @return 성공 메시지 반환
     */
    @PostMapping("/verify-email-code")
    public ResponseEntity<VerifyEmailResponse> verifyEmail(@RequestBody EmailVerificationRequest request) {
        authService.verifyEmail(request);
        return ResponseEntity.ok(new VerifyEmailResponse("이메일 인증이 완료되었습니다."));
    }

    /**
     * 회원가입 요청 처리
     * @param request 회원가입 요청 DTO
     * @return 가입 완료 메시지
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@ModelAttribute RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(new RegisterResponse("회원가입이 완료되었습니다."));
    }

    /**
     * 로그인 요청 처리
     * @param request 로그인 요청 DTO
     * @return 로그인 성공 시 토큰 및 사용자 정보 반환
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * 아이디 찾기 (이메일 기반)
     * @param request 아이디 찾기 요청 DTO
     * @return 사용자명 반환
     */
    @PostMapping("/find-username")
    public ResponseEntity<UsernameFindResponse> findUsername(@RequestBody UsernameFindRequest request) {
        try {
            UsernameFindResponse response = authService.findUsername(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new UsernameFindResponse(false, null, e.getMessage()));
        }
    }

    /**
     * 비밀번호 재설정 요청
     * @param request 비밀번호 재설정 요청 DTO
     * @return 성공 메시지 반환
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ResetPasswordResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            ResetPasswordResponse response = authService.resetPassword(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ResetPasswordResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ResetPasswordResponse(false, "비밀번호 변경 중 오류가 발생했습니다."));
        }
    }
}
