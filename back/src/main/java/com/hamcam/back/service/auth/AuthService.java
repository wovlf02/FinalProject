package com.hamcam.back.service.auth;

import com.hamcam.back.dto.auth.request.LoginRequest;
import com.hamcam.back.dto.auth.request.RegisterRequest;
import com.hamcam.back.dto.auth.response.LoginResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.service.util.FileService;
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
     * 회원가입 처리
     * - 사용자명, 이메일 중복 확인
     * - 비밀번호 암호화 없이 저장
     * - 프로필 이미지 저장 시 경로 저장
     */
    public void register(RegisterRequest request, MultipartFile profileImage) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        String profileImageUrl = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            profileImageUrl = fileService.saveProfileImage(profileImage);
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())  // ✅ 암호화 없음
                .email(request.getEmail())
                .name(request.getName())
                .nickname(request.getNickname())
                .grade(request.getGrade())
                .studyHabit(request.getStudyHabit())
                .phone(request.getPhone())
                .profileImageUrl(profileImageUrl)
                .subjects(Optional.ofNullable(request.getSubjects()).orElseGet(ArrayList::new))
                .build();

        userRepository.save(user);
    }

    /**
     * 로그인 처리
     * - 아이디/비밀번호 검증
     * - 성공 시 DB의 전체 유저 정보 반환 → 프론트 LocalStorage 저장
     */
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_USER_NOT_FOUND));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_PASSWORD_MISMATCH);
        }

        return LoginResponse.from(user); // ✅ 변환
    }


    /**
     * 회원 탈퇴 처리
     * - userId에 해당하는 사용자 DB에서 즉시 삭제
     * - 비밀번호 확인 생략
     */
    public void withdraw(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        userRepository.delete(user);
    }
}
