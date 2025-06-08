package com.hamcam.back.service.util;

import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * 인증 이메일을 발송하는 서비스입니다.
 * Naver SMTP를 통해 인증코드가 포함된 HTML 메일을 발송합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress; // application.yml의 username을 주입받아 사용

    /**
     * 인증 코드가 담긴 이메일을 발송합니다.
     *
     * @param to   수신자 이메일
     * @param code 인증 코드
     * @param type 요청 타입 (register, find-id, reset-pw 등)
     */
    public void sendVerificationCode(String to, String code, String type) {
        try {
            String subject = getSubjectByType(type);
            String content = buildEmailContent(code, type);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
            log.info("✅ 이메일 전송 성공: to={}, subject={}", to, subject);
        } catch (MessagingException e) {
            log.error("❌ 이메일 전송 실패: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

    /**
     * 요청 타입별 제목 반환
     */
    private String getSubjectByType(String type) {
        return switch (type) {
            case "register" -> "[Hamcam] 회원가입 인증코드 안내";
            case "find-id" -> "[Hamcam] 아이디 찾기 인증코드 안내";
            case "reset-pw" -> "[Hamcam] 비밀번호 재설정 인증코드 안내";
            default -> "[Hamcam] 인증코드 안내";
        };
    }

    /**
     * 인증 이메일 HTML 본문 구성
     */
    private String buildEmailContent(String code, String type) {
        String action = convertTypeToKorean(type);
        return String.format("""
                <div style=\"padding: 20px; font-family: 'Arial', sans-serif; border: 1px solid #ddd;\">
                    <h2 style=\"color: #3478ff;\">Hamcam 인증코드 안내</h2>
                    <p>아래 인증코드를 입력하여 %s를 완료해 주세요.</p>
                    <div style=\"font-size: 24px; font-weight: bold; margin-top: 10px; color: #222;\">%s</div>
                    <p style=\"font-size: 12px; color: #777; margin-top: 20px;\">※ 인증코드는 3분 동안 유효합니다.</p>
                </div>
                """, action, code);
    }

    /**
     * 요청 타입을 한글로 변환
     */
    private String convertTypeToKorean(String type) {
        return switch (type) {
            case "register" -> "회원가입";
            case "find-id" -> "아이디 찾기";
            case "reset-pw" -> "비밀번호 재설정";
            default -> "요청";
        };
    }
}