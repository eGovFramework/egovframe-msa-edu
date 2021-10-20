package org.egovframe.cloud.portalservice.config;

import java.util.function.Consumer;

import org.egovframe.cloud.common.dto.AttachmentEntityMessage;
import org.egovframe.cloud.portalservice.api.attachment.dto.AttachmentUploadRequestDto;
import org.egovframe.cloud.portalservice.service.attachment.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class EventStreamConfig {

	@Autowired
	private AttachmentService attachmentService;

	@Bean
	public Consumer<AttachmentEntityMessage> attachmentEntity() {
		return attachmentEntityMessage -> attachmentService.updateEntity(
			attachmentEntityMessage.getAttachmentCode(),
			AttachmentUploadRequestDto.builder()
				.entityName(attachmentEntityMessage.getEntityName())
				.entityId(attachmentEntityMessage.getEntityId())
				.build());
	}
}
