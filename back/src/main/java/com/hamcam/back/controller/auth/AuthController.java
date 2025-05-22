package com.hamcam.back.controller.auth;

import com.hamcam.back.dto.auth.request.LoginRequest;
import com.hamcam.back.dto.auth.request.RegisterRequest;
import com.hamcam.back.dto.auth.response.LoginResponse;
import com.hamcam.back.global.response.ApiResponse;
import com.hamcam.back.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * ✅ 회원가입 요청
     * - 프로필 이미지 포함 가능
     * - 비밀번호 암호화 없이 평문 저장
     */
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> register(
            @RequestPart("request") RegisterRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile file) {
        authService.register(request, file);
        return ApiResponse.ok("✅ 회원가입이 완료되었습니다.");
    }

    /**
     * ✅ 로그인 요청
     * - 성공 시 LoginResponse 반환 (LocalStorage 저장용 전체 정보 포함)
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    /**
     * ✅ 회원 탈퇴 요청
     * - 비밀번호 확인 없이 userId 기반 즉시 삭제
     */
    @DeleteMapping("/withdraw/{userId}")
    public ApiResponse<Void> withdraw(@PathVariable Long userId) {
        authService.withdraw(userId);
        return ApiResponse.ok();
    }
}
