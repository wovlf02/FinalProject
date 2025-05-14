package com.hamcam.back.entity.exam;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "passage")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Passage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "passage_id")
    private Integer passageId;

    @Column(name = "title")
    private String title;

    @Column(name = "content", nullable = false)
    private String content;
} 