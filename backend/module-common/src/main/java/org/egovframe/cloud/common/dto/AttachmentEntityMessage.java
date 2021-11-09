package org.egovframe.cloud.common.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AttachmentEntityMessage {

	private String attachmentCode;
	private String entityName;
	private String entityId;

	@Builder
	public AttachmentEntityMessage(String attachmentCode, String entityName, String entityId) {
		this.attachmentCode = attachmentCode;
		this.entityName = entityName;
		this.entityId = entityId;
	}
}
