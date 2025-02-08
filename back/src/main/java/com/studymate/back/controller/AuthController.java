package com.studymate.back.controller;

import com.studymate.back.dto.*;
import com.studymate.back.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 회원가입, 로그인, 이메일 인증 관련 API 제공
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/check-username")
    public ResponseEntity<UsernameCheckResponse> checkUsername(@RequestBody UsernameCheckRequest request) {
        boolean isAvailable = authService.checkUsername(request.getUsername());
        return ResponseEntity.ok(new UsernameCheckResponse(isAvailable));
    }

    @PostMapping("/send-email-code")
    public ResponseEntity<EmailVerificationResponse> sendVerificationEmail(@RequestBody EmailVerificationRequest request) {
        authService.sendVerificationEmail(request.getEmail());
        return ResponseEntity.ok(new EmailVerificationResponse("인증번호가 이메일로 전송되었습니다."));
    }

    @PostMapping("/verify-email-code")
    public ResponseEntity<VerifyEmailResponse> verifyEmail(@RequestBody EmailVerificationRequest request) {
        authService.verifyEmail(request);
        return ResponseEntity.ok(new VerifyEmailResponse("이메일 인증이 완료되었습니다."));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(new RegisterResponse("회원가입이 완료되었습니다."));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * 아이디 찾기 API
     * @param request UsernameFindRequest (요청 DTO)
     * @return UsernameFindResponse (응답 DTO)
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
}
