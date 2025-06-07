package com.hamcam.back.entity.study.team;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "units")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20)
    private String subject;

    @Column(length = 50)
    private String category;

    @Column(length = 255)
    private String unit;
}