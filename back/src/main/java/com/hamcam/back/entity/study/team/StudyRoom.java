package com.hamcam.back.entity.study.team;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ✅ 추상 팀 학습방 엔티티 (상속 구조)
 * QUIZ / FOCUS 타입으로 상속 분기됨
 */
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

    /**
     * ✅ DiscriminatorColumn에 의해 자동 관리됨
     * - 직접 세팅 금지
     * - insert/update 불가
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", insertable = false, updatable = false)
    protected RoomType roomType;

    @Column(nullable = false)
    protected boolean isActive = true;

    /** ✅ 방장 정보 (Not Null) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    protected User host;

    /** ✅ 참여자 목록 */
    @OneToMany(mappedBy = "studyRoom", cascade = CascadeType.ALL)
    private List<StudyRoomParticipant> participants;

    /**
     * ✅ 하위 클래스에서 호출 (roomType 포함)
     * @param title       방 제목
     * @param password    비밀번호
     * @param inviteCode  초대 코드
     * @param roomType    방 타입 (FOCUS 또는 QUIZ)
     * @param host        방장 (User)
     */
    protected StudyRoom(String title, String password, String inviteCode, RoomType roomType, User host) {
        this.title = title;
        this.password = password;
        this.inviteCode = inviteCode;
        this.roomType = roomType;
        this.host = host;
    }

    /** ✅ 방 비활성화 */
    public void deactivate() {
        this.isActive = false;
    }

    /** ✅ 방장 ID 반환 (isHost 체크용) */
    public Long getHostId() {
        return host != null ? host.getId() : null;
    }
}
