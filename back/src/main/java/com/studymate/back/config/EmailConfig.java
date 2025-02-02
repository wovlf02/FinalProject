package com.studymate.back.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Spring Boot에서 Gmail SMTP를 사용하기 위한 설정 클래스
 */
@Configuration
public class EmailConfig {

    /**
     * JavaMailSender Bean 설정
     * -> Gmail SMTP를 사용하여 이메일 전송
     * @return JavaMailSender 인스턴스
     */
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // 환경 변수에서 값 가져오기 (기본값 설정)
        String host = System.getenv("MAIL_HOST") != null ? System.getenv("MAIL_HOST") : "smtp.gmail.com";
        int port = System.getenv("MAIL_PORT") != null ? Integer.parseInt(System.getenv("MAIL_PORT")) : 587;
        String username = System.getenv("MAIL_USERNAME") != null ? System.getenv("MAIL_USERNAME") : "";
        String password = System.getenv("MAIL_PASSWORD") != null ? System.getenv("MAIL_PASSWORD") : "";

        // SMTP 서버 설정
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        // SMTP 프로퍼티 설정
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.enable", "true"); // SSL 활성화
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.debug", "true"); // 디버깅 로그 활성화

        return mailSender;
    }
}
