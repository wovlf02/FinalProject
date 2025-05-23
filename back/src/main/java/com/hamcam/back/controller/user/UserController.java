package com.hamcam.back.controller.user;

import com.hamcam.back.dto.user.request.UserProfileImageUpdateRequest;
import com.hamcam.back.dto.user.response.UserProfileResponse;
import com.hamcam.back.dto.user.request.UserRequest;
import com.hamcam.back.global.response.ApiResponse;
import com.hamcam.back.service.user.UserService;
import jakarta.validation.Valid;
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

    /** ✅ 내 정보 조회 */
    @PostMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyInfo(@RequestBody @Valid UserRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getMyInfo(request)));
    }

    /** ✅ 회원 탈퇴 */
    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<Void>> withdraw(@RequestBody @Valid UserRequest request) {
        userService.withdraw(request);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    /** ✅ 다른 사용자 정보 조회 */
    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserById(@RequestBody @Valid UserRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getUserProfile(request)));
    }

    /** ✅ 닉네임 변경 */
    @PatchMapping("/nickname")
    public ResponseEntity<ApiResponse<Void>> updateNickname(@RequestBody @Valid UserRequest request) {
        userService.updateNickname(request);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    /** ✅ 이메일 변경 */
    @PatchMapping("/email")
    public ResponseEntity<ApiResponse<Void>> updateEmail(@RequestBody @Valid UserRequest request) {
        userService.updateEmail(request);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    /** ✅ 아이디(username) 변경 */
    @PatchMapping("/username")
    public ResponseEntity<ApiResponse<Void>> updateUsername(@RequestBody @Valid UserRequest request) {
        userService.updateUsername(request);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    /** ✅ 프로필 이미지 변경 */
    @PostMapping("/profile-image")
    public ResponseEntity<ApiResponse<String>> updateProfileImage(@ModelAttribute UserProfileImageUpdateRequest request) {
        String imageUrl = userService.updateProfileImage(request);
        return ResponseEntity.ok(ApiResponse.ok(imageUrl));
    }
}
