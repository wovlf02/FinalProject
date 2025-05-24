package com.hamcam.back.entity.dashboard;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;     // 중요 / 공지 / 일반 등
    private String text;     // 내용
    private String date;     // "2025.09.15" 형식
}
