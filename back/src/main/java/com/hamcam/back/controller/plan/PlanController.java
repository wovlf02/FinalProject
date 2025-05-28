package com.hamcam.back.controller.plan;

import com.hamcam.back.dto.plan.PlanRequest;
import com.hamcam.back.entity.plan.StudyPlan;
import com.hamcam.back.repository.plan.StudyPlanRepository;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;

@RestController
@RequestMapping("/api/plan")
public class PlanController {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Autowired
    private StudyPlanRepository studyPlanRepository;

    @PostMapping("/generate")
    public ResponseEntity<String> generatePlan(@RequestBody PlanRequest request) {
        // === [추가] units(범위) 필수 입력 검증 ===
        if (request.getRange() == null || request.getRange().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("학습 범위(units)를 반드시 선택해야 합니다.");
        }

        // 1. 프론트엔드에서 prompt를 직접 보냈으면 그걸 사용
        String prompt = request.getPrompt();

        // 2. prompt가 없으면(호환성) 기존 방식으로 생성
        if (prompt == null || prompt.isBlank()) {
            prompt = String.format(
                "%s %s 과목의 \"%s\" 범위만 포함해서 %d주 학습 계획을 마크다운 표로 만들어줘. " +
                "표에는 Day, 학습 목표, 시간, 주요 과제, 참고사항 컬럼만 포함하고, 반드시 \"%s\"와 관련된 내용만 넣어줘. " +
                "\"%s\" 범위 외의 다른 단원, 중학교/초등학교 수준의 내용은 절대 포함하지 마. " +
                "표 아래에는 아무 설명도 붙이지 마.",
                request.getGrade(), request.getSubject(), request.getRange(), request.getWeeks(), request.getRange(), request.getRange()
            );
        }

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

        Map<String, Object> body = Map.of(
            "contents", List.of(
                Map.of("parts", List.of(Map.of("text", prompt)))
            )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

        // Gemini 응답에서 텍스트만 추출
        Map responseBody = response.getBody();

        // [1] Gemini 응답 전체 로그 추가
        System.out.println("Gemini 응답 전체: " + responseBody);

        String planText = "";
        if (responseBody != null && responseBody.containsKey("candidates")) {
            List candidates = (List) responseBody.get("candidates");
            if (!candidates.isEmpty()) {
                Map first = (Map) candidates.get(0);
                Map content = (Map) first.get("content");
                List parts = (List) content.get("parts");
                if (!parts.isEmpty()) {
                    Map part = (Map) parts.get(0);
                    planText = (String) part.get("text");
                }
            }
        }

        // [2] planText 로그 추가
        System.out.println("planText 저장 전: [" + planText + "]");

        // === [최종] 줄바꿈, 공백, 개행 모두 처리 ===
        if (planText != null) {
            planText = planText.trim();
            planText = planText.replace("\r\n", "\n").replace("\r", "\n");
            if (!planText.startsWith("\n\n")) {
                planText = "\n\n" + planText;
            }
        }

        // === DB 저장 코드 (userId를 무조건 spongebob1234로) ===
        String userId = "spongebob1234";
        StudyPlan plan = new StudyPlan();
        plan.setUserId(userId);
        plan.setSubject(request.getSubject());
        plan.setGrade(request.getGrade());
        plan.setWeeks(request.getWeeks());
        plan.setUnits(request.getRange());
        plan.setPlanContent(planText);
        studyPlanRepository.save(plan);

        return ResponseEntity.ok(planText);
    }

    // === [추가] 내 학습계획 목록 조회 ===
    @GetMapping("/my")
    public ResponseEntity<List<StudyPlan>> getMyPlans() {
        List<StudyPlan> plans = studyPlanRepository.findByUserId("spongebob1234");
        return ResponseEntity.ok(plans);
    }
}
