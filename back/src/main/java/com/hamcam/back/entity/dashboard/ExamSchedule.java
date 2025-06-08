package com.hamcam.back.entity.dashboard;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "exam_schedule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 시험명 (예: 중간고사, 모의고사 등)
     */
    @Column(name = "exam_name", nullable = false, length = 100) // 🔁 이름 명확하게
    private String examName;

    /**
     * 시험일 (D-Day 기준)
     */
    @Column(nullable = false)
    private LocalDate examDate;

    /**
     * 소속 사용자 (N:1)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
