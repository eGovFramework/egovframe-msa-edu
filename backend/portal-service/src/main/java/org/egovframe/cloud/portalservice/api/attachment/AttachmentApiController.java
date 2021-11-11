package org.egovframe.cloud.portalservice.api.attachment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.common.exception.BusinessMessageException;
import org.egovframe.cloud.portalservice.api.attachment.dto.*;
import org.egovframe.cloud.portalservice.service.attachment.AttachmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * org.egovframe.cloud.portalservice.api.attachment.AttachmentApiController
 * <p>
 * 첨부파일 API controller class
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/07/14
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/14    shinmj  최초 생성
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class AttachmentApiController {

    private final AttachmentService attachmentService;


    /**
     * 첨부파일 업로드 - 단건
     * 물리적 파일에 대해 업로드만 진행 (.temp)
     * 추후 저장 필요
     *
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/api/v1/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public AttachmentFileResponseDto upload(@RequestParam("file") MultipartFile file) {
        return attachmentService.uploadFile(file);
    }

    /**
     * 첨부파일 업로드 - 여러 건
     * 물리적 파일에 대해 업로드만 진행 (.temp)
     * 추후 저장 필요
     *
     * @param files
     * @return
     */
    @PostMapping(value = "/api/v1/upload/multi")
    @ResponseStatus(HttpStatus.CREATED)
    public List<AttachmentFileResponseDto> uploadMulti(@RequestParam("files") List<MultipartFile> files) {
        return attachmentService.uploadFiles(files);
    }

    /**
     * 에디터에서 파일 업로드
     * 현재 이미지만 적용
     *
     * @param editorRequestDto
     * @return
     */
    @PostMapping(value = "/api/v1/upload/editor")
    @ResponseStatus(HttpStatus.CREATED)
    public AttachmentEditorResponseDto uploadEditor(@RequestBody AttachmentBase64RequestDto editorRequestDto) {
        return attachmentService.uploadEditor(editorRequestDto);
    }

    /**
     * 에디터에서 파일 경로(명) 이미지 load
     *
     * @param imagename
     * @return
     * @throws IOException
     */
    @GetMapping(value = "/api/v1/images/editor/{imagename}")
    public ResponseEntity<byte[]> loadImages(@PathVariable("imagename") String imagename) {
        AttachmentImageResponseDto image = attachmentService.loadImage(imagename);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getMimeType()))
                .body(image.getData());
    }

    /**
     * unique id로 이미지 태그에서 이미지 load
     *
     * @param uniqueId
     * @return
     */
    @GetMapping(value = "/api/v1/images/{uniqueId}")
    public ResponseEntity<byte[]> loadImagesByUniqueId(@PathVariable String uniqueId) {
        AttachmentImageResponseDto image = attachmentService.loadImageByUniqueId(uniqueId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getMimeType()))
                .body(image.getData());

    }

    /**
     * 첨부파일 다운로드
     *
     * @param uniqueId
     * @return
     * @throws IOException
     */
    @GetMapping(value = "/api/v1/download/{uniqueId}")
    public ResponseEntity<?> downloadFile(@PathVariable String uniqueId) {
        AttachmentDownloadResponseDto downloadFile = attachmentService.downloadFile(uniqueId);

        String mimeType = null;
        try {
            // get mime type
            URLConnection connection = new URL(downloadFile.getFile().getURL().toString()).openConnection();
            mimeType = connection.getContentType();
        } catch (IOException ex) {
            log.error("download fail", ex);
            throw new BusinessMessageException("Sorry. download fail... \uD83D\uDE3F");
        }

        if (mimeType == null) {
            mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename(downloadFile.getOriginalFileName(), StandardCharsets.UTF_8)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, mimeType);
        headers.setContentDisposition(contentDisposition);

        return ResponseEntity.ok()
                .headers(headers)
                .body(downloadFile.getFile());
    }

    /**
     * 첨부파일 코드로 첨부파일 목록 조회
     *
     * @param attachmentCode
     * @return
     */
    @GetMapping(value = "/api/v1/attachments/{attachmentCode}")
    public List<AttachmentResponseDto> findByCode(@PathVariable String attachmentCode) {
        return attachmentService.findByCode(attachmentCode);
    }

    /**
     * 첨부파일 다운로드
     *
     * @param uniqueId
     * @return
     * @throws IOException
     */
    @GetMapping(value = "/api/v1/attachments/download/{uniqueId}")
    public ResponseEntity<?> downloadAttachment(@PathVariable String uniqueId) {
        AttachmentDownloadResponseDto downloadFile = attachmentService.downloadAttachment(uniqueId);

        String mimeType = null;
        try {
            // get mime type
            URLConnection connection = new URL(downloadFile.getFile().getURL().toString()).openConnection();
            mimeType = connection.getContentType();
        } catch (IOException ex) {
            log.error("download fail", ex);
            throw new BusinessMessageException("Sorry. download fail... \uD83D\uDE3F");
        }

        if (mimeType == null) {
            mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename(downloadFile.getOriginalFileName(), StandardCharsets.UTF_8)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, mimeType);
        headers.setContentDisposition(contentDisposition);

        return ResponseEntity.ok()
                .headers(headers)
                .body(downloadFile.getFile());
    }

    /**
     * 첨부파일 저장 - 물리적 파일은 .temp로 저장 된 후 호출되어야 함
     * 새롭게 attachment code를 생성해야 하는 경우
     *
     * @param saveRequestDtoList
     * @return 새로 생성한 첨부파일 code
     */
    @PostMapping(value = "/api/v1/attachments/file")
    @ResponseStatus(HttpStatus.CREATED)
    public String save(@RequestBody List<AttachmentTempSaveRequestDto> saveRequestDtoList) {
        return attachmentService.save(saveRequestDtoList);
    }

    /**
     * 첨부파일 저장 - 물리적 파일은 .temp로 저장 된 후 호출되어야 함
     * 이미 attachment code 가 있는 경우 seq만 새로 생성해서 저장
     * or
     * isDelete = true 인 경우 삭제 여부 Y
     *
     * @param saveRequestDtoList
     * @return
     */
    @PutMapping(value = "/api/v1/attachments/file/{attachmentCode}")
    public String saveByCode(@PathVariable String attachmentCode, @RequestBody List<AttachmentTempSaveRequestDto> saveRequestDtoList) {
        return attachmentService.saveByCode(attachmentCode, saveRequestDtoList);
    }

    /**
     * 관리자 - 전체 첨부파일 목록 조회
     *
     * @param searchRequestDto
     * @param pageable
     * @return
     */
    @GetMapping(value = "/api/v1/attachments")
    public Page<AttachmentResponseDto> search(RequestDto searchRequestDto, Pageable pageable) {
        return attachmentService.search(searchRequestDto, pageable);
    }

    /**
     * 관리자 - 삭제여부 토글
     *
     * @param uniqueId
     * @param isDelete
     * @return
     */
    @PutMapping(value = "/api/v1/attachments/{uniqueId}/{isDelete}")
    public String toggleDelete(@PathVariable String uniqueId, @PathVariable boolean isDelete) {
        return attachmentService.toggleDelete(uniqueId, isDelete);
    }


    /**
     * 관리자 - 하나의 파일 삭제
     * 물리적 파일 삭제
     *
     * @param uniqueId
     */
    @DeleteMapping(value = "/api/v1/attachments/{uniqueId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String uniqueId) {
        attachmentService.delete(uniqueId);
    }

    /**
     * 첨부파일 저장
     * 새롭게 attachment code를 생성해야 하는 경우
     *
     * @param files
     * @param uploadRequestDto
     * @return
     */
    @PostMapping(value = "/api/v1/attachments/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public String uploadAndSave(@RequestPart(value = "files", required = true) List<MultipartFile> files,
                                @RequestPart(value = "info", required = false) AttachmentUploadRequestDto uploadRequestDto) {
        return attachmentService.uploadAndSave(files, uploadRequestDto);
    }

    /**
     * 첨부파일 저장
     * 이미 attachment code 가 있는 경우 이므로 seq만 새로 생성해서 저장
     * or
     * isDelete = true 인 경우 삭제 여부 Y
     *
     * @param files
     * @param attachmentCode
     * @param uploadRequestDto
     * @param saveRequestDtoList
     * @return
     */
    @PutMapping(value = "/api/v1/attachments/upload/{attachmentCode}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public String uploadAndUpdate(@PathVariable String attachmentCode,
                                  @RequestPart(value = "files", required = true) List<MultipartFile> files,
                                  @RequestPart(value = "info", required = true) AttachmentUploadRequestDto uploadRequestDto,
                                  @RequestPart(value = "list", required = false) List<AttachmentUpdateRequestDto> saveRequestDtoList) {
        return attachmentService.uploadAndUpdate(files, attachmentCode, uploadRequestDto, saveRequestDtoList);
    }

    /**
     * 첨부파일 저장 - 업로드 없이 기존 파일만 삭제
     * isDelete = true 인 경우 삭제 여부 Y
     *
     * @param attachmentCode
     * @param uploadRequestDto
     * @param updateRequestDtoList
     * @return
     */
    @PutMapping(value = "/api/v1/attachments/{attachmentCode}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public String update(@PathVariable String attachmentCode,
                         @RequestPart(value = "info") AttachmentUploadRequestDto uploadRequestDto,
                         @RequestPart(value = "list") List<AttachmentUpdateRequestDto> updateRequestDtoList){

        return attachmentService.uploadAndUpdate(null, attachmentCode,
                uploadRequestDto, updateRequestDtoList);
    }

    /**
     * attachmentCode로 해당하는 모든 첨부파일의 entity 정보 업데이트
     * 신규 entity의 경우 entity가 저장 된 후 entity id가 생성되므로
     * entity 저장 후 해당 api 호출하여 entity 정보를 업데이트 해준다.
     *
     * @param attachmentCode
     * @param uploadRequestDto
     * @return
     */
    @PutMapping("/api/v1/attachments/{attachmentCode}/info")
    public String updateEntity(@PathVariable String attachmentCode,
                               @RequestBody AttachmentUploadRequestDto uploadRequestDto) {
        return attachmentService.updateEntity(attachmentCode, uploadRequestDto);
    }

    /**
     * 첨부파일 저장 후 기능 저장 시 오류 날 경우
     * 해당 첨부파일 코드에 조회되는 첨부파일 목록 전부 삭제
     * rollback
     *
     * @param attachmentCode
     */
    @DeleteMapping("/api/v1/attachments/{attachmentCode}/children")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllEmptyEntity(@PathVariable String attachmentCode) {
        attachmentService.deleteAllEmptyEntity(attachmentCode);
    }
}
