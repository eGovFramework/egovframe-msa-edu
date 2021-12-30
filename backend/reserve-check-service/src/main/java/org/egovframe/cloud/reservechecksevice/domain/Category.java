package org.egovframe.cloud.reservechecksevice.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * org.egovframe.cloud.reservechecksevice.domain.Category
 *
 * 예약 유형 enum class
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/09/15
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/15    shinmj       최초 생성
 * </pre>
 */
@Getter
@RequiredArgsConstructor
public enum Category {
	EDUCATION("education", "교육"),
	EQUIPMENT("equipment", "장비"),
	SPACE("space", "공간");

	private final String key;
	private final String title;

	public boolean isEquals(String compare) {
		return this.getKey().equals(compare);
	}
}
