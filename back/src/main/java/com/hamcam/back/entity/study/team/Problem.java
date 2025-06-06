package com.hamcam.back.entity.study.team;

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
    private Long problemId;

    @Column(nullable = false, length = 50)
    private String subject;

    @Column(name = "correct_rate")
    private Double correctRate;

    @Column(length = 200)
    private String source;

    @Column(nullable = false, length = 20)
    private String answer;

    @Column(name = "image_path", length = 500)
    private String imagePath;

    @Lob
    private String explanation;

    /**
     * ✅ 국어 지문 외래키 (nullable)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passage_id", nullable = true)
    private Passage passage;

    /**
     * ✅ 단원 외래키 (unit_id)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;
}
