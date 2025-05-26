package com.hamcam.back.service.auth;

import com.hamcam.back.dto.auth.request.LoginRequest;
import com.hamcam.back.dto.auth.request.RegisterRequest;
import com.hamcam.back.dto.auth.response.LoginResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.service.util.FileService;
import com.hamcam.back.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final FileService fileService;

    /**
     * ✅ 회원가입 처리 (세션 기반)
     */
    public void register(RegisterRequest request, MultipartFile profileImage, HttpServletRequest httpRequest) {
        // 1. 우선 User 저장
        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .email(request.getEmail())
                .name(request.getName())
                .nickname(request.getNickname())
                .grade(request.getGrade())
                .studyHabit(request.getStudyHabit())
                .phone(request.getPhone())
                .subjects(Optional.ofNullable(request.getSubjects()).orElseGet(ArrayList::new))
                .build();

        user = userRepository.save(user); // ✅ 먼저 DB에 저장

        // 2. 이미지 업로드 → 세션 없이 userId 직접 전달
        String profileImageUrl = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            profileImageUrl = fileService.saveProfileImage(profileImage, user.getId()); // ✅ userId 직접 전달
            user.setProfileImageUrl(profileImageUrl);
            userRepository.save(user); // 다시 update
        }

        // 3. 세션에 로그인 처리 (자동 로그인)
        httpRequest.getSession().setAttribute("userId", user.getId());
    }


    /**
     * ✅ 로그인 처리 (세션에 userId 저장)
     */
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_USER_NOT_FOUND));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_PASSWORD_MISMATCH);
        }

        // ✅ 세션에 사용자 ID 저장
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute("userId", user.getId());

        return LoginResponse.from(user);
    }

    /**
     * ✅ 회원 탈퇴 (세션 기반)
     */
    public void withdraw(HttpServletRequest request) {
        Long userId = SessionUtil.getUserId(request);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        userRepository.delete(user);
    }
}
