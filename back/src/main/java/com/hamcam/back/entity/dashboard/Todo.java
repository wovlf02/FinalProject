package com.hamcam.back.entity.dashboard;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "todo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 할 일 제목
     */
    @Column(nullable = false, length = 100)
    private String title;

    /**
     * 할 일 설명 (선택)
     */
    @Column(length = 1000)
    private String description;

    /**
     * 할 일 날짜 (YYYY-MM-DD)
     */
    @Column(nullable = false)
    private LocalDate todoDate;

    /**
     * 우선순위 (낮음: 1, 중간: 2, 높음: 3)
     */
    @Column(nullable = false)
    private Integer priority;

    /**
     * 완료 여부
     */
    @Column(nullable = false)
    private boolean isCompleted;

    /**
     * 작성자 (N:1)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
