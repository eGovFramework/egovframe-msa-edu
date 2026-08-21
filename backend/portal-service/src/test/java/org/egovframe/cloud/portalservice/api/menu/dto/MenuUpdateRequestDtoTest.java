package org.egovframe.cloud.portalservice.api.menu.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link MenuUpdateRequestDto#hasUrlPath()}의 링크 URL 필수 검증을 확인한다.
 *
 * <p>내부·외부 링크 메뉴는 링크 URL이 필수다. MenuService가 이 메서드의 결과로
 * "링크 Url 값은 필수 입니다" 예외를 던진다.
 */
class MenuUpdateRequestDtoTest {

    private MenuUpdateRequestDto dto(String menuType, String urlPath) {
        return MenuUpdateRequestDto.builder()
                .menuKorName("메뉴")
                .menuType(menuType)
                .urlPath(urlPath)
                .build();
    }

    @Test
    @DisplayName("내부링크에 공백만 있는 URL은 필수 검증을 통과하지 못한다")
    void blankUrlPathIsRejected() {
        assertThat(dto("inside", "   ").hasUrlPath()).isFalse();
        assertThat(dto("outside", "").hasUrlPath()).isFalse();
    }

    @Test
    @DisplayName("내부·외부링크에 URL이 없으면 필수 검증을 통과하지 못한다")
    void nullUrlPathIsRejected() {
        assertThat(dto("inside", null).hasUrlPath()).isFalse();
        assertThat(dto("outside", null).hasUrlPath()).isFalse();
    }

    @Test
    @DisplayName("URL이 있으면 통과하고, 링크 유형이 아니면 검증 대상이 아니다")
    void validUrlPathAndOtherMenuTypesPass() {
        assertThat(dto("inside", "/portal/main").hasUrlPath()).isTrue();
        assertThat(dto("contents", null).hasUrlPath()).isTrue();
    }
}
