package com.hamcam.back.entity.dashboard;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "study_time")
public class StudyTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Integer weeklyGoalMinutes;

    @Column(nullable = false)
    private Integer todayGoalMinutes;

    @Column(nullable = false)
    private Integer todayStudyMinutes;

    @Column(nullable = false)
    private LocalDate date = LocalDate.now();

    @Builder
    public StudyTime(User user, Integer weeklyGoalMinutes, Integer todayGoalMinutes, Integer todayStudyMinutes) {
        this.user = user;
        this.weeklyGoalMinutes = weeklyGoalMinutes;
        this.todayGoalMinutes = todayGoalMinutes;
        this.todayStudyMinutes = todayStudyMinutes;
    }

    public static StudyTime createDefault(User user) {
        return StudyTime.builder()
                .user(user)
                .weeklyGoalMinutes(0)
                .todayGoalMinutes(0)
                .todayStudyMinutes(0)
                .build();
    }

    public void updateGoals(Integer weeklyGoalMinutes, Integer todayGoalMinutes) {
        this.weeklyGoalMinutes = weeklyGoalMinutes;
        this.todayGoalMinutes = todayGoalMinutes;
    }

    public void updateTodayStudyMinutes(Integer todayStudyMinutes) {
        this.todayStudyMinutes = todayStudyMinutes;
    }

    public void setWeeklyGoalMinutes(Integer weeklyGoalMinutes) {
        this.weeklyGoalMinutes = weeklyGoalMinutes;
    }

    public void setTodayGoalMinutes(Integer todayGoalMinutes) {
        this.todayGoalMinutes = todayGoalMinutes;
    }

    public void setTodayStudyMinutes(Integer todayStudyMinutes) {
        this.todayStudyMinutes = todayStudyMinutes;
    }
}
