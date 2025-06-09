package com.hamcam.back.dto.dashboard.exam.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamScheduleResponse {
    private Long id;
    private String title;
    private String subject;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate examDate;
    
    private String description;
    private String location;
    private Long dDay; // D-Day 계산값
}
