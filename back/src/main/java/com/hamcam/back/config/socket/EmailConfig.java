package com.studymate.back.config;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${spring.mail.host}")
    private String mailHost;

    @Value("${spring.mail.port}")
    private int mailPort;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${spring.mail.password}")
    private String mailPassword;

    /**
     * JavaMailSender Bean 설정
     * -> Gmail SMTP를 사용하여 이메일 전송
     * @return JavaMailSender 인스턴스
     */
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // SMTP 서버 설정
        mailSender.setHost(mailHost);
        mailSender.setPort(mailPort);
        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);

        // SMTP 프로퍼티 설정
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "false"); // STARTTLS 비활성화 (SSL 사용 시 불필요)
        props.put("mail.smtp.ssl.enable", "true");       // SSL 활성화
        props.put("mail.smtp.ssl.trust", "smtp.naver.com"); // Gmail SMTP 신뢰
        props.put("mail.debug", "true");                // 디버깅 로그 활성화

        return mailSender;
    }
}
