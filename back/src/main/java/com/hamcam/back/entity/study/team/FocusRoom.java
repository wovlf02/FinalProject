package com.hamcam.back.entity.study.team;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@DiscriminatorValue("FOCUS")
public class FocusRoom extends StudyRoom {

    @Column(nullable = false)
    private Integer targetTime;
}
