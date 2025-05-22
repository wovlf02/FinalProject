package com.hamcam.back.controller.user;

import com.hamcam.back.dto.auth.response.UserProfileResponse;
import com.hamcam.back.dto.user.request.*;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.global.response.ApiResponse;
import com.hamcam.back.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * ✅ 내 정보 조회
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyInfo(@RequestParam("userId") Long userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(UserProfileResponse.from(user));
    }

    /**
     * ✅ 회원 탈퇴
     */
    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdraw(
            @RequestParam("userId") Long userId,
            @RequestBody @Valid PasswordConfirmRequest request
    ) {
        userService.withdraw(userId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * ✅ 다른 사용자 정보 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(UserProfileResponse.from(user));
    }

    /**
     * ✅ 닉네임 변경
     */
    @PatchMapping("/nickname")
    public ApiResponse<Void> updateNickname(
            @RequestParam("userId") Long userId,
            @RequestBody @Valid UpdateNicknameRequest request
    ) {
        userService.updateNickname(userId, request.getNickname());
        return ApiResponse.ok();
    }

    /**
     * ✅ 이메일 변경
     */
    @PatchMapping("/email")
    public ApiResponse<Void> updateEmail(
            @RequestParam("userId") Long userId,
            @RequestBody @Valid UpdateEmailRequest request
    ) {
        userService.updateEmail(userId, request.getEmail());
        return ApiResponse.ok();
    }

    /**
     * ✅ 아이디(username) 변경
     */
    @PatchMapping("/username")
    public ApiResponse<Void> updateUsername(
            @RequestParam("userId") Long userId,
            @RequestBody @Valid UpdateUsernameRequest request
    ) {
        userService.updateUsername(userId, request.getUsername());
        return ApiResponse.ok();
    }

    /**
     * ✅ 프로필 이미지 변경
     */
    @PatchMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> updateProfileImage(
            @RequestParam("userId") Long userId,
            @RequestPart("profileImage") MultipartFile file
    ) {
        try {
            String storedFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadDir = Paths.get("uploads/profile/" + userId);
            Files.createDirectories(uploadDir);

            Path filePath = uploadDir.resolve(storedFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String imageUrl = "/uploads/profile/" + userId + "/" + storedFileName;
            userService.updateProfileImage(userId, imageUrl);

            return ApiResponse.ok(imageUrl);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }
}
