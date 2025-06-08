package com.hamcam.back.controller.user;

import com.hamcam.back.dto.user.request.UserProfileImageUpdateRequest;
import com.hamcam.back.dto.user.response.UserProfileResponse;
import com.hamcam.back.global.response.ApiResponse;
import com.hamcam.back.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** ✅ 내 프로필 조회 */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyInfo(HttpServletRequest request) {
        UserProfileResponse response = userService.getMyInfo(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /** ✅ 회원 탈퇴 */
    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<Void>> withdraw(HttpServletRequest request) {
        userService.withdraw(request);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    /** ✅ 다른 사용자 프로필 조회 (현재는 내 프로필과 동일하게 처리) */
    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserById(HttpServletRequest request) {
        UserProfileResponse response = userService.getUserProfile(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /** ✅ 프로필 이미지 변경 */
    @PostMapping("/profile-image")
    public ResponseEntity<ApiResponse<String>> updateProfileImage(
            @ModelAttribute UserProfileImageUpdateRequest request,
            HttpServletRequest httpRequest
    ) {
        String imageUrl = userService.updateProfileImage(request, httpRequest);
        return ResponseEntity.ok(ApiResponse.ok(imageUrl));
    }
}
