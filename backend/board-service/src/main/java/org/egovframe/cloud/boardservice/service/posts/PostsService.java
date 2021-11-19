package org.egovframe.cloud.boardservice.service.posts;

import lombok.RequiredArgsConstructor;
import org.egovframe.cloud.boardservice.api.board.dto.BoardResponseDto;
import org.egovframe.cloud.boardservice.api.posts.dto.*;
import org.egovframe.cloud.boardservice.domain.posts.*;
import org.egovframe.cloud.boardservice.service.board.BoardService;
import org.egovframe.cloud.common.dto.AttachmentEntityMessage;
import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.common.exception.BusinessMessageException;
import org.egovframe.cloud.common.exception.EntityNotFoundException;
import org.egovframe.cloud.common.exception.InvalidValueException;
import org.egovframe.cloud.common.service.AbstractService;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * org.egovframe.cloud.postsservice.service.posts.PostsService
 * <p>
 * 게시물 서비스 클래스
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
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PostsService extends AbstractService {

    /**
     * 게시물 레파지토리 인터페이스
     */
    private final PostsRepository postsRepository;

    /**
     * 게시물 조회 레파지토리 인터페이스
     */
    private final PostsReadRepository postsReadRepository;

    /**
     * 게시판 서비스 클래스
     */
    private final BoardService boardService;

    /**
     * 이벤트 메시지 발행하기 위한 spring cloud stream 유틸리티 클래스
     */
    private final StreamBridge streamBridge;

    /**
     * 조회 조건에 일치하는 게시물 페이지 목록 조회
     *
     * @param boardNo    게시판 번호
     * @param deleteAt   삭제 여부
     * @param requestDto 요청 DTO
     * @param pageable   페이지 정보
     * @return Page<PostsListResponseDto> 페이지 게시물 목록 응답 DTO
     */
    public Page<PostsListResponseDto> findPage(Integer boardNo, Integer deleteAt, RequestDto requestDto, Pageable pageable) {
        if (boardNo == null || boardNo <= 0) throw new InvalidValueException(getMessage("err.invalid.input.value"));
        return postsRepository.findPage(boardNo, deleteAt, requestDto, pageable);
    }

    /**
     * 최근 게시물이 포함된 게시판 목록 조회
     *
     * @param boardNos   게시판 번호 목록
     * @param postsCount 게시물 수
     * @return Map<Integer, BoardResponseDto> 최근 게시물이 포함된 게시판 상세 응답 DTO Map
     */
    public Map<Integer, BoardResponseDto> findNewest(List<Integer> boardNos, Integer postsCount) throws InvalidValueException {
        if (boardNos == null || boardNos.isEmpty())
            throw new InvalidValueException(getMessage("err.invalid.input.value"));

        List<BoardResponseDto> boards = boardService.findAllByBoardNos(boardNos);

        List<PostsSimpleResponseDto> allPosts = postsRepository.findAllByBoardNosLimitCount(boardNos, postsCount);
        Map<Integer, List<PostsSimpleResponseDto>> postsGroup = allPosts.stream().collect(Collectors.groupingBy(PostsSimpleResponseDto::getBoardNo, Collectors.toList()));

        Map<Integer, BoardResponseDto> data = new HashMap<>(); // 요청한 게시판 순서로 리턴하기 위해서 map 리턴
        for (BoardResponseDto board : boards) {
            List<PostsSimpleResponseDto> posts = postsGroup.get(board.getBoardNo());
            if (posts != null) {
                board.setNewestPosts(posts.stream().map(post -> post.setIsNew(board))
                    .collect(Collectors.toList()));
            }
            data.put(board.getBoardNo(), board);
        }

        return data;
    }

    /**
     * 게시물 단건 조회
     *
     * @param boardNo 게시판 번호
     * @param postsNo 게시물 번호
     * @param userId  사용자 id
     * @param ipAddr  ip 주소
     * @return PostsResponseDto 게시물 응답 DTO
     */
    @Transactional
    public PostsResponseDto findById(Integer boardNo, Integer postsNo, Integer deleteAt, String userId, String ipAddr) {
        return findById(boardNo, postsNo, deleteAt, userId, ipAddr, null);
    }

    /**
     * 게시물 단건 조회
     *
     * @param boardNo    게시판 번호
     * @param postsNo    게시물 번호
     * @param deleteAt   삭제 여부
     * @param userId     사용자 id
     * @param ipAddr     ip 주소
     * @param requestDto 요청 DTO
     * @return PostsResponseDto 게시물 응답 DTO
     */
    @Transactional
    public PostsResponseDto findById(Integer boardNo, Integer postsNo, Integer deleteAt, String userId, String ipAddr, RequestDto requestDto) throws EntityNotFoundException, BusinessMessageException {
        PostsResponseDto dto = postsRepository.findById(boardNo, postsNo, userId, ipAddr);

        if (dto == null) {
            throw new EntityNotFoundException("not found posts : "+ boardNo + ", " + postsNo + ", " + userId + ", " + ipAddr);
        }

        // 삭제 여부 확인
        if (deleteAt != null && deleteAt.intValue() != dto.getDeleteAt().intValue()) {
            throw new BusinessMessageException(getMessage("err.posts.deleted"));
        }

        if (dto.getUserPostsReadCount() == 0) {
            // 게시판 조회 등록
            Integer readNo = postsReadRepository.findNextReadNo(boardNo, postsNo);
            PostsRead postsRead = PostsRead.builder()
                    .boardNo(boardNo)
                    .postsNo(postsNo)
                    .readNo(readNo)
                    .userId(userId)
                    .ipAddr(ipAddr)
                    .build();

            postsReadRepository.save(postsRead);

            // 조회 수 증가
            postsRepository.updateReadCount(boardNo, postsNo);

            // dto 조회 수 증가
            dto.increaseReadCount();
        }

        // 이전글, 다음글 조회
        if (requestDto != null) {
            List<PostsSimpleResponseDto> prevPosts = postsRepository.findNearPost(boardNo, postsNo, -1, deleteAt, requestDto);
            dto.setPrevPosts(prevPosts);
            List<PostsSimpleResponseDto> nextPosts = postsRepository.findNearPost(boardNo, postsNo, 1, deleteAt, requestDto);
            dto.setNextPosts(nextPosts);
        }

        return dto;
    }

    /**
     * 게시물 등록
     *
     * @param boardNo    게시판 번호
     * @param requestDto 게시물 등록 요청 DTO
     * @return PostsResponseDto 게시물 응답 DTO
     */
    @Transactional
    public PostsResponseDto save(Integer boardNo, PostsSaveRequestDto requestDto) {
        Integer postsNo = postsRepository.findNextPostsNo(boardNo);

        Posts entity = postsRepository.save(requestDto.toEntity(boardNo, postsNo));

        /**
         * 첨부파일 entity 정보 저장 이벤트 발생
         */
        if (StringUtils.hasText(entity.getAttachmentCode())) {
            sendAttachmentEvent(entity);
        }

        return new PostsResponseDto(entity);
    }

    /**
     * 게시물 수정(권한 체크 안함)
     *
     * @param boardNo    게시판 번호
     * @param postsNo    게시물 번호
     * @param requestDto 게시물 수정 요청 DTO
     * @return PostsResponseDto 게시물 응답 DTO
     */
    @Transactional
    public PostsResponseDto update(Integer boardNo, Integer postsNo, PostsUpdateRequestDto requestDto) {
        Posts entity = findPosts(boardNo, postsNo);

        // 수정
        entity.update(requestDto.getPostsTitle(), requestDto.getPostsContent(), requestDto.getPostsAnswerContent(), requestDto.getAttachmentCode(), requestDto.getNoticeAt());

        return new PostsResponseDto(entity);
    }

    /**
     * 게시물 다건 삭제(권한 체크 안함)
     *
     * @param requestDtoList 게시물 삭제 요청 DTO List
     * @param userId         사용자 id
     * @return Long 삭제 건수
     */
    @Transactional
    public Long remove(List<PostsDeleteRequestDto> requestDtoList, String userId) {
        Map<Integer, List<Integer>> data = requestDtoList.stream()
                .collect(Collectors.groupingBy(PostsDeleteRequestDto::getBoardNo,
                        Collectors.mapping(PostsDeleteRequestDto::getPostsNo, Collectors.toList())));

        return postsRepository.updateDeleteAt(data, 2, userId);
    }

    /**
     * 게시물 다건 복원(권한 체크 안함)
     *
     * @param requestDtoList 게시물 삭제 요청 DTO List
     * @param userId         사용자 id
     * @return Long 복원 건수
     */
    @Transactional
    public Long restore(List<PostsDeleteRequestDto> requestDtoList, String userId) {
        Map<Integer, List<Integer>> data = requestDtoList.stream()
                .collect(Collectors.groupingBy(PostsDeleteRequestDto::getBoardNo,
                        Collectors.mapping(PostsDeleteRequestDto::getPostsNo, Collectors.toList())));

        return postsRepository.updateDeleteAt(data, 0, userId);
    }

    /**
     * 게시물 다건 완전 삭제(권한 체크 안함)
     *
     * @param requestDtoList 게시물 삭제 요청 DTO List
     */
    @Transactional
    public void delete(List<PostsDeleteRequestDto> requestDtoList) {
        List<Posts> deleteEntityList = requestDtoList.stream()
                .map(PostsDeleteRequestDto::toEntity)
                .collect(Collectors.toList());

        // 일괄 처리
        postsRepository.deleteAll(deleteEntityList);
    }

    /**
     * 게시물 등록(작성자 체크)
     *
     * @param boardNo    게시판 번호
     * @param requestDto 게시물 등록 요청 DTO
     * @return PostsResponseDto 게시물 응답 DTO
     */
    @Transactional
    public PostsResponseDto save(Integer boardNo, PostsSimpleSaveRequestDto requestDto, String userId) {
        checkUserWritable(boardNo);

        Integer postsNo = postsRepository.findNextPostsNo(boardNo);

        Posts entity = postsRepository.save(requestDto.toEntity(boardNo, postsNo));

        /**
         * 첨부파일 entity 정보 저장 이벤트 발생
         */
        sendAttachmentEvent(entity);

        return new PostsResponseDto(entity);
    }

    /**
     * 게시물 수정(작성자 체크)
     *
     * @param boardNo    게시판 번호
     * @param postsNo    게시물 번호
     * @param requestDto 게시물 수정 요청 DTO
     * @param userId     사용자 id
     * @return PostsResponseDto 게시물 응답 DTO
     */
    @Transactional
    public PostsResponseDto update(Integer boardNo, Integer postsNo, PostsSimpleSaveRequestDto requestDto, String userId) {
        Posts entity = findPostsByCreatedBy(boardNo, postsNo, userId);

        // 수정
        entity.update(requestDto.getPostsTitle(), requestDto.getPostsContent(), requestDto.getAttachmentCode());

        return new PostsResponseDto(entity);
    }

    /**
     * 게시물 삭제(작성자 체크)
     *
     * @param boardNo 게시판 번호
     * @param postsNo 게시물 번호
     * @param userId  사용자 id
     */
    @Transactional
    public void remove(Integer boardNo, Integer postsNo, String userId) {
        Posts entity = findPostsByCreatedBy(boardNo, postsNo, userId);

        // 삭제 여부 수정
        entity.updateDeleteAt(1);
    }

    /**
     * 게시물 번호로 게시물 엔티티 조회
     *
     * @param boardNo 게시판 번호
     * @param postsNo 게시물 번호
     * @return Posts 게시물 엔티티
     * @throws InvalidValueException 입력값 예외
     * @throws EntityNotFoundException 엔티티 예외
     */
    public Posts findPosts(Integer boardNo, Integer postsNo) throws InvalidValueException, EntityNotFoundException {
        if (boardNo == null || postsNo == null) {
            throw new InvalidValueException(getMessage("err.invalid.input.value"));
        }

        PostsId id = PostsId.builder().boardNo(boardNo).postsNo(postsNo).build();

        return postsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(getMessage("valid.notexists.format", new Object[]{getMessage("posts")})));
    }

    /**
     * 게시물 번호, 작성자로 게시물 엔티티 조회
     * 로그인 확인
     * 로그인 사용자가 작성자인지 확인
     *
     * @param boardNo 게시판 번호
     * @param postsNo 게시물 번호
     * @param userId  사용자 id
     * @return Posts 게시물 엔티티
     * @throws BusinessMessageException 비지니스 예외
     */
    private Posts findPostsByCreatedBy(Integer boardNo, Integer postsNo, String userId) throws BusinessMessageException {
        if (userId == null || "".equals(userId)) {
            throw new BusinessMessageException(getMessage("err.required.login")); // 로그인 후 다시 시도해주세요.
        }

        Posts entity = findPosts(boardNo, postsNo);

        if (!userId.equals(entity.getCreatedBy())) {
            throw new BusinessMessageException(getMessage("err.unauthorized")); // 권한이 불충분합니다
        }

        checkUserWritable(boardNo);

        return entity;
    }

    /**
     * 게시판 사용자 작성 여부 확인
     *
     * @param boardNo 게시판 번호
     * @throws BusinessMessageException 비지니스 예외
     */
    private void checkUserWritable(Integer boardNo) throws BusinessMessageException {
        BoardResponseDto board = boardService.findById(boardNo);
        if (!Boolean.TRUE.equals(board.getUserWriteAt())) {
            // 게시판 작성 가능 여부를 알려줄 필요는 없다.
            throw new BusinessMessageException(getMessage("err.unauthorized")); // 권한이 불충분합니다.
        }
    }

    /**
     * 첨부파일 entity 정보 업데이트 하기 위해 이벤트 메세지 발행
     *
     * @param entity
     */
    private void sendAttachmentEvent(Posts entity) {
        if (!StringUtils.hasText(entity.getAttachmentCode())) {
            return;
        }
        sendAttachmentEntityInfo(streamBridge,
            AttachmentEntityMessage.builder()
                .attachmentCode(entity.getAttachmentCode())
                .entityName(entity.getClass().getName())
                .entityId(entity.getPostsId().toString())
                .build());
    }

}
