package com.hamcam.back.controller.auth;

import com.hamcam.back.dto.auth.request.*;
import com.hamcam.back.dto.auth.response.LoginResponse;
import com.hamcam.back.dto.auth.response.TokenResponse;
import com.hamcam.back.global.response.ApiResponse;
import com.hamcam.back.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 인증 및 회원 관련 API를 제공하는 컨트롤러입니다.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/check-username")
    public ApiResponse<Boolean> checkUsername(@RequestBody @Valid UsernameCheckRequest request) {
        return ApiResponse.ok(authService.checkUsername(request));
    }

    @PostMapping("/check-nickname")
    public ApiResponse<Boolean> checkNickname(@RequestBody @Valid NicknameCheckRequest request) {
        return ApiResponse.ok(authService.checkNickname(request));
    }

    @PostMapping("/check-email")
    public ApiResponse<Boolean> checkEmail(@RequestBody @Valid EmailRequest request) {
        return ApiResponse.ok(authService.checkEmail(request));
    }

    @PostMapping("/send-code")
    public ApiResponse<String> sendVerificationCode(@RequestBody @Valid EmailSendRequest request) {
        return ApiResponse.ok(authService.sendVerificationCode(request));
    }

    @PostMapping("/verify-code")
    public ApiResponse<Boolean> verifyCode(@RequestBody @Valid EmailVerifyRequest request) {
        return ApiResponse.ok(authService.verifyCode(request));
    }

    @DeleteMapping("/temp")
    public ApiResponse<Void> deleteTempData(@RequestBody @Valid EmailRequest request) {
        System.out.println("RegisterRequest: " + request);
        authService.deleteTempData(request);
        return ApiResponse.ok();
    }

    /**
     * 최종 회원가입 (학습 정보, 프로필 포함, 이미지 파일 업로드 지원)
     */
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Void> register(
            @RequestPart("request") @Valid RegisterRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        System.out.println("RegisterRequest: " + request);
        System.out.println("name: " + request.getName());
        System.out.println("phone: " + request.getPhone());
        System.out.println("프로필 이미지: " + (profileImage != null ? profileImage.getOriginalFilename() : "없음"));
        authService.register(request, profileImage);
        return ApiResponse.ok();
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody @Valid TokenRequest request) {
        authService.logout(request);
        return ApiResponse.ok();
    }

    @PostMapping("/reissue")
    public ApiResponse<TokenResponse> reissue(@RequestBody @Valid TokenRequest request) {
        return ApiResponse.ok(authService.reissue(request));
    }

    @PostMapping("/find-username/send-code")
    public ApiResponse<String> sendFindUsernameCode(@RequestBody @Valid EmailRequest request) {
        return ApiResponse.ok(authService.sendFindUsernameCode(request));
    }

    @PostMapping("/find-username/verify-code")
    public ApiResponse<String> verifyFindUsernameCode(@RequestBody @Valid EmailVerifyRequest request) {
        return ApiResponse.ok(authService.verifyFindUsernameCode(request));
    }

    @PostMapping("/password/request")
    public ApiResponse<String> requestPasswordReset(@RequestBody @Valid PasswordResetRequest request) {
        return ApiResponse.ok(authService.requestPasswordReset(request));
    }

    @PostMapping("/password/verify-code")
    public ApiResponse<Boolean> verifyPasswordResetCode(@RequestBody @Valid EmailVerifyRequest request) {
        return ApiResponse.ok(authService.verifyPasswordResetCode(request));
    }

    @PutMapping("/password/update")
    public ApiResponse<Void> updatePassword(@RequestBody @Valid PasswordChangeRequest request) {
        authService.updatePassword(request);
        return ApiResponse.ok();
    }

    @DeleteMapping("/withdraw")
    public ApiResponse<Void> withdraw(@RequestBody @Valid PasswordConfirmRequest request) {
        authService.withdraw(request);
        return ApiResponse.ok();
    }
}
