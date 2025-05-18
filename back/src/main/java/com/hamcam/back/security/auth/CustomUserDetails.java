package com.hamcam.back.security.auth;

import com.hamcam.back.entity.auth.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Spring Security의 UserDetails 구현체
 * 사용자 인증 정보를 담는 커스텀 객체로, User 엔티티 기반으로 생성됨
 */
@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public Long getUserId() {
        return user.getId();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * 사용자 권한 반환
     * 현재는 비어 있지만 필요시 Role -> GrantedAuthority 변환해 추가
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // 권한 기능 필요 시 수정
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 정책 추가 시 변경
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 잠금 정책 적용 시 변경
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비밀번호 유효기간 정책 적용 시 변경
    }

    @Override
    public boolean isEnabled() {
        return true; // User 엔티티에 활성 여부 필드 도입 시 변경
    }
}
