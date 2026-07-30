package org.egovframe.cloud.reservechecksevice.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryTest {

    @Test
    @DisplayName("각 예약 유형의 key와 title이 정확하게 정의된다")
    void 예약_유형_key_title_검증() {
        assertThat(Category.EDUCATION.getKey()).isEqualTo("education");
        assertThat(Category.EDUCATION.getTitle()).isEqualTo("교육");

        assertThat(Category.EQUIPMENT.getKey()).isEqualTo("equipment");
        assertThat(Category.EQUIPMENT.getTitle()).isEqualTo("장비");

        assertThat(Category.SPACE.getKey()).isEqualTo("space");
        assertThat(Category.SPACE.getTitle()).isEqualTo("공간");
    }

    @Test
    @DisplayName("isEquals는 key 문자열이 일치할 때 true를 반환한다")
    void isEquals_일치하는_key_true반환() {
        assertThat(Category.EDUCATION.isEquals("education")).isTrue();
        assertThat(Category.EQUIPMENT.isEquals("equipment")).isTrue();
        assertThat(Category.SPACE.isEquals("space")).isTrue();
    }

    @Test
    @DisplayName("isEquals는 key 문자열이 다를 때 false를 반환한다")
    void isEquals_다른_key_false반환() {
        assertThat(Category.EDUCATION.isEquals("equipment")).isFalse();
        assertThat(Category.EDUCATION.isEquals("EDUCATION")).isFalse();
        assertThat(Category.EQUIPMENT.isEquals("space")).isFalse();
        assertThat(Category.SPACE.isEquals("")).isFalse();
    }

    @Test
    @DisplayName("전체 enum 상수 개수는 3개이다")
    void 예약_유형_상수_개수_검증() {
        assertThat(Category.values()).hasSize(3);
    }
}
