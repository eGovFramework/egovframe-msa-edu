package org.egovframe.cloud.boardservice.api.posts;

import lombok.RequiredArgsConstructor;
import org.egovframe.cloud.boardservice.api.board.dto.BoardResponseDto;
import org.egovframe.cloud.boardservice.api.posts.dto.*;
import org.egovframe.cloud.boardservice.service.posts.PostsService;
import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.common.util.LogUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * org.egovframe.cloud.postservice.api.posts.PostsApiController
 * <p>
 * 게시물 Rest API 컨트롤러 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/28
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/28    jooho       최초 생성
 * </pre>
 */
@RequiredArgsConstructor
@RestController
public class PostsApiController {

    /**
     * 게시물 서비스
     */
    private final PostsService postsService;

    /**
     * 게시물(삭제 포함) 페이지 목록 조회
     *
     * @param boardNo    게시판 번호
     * @param requestDto 요청 DTO
     * @param pageable   페이지 정보
     * @return Page<PostsListResponseDto> 페이지 게시물 목록 응답 DTO
     */
    @GetMapping("/api/v1/posts/{boardNo}")
    public Page<PostsListResponseDto> findPage(@PathVariable Integer boardNo,
                                               RequestDto requestDto,
                                               @SortDefault.SortDefaults({
                                                       @SortDefault(sort = "notice_at", direction = Sort.Direction.DESC),
                                                       @SortDefault(sort = "board_no", direction = Sort.Direction.ASC),
                                                       @SortDefault(sort = "posts_no", direction = Sort.Direction.DESC)
                                               }) Pageable pageable) {
        return postsService.findPage(boardNo, null, requestDto, pageable);
    }

    /**
     * 게시물(삭제 제외) 페이지 목록 조회
     *
     * @param boardNo    게시판 번호
     * @param requestDto 요청 DTO
     * @param pageable   페이지 정보
     * @return Page<PostsListResponseDto> 페이지 게시물 목록 응답 DTO
     */
    @GetMapping("/api/v1/posts/list/{boardNo}")
    public Page<PostsListResponseDto> findListPage(@PathVariable Integer boardNo,
                                                   RequestDto requestDto,
                                                   @SortDefault.SortDefaults({
                                                           @SortDefault(sort = "notice_at", direction = Sort.Direction.DESC),
                                                           @SortDefault(sort = "board_no", direction = Sort.Direction.ASC),
                                                           @SortDefault(sort = "posts_no", direction = Sort.Direction.DESC)
                                                   }) Pageable pageable) {
        return postsService.findPage(boardNo, 0, requestDto, pageable);
    }

    /**
     * 최근 게시물이 포함된 게시판 목록 조회
     *
     * @param boardNos   게시판 번호 목록
     * @param postsCount 게시물 수
     * @return Map<Integer, BoardResponseDto> 최근 게시물이 포함된 게시판 상세 응답 DTO Map
     */
    @GetMapping("/api/v1/posts/newest/{boardNos}/{postsCount}")
    public Map<Integer, BoardResponseDto> findNewest(@PathVariable List<Integer> boardNos, @PathVariable Integer postsCount) {
        return postsService.findNewest(boardNos, postsCount);
    }

    /**
     * 게시물(삭제 포함) 단건 조회
     *
     * @param boardNo 게시판 번호
     * @param postsNo 게시물 번호
     * @return PostsResponseDto 게시물 상세 응답 DTO
     */
    @GetMapping("/api/v1/posts/{boardNo}/{postsNo}")
    public PostsResponseDto findById(@PathVariable Integer boardNo, @PathVariable Integer postsNo) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        userId = "anonymousUser".equals(userId) ? null : userId;

