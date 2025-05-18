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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

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

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> register(
            @RequestParam("username") String username,
            @RequestParam("password") String rawPassword,
            @RequestParam("email") String email,
            @RequestParam("name") String name,
            @RequestParam("nickname") String nickname,
            @RequestParam("grade") Integer grade,
            @RequestParam("subjects") List<String> subjects,
            @RequestParam("studyHabit") String studyHabit,
            @RequestParam("phone") String phone,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        try {
            // 1. 프로필 이미지 저장
            String profileImageUrl = null;
            if (profileImage != null && !profileImage.isEmpty()) {
                String originalFilename = profileImage.getOriginalFilename();
                String storedName = UUID.randomUUID() + "_" + originalFilename;
                Path uploadDir = Paths.get("uploads/profile");
                Files.createDirectories(uploadDir);
                Path targetPath = uploadDir.resolve(storedName);
                Files.copy(profileImage.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                profileImageUrl = "/uploads/profile/" + storedName;
            }

            // 2. DTO 생성
            RegisterRequest request = RegisterRequest.builder()
                    .username(username)
                    .password(rawPassword)
                    .email(email)
                    .name(name)
                    .nickname(nickname)
                    .grade(grade)
                    .subjects(subjects)
                    .studyHabit(studyHabit)
                    .phone(phone)
                    .profileImageUrl(profileImageUrl)
                    .build();

            // 3. 회원가입 서비스 호출
            authService.register(request);
            return ApiResponse.ok("회원가입이 완료되었습니다.");

        } catch (NumberFormatException e) {
            throw new CustomException("학년(grade)은 숫자여야 합니다.");
        } catch (IOException e) {
            throw new CustomException("프로필 이미지 업로드 중 오류가 발생했습니다.");
        } catch (Exception e) {
            log.error("회원가입 중 예외 발생", e);
            throw new CustomException("회원가입 처리 중 오류가 발생했습니다.");
        }
    }


    /**
     * 로그인 요청 - JWT 발급
     */
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid LoginRequest request, HttpServletResponse response) {
        // 1. 로그인 검증
        authService.login(request);

        // 2. 토큰 생성 후 쿠키로 전달
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        String accessToken = jwtProvider.generateAccessToken(user);

        ResponseCookie accessCookie = ResponseCookie.from(JwtProvider.ACCESS_COOKIE, accessToken)
                .httpOnly(true)
                .secure(true) // 배포 환경에서는 true
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ofHours(1))
                .build();

        response.setHeader("Set-Cookie", accessCookie.toString());

        return ResponseEntity.ok().build(); // ✅ 응답 바디 없음
    }

    /**
     * 로그아웃 - refresh 제거 및 access 블랙리스트 처리
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = JwtProvider.ACCESS_COOKIE, required = false) String accessToken,
                                       HttpServletResponse response) {
        if (accessToken == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN); // 혹은 204 OK
        }

        authService.logout(accessToken);

        // ✅ 쿠키 삭제: Set-Cookie with Max-Age 0
        ResponseCookie deleteCookie = ResponseCookie.from(JwtProvider.ACCESS_COOKIE, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();

        response.setHeader("Set-Cookie", deleteCookie.toString());

        return ResponseEntity.ok().build();
    }


    /**
     * access 토큰 재발급 (Sliding 방식)
     */
    @PostMapping("/reissue")
    public ResponseEntity<Void> reissue(@CookieValue(name = JwtProvider.REFRESH_COOKIE, required = false) String refreshToken,
                                        HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        TokenResponse token = authService.reissue(refreshToken);

        ResponseCookie accessCookie = ResponseCookie.from(JwtProvider.ACCESS_COOKIE, token.getAccessToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ofHours(1))
                .build();

        response.setHeader("Set-Cookie", accessCookie.toString());

        return ResponseEntity.ok().build();
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
     * 비밀번호 재설정 - 새 비밀번호 저장
     */
    @PutMapping("/password/update")
    public ApiResponse<Void> updatePassword(@RequestBody @Valid UpdatePasswordRequest request) {
        authService.updatePassword(request);
        return ApiResponse.ok();
    }

    /**
     * 회원 탈퇴 요청
     */
    @DeleteMapping("/withdraw")
    public ApiResponse<Void> withdraw(@RequestBody @Valid PasswordConfirmRequest request) {
        userService.withdraw(request);
        return ApiResponse.ok();
    }
}
