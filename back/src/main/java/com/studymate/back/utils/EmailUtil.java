package com.studymate.back.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * 이메일 인증번호 생성 및 전송 유틸리티 클래스
 */
@Component
@RequiredArgsConstructor
public class EmailUtil {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}") // 발신자 이메일을 yml에서 가져오기
    private String fromEmail;

    @Value("${spring.mail.sender-name:StudyMate}") // 발신자 이름 (기본값: StudyMate)
    private String senderName;

    /**
     * 인증번호 생성 메서드
     * @return 6자리 인증번호
     */
    public String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000)); // 000000 ~ 999999 범위
    }

    /**
     * 이메일 전송 메서드 -> 네이버 SMTP를 사용하여 인증번호 포함 이메일 전송
     * @param recipientEmail 수신자 이메일 주소
     * @param verificationCode 이메일 인증번호
     * @throws MessagingException 이메일 전송 실패 시 예외 발생
     */
    public void sendVerificationEmail(String recipientEmail, String verificationCode) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

        helper.setTo(recipientEmail);
        helper.setFrom(fromEmail, senderName); // yml에서 가져온 발신자 이메일과 이름 설정
        helper.setSubject("StudyMate 이메일 인증번호입니다.");
        helper.setText(buildEmailContent(verificationCode), true);

        mailSender.send(message);
    }

    /**
     * 이메일 본문 생성 메서드
     * @param verificationCode 이메일 인증번호
     * @return 이메일 본문 HTML
     */
    private String buildEmailContent(String verificationCode) {
        return "<div style='font-family: Arial, sans-serif; padding: 20px; border: 1px solid #ddd;'>"
                + "<h2>StudyMate를 이용해 주셔서 감사합니다.</h2>"
                + "<p>아래의 인증번호를 정확히 입력해주세요.</p>"
                + "<h3 style='color: #007bff; font-size: 24px;'>" + verificationCode + "</h3>"
                + "<p>감사합니다.</p>"
                + "</div>";
    }
}
