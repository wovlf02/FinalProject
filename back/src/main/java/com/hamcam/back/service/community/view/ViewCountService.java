package com.hamcam.back.service.community.view;

import com.hamcam.back.entity.community.Post;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.repository.community.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게시글 조회수(View Count) 서비스
 * - 조회수 1 증가 처리
 */
@Service
@RequiredArgsConstructor
public class ViewCountService {

    private final PostRepository postRepository;

    /**
     * 게시글의 조회수를 1 증가시킵니다.
     *
     * @param postId 게시글 ID
     */
    @Transactional
    public void increaseViewCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException("해당 게시글이 존재하지 않습니다."));
        post.incrementViewCount(); // Post 엔티티 내부 로직
    }
}
