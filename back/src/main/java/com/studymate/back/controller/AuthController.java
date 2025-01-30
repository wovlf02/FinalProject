package com.studymate.back.controller;

import com.studymate.back.dto.AuthRequest;
import com.studymate.back.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 관련 API 컨트롤러
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 아이디 중복확인
     * @param username 아이디
     * @return 중복확인 완료 결과값
     */
    @GetMapping("/check-username")
    public ResponseEntity<Boolean> checkUsernameAvailability(@RequestParam String username) {
        boolean available = authService.checkUsernameAvailability(username);
        return ResponseEntity.ok(available);
    }

    /**
     * 이메일 인증번호 발송 API
     * @param email 이메일
     * @return 인증번호 발송 메시지
     */
    @PostMapping("/send-email")
    public ResponseEntity<String> sendVerificationEmail(@RequestParam String email) {
        authService.sendVerificationEmail(email);
        return ResponseEntity.ok("이메일 인증번호가 발송되었습니다.");
    }

    /**
     * 이메일 인증번호 검증 API
     * @param email 이메일
     * @param code 인증번호
     * @return 인증번호 검증 결과값
     */
    @PostMapping("/verify-email")
    public ResponseEntity<Boolean> verifyEmail(@RequestParam String email, @RequestParam String code) {
        boolean verified = authService.verifyEmail(email, code);
        return ResponseEntity.ok(verified);
    }

    /**
     * 회원가입 API -> 아이디 중복확인, 이메일 인증 성공해야 가능
     * @param request 회원가입 요청 데이터
     * @return 회원가입 완료 메시지
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest.RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

}
