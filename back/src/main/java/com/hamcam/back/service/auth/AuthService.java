package com.hamcam.back.service.auth;

import com.hamcam.back.config.auth.JwtProvider;
import com.hamcam.back.dto.auth.request.*;
import com.hamcam.back.dto.auth.response.LoginResponse;
import com.hamcam.back.dto.auth.response.TokenResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public boolean checkUsername(UsernameCheckRequest request) {
        return !userRepository.existsByUsername(request.getUsername());
    }

    public boolean checkNickname(NicknameCheckRequest request) {
        return !userRepository.existsByNickname(request.getNickname());
    }

    public boolean checkEmail(EmailRequest request) {
        return !userRepository.existsByEmail(request.getEmail());
    }

    public String sendVerificationCode(EmailSendRequest request) {
        return "인증 코드가 전송되었습니다.";
    }

    public boolean verifyCode(EmailVerifyRequest request) {
        return true;
    }

    public void deleteTempData(EmailRequest request) {
    }

    public void register(RegisterRequest request) {
    }

    public LoginResponse login(LoginRequest request) {
        return login(request.getUsername(), request.getPassword());
    }

    public LoginResponse login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }

    public void logout(TokenRequest request) {
    }

    public TokenResponse reissue(TokenRequest request) {
        User user = userRepository.findById(1L).orElseThrow(); // TODO: 실제 사용자 식별 필요
        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);

        return new TokenResponse(accessToken, refreshToken);
    }

    public String sendFindUsernameCode(EmailRequest request) {
        return "아이디 찾기 인증 코드 전송 완료";
    }

    public String verifyFindUsernameCode(EmailVerifyRequest request) {
        return "user123";
    }

    public String requestPasswordReset(PasswordResetRequest request) {
        return "비밀번호 재설정 요청 완료";
    }

    public boolean verifyPasswordResetCode(EmailVerifyRequest request) {
        return true;
    }

    public void updatePassword(PasswordChangeRequest request) {
    }

    public void withdraw(PasswordConfirmRequest request) {
    }
}
