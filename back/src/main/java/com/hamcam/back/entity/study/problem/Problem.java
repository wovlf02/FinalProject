package com.hamcam.back.entity.study.problem;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "problem")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "problem_id")
    private Long id;

    @Column(length = 50, nullable = false)
    private String subject;

    @Column(name = "correct_rate") // ✅ precision/scale 제거
    private Double correctRate;

    @Column(length = 200)
    private String source;

    @Column(length = 20, nullable = false)
    private String answer;

    @Column(name = "image_path", length = 500)
    private String imagePath;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passage_id")
    private Passage passage;
}
