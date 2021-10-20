package org.egovframe.cloud.boardservice.api.posts.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egovframe.cloud.boardservice.domain.posts.Posts;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * org.egovframe.cloud.boardservice.api.board.dto.PostsUpdateRequestDto
 * <p>
 * 게시물 수정 요청 DTO 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/08
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/08    jooho       최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
public class PostsUpdateRequestDto {

    /**
     * 게시물 제목
     */
    @NotBlank(message = "{posts.posts_title} {err.required}")
    private String postsTitle;

    /**
     * 게시물 내용
     */
    @NotBlank(message = "{posts.posts_content} {err.required}")
    private String postsContent;

    /**
     * 게시물 답변 내용
     */
    private String postsAnswerContent;

    /**
     * 첨부파일 코드
     */
    private String attachmentCode;

    /**
     * 공지 여부
     */
    @NotNull(message = "{posts.notice_at} {err.required}")
    private Boolean noticeAt;

    /**
     * 게시물 등록 요청 DTO 클래스 생성자
     * 빌더 패턴으로 객체 생성
     *
     * @param postsTitle     게시물 제목
     * @param postsContent   게시물 내용
     * @param postsAnswerContent 게시물 답변 내용
     * @param attachmentCode 첨부파일 코드
     * @param noticeAt       공지 여부
     */
    @Builder
    public PostsUpdateRequestDto(String postsTitle, String postsContent, String postsAnswerContent, String attachmentCode, Boolean noticeAt) {
        this.postsTitle = postsTitle;
        this.postsContent = postsContent;
        this.postsAnswerContent = postsAnswerContent;
        this.attachmentCode = attachmentCode;
        this.noticeAt = noticeAt;
    }

    /**
     * 게시물 등록 요청 DTO 속성 값으로 게시물 엔티티 빌더를 사용하여 객체 생성
     *
     * @return Posts 게시물 엔티티
     */
    public Posts toEntity() {
        return Posts.builder()
                .postsTitle(postsTitle)
                .postsContent(postsContent)
                .postsAnswerContent(postsAnswerContent)
                .attachmentCode(attachmentCode)
                .noticeAt(noticeAt)
                .build();
    }

}
