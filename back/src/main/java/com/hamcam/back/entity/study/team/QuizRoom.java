package com.hamcam.back.entity.study.team;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@DiscriminatorValue("QUIZ")
public class QuizRoom extends StudyRoom {

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String grade;

    @Column(nullable = false)
    private String month;

    @Column(nullable = false)
    private String difficulty;

    @Column(nullable = false)
    private Long problemId;
}
