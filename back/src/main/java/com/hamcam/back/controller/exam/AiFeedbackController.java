package com.hamcam.back.controller.exam;

import com.hamcam.back.dto.AiFeedbackRequest;
import com.hamcam.back.service.GeminiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AiFeedbackController {

    private final GeminiService geminiService;

    public AiFeedbackController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/ai-feedback")
    public Mono<String> getAiFeedback(@RequestBody Map<String, String> request) {
        return geminiService.getGeminiFeedback(
            request.get("question"),
            request.get("userAnswer"),
            request.get("correctAnswer"),
            request.get("explanation"),
            request.get("imageUrl"),
            request.get("base64Image")
        );
    }
}
