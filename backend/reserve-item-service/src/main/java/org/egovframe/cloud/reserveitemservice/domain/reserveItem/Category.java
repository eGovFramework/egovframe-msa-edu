package org.egovframe.cloud.reserveitemservice.domain.reserveItem;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
