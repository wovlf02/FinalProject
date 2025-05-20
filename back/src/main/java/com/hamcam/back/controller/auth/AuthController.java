package com.hamcam.back.controller.auth;

import com.hamcam.back.config.auth.JwtProvider;
import com.hamcam.back.dto.auth.request.*;
import com.hamcam.back.dto.auth.response.TokenResponse;
import com.hamcam.back.dto.user.request.UpdatePasswordRequest;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.global.response.ApiResponse;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.service.auth.AuthService;
import com.hamcam.back.service.user.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.List;

/**
 * 인증 및 회원 관련 API를 제공하는 컨트롤러입니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final JwtProvider jwtProvider;
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

    /**
     * 회원가입 요청 (Multipart + JSON 혼합)
     */
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> register(@ModelAttribute RegisterRequest request,
                                        @RequestPart(value = "profileImage", required = false) MultipartFile file) {
        authService.register(request, file);
        return ApiResponse.ok("회원가입이 완료되었습니다.");
    }

    // ---------------- 로그인 / 로그아웃 / 토큰 재발급 ----------------

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid LoginRequest request, HttpServletResponse response) {
        authService.login(request);

        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);

        response.addHeader("Set-Cookie", jwtProvider.createAccessTokenCookie(accessToken).toString());
        response.addHeader("Set-Cookie", jwtProvider.createRefreshTokenCookie(refreshToken).toString());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = JwtProvider.ACCESS_COOKIE, required = false) String accessToken,
            HttpServletResponse response) {

        if (accessToken == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        authService.logout(accessToken);

        response.addHeader("Set-Cookie", jwtProvider.deleteCookie(JwtProvider.ACCESS_COOKIE).toString());
        response.addHeader("Set-Cookie", jwtProvider.deleteCookie(JwtProvider.REFRESH_COOKIE).toString());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/reissue")
    public ResponseEntity<Void> reissue(
            @CookieValue(name = JwtProvider.REFRESH_COOKIE, required = false) String refreshToken,
            HttpServletResponse response) {

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        TokenResponse token = authService.reissue(refreshToken);

        response.addHeader("Set-Cookie", jwtProvider.createAccessTokenCookie(token.getAccessToken()).toString());
        return ResponseEntity.ok().build();
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
