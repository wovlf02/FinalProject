package com.hamcam.back.auth.repository;

import com.hamcam.back.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository
 *
 * 사용자 엔티티(User)에 대한 데이터베이스 접근을 처리하는 인터페이스
 * Spring Data JPA를 사용하여 자동으로 쿼리 메서드를 구현
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 아이디로 사용자 존재 여부 확인
     * @param username 아이디
     * @return 존재하면 true, 없으면 false
     */
    boolean existsByUsername(String username);

    /**
     * 이메일로 사용자 존재 여부 확인
     * @param email 이메일 주소
     * @return 존재하면 true
     */
    boolean existsByEmail(String email);

    /**
     * 닉네임으로 사용자 존재 여부 확인
     * @param nickname 닉네임
     * @return 존재하면 true
     */
    boolean existsByNickname(String nickname);

    /**
     * 아이디로 사용자 조회
     * @param username 아이디
     * @return 해당 사용자 정보(Optional)
     */
    Optional<User> findByUsername(String username);

    /**
     * 이메일로 사용자 조회
     * @param email 이메일 주소
     * @return 해당 사용자 정보(Optional)
     */
    Optional<User> findByEmail(String email);

    /**
     * 닉네임으로 사용자 조회
     * @param nickname 닉네임
     * @return 해당 사용자 정보(Optional)
     */
    Optional<User> findByNickname(String nickname);
}
