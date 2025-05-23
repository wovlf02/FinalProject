package com.hamcam.back.service.study.team;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserRepository userRepository;

    /**
     * 포인트 지급
     */
    @Transactional
    public void grantPoint(Long userId, int amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        int current = user.getPoint();
        user.setPoint(current + amount);
    }

    /**
     * (옵션) 현재 포인트 조회
     */
    @Transactional(readOnly = true)
    public int getCurrentPoint(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return user.getPoint();
    }
}
