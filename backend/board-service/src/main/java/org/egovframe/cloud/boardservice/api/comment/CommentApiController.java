package org.egovframe.cloud.boardservice.api.comment;

import lombok.RequiredArgsConstructor;
import org.egovframe.cloud.boardservice.api.comment.dto.CommentResponseDto;
import org.egovframe.cloud.boardservice.api.comment.dto.CommentSaveRequestDto;
import org.egovframe.cloud.boardservice.api.comment.dto.CommentUpdateRequestDto;
import org.egovframe.cloud.boardservice.service.comment.CommentService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * org.egovframe.cloud.commentservice.api.comment.CommentApiController
 * <p>
 * 댓글 Rest API 컨트롤러 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/08/04
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/08/04    jooho       최초 생성
 * </pre>
 */
@RequiredArgsConstructor
@RestController
public class CommentApiController {

    /**
     * 댓글 서비스
     */
    private final CommentService commentService;

    /**
     * 게시글의 전체 댓글 목록 조회
     *
     * @param boardNo  게시판 번호
     * @param postsNo  게시물 번호
     * @return Map<String, Object> 페이지 댓글 목록 응답 DTO
     */
    @GetMapping("/api/v1/comments/total/{boardNo}/{postsNo}")
    public Map<String, Object> findTotal(@PathVariable Integer boardNo, @PathVariable Integer postsNo) {
        return commentService.findAll(boardNo, postsNo, null);
    }

    /**
     * 게시글의 전체 미삭제 댓글 목록 조회
     *
     * @param boardNo  게시판 번호
     * @param postsNo  게시물 번호
     * @return Map<String, Object> 페이지 댓글 목록 응답 DTO
     */
    @GetMapping("/api/v1/comments/all/{boardNo}/{postsNo}")
    public Map<String, Object> findAll(@PathVariable Integer boardNo, @PathVariable Integer postsNo) {
        return commentService.findAll(boardNo, postsNo, 0);
    }

    /**
     * 게시글의 댓글 목록 조회
     *
     * @param boardNo  게시판 번호
     * @param postsNo  게시물 번호
     * @param pageable 페이지 정보
     * @return Map<String, Object> 페이지 댓글 목록 응답 DTO
     */
    @GetMapping("/api/v1/comments/{boardNo}/{postsNo}")
    public Map<String, Object> findPage(@PathVariable Integer boardNo, @PathVariable Integer postsNo, Pageable pageable) {
        return commentService.findPage(boardNo, postsNo, null, pageable);
    }

    /**
     * 게시글의 미삭제 댓글 목록 조회
     *
     * @param boardNo  게시판 번호
     * @param postsNo  게시물 번호
     * @param pageable 페이지 정보
     * @return Map<String, Object> 페이지 댓글 목록 응답 DTO
     */
    @GetMapping("/api/v1/comments/list/{boardNo}/{postsNo}")
    public Map<String, Object> findListPage(@PathVariable Integer boardNo, @PathVariable Integer postsNo, Pageable pageable) {
        return commentService.findPage(boardNo, postsNo, 0, pageable);
    }

    /**
     * 댓글 등록
     *
     * @param requestDto 댓글 등록 요청 DTO
     */
    @PostMapping("/api/v1/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto save(@RequestBody @Valid CommentSaveRequestDto requestDto) {
        return commentService.save(requestDto);
    }

    /**
     * 댓글 수정(작성자 체크)
     *
     * @param requestDto 게시물 수정 요청 DTO
     * @return PostsResponseDto 게시물 상세 응답 DTO
     */
    @PutMapping("/api/v1/comments/update")
    public CommentResponseDto updateByCreator(@RequestBody @Valid CommentUpdateRequestDto requestDto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        return commentService.update(requestDto, userId);
    }

    /**
     * 댓글 삭제(작성자 체크)
     *
     * @param boardNo   게시판 번호
     * @param postsNo   게시물 번호
     * @param commentNo 댓글 번호
     */
    @DeleteMapping("/api/v1/comments/delete/{boardNo}/{postsNo}/{commentNo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByCreator(@PathVariable Integer boardNo, @PathVariable Integer postsNo, @PathVariable Integer commentNo) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        commentService.delete(boardNo, postsNo, commentNo, userId);
    }

    /**
     * 댓글 수정
     *
     * @param requestDto 댓글 수정 요청 DTO
     */
    @PutMapping("/api/v1/comments")
    public CommentResponseDto update(@RequestBody @Valid CommentUpdateRequestDto requestDto) {
        return commentService.update(requestDto);
    }

    /**
     * 댓글 삭제
     *
     * @param boardNo   게시판 번호
     * @param postsNo   게시물 번호
     * @param commentNo 댓글 번호
     */
    @DeleteMapping("/api/v1/comments/{boardNo}/{postsNo}/{commentNo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer boardNo, @PathVariable Integer postsNo, @PathVariable Integer commentNo) {
        commentService.delete(boardNo, postsNo, commentNo);
    }

}
