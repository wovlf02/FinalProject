package com.studymate.back.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * 이메일 전송 유틸리티 클래스
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmailUtil {

    private final JavaMailSender mailSender;

    /**
     * 이메일 발송 메서드
     * @param to        수신자 이메일 주소
     * @param subject   이메일 제목
     * @param content   이메일 본문
     */
    public void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);  // HTML 형식 가능

            mailSender.send(message);
            log.info("이메일 발송 성공: {}", to);
        } catch (MessagingException e) {
            log.error("이메일 발송 실패: {}", to, e);
            throw new RuntimeException("이메일 발송 중 오류가 발생했습니다.");
        }
    }
}
