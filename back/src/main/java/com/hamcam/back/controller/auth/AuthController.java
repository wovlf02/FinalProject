package com.hamcam.back.controller.auth;

import com.hamcam.back.dto.auth.request.*;
import com.hamcam.back.dto.auth.response.LoginResponse;
import com.hamcam.back.dto.auth.response.TokenResponse;
import com.hamcam.back.global.response.ApiResponse;
import com.hamcam.back.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 및 회원 관련 API를 제공하는 컨트롤러입니다.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 아이디 중복 확인
     */
    @PostMapping("/check-username")
    public ApiResponse<Boolean> checkUsername(@RequestBody @Valid UsernameCheckRequest request) {
        return ApiResponse.ok(authService.checkUsername(request));
    }

    /**
     * 닉네임 중복 확인
     */
    @PostMapping("/check-nickname")
    public ApiResponse<Boolean> checkNickname(@RequestBody @Valid NicknameCheckRequest request) {
        return ApiResponse.ok(authService.checkNickname(request));
    }

    /**
     * 이메일 중복 확인
     */
    @PostMapping("/check-email")
    public ApiResponse<Boolean> checkEmail(@RequestBody @Valid EmailRequest request) {
        return ApiResponse.ok(authService.checkEmail(request));
    }

    /**
     * 이메일 인증코드 발송
     */
    @PostMapping("/send-code")
    public ApiResponse<String> sendVerificationCode(@RequestBody @Valid EmailSendRequest request) {
        return ApiResponse.ok(authService.sendVerificationCode(request));
    }

    /**
     * 이메일 인증코드 검증
     */
    @PostMapping("/verify-code")
    public ApiResponse<Boolean> verifyCode(@RequestBody @Valid EmailVerifyRequest request) {
        return ApiResponse.ok(authService.verifyCode(request));
    }

    /**
     * 회원가입 도중 임시 데이터 삭제 (Redis/DB)
     */
    @DeleteMapping("/temp")
    public ApiResponse<Void> deleteTempData(@RequestBody @Valid EmailRequest request) {
        authService.deleteTempData(request);
        return ApiResponse.ok();
    }

    /**
     * 최종 회원가입 (학습 정보, 프로필 포함)
     */
    @PostMapping("/register")
    public ApiResponse<Void> register(@RequestBody @Valid RegisterRequest request) {
        authService.register(request);
        return ApiResponse.ok();
    }

    /**
     * 로그인 요청 - JWT 발급
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    /**
     * 로그아웃 - refresh 제거 및 access 블랙리스트 처리
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody @Valid TokenRequest request) {
        authService.logout(request);
        return ApiResponse.ok();
    }

    /**
     * access 토큰 재발급 (Sliding 방식)
     */
    @PostMapping("/reissue")
    public ApiResponse<TokenResponse> reissue(@RequestBody @Valid TokenRequest request) {
        return ApiResponse.ok(authService.reissue(request));
    }

    /**
     * 아이디 찾기 - 인증 코드 발송
     */
    @PostMapping("/find-username/send-code")
    public ApiResponse<String> sendFindUsernameCode(@RequestBody @Valid EmailRequest request) {
        return ApiResponse.ok(authService.sendFindUsernameCode(request));
    }

    /**
     * 아이디 찾기 - 인증코드 검증 및 반환
     */
    @PostMapping("/find-username/verify-code")
    public ApiResponse<String> verifyFindUsernameCode(@RequestBody @Valid EmailVerifyRequest request) {
        return ApiResponse.ok(authService.verifyFindUsernameCode(request));
    }

    /**
     * 비밀번호 재설정 - 본인 확인 요청
     */
    @PostMapping("/password/request")
    public ApiResponse<String> requestPasswordReset(@RequestBody @Valid PasswordResetRequest request) {
        return ApiResponse.ok(authService.requestPasswordReset(request));
    }

    /**
     * 비밀번호 재설정 - 인증 코드 검증
     */
    @PostMapping("/password/verify-code")
    public ApiResponse<Boolean> verifyPasswordResetCode(@RequestBody @Valid EmailVerifyRequest request) {
        return ApiResponse.ok(authService.verifyPasswordResetCode(request));
    }

    /**
     * 비밀번호 재설정 - 새 비밀번호 저장
     */
    @PutMapping("/password/update")
    public ApiResponse<Void> updatePassword(@RequestBody @Valid PasswordChangeRequest request) {
        authService.updatePassword(request);
        return ApiResponse.ok();
    }

    /**
     * 회원 탈퇴 요청
     */
    @DeleteMapping("/withdraw")
    public ApiResponse<Void> withdraw(@RequestBody @Valid PasswordConfirmRequest request) {
        authService.withdraw(request);
        return ApiResponse.ok();
    }
}
