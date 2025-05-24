package com.hamcam.back.controller.auth;

import com.hamcam.back.dto.auth.request.LoginRequest;
import com.hamcam.back.dto.auth.request.RegisterRequest;
import com.hamcam.back.dto.auth.response.LoginResponse;
import com.hamcam.back.global.response.ApiResponse;
import com.hamcam.back.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * [AuthController]
 * 로그인/회원가입/탈퇴 처리 컨트롤러
 * - 로그인 시 전체 사용자 정보 반환 (LocalStorage용)
 * - 비밀번호는 평문 저장 (보안 제외: 프로토타입 목적)
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /** ✅ 회원가입 요청 - Multipart (profileImage 포함 가능) */
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> register(
            @RequestPart("request") RegisterRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile file,
            HttpServletRequest httpRequest
    ) {
        authService.register(request, file, httpRequest);
        return ResponseEntity.ok(ApiResponse.ok("✅ 회원가입이 완료되었습니다."));
    }

    /** ✅ 로그인 요청 - 사용자 전체 정보 반환 + 세션 저장 */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        LoginResponse response = authService.login(request, httpRequest);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /** ✅ 회원 탈퇴 - 세션 기반 사용자 식별 */
    @DeleteMapping("/withdraw")
    public ResponseEntity<ApiResponse<Void>> withdraw(HttpServletRequest request) {
        authService.withdraw(request);
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
