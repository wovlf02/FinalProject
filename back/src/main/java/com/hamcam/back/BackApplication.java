package com.hamcam.back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(scanBasePackages = "com.hamcam.back")
public class BackApplication {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(BackApplication.class, args);
        System.out.println("🚀 StudyMate 백엔드 서버가 실행되었습니다! 🚀");

        // PlanController 빈 등록 여부 확인
        boolean found = false;
        for (String name : ctx.getBeanDefinitionNames()) {
            if (name.toLowerCase().contains("plancontroller")) {
                found = true;
                System.out.println("✅ PlanController 빈 등록 확인: " + name);
            }
        }
        if (!found) {
            System.out.println("❌ PlanController 빈이 등록되지 않았습니다!");
        }
    }
}
