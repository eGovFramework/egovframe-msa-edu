package org.egovframe.cloud.boardservice.service.comment;

import lombok.RequiredArgsConstructor;
import org.egovframe.cloud.boardservice.api.comment.dto.CommentListResponseDto;
import org.egovframe.cloud.boardservice.api.comment.dto.CommentResponseDto;
import org.egovframe.cloud.boardservice.api.comment.dto.CommentSaveRequestDto;
import org.egovframe.cloud.boardservice.api.comment.dto.CommentUpdateRequestDto;
import org.egovframe.cloud.boardservice.domain.board.Board;
import org.egovframe.cloud.boardservice.domain.comment.Comment;
import org.egovframe.cloud.boardservice.domain.comment.CommentId;
import org.egovframe.cloud.boardservice.domain.comment.CommentRepository;
import org.egovframe.cloud.boardservice.domain.posts.Posts;
import org.egovframe.cloud.boardservice.domain.posts.PostsId;
import org.egovframe.cloud.boardservice.service.posts.PostsService;
import org.egovframe.cloud.common.exception.BusinessMessageException;
import org.egovframe.cloud.common.exception.EntityNotFoundException;
import org.egovframe.cloud.common.exception.InvalidValueException;
import org.egovframe.cloud.common.service.AbstractService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * org.egovframe.cloud.boardservice.service.comment.CommentService
 * <p>
 * 댓글 서비스 클래스
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
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CommentService extends AbstractService {

    /**
     * 게시물 레파지토리 인터페이스
     */
    private final CommentRepository commentRepository;

    /**
     * 게시물 서비스
     */
    private final PostsService postsService;

    /**
     * 게시글의 댓글 전체 목록 조회
     *
     * @param boardNo 게시판 번호
     * @param postsNo 게시물 번호
     * @return Map<String, Object> 페이지 댓글 목록 응답 DTO
     */
    public Map<String, Object> findAll(Integer boardNo, Integer postsNo) {
        return findAll(boardNo, postsNo, null);
    }

    /**
     * 게시글의 댓글 전체 목록 조회
     *
     * @param boardNo  게시판 번호
     * @param postsNo  게시물 번호
     * @param deleteAt 삭제 여부
     * @return Map<String, Object> 페이지 댓글 목록 응답 DTO
     */
    public Map<String, Object> findAll(Integer boardNo, Integer postsNo, Integer deleteAt) {
        List<CommentListResponseDto> comments = commentRepository.findAll(boardNo, postsNo, deleteAt);

        // 페이지 인터페이스와 동일한 속성의 맵 리턴
        Map<String, Object> result = new HashMap<>();

        result.put("content", comments);
        result.put("empty", comments.isEmpty());
        result.put("first", true);
        result.put("last", true);
        result.put("number", 0);
        result.put("numberOfElements", comments.size());
        result.put("size", comments.size());
        result.put("totalElements", comments.size());
        result.put("totalPages", 1);

        return result;
    }

    /**
     * 게시글의 댓글 목록 조회
     *
     * @param boardNo  게시판 번호
     * @param postsNo  게시물 번호
     * @param pageable 페이지 정보
     * @return Map<String, Object> 페이지 댓글 목록 응답 DTO
     */
    public Map<String, Object> findPage(Integer boardNo, Integer postsNo, Pageable pageable) {
        return commentRepository.findPage(boardNo, postsNo, null, pageable);
    }

    /**
     * 게시글의 댓글 목록 조회
     *
     * @param boardNo  게시판 번호
     * @param postsNo  게시물 번호
     * @param deleteAt 삭제 여부
     * @param pageable 페이지 정보
     * @return Map<String, Object> 페이지 댓글 목록 응답 DTO
     */
    public Map<String, Object> findPage(Integer boardNo, Integer postsNo, Integer deleteAt, Pageable pageable) {
        return commentRepository.findPage(boardNo, postsNo, deleteAt, pageable);
    }

    /**
     * 댓글 등록
     *
     * @param requestDto 댓글 등록 요청 DTO
     */
    @Transactional
    public CommentResponseDto save(CommentSaveRequestDto requestDto) throws InvalidValueException {
        if (requestDto.getBoardNo() == null || requestDto.getPostsNo() == null) {
            throw new InvalidValueException(getMessage("err.invalid.input.value"));
        }

        Posts posts = postsService.findPosts(requestDto.getBoardNo(), requestDto.getPostsNo());
        checkEditableComment(posts); // 저장 가능 여부 확인

        Integer sortSeq;
        if (requestDto.getParentCommentNo() != null) { // 대댓글
            sortSeq = commentRepository.findNextSortSeq(requestDto.getBoardNo(), requestDto.getPostsNo(), requestDto.getGroupNo(), requestDto.getParentCommentNo(), requestDto.getDepthSeq());
            if (sortSeq != null) {
                commentRepository.updateSortSeq(requestDto.getGroupNo(), sortSeq, null, 1); // 들어갈 위치와 같거나 큰 대댓글 정렬 순서 +1
            } else {
                sortSeq = commentRepository.findLastSortSeq(requestDto.getBoardNo(), requestDto.getPostsNo(), requestDto.getGroupNo()); // 들어갈 위치가 검색되지 않으면 max
            }
        } else {
            sortSeq = 1;
        }

        Integer commentNo = commentRepository.findNextCommentNo(requestDto.getBoardNo(), requestDto.getPostsNo()); // 댓글 번호 채번
        Integer groupNo; // 댓글 그룹 번호
        if (requestDto.getGroupNo() != null) groupNo = requestDto.getGroupNo();
        else groupNo = commentNo; // 최상위 댓글

        Comment entity = commentRepository.save(requestDto.toEntity(posts, commentNo, groupNo, sortSeq));

        return new CommentResponseDto(entity);
    }

    /**
     * 댓글 수정(작성자 체크)
     *
     * @param requestDto 댓글 수정 요청 DTO
     */
    @Transactional
    public CommentResponseDto update(CommentUpdateRequestDto requestDto, String userId) {
        Comment entity = findCommentByCreatedBy(requestDto.getBoardNo(), requestDto.getPostsNo(), requestDto.getCommentNo(), userId);

        checkEditableComment(entity.getPosts()); // 저장 가능 여부 확인

        // 수정
        entity.update(requestDto.getCommentContent());

        return new CommentResponseDto(entity);
    }

    /**
     * 댓글 삭제(작성자 체크)
     *
     * @param boardNo   게시판 번호
     * @param postsNo   게시물 번호
     * @param commentNo 댓글 번호
     */
    @Transactional
    public void delete(Integer boardNo, Integer postsNo, Integer commentNo, String userId) {
        Comment entity = findCommentByCreatedBy(boardNo, postsNo, commentNo, userId);

        checkEditableComment(entity.getPosts()); // 변경 가능 여부 확인

        entity.updateDeleteAt(1); // 작성자 삭제
    }

    /**
     * 댓글 수정
     *
     * @param requestDto 댓글 수정 요청 DTO
     */
    @Transactional
    public CommentResponseDto update(CommentUpdateRequestDto requestDto) {
        Comment entity = findComment(requestDto.getBoardNo(), requestDto.getPostsNo(), requestDto.getCommentNo());

        // 수정
        entity.update(requestDto.getCommentContent());

        return new CommentResponseDto(entity);
    }

    /**
     * 댓글 삭제
     *
     * @param boardNo   게시판 번호
     * @param postsNo   게시물 번호
     * @param commentNo 댓글 번호
     */
    @Transactional
    public void delete(Integer boardNo, Integer postsNo, Integer commentNo) {
        Comment entity = findComment(boardNo, postsNo, commentNo);

        entity.updateDeleteAt(2); // 관리자 삭제 - 관리자가 본인 댓글 지울 경우 관리자 삭제로 처리
    }

    /**
     * 댓글 기본키로 댓글 엔티티 조회
     *
     * @param boardNo   게시판 번호
     * @param postsNo   게시물 번호
     * @param commentNo 댓글 번호
     * @return Comment 댓글 엔티티
     */
    private Comment findComment(Integer boardNo, Integer postsNo, Integer commentNo) throws InvalidValueException {
        if (boardNo == null || postsNo == null || commentNo == null) {
            throw new InvalidValueException(getMessage("err.invalid.input.value"));
        }

        CommentId id = CommentId.builder()
                .postsId(PostsId.builder()
                        .boardNo(boardNo)
                        .postsNo(postsNo)
                        .build())
                .commentNo(commentNo)
                .build();

        return commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(getMessage("valid.notexists.format", new Object[]{getMessage("comment")}))); // 게시물이(가) 없습니다.
    }

    /**
     * 댓글 기본키로 댓글 엔티티 조회
     * 작성자 체크하여 본인 댓글이 아닌 경우 예외 발생
     *
     * @param boardNo   게시판 번호
     * @param postsNo   게시물 번호
     * @param commentNo 댓글 번호
     * @param userId    사용자 id
     * @return Comment 댓글 엔티티
     */
    private Comment findCommentByCreatedBy(Integer boardNo, Integer postsNo, Integer commentNo, String userId) throws BusinessMessageException {
        if (userId == null) {
            throw new BusinessMessageException(getMessage("err.required.login")); // 로그인 후 다시 시도해주세요.
        }

        Comment entity = findComment(boardNo, postsNo, commentNo);

        if (!userId.equals(entity.getCreatedBy())) {
            throw new BusinessMessageException(getMessage("err.unauthorized")); // 권한이 불충분합니다
        }

        return entity;
    }

    /**
     * 댓글 등록/수정/삭제 가능 여부 확인
     * 댓글 사용 여부, 게시물 삭제 여부 체크해서 예외 발생
     *
     * @param posts 게시물 엔티티
     */
    private void checkEditableComment(Posts posts) throws EntityNotFoundException, BusinessMessageException {
        Board board = posts.getBoard();
        if (board == null) {
            throw new EntityNotFoundException(getMessage("valid.notexists.format", new Object[]{getMessage("board")})); // 게시판이(가) 없습니다.
        }
        if (Boolean.FALSE.equals(board.getCommentUseAt())) {
            throw new BusinessMessageException(getMessage("err.board.not_use_comment")); // 댓글 사용이 금지된 게시판입니다.
        }
        if (posts.getDeleteAt().compareTo(0) > 0) {
            throw new BusinessMessageException(getMessage("err.posts.deleted")); // 삭제된 게시물입니다.
        }
    }

}
