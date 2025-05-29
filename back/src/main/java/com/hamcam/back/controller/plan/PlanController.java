package com.hamcam.back.controller.plan;

import com.hamcam.back.dto.plan.PlanRequest;
import com.hamcam.back.entity.plan.StudyPlan;
import com.hamcam.back.repository.plan.StudyPlanRepository;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/plan")
public class PlanController {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Autowired
    private StudyPlanRepository studyPlanRepository;

    @PostMapping("/generate")
    public ResponseEntity<String> generatePlan(@RequestBody PlanRequest request, HttpServletRequest httpRequest) {
        Object userIdObj = httpRequest.getSession().getAttribute("userId");
        if (userIdObj == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }
        String userId = String.valueOf(userIdObj);

        if (request.getRange() == null || request.getRange().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("학습 범위(units)를 반드시 선택해야 합니다.");
        }

        // 오늘 날짜와 종료 날짜 계산
        LocalDate today = LocalDate.now();
        int totalDays = request.getWeeks() * 7;
        LocalDate endDate = today.plusDays(totalDays - 1);

        // 프롬프트 생성: 날짜 컬럼 포함
        String prompt = request.getPrompt();
        if (prompt == null || prompt.isBlank()) {
            prompt = String.format(
                "오늘 날짜(%s)부터 %d주 동안 %s \"%s\" 범위 학습계획을 마크다운 표로 만들어줘. " +
                "표에는 날짜(YYYY-MM-DD), 학습 목표, 시간, 주요 과제, 참고사항 컬럼만 포함해. " +
                "각 날짜는 오늘(%s)부터 하루씩 증가해서 %s까지 실제 날짜(YYYY-MM-DD)로 써줘. " +
                "\"Day 1\", \"Day 2\", \"월\", \"화\" 이런 식으로 쓰지 말고, 반드시 2025-05-30처럼 써. " +
                "\"%s\" 범위 외의 내용은 절대 넣지 마. " +
                "표 아래에는 아무 설명도 붙이지 마.",
                today, request.getWeeks(), request.getSubject(), request.getRange(),
                today, endDate, request.getRange()
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

        Map responseBody = response.getBody();
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

        System.out.println("planText 저장 전: [" + planText + "]");

        if (planText != null) {
            planText = planText.trim();
            planText = planText.replace("\r\n", "\n").replace("\r", "\n");
            if (!planText.startsWith("\n\n")) {
                planText = "\n\n" + planText;
            }
        }

        StudyPlan plan = new StudyPlan();
        plan.setUserId(userId);
        plan.setSubject(request.getSubject());
        plan.setGrade(request.getGrade());
        plan.setWeeks(request.getWeeks());
        plan.setUnits(request.getRange());
        plan.setPlanContent(planText);
        plan.setChecked(false); // 생성 시 기본값
        studyPlanRepository.save(plan);

        return ResponseEntity.ok(planText);
    }

    @GetMapping("/my")
    public ResponseEntity<List<StudyPlan>> getMyPlans(HttpServletRequest httpRequest) {
        Object userIdObj = httpRequest.getSession().getAttribute("userId");
        if (userIdObj == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.emptyList());
        }
        String userId = String.valueOf(userIdObj);
        List<StudyPlan> plans = studyPlanRepository.findByUserId(userId);
        return ResponseEntity.ok(plans);
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<?> deletePlan(@PathVariable("planId") Long planId, HttpServletRequest httpRequest) {
        Object userIdObj = httpRequest.getSession().getAttribute("userId");
        if (userIdObj == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        String userId = String.valueOf(userIdObj);

        Optional<StudyPlan> planOpt = studyPlanRepository.findById(planId);
        if (planOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("계획을 찾을 수 없습니다.");
        }
        StudyPlan plan = planOpt.get();
        if (!userId.equals(plan.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("본인 계획만 삭제할 수 있습니다.");
        }
        studyPlanRepository.deleteById(planId);
        return ResponseEntity.ok("삭제되었습니다.");
    }

    @PatchMapping("/{planId}/check")
    public ResponseEntity<?> checkPlan(
            @PathVariable("planId") Long planId,
            @RequestParam("checked") boolean checked,
            HttpServletRequest httpRequest
    ) {
        Object userIdObj = httpRequest.getSession().getAttribute("userId");
        if (userIdObj == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        String userId = String.valueOf(userIdObj);

        Optional<StudyPlan> planOpt = studyPlanRepository.findById(planId);
        if (planOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("계획을 찾을 수 없습니다.");
        }
        StudyPlan plan = planOpt.get();
        if (!userId.equals(plan.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("본인 계획만 체크할 수 있습니다.");
        }
        plan.setChecked(checked);
        studyPlanRepository.save(plan);
        return ResponseEntity.ok("상태가 변경되었습니다.");
    }

    // === planContent(계획 내용) 수정 API ===
    @PatchMapping("/{planId}/content")
    public ResponseEntity<?> updatePlanContent(
            @PathVariable("planId") Long planId,
            @RequestBody Map<String, String> body,
            HttpServletRequest httpRequest
    ) {
        Object userIdObj = httpRequest.getSession().getAttribute("userId");
        if (userIdObj == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        String userId = String.valueOf(userIdObj);

        Optional<StudyPlan> planOpt = studyPlanRepository.findById(planId);
        if (planOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("계획을 찾을 수 없습니다.");
        }
        StudyPlan plan = planOpt.get();
        if (!userId.equals(plan.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("본인 계획만 수정할 수 있습니다.");
        }
        String newContent = body.get("planContent");
        if (newContent == null || newContent.isBlank()) {
            return ResponseEntity.badRequest().body("내용이 비어 있습니다.");
        }
        plan.setPlanContent(newContent);
        studyPlanRepository.save(plan);
        return ResponseEntity.ok("계획이 수정되었습니다.");
    }
}
