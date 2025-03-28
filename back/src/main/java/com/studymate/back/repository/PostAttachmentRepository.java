package com.studymate.back.repository;

import com.studymate.back.entity.PostAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * PostAttachmentRepository (게시글 첨부파일 리포지토리)
 * post_attachments 테이블과 연동되는 JPA Repository
 * 기본적인 CRUD 기능 제공
 * 특정 게시글에 첨부된 파일 목록 조회 기능 추가
 */
@Repository
public interface PostAttachmentRepository extends JpaRepository<PostAttachment, Long> {

    /**
     * 특정 게시글에 첨부된 파일 목록 조회
     * 게시글 ID를 기준으로 해당 게시글에 업로드된 파일을 모두 가져옴
     * @param postId    조회할 게시글 ID
     * @return  해당 게시글에 첨부된 파일 목록
     */
    List<PostAttachment> findByPostPostId(Long postId);

    /**
     * 특정 게시글에 속한 모든 첨부파일 삭제
     * 게시글 삭제 시 관련 첨부파일도 함께 삭제
     * @param postId    삭제할 게시글 ID
     */
    void deleteByPostPostId(Long postId);
}
