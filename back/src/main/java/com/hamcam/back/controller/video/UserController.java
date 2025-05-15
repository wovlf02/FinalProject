package com.hamcam.back.controller.video;

import com.hamcam.back.dto.auth.request.UserDto;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getMyInfo(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

        // accessToken, refreshToken은 이 API에서는 null 처리
        UserDto userDto = new UserDto(
                null, // accessToken
                null, // refreshToken
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getNickname(),
                user.getProfileImageUrl()
        );

        return ResponseEntity.ok(userDto);
    }
}
