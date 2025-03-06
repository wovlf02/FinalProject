package com.studymate.back.repository;

import com.studymate.back.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * FriendRepository (친구 관계 리포지토리)
 * friends 테이블과 연동되는 JPA Repository
 * 기본적인 CRUD 기능 제공
 * 특정 사용자의 친구 목록 조회 기능 추가
 * 두 사용자가 친구인지 확인하는 기능 추가
 * 친구 관계 삭제 기능 추가
 */
@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {

    /**
     * 특정 사용자의 친구 목록 조회
     * userId를 기준으로 해당 사용자의 친구 목록을 가져옴
     * @param userId    친구 목록을 조회할 사용자 ID
     * @return  해당 사용자의 친구 목록
     */
    List<Friend> findByUserId(Long userId);

    /**
     * 특정 사용자와 특정 사용자가 친구인지 확인
     * 친구 관계를 확인하여 true/false 반환
     * @param userId    확인할 사용자 ID
     * @param friendId  확인할 친구 ID
     * @return  친구 관계가 존재하면 true, 없으면 false
     */
    boolean existsByUserIdAndFriendId(Long userId, Long friendId);

    /**
     * 친구 관계삭제
     * 특정 사용자와 특정 친구 ID를 기준으로 친구 관계삭제
     * 양방향 관계이므로 서로 삭제 필요
     * @param userId    삭제할 사용자 ID
     * @param friendId  삭제할 친구 ID
     */
    void deleteByUserIdAndFriendId(Long userId, Long friendId);
}
