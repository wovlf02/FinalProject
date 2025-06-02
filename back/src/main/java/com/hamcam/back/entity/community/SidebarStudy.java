package com.hamcam.back.entity.community;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SidebarStudy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String info;

    private String schedule;

    private String status; // 모집중 / 마감

    private String tag;

    private String color;

    private String tagColor;

    private int members;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;
}
