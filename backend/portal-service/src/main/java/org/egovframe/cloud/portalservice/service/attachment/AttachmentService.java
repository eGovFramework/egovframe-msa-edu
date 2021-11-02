package org.egovframe.cloud.portalservice.service.attachment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.common.exception.BusinessMessageException;
import org.egovframe.cloud.common.exception.EntityNotFoundException;
import org.egovframe.cloud.common.service.AbstractService;
import org.egovframe.cloud.portalservice.api.attachment.dto.*;
import org.egovframe.cloud.portalservice.domain.attachment.Attachment;
import org.egovframe.cloud.portalservice.domain.attachment.AttachmentId;
import org.egovframe.cloud.portalservice.domain.attachment.AttachmentRepository;
import org.egovframe.cloud.portalservice.utils.StorageUtils;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * org.egovframe.cloud.portalservice.service.attachment.AttachmentService
 * <p>
 * 첨부파일 서비스 class
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/07/13
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/13    shinmj  최초 생성
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class AttachmentService extends AbstractService {

    private final AttachmentRepository attachmentRepository;
    private final StorageUtils storageUtils;

    /**
     * 첨부파일 업로드
     * .temp 파일 생성
     * 추후 저장로직에서 rename
     *
     * @param file
     * @return
     */
    public AttachmentFileResponseDto uploadFile(MultipartFile file) {

        String basePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));

        return upload(file, basePath, true);
    }

    /**
     * 첨부파일 업로드
     * .temp 파일 생성
     * 추후 저장로직에서 rename
     *
     * @param file
     * @param basePath
     * @return
     */
    private AttachmentFileResponseDto upload(MultipartFile file, String basePath, boolean isTemp) {
        String storeFile = storageUtils.storeFile(file, basePath, isTemp);
        return AttachmentFileResponseDto.builder()
                .originalFileName(file.getOriginalFilename())
                .physicalFileName(StringUtils.cleanPath(basePath + "/" + storeFile))
                .message("Success")
                .size(file.getSize())
                .fileType(file.getContentType())
                .build();
    }


    /**
     * 여러 첨부파일 업로드
     * .temp 파일 생성
     * 추후 저장로직에서 rename
     *
     * @param files
     * @return
     */
    public List<AttachmentFileResponseDto> uploadFiles(List<MultipartFile> files) {
        String basePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        List<AttachmentFileResponseDto> responseDtoList = new ArrayList<>();

        for (MultipartFile file : files) {
            responseDtoList.add(upload(file, basePath, true));
        }
        return responseDtoList;
    }

    /**
     * 에디터 파일 업로드
     *
     * @param editorRequestDto
     * @return
     */
    public AttachmentEditorResponseDto uploadEditor(AttachmentBase64RequestDto editorRequestDto) {
        String fileBase64 = editorRequestDto.getFileBase64();

        if (fileBase64 == null || fileBase64.equals("")) {
            // 업로드할 파일이 없습니다.
            throw new BusinessMessageException("valid.file.not_exists");
        }

        if (fileBase64.length() > 400000) {
            //파일 용량이 너무 큽니다.
            throw new BusinessMessageException(getMessage("valid.file.too_big"));
        }

        String basePath = "editor/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        String storeFile = storageUtils.storeBase64File(editorRequestDto, basePath);

        return AttachmentEditorResponseDto.builder()
                .uploaded(1)
                .url(basePath.replaceAll("/", "-") + "-" + storeFile)
                .originalFileName(editorRequestDto.getOriginalName())
                .size(editorRequestDto.getSize())
                .fileType(editorRequestDto.getFileType())
                .message("Success")
                .build();
    }

    /**
     * 에디터에서 호출 시 byte[] 형태의 값으로 incoding
     *
     * @param imagename
     * @return
     */
    @Transactional(readOnly = true)
    public AttachmentImageResponseDto loadImage(String imagename) {
        imagename = imagename.replaceAll("-", "/");
        return storageUtils.loadImage(imagename);
    }

    /**
     * img 태그에서 호출 시 byte[] 형태의 값으로 incoding
     *
     * @param uniqueId
     * @return
     */
    @Transactional(readOnly = true)
    public AttachmentImageResponseDto loadImageByUniqueId(String uniqueId) {
        Attachment attachment = attachmentRepository.findAllByUniqueId(uniqueId)
                // 파일을 찾을 수 없습니다.
                .orElseThrow(() -> new EntityNotFoundException(getMessage("valid.file.not_found") + " ID= " + uniqueId));

        return storageUtils.loadImage(attachment.getPhysicalFileName());
    }

    /**
     * 첨부파일 다운로드 - 삭제 파일 불가
     *
     * @param uniqueId
     * @return
     */
    public AttachmentDownloadResponseDto downloadFile(String uniqueId) {
        Attachment attachment = attachmentRepository.findAllByUniqueId(uniqueId)
                // 파일을 찾을 수 없습니다.
                .orElseThrow(() -> new EntityNotFoundException(getMessage("valid.file.not_found") + " ID= " + uniqueId));

        if (Boolean.TRUE.equals(attachment.getIsDelete())) {
            throw new BusinessMessageException(getMessage("err.entity.not.found"));
        }

        Resource resource = storageUtils.downloadFile(attachment.getPhysicalFileName());

        // 첨부파일 다운로드 할 때 마다 Download 횟수 + 1
        attachment.updateDownloadCnt();

        return AttachmentDownloadResponseDto.builder()
                .file(resource)
                .originalFileName(attachment.getOriginalFileName())
                .build();
    }

    /**
     * 첨부파일 코드로 첨부파일 목록 조회
     *
     * @param attachmentCode
     * @return
     */
    @Transactional(readOnly = true)
    public List<AttachmentResponseDto> findByCode(String attachmentCode) {
        List<Attachment> attachmentList = attachmentRepository.findByCode(attachmentCode);
        return attachmentList.stream()
                .map(attachment -> AttachmentResponseDto.builder().attachment(attachment).build())
                .collect(Collectors.toList());
    }

    /**
     * 첨부파일 다운로드 - 삭제 파일 가능
     *
     * @param uniqueId
     * @return
     */
    public AttachmentDownloadResponseDto downloadAttachment(String uniqueId) {
        Attachment attachment = attachmentRepository.findAllByUniqueId(uniqueId)
                // 파일을 찾을 수 없습니다.
                .orElseThrow(() -> new EntityNotFoundException(getMessage("valid.file.not_found") + " ID= " + uniqueId));

        Resource resource = storageUtils.downloadFile(attachment.getPhysicalFileName());

        // 첨부파일 다운로드 할 때 마다 Download 횟수 + 1
        attachment.updateDownloadCnt();

        return AttachmentDownloadResponseDto.builder()
                .file(resource)
                .originalFileName(attachment.getOriginalFileName())
                .build();
    }

    /**
     * 첨부파일 저장
     *
     * @param saveRequestDtoList
     * @return 생성된 첨부파일 코드
     */
    public String save(List<AttachmentTempSaveRequestDto> saveRequestDtoList) {
        String attachmentCode = RandomStringUtils.randomAlphanumeric(20);
        for (int i = 0; i < saveRequestDtoList.size(); i++) {
            AttachmentTempSaveRequestDto requestDto = saveRequestDtoList.get(i);
            AttachmentId attachmentId = AttachmentId.builder()
                    .code(attachmentCode)
                    .seq(i + 1L)
                    .build();

            // 첨부파일 .temp 제거
            String renameTemp = storageUtils.renameTemp(requestDto.getPhysicalFileName());

            attachmentRepository.save(
                    Attachment.builder()
                            .attachmentId(attachmentId)
                            .uniqueId(UUID.randomUUID().toString())
                            .physicalFileName(renameTemp)
                            .originalFileName(requestDto.getOriginalName())
                            .size(requestDto.getSize())
                            .fileType(requestDto.getFileType())
                            .entityName(requestDto.getEntityName())
                            .entityId(requestDto.getEntityId())
                            .build()
            );
        }
        return attachmentCode;
    }

    /**
     * 첨부파일 저장
     * 이미 attachment code 가 있는 경우 seq만 새로 생성해서 저장
     * or
     * isUserDelete = true 인 경우 삭제 여부 Y
     *
     * @param attachmentCode
     * @param saveRequestDtoList
     * @return
     */
    public String saveByCode(String attachmentCode, List<AttachmentTempSaveRequestDto> saveRequestDtoList) {
        for (AttachmentTempSaveRequestDto saveRequestDto : saveRequestDtoList) {
            // 사용자 삭제인 경우 삭제여부 Y
            if (saveRequestDto.isDelete()) {
                Attachment attachment = attachmentRepository.findAllByUniqueId(saveRequestDto.getUniqueId())
                        // 파일을 찾을 수 없습니다.
                        .orElseThrow(() -> new EntityNotFoundException(getMessage("valid.file.not_found") + " ID= " + saveRequestDto.getUniqueId()));
                attachment.updateIsDelete(saveRequestDto.isDelete());
            } else if (saveRequestDto.getUniqueId() == null || saveRequestDto.getUniqueId().equals("")) {
                // 해당 attachment에 seq 조회해서 attachmentid 생성
                AttachmentId attachmentId = attachmentRepository.getId(attachmentCode);
                //새로운 첨부파일 저장 (물리적 파일 .temp 제거)
                String renameTemp = storageUtils.renameTemp(saveRequestDto.getPhysicalFileName());
                attachmentRepository.save(
                        Attachment.builder()
                                .attachmentId(attachmentId)
                                .uniqueId(UUID.randomUUID().toString())
                                .originalFileName(saveRequestDto.getOriginalName())
                                .physicalFileName(renameTemp)
                                .size(saveRequestDto.getSize())
                                .fileType(saveRequestDto.getFileType())
                                .entityName(saveRequestDto.getEntityName())
                                .entityId(saveRequestDto.getEntityId())
                                .build()
                );
            }
        }

        return attachmentCode;
    }

    /**
     * 관리자 - 전체 첨부파일 목록 조회
     *
     * @param searchRequestDto
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public Page<AttachmentResponseDto> search(RequestDto searchRequestDto, Pageable pageable) {
        return attachmentRepository.search(searchRequestDto, pageable);
    }

    /**
     * 관리자 - 삭제여부 토글
     *
     * @param uniqueId
     * @param isDelete
     * @return
     */
    public String toggleDelete(String uniqueId, boolean isDelete) {
        Attachment attachment = attachmentRepository.findAllByUniqueId(uniqueId)
                // 파일을 찾을 수 없습니다.
                .orElseThrow(() -> new EntityNotFoundException(getMessage("valid.file.not_found") + " ID= " + uniqueId));

        attachment.updateIsDelete(isDelete);
        return uniqueId;
    }

    /**
     * 관리자 - 첨부파일 한건 완전 삭제
     *
     * @param uniqueId
     */
    public void delete(String uniqueId) {
        Attachment attachment = attachmentRepository.findAllByUniqueId(uniqueId)
                // 파일을 찾을 수 없습니다.
                .orElseThrow(() -> new EntityNotFoundException(getMessage("valid.file.not_found") + " ID= " + uniqueId));
        // 물리적 파일 삭제
        boolean deleted = storageUtils.deleteFile(attachment.getPhysicalFileName());
        if (deleted) {
            attachmentRepository.delete(attachment);
        } else {
            throw new BusinessMessageException(getMessage("valid.file.not_deleted"));
        }
    }

    /**
     * 첨부파일 업로드 및 저장
     *
     * @param files
     * @param uploadRequestDto
     */
    public String uploadAndSave(List<MultipartFile> files, AttachmentUploadRequestDto uploadRequestDto) {
        String basePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        String attachmentCode = RandomStringUtils.randomAlphanumeric(20);

        for (int i = 0; i < files.size(); i++) {
            AttachmentId attachmentId = AttachmentId.builder()
                    .code(attachmentCode)
                    .seq(i + 1L)
                    .build();

            // 물리적 파일 생성
            AttachmentFileResponseDto fileResponseDto = upload(files.get(i), basePath, false);

            attachmentRepository.save(
                    Attachment.builder()
                            .attachmentId(attachmentId)
                            .uniqueId(UUID.randomUUID().toString())
                            .physicalFileName(fileResponseDto.getPhysicalFileName())
                            .originalFileName(fileResponseDto.getOriginalFileName())
                            .size(fileResponseDto.getSize())
                            .fileType(fileResponseDto.getFileType())
                            .entityName(uploadRequestDto.getEntityName())
                            .entityId(uploadRequestDto.getEntityId())
                            .build()
            );
        }

        return attachmentCode;
    }

    /**
     * 첨부파일 저장
     * 이미 attachment code 가 있는 경우 이므로 seq만 새로 생성해서 저장
     * or
     * isUserDelete = true 인 경우 삭제 여부 Y
     *
     * @param files
     * @param attachmentCode
     * @param uploadRequestDto
     * @param updateRequestDtoList
     * @return
     */
    public String uploadAndUpdate(List<MultipartFile> files,
                                  String attachmentCode,
                                  AttachmentUploadRequestDto uploadRequestDto,
                                  List<AttachmentUpdateRequestDto> updateRequestDtoList) {
        String basePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));

        // 기존 파일 삭제 처리
        if (updateRequestDtoList != null) {
            for (AttachmentUpdateRequestDto saveRequestDto : updateRequestDtoList) {
                if (saveRequestDto.getIsDelete()) {
                    Attachment attachment = attachmentRepository.findAllByUniqueId(saveRequestDto.getUniqueId())
                            // 파일을 찾을 수 없습니다.
                            .orElseThrow(() -> new EntityNotFoundException(getMessage("valid.file.not_found") + " ID= " + saveRequestDto.getUniqueId()));
                    attachment.updateIsDelete(saveRequestDto.getIsDelete());
                }
            }
        }

        if (files != null) {
            //새로운 파일 저장 처리
            for (int i = 0; i < files.size(); i++) {
                // 해당 attachment에 seq 조회해서 attachmentid 생성
                AttachmentId attachmentId = attachmentRepository.getId(attachmentCode);

                // 물리적 파일 생성
                AttachmentFileResponseDto fileResponseDto = upload(files.get(i), basePath, false);

                attachmentRepository.save(
                        Attachment.builder()
                                .attachmentId(attachmentId)
                                .uniqueId(UUID.randomUUID().toString())
                                .physicalFileName(fileResponseDto.getPhysicalFileName())
                                .originalFileName(fileResponseDto.getOriginalFileName())
                                .size(fileResponseDto.getSize())
                                .fileType(fileResponseDto.getFileType())
                                .entityName(uploadRequestDto.getEntityName())
                                .entityId(uploadRequestDto.getEntityId())
                                .build()
                );
            }
        }


        return attachmentCode;
    }

    /**
     * entity 정보 update
     *
     * @param attachmentCode
     * @param uploadRequestDto
     * @return
     */
    public String updateEntity(String attachmentCode, AttachmentUploadRequestDto uploadRequestDto) {
        List<Attachment> attachments = attachmentRepository.findByCode(attachmentCode);
        for (Attachment attachment : attachments) {
            attachment.updateEntity(uploadRequestDto.getEntityName(), uploadRequestDto.getEntityId());
        }

        return attachmentCode;
    }

    /**
     * 첨부파일 저장 후 기능 저장 시 오류 날 경우
     * 조회되는 첨부파일 목록 전부 삭제
     * rollback
     *
     * @param attachmentCode
     */
    public void deleteAllEmptyEntity(String attachmentCode) {
        List<Attachment> attachmentList = attachmentRepository.findByCode(attachmentCode);

        if (attachmentList == null || attachmentList.size() <= 0) {
            throw new EntityNotFoundException(getMessage("valid.file.not_found") + " ID= " + attachmentCode);
        }

        for (Attachment attachment: attachmentList) {
            // 첨부파일 저장 후 기능 저장 시 오류 날 경우에만 첨부파일 전체 삭제를 하므로
            // entity 정보가 있는 경우에는 삭제하지 못하도록 한다.
            if ((attachment.getEntityId() != null || StringUtils.hasText(attachment.getEntityId())) && !attachment.getEntityId().equals("-1")) {
                throw new BusinessMessageException(getMessage("valid.file.not_deleted"));
            }
            // 물리적 파일 삭제
            boolean deleted = storageUtils.deleteFile(attachment.getPhysicalFileName());
            if (deleted) {
                attachmentRepository.delete(attachment);
            } else {
                throw new BusinessMessageException(getMessage("valid.file.not_deleted"));
            }
        }
    }
}
