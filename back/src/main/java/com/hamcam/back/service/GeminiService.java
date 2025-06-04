package com.hamcam.back.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent")
            .build();

    public Mono<String> getGeminiFeedback(String question, String userAnswer, String correctAnswer, String explanation, String imageUrl, String base64Image) {
        String prompt = String.format(
            "문제: %s\n제출한 답: %s\n정답: %s\n해설: %s\n" +
            "네가 인지한 제출한 답과 정답이 무엇인지 말해줘" +
            "위 정보를 바탕으로 학생에게 문제에 대한 설명명을 3~4문장으로 제공해줘. " +
            "정답과 제출한 답이 다르면 반드시 오답임을 명확히 알려주고, " +
            "정답과 제출한 답이 같으면 칭찬과 함께 해설을 요약해줘.",
            question, userAnswer, correctAnswer, explanation
        );
        List<Map<String, Object>> parts = new ArrayList<>();
        parts.add(Map.of("text", prompt));
        if (base64Image != null && !base64Image.isEmpty()) {
            parts.add(Map.of(
                "inline_data", Map.of(
                    "mime_type", "image/png", // 실제 이미지 타입에 맞게 수정 가능
                    "data", base64Image
                )
            ));
        }
        Map<String, Object> body = Map.of(
            "contents", List.of(
                Map.of("parts", parts)
            )
        );
        System.out.println("Gemini API 요청 body: " + body);
        return webClient.post()
            .uri(uriBuilder -> uriBuilder.queryParam("key", geminiApiKey).build())
            .header("Content-Type", "application/json")
            .bodyValue(body)
            .retrieve()
            .bodyToMono(Map.class)
            .map(response -> {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                    List<Map<String, Object>> responseParts = (List<Map<String, Object>>) content.get("parts");
                    if (responseParts != null && !responseParts.isEmpty()) {
                        return (String) responseParts.get(0).get("text");
                    }
                }
                return "AI 피드백을 생성하지 못했습니다.";
            })
            .onErrorResume(e -> Mono.just("Gemini API 호출 실패: " + e.getMessage()));
    }
} 