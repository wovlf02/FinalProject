package com.hamcam.back.entity.study.team;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "room_type") // 이 컬럼은 Hibernate가 자동 관리
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class StudyRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 30)
    private String password;

    @Column(nullable = false, unique = true)
    private String inviteCode;

    private boolean isActive;

    @CreationTimestamp
    private LocalDateTime createdAt;

    /** ✅ 참가자 리스트 (삭제 연동) */
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyRoomParticipant> participants;

    /** ✅ 하위 클래스에서 roomType을 명시적으로 제공하도록 강제 */
    public abstract RoomType getRoomType();
}
