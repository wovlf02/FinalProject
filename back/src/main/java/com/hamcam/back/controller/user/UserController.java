package com.hamcam.back.controller.user;

import com.hamcam.back.dto.auth.request.PasswordConfirmRequest;
import com.hamcam.back.dto.auth.response.UserProfileResponse;
import com.hamcam.back.dto.user.request.*;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.global.response.ApiResponse;
import com.hamcam.back.global.security.SecurityUtil;
import com.hamcam.back.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * [UserController]
 * 사용자 프로필 관련 API 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SecurityUtil securityUtil;

    /**
     * ✅ 내 정보 조회 (쿠키 기반 인증)
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyInfo() {
        User user = securityUtil.getCurrentUser();
        return ResponseEntity.ok(UserProfileResponse.from(user));
    }

    /**
     * ✅ 회원 탈퇴 (비밀번호 확인)
     */
    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdraw(@RequestBody @Valid PasswordConfirmRequest request) {
        userService.withdraw(request);
        return ResponseEntity.ok().build();
    }

    /**
     * ✅ 다른 사용자 ID로 프로필 조회 (친구, 커뮤니티 등에서 사용)
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable Long id) {
        User user = securityUtil.getUserById(id);
        return ResponseEntity.ok(UserProfileResponse.from(user));
    }

    /**
     * ✅ 닉네임 변경
     */
    @PatchMapping("/nickname")
    public ApiResponse<Void> updateNickname(@RequestBody @Valid UpdateNicknameRequest request) {
        userService.updateNickname(request.getNickname());
        return ApiResponse.ok();
    }

    /**
     * ✅ 이메일 변경
     */
    @PatchMapping("/email")
    public ApiResponse<Void> updateEmail(@RequestBody @Valid UpdateEmailRequest request) {
        userService.updateEmail(request.getEmail());
        return ApiResponse.ok();
    }

    /**
     * ✅ 아이디(username) 변경
     */
    @PatchMapping("/username")
    public ApiResponse<Void> updateUsername(@RequestBody @Valid UpdateUsernameRequest request) {
        userService.updateUsername(request.getUsername());
        return ApiResponse.ok();
    }

    /**
     * ✅ 프로필 이미지 변경 (Multipart 전송)
     */
    @PatchMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> updateProfileImage(@RequestPart("profileImage") MultipartFile file) {
        try {
            // 파일명 유니크 생성
            String storedFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadDir = Paths.get("uploads/profile");
            Files.createDirectories(uploadDir);

            Path filePath = uploadDir.resolve(storedFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String imageUrl = "/uploads/profile/" + storedFileName;

            // 서비스 레이어에서 DB 반영
            userService.updateProfileImage(imageUrl);

            return ApiResponse.ok(imageUrl);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }
}
