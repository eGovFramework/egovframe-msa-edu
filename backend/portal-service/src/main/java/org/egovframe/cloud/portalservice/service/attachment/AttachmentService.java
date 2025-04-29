package org.egovframe.cloud.portalservice.service.attachment;

import java.io.File;
import java.util.Objects;
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
import org.egovframe.cloud.portalservice.utils.PortalUtils;
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
    private static final String SUCCESS_MESSAGE = "Success";
    private static final String FILE_SEPARATOR = File.separator;
    private static final String EDITOR_FILE_SEPARATOR = "-";
    private static final String BASE_PATH = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
    private static final String EDITOR_PATH = "editor/"+BASE_PATH;

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
        return upload(file, BASE_PATH, true);
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
                .physicalFileName(StringUtils.cleanPath(basePath + FILE_SEPARATOR + storeFile))
                .message(SUCCESS_MESSAGE)
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
        List<AttachmentFileResponseDto> responseDtoList = new ArrayList<>();

        for (MultipartFile file : files) {
            responseDtoList.add(upload(file, BASE_PATH, true));
        }
        return responseDtoList;
    }

    /**
     * 에디터 파일 업로드
     *
     * @param editorRequestDto
     * @return
     */
    public AttachmentEditorResponseDto uploadEditor(AttachmentBase64RequestDto editorRequestDto) throws BusinessMessageException {
        String fileBase64 = editorRequestDto.getFileBase64();

        if (Objects.isNull(fileBase64) || "".equals(fileBase64)) {
            // 업로드할 파일이 없습니다.
            throw new BusinessMessageException("valid.file.not_exists");
        }

        if (fileBase64.length() > 400000) {
            //파일 용량이 너무 큽니다.
            throw new BusinessMessageException(getMessage("valid.file.too_big"));
        }

        String storeFile = storageUtils.storeBase64File(editorRequestDto, EDITOR_PATH);

        return AttachmentEditorResponseDto.builder()
                .uploaded(1)
                .url(EDITOR_PATH.replaceAll(FILE_SEPARATOR, EDITOR_FILE_SEPARATOR) + EDITOR_FILE_SEPARATOR + storeFile)
                .originalFileName(editorRequestDto.getOriginalName())
                .size(editorRequestDto.getSize())
                .fileType(editorRequestDto.getFileType())
                .message(SUCCESS_MESSAGE)
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
    	if(FILE_SEPARATOR.equals("\\")) {//윈도우기반 자바시스템일 때 하이픈 character to be escaped is missing 에러방지
    		imagename = imagename.replaceAll(EDITOR_FILE_SEPARATOR, "\\\\"); //getFileSystem().getPath에서 디스크의 경로를 사용할 때 
    	} else { //리눅스 또는 맥 기반 자바시스템 경로일 때(아래)
    		imagename = imagename.replaceAll(EDITOR_FILE_SEPARATOR, FILE_SEPARATOR);
    	}
        return storageUtils.loadImage(imagename);
    }

    /**
     * img 태그에서 호출 시 byte[] 형태의 값으로 incoding
     *
     * @param uniqueId
     * @return
     */
    @Transactional(readOnly = true)
    public AttachmentImageResponseDto loadImageByUniqueId(String uniqueId) throws EntityNotFoundException {
        Attachment attachment = findAttachmentByUniqueId(uniqueId);

        return storageUtils.loadImage(attachment.getPhysicalFileName());
    }

    /**
     * 첨부파일 다운로드 - 삭제 파일 불가
     *
     * @param uniqueId
     * @return
     */
    public AttachmentDownloadResponseDto downloadFile(String uniqueId) throws EntityNotFoundException, BusinessMessageException {
        Attachment attachment = findAttachmentByUniqueId(uniqueId);

        if (attachment.isDeleted()) {
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
    public AttachmentDownloadResponseDto downloadAttachment(String uniqueId) throws EntityNotFoundException {
        Attachment attachment = findAttachmentByUniqueId(uniqueId);

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
        String attachmentCode = PortalUtils.randomAlphanumeric(20);
        for (int i = 0; i < saveRequestDtoList.size(); i++) {
            AttachmentTempSaveRequestDto requestDto = saveRequestDtoList.get(i);
            AttachmentId attachmentId = AttachmentId.builder()
                    .code(attachmentCode)
                    .seq(i + 1L)
                    .build();

            // 첨부파일 .temp 제거
            String renameTemp = storageUtils.renameTemp(requestDto.getPhysicalFileName());

            attachmentRepository.save(requestDto.toEntity(attachmentId, renameTemp));
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
    public String saveByCode(String attachmentCode, List<AttachmentTempSaveRequestDto> saveRequestDtoList) throws EntityNotFoundException {
        for (AttachmentTempSaveRequestDto saveRequestDto : saveRequestDtoList) {
            // 사용자 삭제인 경우 삭제여부 Y
            if (saveRequestDto.isDelete()) {
                Attachment attachment = findAttachmentByUniqueId(saveRequestDto.getUniqueId());
                attachment.updateIsDelete(saveRequestDto.isDelete());
                continue;
            }

            if (!saveRequestDto.hasUniqueId()) {
                // 해당 attachment에 seq 조회해서 attachmentid 생성
                AttachmentId attachmentId = attachmentRepository.getId(attachmentCode);
                //새로운 첨부파일 저장 (물리적 파일 .temp 제거)
                String renameTemp = storageUtils.renameTemp(saveRequestDto.getPhysicalFileName());

                attachmentRepository.save(saveRequestDto.toEntity(attachmentId, renameTemp));
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
    public String toggleDelete(String uniqueId, boolean isDelete) throws EntityNotFoundException {
        Attachment attachment = findAttachmentByUniqueId(uniqueId);
        attachment.updateIsDelete(isDelete);
        return uniqueId;
    }

    /**
     * 관리자 - 첨부파일 한건 완전 삭제
     *
     * @param uniqueId
     */
    public void delete(String uniqueId) throws EntityNotFoundException {
        Attachment attachment = findAttachmentByUniqueId(uniqueId);
        deleteFile(attachment);
    }

    /**
     * 첨부파일 업로드 및 저장
     *
     * @param files
     * @param uploadRequestDto
     */
    public String uploadAndSave(List<MultipartFile> files, AttachmentUploadRequestDto uploadRequestDto) {
        String attachmentCode = PortalUtils.randomAlphanumeric(20);

        for (int i = 0; i < files.size(); i++) {
            AttachmentId attachmentId = AttachmentId.builder()
                    .code(attachmentCode)
                    .seq(i + 1L)
                    .build();

            // 물리적 파일 생성
            AttachmentFileResponseDto fileResponseDto = upload(files.get(i), BASE_PATH, false);

            attachmentRepository.save(fileResponseDto.toEntity(attachmentId, uploadRequestDto));
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
                                  List<AttachmentUpdateRequestDto> updateRequestDtoList) throws EntityNotFoundException {

        // 기존 파일 삭제 처리
        deleteExistingFile(updateRequestDtoList);

        if (Objects.nonNull(files)) {
            //새로운 파일 저장 처리
            for (int i = 0; i < files.size(); i++) {
                // 해당 attachment에 seq 조회해서 attachmentid 생성
                AttachmentId attachmentId = attachmentRepository.getId(attachmentCode);

                // 물리적 파일 생성
                AttachmentFileResponseDto fileResponseDto = upload(files.get(i), BASE_PATH, false);

                attachmentRepository.save(fileResponseDto.toEntity(attachmentId, uploadRequestDto));
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
    public void deleteAllEmptyEntity(String attachmentCode) throws EntityNotFoundException, BusinessMessageException {
        List<Attachment> attachmentList = attachmentRepository.findByCode(attachmentCode);

        if (Objects.isNull(attachmentList) || attachmentList.size() <= 0) {
            throw new EntityNotFoundException(getMessage("valid.file.not_found") + " ID= " + attachmentCode);
        }

        for (Attachment attachment: attachmentList) {
            // 첨부파일 저장 후 기능 저장 시 오류 날 경우에만 첨부파일 전체 삭제를 하므로
            // entity 정보가 있는 경우에는 삭제하지 못하도록 한다.
            if (attachment.hasEntityId()) {
                throw new BusinessMessageException(getMessage("valid.file.not_deleted"));
            }
            deleteFile(attachment);
        }
    }

    /**
     * 첨부파일 삭제
     * 
     * @param attachment
     */
    private void deleteFile(Attachment attachment) {
        // 물리적 파일 삭제
        boolean deleted = storageUtils.deleteFile(attachment.getPhysicalFileName());
        if (!deleted) {
            throw new BusinessMessageException(getMessage("valid.file.not_deleted"));
        }
        attachmentRepository.delete(attachment);
    }

    /**
     * unique id 로 첨부파일 조회
     *
     * @param uniqueId
     * @return
     */
    private Attachment findAttachmentByUniqueId(String uniqueId) {
        return attachmentRepository.findAllByUniqueId(uniqueId)
            // 파일을 찾을 수 없습니다.
            .orElseThrow(() -> new EntityNotFoundException(getMessage("valid.file.not_found") + " ID= " + uniqueId));
    }

    /**
     * 기존 첨부파일 삭제 처리
     *
     * @param updateRequestDtoList
     */
    private void deleteExistingFile(List<AttachmentUpdateRequestDto> updateRequestDtoList) {
        if (Objects.isNull(updateRequestDtoList)) {
            return;
        }
        for (AttachmentUpdateRequestDto saveRequestDto : updateRequestDtoList) {
            if (saveRequestDto.getIsDelete()) {
                Attachment attachment = findAttachmentByUniqueId(saveRequestDto.getUniqueId());
                attachment.updateIsDelete(saveRequestDto.getIsDelete());
            }
        }
    }

}
