package com.hamcam.back.service.community.attachment;

import com.hamcam.back.dto.community.attachment.request.AttachmentIdRequest;
import com.hamcam.back.dto.community.attachment.request.AttachmentUploadRequest;
import com.hamcam.back.dto.community.attachment.request.PostIdRequest;
import com.hamcam.back.dto.community.attachment.response.AttachmentListResponse;
import com.hamcam.back.dto.community.attachment.response.AttachmentResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.community.Attachment;
import com.hamcam.back.entity.community.Post;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.community.attachment.AttachmentRepository;
import com.hamcam.back.repository.community.post.PostRepository;
import com.hamcam.back.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private static final Path ATTACHMENT_DIR = Paths.get("uploads/community");

    private final AttachmentRepository attachmentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * ✅ 게시글 첨부파일 업로드 (세션 기반 userId)
     */
    public int uploadPostFiles(AttachmentUploadRequest request, HttpServletRequest httpRequest) {
        MultipartFile[] files = request.getFiles();
        Long postId = request.getPostId();
        Long userId = SessionUtil.getUserId(httpRequest);

        if (files == null || files.length == 0) {
            throw new CustomException(ErrorCode.MISSING_PARAMETER);
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (!post.getWriter().getId().equals(userId)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        List<Attachment> attachments = saveFiles(files).stream()
                .map(meta -> Attachment.builder()
                        .post(post)
                        .originalFileName(meta.original())
                        .storedFileName(meta.stored())
                        .contentType(meta.type())
                        .previewAvailable(isPreviewable(meta.type()))
                        .build())
                .collect(Collectors.toList());

        attachmentRepository.saveAll(attachments);
        return attachments.size();
    }

    /**
     * ✅ 첨부파일 목록 조회
     */
    public AttachmentListResponse getPostAttachments(PostIdRequest request) {
        List<Attachment> list = attachmentRepository.findByPostId(request.getPostId());
        return toListResponse(list);
    }

    /**
     * ✅ 첨부파일 다운로드
     */
    public Resource downloadAttachment(AttachmentIdRequest request) {
        Attachment attachment = attachmentRepository.findById(request.getAttachmentId())
                .orElseThrow(() -> new CustomException(ErrorCode.FILE_NOT_FOUND));

        try {
            Path path = ATTACHMENT_DIR.resolve(attachment.getStoredFileName()).normalize();
            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new CustomException(ErrorCode.FILE_NOT_FOUND);
            }

            return resource;
        } catch (MalformedURLException e) {
            throw new CustomException(ErrorCode.FILE_DOWNLOAD_FAILED);
        }
    }

    /**
     * ✅ 첨부파일 삭제 (세션 기반 사용자 확인)
     */
    public void deleteAttachment(AttachmentIdRequest request, HttpServletRequest httpRequest) {
        Long userId = SessionUtil.getUserId(httpRequest);

        Attachment attachment = attachmentRepository.findById(request.getAttachmentId())
                .orElseThrow(() -> new CustomException(ErrorCode.FILE_NOT_FOUND));

        if (!attachment.getPost().getWriter().getId().equals(userId)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        Path filePath = ATTACHMENT_DIR.resolve(attachment.getStoredFileName()).normalize();
        try {
            Files.deleteIfExists(filePath);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FILE_DELETE_FAILED);
        }

        attachmentRepository.delete(attachment);
    }

    // ===== 내부 유틸 =====

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
            if (!Files.exists(ATTACHMENT_DIR)) {
                Files.createDirectories(ATTACHMENT_DIR);
            }

            return Arrays.stream(files)
                    .filter(file -> !file.isEmpty())
                    .map(file -> {
                        String original = file.getOriginalFilename();
                        if (original == null || !original.contains(".")) {
                            throw new CustomException(ErrorCode.INVALID_INPUT);
                        }

                        String extension = original.substring(original.lastIndexOf('.') + 1);
                        String stored = UUID.randomUUID() + "_" + original;

                        try {
                            Path target = ATTACHMENT_DIR.resolve(stored);
                            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
                        } catch (Exception e) {
                            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
                        }

                        return new FileMeta(original, stored, extension);
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    private boolean isPreviewable(String type) {
        String lower = type.toLowerCase();
        return lower.startsWith("image/") || lower.matches(".*(jpg|jpeg|png|gif|bmp|webp)$");
    }
}
