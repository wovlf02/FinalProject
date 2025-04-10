package com.hamcam.back.auth.util;

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

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.mail.sender-name:HamCam}")
    private String senderName;

    /**
     * 6자리 랜덤 인증번호 생성
     * @return 6자리 인증번호 (000000 ~ 999999)
     */
    public String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    /**
     * 인증번호가 포함된 이메일 전송
     * @param receipientEmail 수신자 이메일
     * @param verificationCode 인증번호
     * @throws MessagingException 이메일 전송 실패 시
     * @throws UnsupportedEncodingException 발신자 이름 인코딩 실패 시
     */
    public void sendVerificationEmail(String receipientEmail, String verificationCode) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

        helper.setTo(receipientEmail);
        helper.setFrom(fromEmail, senderName);
        helper.setSubject("함캠 이메일 인증번호입니다.");
        helper.setText(buildEmailContent(verificationCode), true);

        mailSender.send(message);
    }

    /**
     * 이메일 본문 HTML 구성
     * @param verificationCode 인증번호
     * @return HTML 형식 본문
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
