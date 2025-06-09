package com.hamcam.back.entity.dashboard;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "goal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 사용자 (N:1 관계)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 하루 목표 공부 시간 (분 단위)
     * 예: 150분 = 2시간 30분
     */
    @Column(nullable = false)
    private Integer dailyGoalMinutes;

    /**
     * 이 목표가 설정된 일시
     */
    @Column(nullable = false)
    private LocalDateTime setAt;

    /**
     * GPT에 의해 자동으로 제안된 목표인지 여부
     */
    @Column(nullable = false)
    private boolean isSuggested;
}
