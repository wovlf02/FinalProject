package com.hamcam.back.entity.study;

import jakarta.persistence.*;
import lombok.*;

/**
 * 발표자에 대한 투표를 기록하는 엔티티
 */
@Entity
@Table(name = "vote")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 어떤 팀방에서의 투표인지 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private TeamRoom room;

    /** 투표 대상 발표자 ID */
    private Long presenterId;

    /** 점수 (1~5) */
    private Integer score;
}
