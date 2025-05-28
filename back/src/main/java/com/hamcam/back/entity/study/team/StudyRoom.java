package com.hamcam.back.entity.study.team;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "room_type")
public abstract class StudyRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    protected String title;

    @Column(nullable = false, unique = true, length = 10)
    protected String inviteCode;

    @Column(length = 30)
    protected String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    protected RoomType roomType;

    @Column(nullable = false)
    protected boolean isActive = true;

    @Column(nullable = false, updatable = false)
    protected LocalDateTime createdAt = LocalDateTime.now();

    public void generateInviteCode() {
        this.inviteCode = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    public void deactivate() {
        this.isActive = false;
    }
}
