package com.hamcam.back.repository.auth;

import com.hamcam.back.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * [UserRepository]
 *
 * 사용자 정보를 관리하는 JPA 리포지토리입니다.
 * 회원가입, 로그인, 사용자 검색, 친구 요청, 이메일 기반 인증 등에서 활용됩니다.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 주어진 아이디(username)를 가진 사용자가 존재하는지 확인합니다.
     *
     * @param username 사용자 아이디
     * @return 존재 여부
     */
    boolean existsByUsername(String username);

    /**
     * 주어진 닉네임(nickname)을 가진 사용자가 존재하는지 확인합니다.
     *
     * @param nickname 사용자 닉네임
     * @return 존재 여부
     */
    boolean existsByNickname(String nickname);

    /**
     * 주어진 이메일(email)을 가진 사용자가 존재하는지 확인합니다.
     *
     * @param email 사용자 이메일
     * @return 존재 여부
     */
    boolean existsByEmail(String email);

    /**
     * 사용자 아이디(username)로 사용자 정보를 조회합니다.
     *
     * @param username 사용자 아이디
     * @return 사용자 정보 (Optional)
     */
    Optional<User> findByUsername(String username);

    /**
     * 사용자 이메일(email)로 사용자 정보를 조회합니다.
     *
     * @param email 사용자 이메일
     * @return 사용자 정보 (Optional)
     */
    Optional<User> findByEmail(String email);

    /**
     * 주어진 닉네임(nickname)으로 사용자 정보를 조회합니다.
     *
     * @param nickname 사용자 닉네임
     * @return 사용자 정보 (Optional)
     */
    Optional<User> findByNickname(String nickname);


    /**
     * 닉네임에 특정 키워드가 포함된 사용자 목록을 조회합니다.
     * (친구 검색 기능 등에서 사용)
     *
     * @param keyword 닉네임 검색 키워드
     * @return 해당 키워드가 포함된 사용자 목록
     */
    List<User> findByNicknameContaining(String keyword);
}
