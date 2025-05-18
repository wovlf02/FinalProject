package com.hamcam.back.repository.community.attachment;

import com.hamcam.back.entity.community.Attachment;
import com.hamcam.back.entity.community.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * [AttachmentRepository]
 *
 * 커뮤니티 게시글 첨부파일(Attachment) 관련 JPA Repository입니다.
 * - 게시글에 첨부된 파일 조회
 * - 첨부파일 삭제 등에 사용됩니다.
 */
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    /**
     * [게시글 ID 기반 첨부파일 조회]
     * 게시글 ID를 기준으로 첨부된 모든 파일을 조회합니다.
     *
     * @param postId 게시글 ID
     * @return 첨부파일 목록
     */
    List<Attachment> findByPostId(Long postId);

    /**
     * [게시글 엔티티 기반 첨부파일 조회]
     * 게시글 엔티티를 기준으로 첨부된 모든 파일을 조회합니다.
     *
     * @param post 게시글 엔티티
     * @return 첨부파일 목록
     */
    List<Attachment> findByPost(Post post);
}
