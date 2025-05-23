package com.hamcam.back.entity.study;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "team_room")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomName;

    @Enumerated(EnumType.STRING)
    private TeamRoomMode mode;

    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    private String password;

    private Integer targetTime;

    private Integer currentQuestionIndex;

    private Long currentPresenterId;

    @ElementCollection
    @CollectionTable(name = "team_room_raised_hands", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "user_id")
    private Set<Long> raisedHands = new HashSet<>();

    private Long winnerId;

    private LocalDateTime quizStartedAt;

    private LocalDateTime focusCompletedAt;

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    /** 발표자에 대한 투표 리스트 */
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> voteList = new ArrayList<>();

    // === 유틸 ===
    public void addVote(Vote vote) {
        vote.setRoom(this);
        voteList.add(vote);
    }
}
