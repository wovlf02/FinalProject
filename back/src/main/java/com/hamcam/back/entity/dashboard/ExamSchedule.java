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
    @Column(nullable = false, length = 100)
    private String title;

    /**
     * 과목명 (예: 수학, 영어 등)
     */
    @Column(length = 50)
    private String subject;

    /**
     * 시험일 (D-Day 기준)
     */
    @Column(nullable = false)
    private LocalDate examDate;

    /**
     * 시험 설명 (선택)
     */
    @Column(length = 500)
    private String description;

    /**
     * 시험 장소 (선택)
     */
    @Column(length = 200)
    private String location;

    /**
     * 소속 사용자 (N:1)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
