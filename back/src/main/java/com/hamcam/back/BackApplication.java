package com.hamcam.back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(scanBasePackages = "com.hamcam.back")
public class BackApplication {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(BackApplication.class, args);
        System.out.println("🚀 Hamcam 백엔드 서버가 실행되었습니다! 🚀");
    }
}
