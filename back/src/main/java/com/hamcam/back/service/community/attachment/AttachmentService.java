package com.hamcam.back.service.community.attachment;

import com.hamcam.back.dto.community.attachment.response.AttachmentListResponse;
import com.hamcam.back.dto.community.attachment.response.AttachmentResponse;
import com.hamcam.back.entity.community.*;
import com.hamcam.back.repository.community.attachment.AttachmentRepository;
import com.hamcam.back.repository.community.comment.CommentRepository;
import com.hamcam.back.repository.community.comment.ReplyRepository;
import com.hamcam.back.repository.community.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 첨부파일(Attachment) 서비스
 * <p>
 * 게시글, 댓글, 대댓글에 첨부된 파일을 업로드, 다운로드, 조회, 삭제하는 기능을 제공합니다.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class AttachmentService {

    private static final String ATTACHMENT_DIR = "uploads/community/";

    private final AttachmentRepository attachmentRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;

    // ===== 파일 업로드 =====

    public int uploadPostFiles(Long postId, MultipartFile[] files) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        List<Attachment> attachments = saveFiles(files).stream()
                .map(fileName -> Attachment.builder()
                        .post(post)
                        .originalFileName(fileName.original)
                        .storedFileName(fileName.stored)
                        .contentType(fileName.type)
                        .previewAvailable(isPreviewable(fileName.type))
                        .build())
                .collect(Collectors.toList());

        attachmentRepository.saveAll(attachments);
        return attachments.size();
    }

    public int uploadCommentFiles(Long commentId, MultipartFile[] files) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        List<Attachment> attachments = saveFiles(files).stream()
                .map(fileName -> Attachment.builder()
                        .comment(comment)
                        .originalFileName(fileName.original)
                        .storedFileName(fileName.stored)
                        .contentType(fileName.type)
                        .previewAvailable(isPreviewable(fileName.type))
                        .build())
                .collect(Collectors.toList());

        attachmentRepository.saveAll(attachments);
        return attachments.size();
    }

    public int uploadReplyFiles(Long replyId, MultipartFile[] files) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 대댓글이 존재하지 않습니다."));

        List<Attachment> attachments = saveFiles(files).stream()
                .map(fileName -> Attachment.builder()
                        .reply(reply)
                        .originalFileName(fileName.original)
                        .storedFileName(fileName.stored)
                        .contentType(fileName.type)
                        .previewAvailable(isPreviewable(fileName.type))
                        .build())
                .collect(Collectors.toList());

        attachmentRepository.saveAll(attachments);
        return attachments.size();
    }

    // ===== 목록 조회 =====

    public AttachmentListResponse getPostAttachments(Long postId) {
        List<Attachment> list = attachmentRepository.findByPostId(postId);
        return toListResponse(list);
    }

    public AttachmentListResponse getCommentAttachments(Long commentId) {
        List<Attachment> list = attachmentRepository.findByCommentId(commentId);
        return toListResponse(list);
    }

    public AttachmentListResponse getReplyAttachments(Long replyId) {
        List<Attachment> list = attachmentRepository.findByReplyId(replyId);
        return toListResponse(list);
    }

    // ===== 다운로드 =====

    public Resource downloadAttachment(Long attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new IllegalArgumentException("첨부파일을 찾을 수 없습니다."));

        try {
            Path path = Paths.get(ATTACHMENT_DIR).resolve(attachment.getStoredFileName()).normalize();
            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists()) {
                throw new RuntimeException("파일이 존재하지 않습니다.");
            }

            return resource;

        } catch (MalformedURLException e) {
            throw new RuntimeException("파일 경로 오류", e);
        }
    }

    // ===== 삭제 =====

    public void deleteAttachment(Long attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 첨부파일이 존재하지 않습니다."));

        Path filePath = Paths.get(ATTACHMENT_DIR).resolve(attachment.getStoredFileName()).normalize();
        try {
            Files.deleteIfExists(filePath);
        } catch (Exception e) {
            throw new RuntimeException("파일 삭제 중 오류 발생", e);
        }

        attachmentRepository.delete(attachment);
    }

    // ===== 유틸 =====

    private AttachmentListResponse toListResponse(List<Attachment> list) {
        List<AttachmentResponse> result = list.stream()
                .map(file -> AttachmentResponse.builder()
                        .attachmentId(file.getId())
                        .originalName(file.getOriginalFileName())
                        .storedName(file.getStoredFileName())
                        .contentType(file.getContentType())
                        .previewAvailable(file.isPreviewAvailable())
                        .build())
                .collect(Collectors.toList());
        return new AttachmentListResponse(result);
    }

    private record FileMeta(String original, String stored, String type) {}

    private List<FileMeta> saveFiles(MultipartFile[] files) {
        try {
            Path dirPath = Paths.get(ATTACHMENT_DIR);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            return List.of(files).stream()
                    .filter(file -> !file.isEmpty())
                    .map(file -> {
                        String original = file.getOriginalFilename();
                        String extension = original.substring(original.lastIndexOf('.') + 1);
                        String stored = UUID.randomUUID() + "_" + original;

                        try {
                            Path target = dirPath.resolve(stored);
                            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
                        } catch (Exception e) {
                            throw new RuntimeException("파일 저장 실패", e);
                        }

                        return new FileMeta(original, stored, extension);
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }

    private boolean isPreviewable(String contentTypeOrExtension) {
        String lower = contentTypeOrExtension.toLowerCase();
        return lower.matches(".*(jpg|jpeg|png|gif|bmp|webp)$") || lower.startsWith("image/");
    }
}
