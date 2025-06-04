package com.hamcam.back.entity.exam;

import jakarta.persistence.*;
import lombok.*;
import lombok.Getter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "problem")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "problem_id")
    private Integer problemId;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "correct_rate")
    private Double correctRate;

    @Column(name = "source")
    private String source;

    @Column(name = "answer", nullable = false)
    private String answer;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "explanation")
    private String explanation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passage_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Passage passage;
    
    public Double getCorrectRate() {
        return this.correctRate;
    }
} 