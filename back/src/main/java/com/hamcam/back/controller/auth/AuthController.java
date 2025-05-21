package com.hamcam.back.controller.auth;

import com.hamcam.back.dto.auth.request.*;
import com.hamcam.back.dto.user.request.UpdatePasswordRequest;
import com.hamcam.back.global.response.ApiResponse;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.service.auth.AuthService;
import com.hamcam.back.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final UserRepository userRepository;

    // ---------------- 회원가입 & 인증 ----------------

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
        authService.deleteTempData(request);
        return ApiResponse.ok();
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> register(
            @RequestPart("request") RegisterRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile file) {
        authService.register(request, file);
        return ApiResponse.ok("회원가입이 완료되었습니다.");
    }

    // ---------------- 로그인 (보안 제거) ----------------

    @PostMapping("/login")
    public ApiResponse<Long> login(@RequestBody @Valid LoginRequest request) {
        authService.login(request);
        Long userId = userRepository.findByUsername(request.getUsername())
                .orElseThrow()
                .getId();
        return ApiResponse.ok(userId);
    }

    // ---------------- 아이디/비밀번호 찾기 ----------------

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

    @PutMapping("/password/update")
    public ApiResponse<Void> updatePassword(@RequestBody @Valid UpdatePasswordRequest request) {
        authService.updatePassword(request);
        return ApiResponse.ok();
    }

    @DeleteMapping("/withdraw")
    public ApiResponse<Void> withdraw(@RequestBody @Valid PasswordConfirmRequest request) {
        userService.withdraw(request);
        return ApiResponse.ok();
    }
}
