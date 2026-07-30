package org.egovframe.cloud.portalservice.service.content;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.egovframe.cloud.common.exception.EntityNotFoundException;
import org.egovframe.cloud.common.util.MessageUtil;
import org.egovframe.cloud.portalservice.api.content.dto.ContentResponseDto;
import org.egovframe.cloud.portalservice.api.content.dto.ContentSaveRequestDto;
import org.egovframe.cloud.portalservice.domain.content.Content;
import org.egovframe.cloud.portalservice.domain.content.ContentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * org.egovframe.cloud.portalservice.service.content.ContentServiceTest
 * <p>
 * 컨텐츠 서비스 단위 테스트 클래스
 *
 * @author 표준프레임워크센터
 * @version 1.0
 * @since 2021/07/22
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/22    표준프레임워크센터  최초 생성
 * </pre>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ContentServiceTest {

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private MessageUtil messageUtil;

    @InjectMocks
    private ContentService contentService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(contentService, "messageUtil", messageUtil);
        given(messageUtil.getMessage(anyString())).willReturn("message");
        given(messageUtil.getMessage(anyString(), any(Object[].class))).willReturn("message");
    }

    @DisplayName("컨텐츠 번호가 주어지면 해당 컨텐츠를 반환한다")
    @Test
    void should_returnContent_when_contentNoIsGiven() {
        // given
        Integer contentNo = 1;
        Content content = Content.builder()
                .contentNo(contentNo)
                .contentName("메인 컨텐츠")
                .contentRemark("메인 페이지 컨텐츠")
                .contentValue("<p>내용</p>")
                .build();
        given(contentRepository.findById(contentNo)).willReturn(Optional.of(content));

        // when
        ContentResponseDto result = contentService.findById(contentNo);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContentNo()).isEqualTo(contentNo);
        assertThat(result.getContentName()).isEqualTo("메인 컨텐츠");
        assertThat(result.getContentRemark()).isEqualTo("메인 페이지 컨텐츠");
        assertThat(result.getContentValue()).isEqualTo("<p>내용</p>");
        verify(contentRepository).findById(contentNo);
    }

    @DisplayName("존재하지 않는 컨텐츠 번호가 주어지면 EntityNotFoundException을 던진다")
    @Test
    void should_throwEntityNotFoundException_when_contentNotFound() {
        // given
        Integer contentNo = 999;
        given(contentRepository.findById(contentNo)).willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> contentService.findById(contentNo))
                .isInstanceOf(EntityNotFoundException.class);
        verify(contentRepository).findById(contentNo);
    }

    @DisplayName("컨텐츠 등록 요청 DTO가 주어지면 저장 후 응답 DTO를 반환한다")
    @Test
    void should_returnSavedContent_when_saveRequestDtoIsGiven() {
        // given
        Content content = Content.builder()
                .contentNo(1)
                .contentName("공지 컨텐츠")
                .contentRemark("공지 페이지 컨텐츠")
                .contentValue("<p>공지</p>")
                .build();
        given(contentRepository.save(any(Content.class))).willReturn(content);

        ContentSaveRequestDto saveRequestDto = new ContentSaveRequestDto();
        ReflectionTestUtils.setField(saveRequestDto, "contentName", "공지 컨텐츠");
        ReflectionTestUtils.setField(saveRequestDto, "contentRemark", "공지 페이지 컨텐츠");
        ReflectionTestUtils.setField(saveRequestDto, "contentValue", "<p>공지</p>");

        // when
        ContentResponseDto result = contentService.save(saveRequestDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContentName()).isEqualTo("공지 컨텐츠");
        assertThat(result.getContentValue()).isEqualTo("<p>공지</p>");
        verify(contentRepository).save(any(Content.class));
    }

    @DisplayName("컨텐츠 번호와 수정 요청이 주어지면 컨텐츠를 수정하고 응답 DTO를 반환한다")
    @Test
    void should_returnUpdatedContent_when_updateRequestIsGiven() {
        // given
        Integer contentNo = 1;
        Content content = Content.builder()
                .contentNo(contentNo)
                .contentName("기존 컨텐츠")
                .contentRemark("기존 비고")
                .contentValue("<p>기존 내용</p>")
                .build();
        given(contentRepository.findById(contentNo)).willReturn(Optional.of(content));

        org.egovframe.cloud.portalservice.api.content.dto.ContentUpdateRequestDto updateRequestDto =
                new org.egovframe.cloud.portalservice.api.content.dto.ContentUpdateRequestDto();
        ReflectionTestUtils.setField(updateRequestDto, "contentName", "수정된 컨텐츠");
        ReflectionTestUtils.setField(updateRequestDto, "contentRemark", "수정된 비고");
        ReflectionTestUtils.setField(updateRequestDto, "contentValue", "<p>수정된 내용</p>");

        // when
        ContentResponseDto result = contentService.update(contentNo, updateRequestDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContentName()).isEqualTo("수정된 컨텐츠");
        assertThat(result.getContentValue()).isEqualTo("<p>수정된 내용</p>");
    }

    @DisplayName("컨텐츠 삭제 시 존재하지 않는 번호이면 EntityNotFoundException을 던진다")
    @Test
    void should_throwEntityNotFoundException_when_deleteNonExistentContent() {
        // given
        Integer contentNo = 999;
        given(contentRepository.findById(contentNo)).willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> contentService.delete(contentNo))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
