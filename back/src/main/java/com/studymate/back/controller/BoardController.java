package com.studymate.back.controller;

import com.studymate.back.dto.BoardRequest;
import com.studymate.back.dto.BoardResponse;
import com.studymate.back.dto.ReportRequest;
import com.studymate.back.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 게시판 컨트롤러
 *
 * 게시글 CRUD (작성, 수정, 삭제, 조회)
 * 게시글 검색
 * 게시글 좋아요/추천
 * 게시글 신고
 * 파일 업로드/다운로드
 */
@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    /**
     * 게시글 작성 API
     * 사용자가 새로운 게시글을 작성할 수 있음
     * 게시글 작성 시 제목과 내용이 필수이며, 첨부파일은 선택 사항
     * 작성된 게시글의 정보를 반환함
     * @param request   게시글 작성 요청 (제목, 내용 포함)
     * @param files 업로드할 파일 목록 (선택 사항)
     * @return  작성된 게시글의 정보 (ID, 제목, 내용, 첨부파일 URL 등)
     */
    @PostMapping
    public ResponseEntity<BoardResponse> createPost(
            @RequestPart("request") BoardRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        BoardResponse response = boardService.createPost(request, files);
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 수정 API
     * 사용자는 자신이 작성한 게시글을 수정할 수 있음
     * 제목, 본문, 첨부파일 변경 가능
     * 요청한 사용자가 게시글 작성자인지 확인 후 처리함
     * @param id    수정할 게시글의 ID
     * @param request   수정할 게시글 제이터 (제목, 내용 포함)
     * @param files 변경할 첨부파일 목록 (선택 사항)
     * @return 수정된 게시글의 정보 (ID, 제목, 내용, 첨부파일 URL 등)
     */
    @PutMapping("/{id}")
    public ResponseEntity<BoardResponse> updatePost(
            @PathVariable Long id,
            @RequestPart("request") BoardRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        BoardResponse response = boardService.updatePost(id, request, files);
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 삭제 API
     * 사용자는 자신이 작성한 게시글을삭제할 수 있음
     * 삭제된 게시글은 복구 불가능
     * 요청한 사용자가 게시글 작성자인지 확인 후 처리함
     * @param id    삭제할 게시글의 ID
     * @return  삭제 성공 시 HTTP 204 응답 반환 (내용 없음)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        boardService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 게시글 목록 조회 API
     * 모든 사용자는 최신순 또는 좋아요 순으로 게시글을 조회할 수 있음
     * 페이지네이션 적용 (기본 10개씩)
     * 정렬 방식: 최신순(latest) 또는 추천순(likes) 선택 가능
     * @param page  페이지 번호 (기본값: 0)
     * @param size  한 페이지에 표시할 게시글 개수 (기본값: 10)
     * @param sort 정렬 방식 (최신순 또는 추천순, Default: 최신순)
     * @return 게시글목록 (제목, 작성자, 추천 수 등 포함)
     */
    @GetMapping("/list")
    public ResponseEntity<List<BoardResponse>> getPostList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String sort
    ) {
        List<BoardResponse> response = boardService.getPostList(page, size, sort);
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 상세 조회 API
     * 사용자는 특정 게시글을 선택하여 상세 내용을 조회할 수 있음
     * @param id    조회할 게시글의 ID
     * @return      게시글 상세 정보 (제목, 내용, 작성자, 첨부파일 등)
     */
    @GetMapping("/{id}")
    public ResponseEntity<BoardResponse> getPostList(@PathVariable Long id) {
        BoardResponse response = boardService.getPostDetail(id);
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 검색 API
     * 사용자는 특정 키워드로 게시글을 검색할 수 있음
     * 검색 결과는 최신순으로 정렬됨
     * @param keyword   검색할 키워드 (게시글 제목 기준)
     * @return  검색된 게시글 목록
     */
    @GetMapping("/search")
    public ResponseEntity<List<BoardResponse>> searchPosts(@RequestParam String keyword) {
        List<BoardResponse> response = boardService.searchPosts(keyword);
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 추천 API
     * 사용자는 특정 게시글에 추천을 누를 수 있음
     * 1개 계정당 1개의 게시글에 대해 1회만 가능 (활성화/비활성화)
     * @param id    추천을 누른 게시글 ID
     * @return  좋아요 반영 성공 시 HTTP 200 반환
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likePost(@PathVariable Long id) {
        boardService.likePost(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 게시글 신고 API
     * 사용자는 게시글을 신고할 수 있음
     * 신고 사유를 선택하거나 직접 입력 가능
     * @param id    신고할 게시글 ID
     * @param request   신고 요청 데이터 (사유 포함)
     * @return  신고 처리 성공 시 HTTP 200 반환
     */
    @PostMapping("/{id}/report")
    public ResponseEntity<Void> reportPost(@PathVariable Long id, @RequestBody ReportRequest request) {
        boardService.reportPost(id, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 파일 업로드 API
     * 사용자는 게시글 작성 시 첨부파일(이미지, 문서, 동영상 등)을 업로드할 수 있음
     * 여러 개의 파일을 동시에 업로드 가능
     * 업로드된 파일의 URL 목록을 반환하여 클라이언트에서 활용 가능
     * 파일의 형식과 크기에 대한 유효성 검사를 서비스 레이어에서 수행
     * @param files 업로드할 파일 목록 (최소 1개 이상, 선택 사항)
     * @return  업로드된 파일의 URL 목록
     */
    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadFiles(@RequestParam("files") List<MultipartFile> files) {
        List<String> fileUrls = boardService.uploadFiles(files);
        return ResponseEntity.ok(fileUrls);
    }

    /**
     * 파일 다운로드 API
     * 사용자는 게시글에 첨부된 파일을 다운로드할 수 있음
     * 파일 ID를 기준으로 데이터베이스에서 해당 파일의 정보를 조회 후 반환
     * 다운로드 가능한 파일 형식인지 검증 후 반환
     * 반환되는 데이터는 바이트 배열이며, HTTP 응답 페더에 적절한 'Content-Type' 설정 필요
     * @param fileId    다운로드할 파일의 ID
     * @return  파일 데이터 (바이트 배열 형태)
     */
    @GetMapping("/download/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileId) {
        return boardService.downloadFile(fileId);
    }
}
