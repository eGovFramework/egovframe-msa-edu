package org.egovframe.cloud.reserverequestservice.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CategoryTest {

    @Test
    @DisplayName("EDUCATION 카테고리의 key와 title이 올바르게 반환된다")
    void education_key_and_title() {
        assertThat(Category.EDUCATION.getKey()).isEqualTo("education");
        assertThat(Category.EDUCATION.getTitle()).isEqualTo("교육");
    }

    @Test
    @DisplayName("EQUIPMENT 카테고리의 key와 title이 올바르게 반환된다")
    void equipment_key_and_title() {
        assertThat(Category.EQUIPMENT.getKey()).isEqualTo("equipment");
        assertThat(Category.EQUIPMENT.getTitle()).isEqualTo("장비");
    }

    @Test
    @DisplayName("SPACE 카테고리의 key와 title이 올바르게 반환된다")
    void space_key_and_title() {
        assertThat(Category.SPACE.getKey()).isEqualTo("space");
        assertThat(Category.SPACE.getTitle()).isEqualTo("공간");
    }

    @Test
    @DisplayName("isEquals는 동일한 key 문자열에 대해 true를 반환한다")
    void is_equals_returns_true_for_matching_key() {
        assertThat(Category.EQUIPMENT.isEquals("equipment")).isTrue();
        assertThat(Category.SPACE.isEquals("space")).isTrue();
        assertThat(Category.EDUCATION.isEquals("education")).isTrue();
    }

    @Test
    @DisplayName("isEquals는 다른 key 문자열에 대해 false를 반환한다")
    void is_equals_returns_false_for_non_matching_key() {
        assertThat(Category.EQUIPMENT.isEquals("space")).isFalse();
        assertThat(Category.SPACE.isEquals("education")).isFalse();
        assertThat(Category.EDUCATION.isEquals("equipment")).isFalse();
    }

    @Test
    @DisplayName("Category는 총 3개의 값을 가진다")
    void category_total_count() {
        assertThat(Category.values()).hasSize(3);
    }
}
