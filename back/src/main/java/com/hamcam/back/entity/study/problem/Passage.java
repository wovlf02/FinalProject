package com.hamcam.back.entity.study.problem;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "passage")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Passage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "passage_id")
    private Long id;

    @Column(length = 200)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
}
