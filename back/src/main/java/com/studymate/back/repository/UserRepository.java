package com.studymate.back.repository;

import com.studymate.back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository (사용자 리포지토리)
 * users 테이블과 연동되는 JPA Repository
 * 기본적인 CRUD 기능 제공
 * 사용자 아이디(username), 닉네임(nickname), 이메일(email) 기반 조회 기능 추가
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 아이디(username)로 사용자 찾기
     * @param username  조회할 사용자 아이디
     * @return  Optional<User> 객체 반환 (존재하지 않을 경우 Optional.empty())
     */
    Optional<User> findByUsername(String username);

    /**
     * 이메일(email)로 사용자 찾기
     * @param email 조회할 사용자 이메일
     * @return  Optional<User> 객체 반환 (존재하지 않을 경우 Optional.empty())
     */
    Optional<User> findByEmail(String email);

    /**
     * 닉네임(nickname)으로 사용자 찾기
     * @param nickname 조회할 사용자 닉네임
     * @return  Optional<User> 객체 반환 (존재하지 않을 경우 Optional.empty())
     */
    Optional<User> findByNickname(String nickname);

    /**
     * 특정 아이디(username)가 존재하는지 확인
     * @param username  중복 여부 확인할 아이디
     * @return  존재하면 true, 존재하지 않으면 false 반환
     */
    boolean existsByUsername(String username);

    /**
     * 특정 이메일(email)이 존재하는지 확인
     * @param email 중복 여부 확인할 이메일
     * @return  존재하면 true, 없으면 false 반환
     */
    boolean existsByEmail(String email);

    /**
     * 특정 닉네임(nickname)이 존재하는지 확인
     * @param nickname  중복 여부 확인할 닉네임
     * @return  존재하면 true, 없으면 false 반환
     */
    boolean existsByNickname(String nickname);
}
