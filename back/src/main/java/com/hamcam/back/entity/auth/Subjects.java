package com.hamcam.back.entity.auth;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_subjects") // 기존 ElementCollection에서 사용하던 이름 그대로 사용
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subjects {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 과목명 (예: 수학, 영어)
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 소속 사용자 (N:1 관계)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