        return postsService.findById(boardNo, postsNo, null, userId, LogUtil.getUserIp());
    }

    /**
     * 게시물(삭제 제외) 단건 조회
     *
     * @param boardNo 게시판 번호
     * @param postsNo 게시물 번호
     * @return PostsResponseDto 게시물 상세 응답 DTO
     */
    @GetMapping("/api/v1/posts/view/{boardNo}/{postsNo}")
    public PostsResponseDto findViewById(@PathVariable Integer boardNo, @PathVariable Integer postsNo, RequestDto requestDto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        userId = "anonymousUser".equals(userId) ? null : userId;
        final Integer deleteAt = 0; // 미삭제 게시물만 조회

        return postsService.findById(boardNo, postsNo, deleteAt, userId, LogUtil.getUserIp(), requestDto);
    }

    /**
     * 게시물 등록(작성자 체크)
     *
     * @param boardNo    게시판 번호
     * @param requestDto 게시물 등록 요청 DTO
     * @return PostsResponseDto 게시물 상세 응답 DTO
     */
    @PostMapping("/api/v1/posts/save/{boardNo}")
    @ResponseStatus(HttpStatus.CREATED)
    public PostsResponseDto saveByCreator(@PathVariable Integer boardNo, @RequestBody @Valid PostsSimpleSaveRequestDto requestDto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return postsService.save(boardNo, requestDto, userId);
    }

    /**
     * 게시물 수정(작성자 체크)
     *
     * @param boardNo    게시판 번호
     * @param postsNo    게시물 번호
     * @param requestDto 게시물 수정 요청 DTO
     * @return PostsResponseDto 게시물 상세 응답 DTO
     */
    @PutMapping("/api/v1/posts/update/{boardNo}/{postsNo}")
    public PostsResponseDto updateByCreator(@PathVariable Integer boardNo, @PathVariable Integer postsNo, @RequestBody @Valid PostsSimpleSaveRequestDto requestDto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        return postsService.update(boardNo, postsNo, requestDto, userId);
    }

    /**
     * 게시물 삭제(작성자 체크)
     *
     * @param boardNo 게시판 번호
     * @param postsNo 게시물 번호
     */
    @DeleteMapping("/api/v1/posts/remove/{boardNo}/{postsNo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByCreator(@PathVariable Integer boardNo, @PathVariable Integer postsNo) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        postsService.remove(boardNo, postsNo, userId);
    }

    /**
     * 게시물 등록
     *
     * @param boardNo    게시판 번호
     * @param requestDto 게시물 등록 요청 DTO
     * @return PostsResponseDto 게시물 상세 응답 DTO
     */
    @PostMapping("/api/v1/posts/{boardNo}")
    @ResponseStatus(HttpStatus.CREATED)
    public PostsResponseDto save(@PathVariable Integer boardNo, @RequestBody @Valid PostsSaveRequestDto requestDto) {
        return postsService.save(boardNo, requestDto);
    }

    /**
     * 게시물 수정
     *
     * @param boardNo    게시판 번호
     * @param postsNo    게시물 번호
     * @param requestDto 게시물 수정 요청 DTO
     * @return PostsResponseDto 게시물 상세 응답 DTO
     */
    @PutMapping("/api/v1/posts/{boardNo}/{postsNo}")
    public PostsResponseDto update(@PathVariable Integer boardNo, @PathVariable Integer postsNo, @RequestBody @Valid PostsUpdateRequestDto requestDto) {
        return postsService.update(boardNo, postsNo, requestDto);
    }

    /**
     * 게시물 다건 삭제
     *
     * @param requestDtoList 게시물 삭제 요청 DTO List
     * @return Long 삭제 건수
     */
    @PutMapping("/api/v1/posts/remove")
    public Long remove(@RequestBody @Valid List<PostsDeleteRequestDto> requestDtoList) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        return postsService.remove(requestDtoList, userId);
    }

    /**
     * 게시물 다건 복원
     *
     * @param requestDtoList 게시물 삭제 요청 DTO List
     * @return Long 복원 건수
     */
    @PutMapping("/api/v1/posts/restore")
    public Long restore(@RequestBody @Valid List<PostsDeleteRequestDto> requestDtoList) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        return postsService.restore(requestDtoList, userId);
    }

    /**
     * 게시물 다건 완전 삭제
     *
     * @param requestDtoList 게시물 삭제 요청 DTO List
     */
    @PutMapping("/api/v1/posts/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestBody @Valid List<PostsDeleteRequestDto> requestDtoList) {
        postsService.delete(requestDtoList);
    }

}
