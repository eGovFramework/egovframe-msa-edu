package org.egovframe.cloud.boardservice.api.board.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.egovframe.cloud.boardservice.domain.board.Board;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * org.egovframe.cloud.boardservice.api.board.dto.BoardSaveRequestDtoTest
 * <p>
 * 게시판 등록 요청 DTO 단위 테스트 클래스
 *
 * @author 표준프레임워크센터
 * @version 1.0
 * @since 2024/01/01
 */
class BoardSaveRequestDtoTest {

    @DisplayName("모든 필드를 설정한 DTO로 toEntity()를 호출하면 동일한 값을 가진 Board 엔티티가 생성된다")
    @Test
    void should_createBoardEntity_when_allFieldsAreSet() {
        // given
        BoardSaveRequestDto dto = BoardSaveRequestDto.builder()
                .boardName("공지사항")
                .skinTypeCode("NORMAL")
                .titleDisplayLength(30)
                .postDisplayCount(15)
                .pageDisplayCount(10)
                .newDisplayDayCount(5)
                .editorUseAt(true)
                .userWriteAt(false)
                .commentUseAt(true)
                .uploadUseAt(true)
                .uploadLimitCount(3)
                .uploadLimitSize(new BigDecimal("52428800"))
                .build();

        // when
        Board entity = dto.toEntity();

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getBoardName()).isEqualTo("공지사항");
        assertThat(entity.getSkinTypeCode()).isEqualTo("NORMAL");
        assertThat(entity.getTitleDisplayLength()).isEqualTo(30);
        assertThat(entity.getPostDisplayCount()).isEqualTo(15);
        assertThat(entity.getPageDisplayCount()).isEqualTo(10);
        assertThat(entity.getNewDisplayDayCount()).isEqualTo(5);
        assertThat(entity.getEditorUseAt()).isTrue();
        assertThat(entity.getUserWriteAt()).isFalse();
        assertThat(entity.getCommentUseAt()).isTrue();
        assertThat(entity.getUploadUseAt()).isTrue();
        assertThat(entity.getUploadLimitCount()).isEqualTo(3);
        assertThat(entity.getUploadLimitSize()).isEqualByComparingTo(new BigDecimal("52428800"));
    }

    @DisplayName("선택 필드(uploadLimitCount, uploadLimitSize)를 설정하지 않아도 Board 엔티티가 생성된다")
    @Test
    void should_createBoardEntity_when_optionalFieldsAreNull() {
        // given
        BoardSaveRequestDto dto = BoardSaveRequestDto.builder()
                .boardName("자유게시판")
                .skinTypeCode("NORMAL")
                .titleDisplayLength(20)
                .postDisplayCount(10)
                .pageDisplayCount(10)
                .newDisplayDayCount(3)
                .editorUseAt(false)
                .userWriteAt(true)
                .commentUseAt(false)
                .uploadUseAt(false)
                .build();

        // when
        Board entity = dto.toEntity();

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getBoardName()).isEqualTo("자유게시판");
        assertThat(entity.getUploadLimitCount()).isNull();
        assertThat(entity.getUploadLimitSize()).isNull();
        // boardNo는 DB 채번 전이므로 null
        assertThat(entity.getBoardNo()).isNull();
    }
}
