package com.hamcam.back.controller.auth;

import com.hamcam.back.dto.auth.request.PasswordConfirmRequest;
import com.hamcam.back.dto.auth.response.UserProfileResponse;
import com.hamcam.back.service.auth.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("authUserController")
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    /**
     * 마이페이지: 내 정보 조회
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyInfo() {
        return ResponseEntity.ok(userService.getMyProfile());
    }

    /**
     * 회원 탈퇴
     */
    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdraw(@RequestBody PasswordConfirmRequest request) {
        userService.withdraw(request);
        return ResponseEntity.ok().build();
    }
}

