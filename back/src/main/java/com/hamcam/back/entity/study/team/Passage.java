package com.hamcam.back.entity.study.team;

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
    private Long passageId;

    @Column(length = 200)
    private String title;

    @Lob
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;
}
