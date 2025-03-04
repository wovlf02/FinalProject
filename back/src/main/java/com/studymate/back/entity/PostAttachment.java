package com.studymate.back.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

/**
 * PostAttachment Entity (게시글 첨부파일)
 * 게시글에 첨부된 파일을 관리하는 JPA 엔티티
 * posts 테이블과 연관 (어떤 게시글의 첨부파일인지 저장
 */
@Entity
@Table(name = "post_attachments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostAttachment {

    
}
