package org.egovframe.cloud.reserveitemservice.domain.reserveItem;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * org.egovframe.cloud.reserveitemservice.domain.reserveItem.CategoryTest
 * <p>
 * Category 열거형 단위 테스트
 *
 * @author 표준프레임워크센터
 * @version 1.0
 * @since 2024/01/01
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2024/01/01    contributors  최초 생성
 * </pre>
 */
class CategoryTest {

    @DisplayName("Category 열거형 key·title 값이 올바르게 정의된다")
    @Test
    void category_values_are_defined_correctly() {
        assertThat(Category.EDUCATION.getKey()).isEqualTo("education");
        assertThat(Category.EDUCATION.getTitle()).isEqualTo("교육");

        assertThat(Category.EQUIPMENT.getKey()).isEqualTo("equipment");
        assertThat(Category.EQUIPMENT.getTitle()).isEqualTo("장비");

        assertThat(Category.SPACE.getKey()).isEqualTo("space");
        assertThat(Category.SPACE.getTitle()).isEqualTo("공간");
    }

    @DisplayName("isEquals 는 key 문자열이 일치할 때만 true 를 반환한다")
    @Test
    void isEquals_returns_true_only_for_matching_key() {
        assertThat(Category.EDUCATION.isEquals("education")).isTrue();
        assertThat(Category.EDUCATION.isEquals("equipment")).isFalse();
        assertThat(Category.EDUCATION.isEquals("EDUCATION")).isFalse();
        assertThat(Category.EQUIPMENT.isEquals("equipment")).isTrue();
        assertThat(Category.SPACE.isEquals("space")).isTrue();
    }

    @DisplayName("Category 열거형은 세 가지 값만 존재한다")
    @Test
    void category_has_exactly_three_values() {
        assertThat(Category.values()).hasSize(3);
    }

    @DisplayName("Category.valueOf 로 이름을 통해 열거형을 조회할 수 있다")
    @Test
    void category_can_be_retrieved_by_name() {
        assertThat(Category.valueOf("EDUCATION")).isEqualTo(Category.EDUCATION);
        assertThat(Category.valueOf("EQUIPMENT")).isEqualTo(Category.EQUIPMENT);
        assertThat(Category.valueOf("SPACE")).isEqualTo(Category.SPACE);
    }
}
