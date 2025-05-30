package com.hamcam.back.entity.study.team;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "room_type")
public abstract class StudyRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    protected String title;

    @Column(length = 30)
    protected String password;

    @Column(nullable = false, unique = true)
    protected String inviteCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", insertable = false, updatable = false)
    protected RoomType roomType;

    @Column(nullable = false)
    protected boolean isActive = true;

    /** ✅ 방장 정보 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    protected User host;

    // 생성자 (하위 클래스에서 사용)
    protected StudyRoom(String title, String password, String inviteCode, RoomType roomType, User host) {
        this.title = title;
        this.password = password;
        this.inviteCode = inviteCode;
        this.roomType = roomType;
        this.host = host;
    }

    public void deactivate() {
        this.isActive = false;
    }
}
