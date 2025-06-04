package com.hamcam.back.entity.study.team;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "problem")  // ✅ 실제 테이블명 확인 필요 (복수형이면 "problems"로 유지)
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

    // ✅ precision/scale 제거: Double 타입에는 의미 없음
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
     * ✅ passage_id 외래키: nullable 가능
     * 지문이 없는 문제도 있으므로 Optional 관계로 설정
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passage_id", nullable = true)
    private Passage passage;
}
