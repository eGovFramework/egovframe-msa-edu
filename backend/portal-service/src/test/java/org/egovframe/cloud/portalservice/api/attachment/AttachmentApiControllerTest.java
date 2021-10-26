package org.egovframe.cloud.portalservice.api.attachment;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Base64;
import org.egovframe.cloud.portalservice.api.attachment.dto.*;
import org.egovframe.cloud.portalservice.domain.attachment.Attachment;
import org.egovframe.cloud.portalservice.domain.attachment.AttachmentRepository;
import org.egovframe.cloud.portalservice.service.attachment.AttachmentService;
import org.egovframe.cloud.portalservice.util.RestResponsePage;
import org.egovframe.cloud.portalservice.utils.FileStorageUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles(profiles = "test")
class AttachmentApiControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    FileStorageUtils fileStorageUtils;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    AttachmentRepository attachmentRepository;

    @AfterEach
    public void teardown() {
        List<Attachment> all = attachmentRepository.findAll();

        for (int i = 0; i < all.size(); i++) {
            Attachment attachment = all.get(i);
            attachmentService.delete(attachment.getUniqueId());
        }
    }

    /**
     * file to byte[]
     *
     * @param file
     * @return
     */
    public byte[] getByteFile(File file) {
        byte[] data = new byte[(int) file.length()];
        try {
            FileInputStream inputStream = new FileInputStream(file);
            inputStream.read(data, 0, data.length);
            inputStream.close();

        } catch (FileNotFoundException e) {
            log.debug("file not found = {}", e);
        } catch (IOException e) {
            log.debug("file IO exception = {}", e);
        }
        return data;
    }

    /**
     * test.txt 파일 생성
     *
     * @return
     * @throws IOException
     */
    public static Resource getTestFile() throws IOException {
        Path testFile = Files.createTempFile("test-file", ".txt");
        System.out.println("Creating and Uploading Test File: " + testFile);
        Files.write(testFile, "Hello World !!, This is a test file.".getBytes());
        testFile.toFile().deleteOnExit();
        return new FileSystemResource(testFile.toFile());
    }

    /**
     * 하나의 멀티파트 파일 생성
     *
     * @return
     * @throws IOException
     */
    private MultipartFile getMultipartFile() throws IOException {
        Resource resource = getTestFile();
        //String name, @Nullable String originalFilename, @Nullable String contentType, @Nullable byte[] content
        return new MockMultipartFile("files", resource.getFilename(),
                Files.probeContentType(resource.getFile().toPath()), resource.getInputStream());
    }

    /**
     * 여러 건의 멀티파트 파일 생성
     *
     * @param size
     * @return
     * @throws IOException
     */
    private List<MultipartFile> getMultipartFileList(int size) throws IOException {
        List<MultipartFile> multipartFiles = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            multipartFiles.add(getMultipartFile());
        }

        return multipartFiles;
    }


    /**
     * 여러 건의 .temp 파일 생성 후 AttachmentSaveRequestDto List return
     *
     * @param size
     * @return
     * @throws IOException
     */
    private List<AttachmentTempSaveRequestDto> getTempSaveDto(int size) throws IOException {
        List<MultipartFile> multipartFiles = getMultipartFileList(size);
        List<AttachmentFileResponseDto> responseDtos = attachmentService.uploadFiles(multipartFiles);

        List<AttachmentTempSaveRequestDto> saveRequestDtoList = new ArrayList<>();
        for (int i = 0; i < responseDtos.size(); i++) {
            AttachmentFileResponseDto responseDto = responseDtos.get(i);
            saveRequestDtoList.add(AttachmentTempSaveRequestDto.builder()
                    .physicalFileName(responseDto.getPhysicalFileName())
                    .originalName(responseDto.getOriginalFileName())
                    .size(responseDto.getSize())
                    .fileType(responseDto.getFileType())
                    .entityName("Policy")
                    .entityId("testEntityId_"+i)
                    .build()
            );
        }

        return saveRequestDtoList;
    }


    @Test
    public void 이미지_BASE64인코딩후_업로드_정상() throws Exception {
        //given
        String url = "/api/v1/upload/editor";

        Path testFile = Paths.get("/Users/violet/Desktop/test/300.jpg")
                .toAbsolutePath().normalize();

        String base64data = Base64.toBase64String(getByteFile(testFile.toFile()));
        AttachmentBase64RequestDto requestDto = AttachmentBase64RequestDto.builder()
                .fieldName("upload")
                .fileType("image/jpg")
                .fileBase64(base64data)
                .originalName("300.jpg")
                .size(testFile.toFile().length())
                .build();


        ResponseEntity<AttachmentEditorResponseDto> responseEntity =
                restTemplate.postForEntity(url, requestDto, AttachmentEditorResponseDto.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getOriginalFileName()).isEqualTo("300.jpg");
    }

    @Test
    public void 첨부파일_싱글_업로드_정상() throws Exception {
        //given
        String url = "/api/v1/upload";
        ObjectMapper objectMapper = new ObjectMapper();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", getTestFile());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity(body, headers);

        //when
        ResponseEntity<AttachmentFileResponseDto> responseEntity =
                restTemplate.postForEntity(url, requestEntity, AttachmentFileResponseDto.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    public void 첨부파일_멀티_업로드_정상() throws Exception {
        //given
        String url = "/api/v1/upload/multi";
        ObjectMapper objectMapper = new ObjectMapper();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("files", getTestFile());
        body.add("files", getTestFile());
        body.add("files", getTestFile());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity(body, headers);

        //when
        ResponseEntity<List<AttachmentFileResponseDto>> responseEntity =
                restTemplate.exchange(url, HttpMethod.POST, requestEntity,
                        new ParameterizedTypeReference<List<AttachmentFileResponseDto>>() {});

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    public void 에디터이미지업로드_후_이미지태그에서_이미지파일_조회_정상() throws Exception {
        //given
        Path testFile = Paths.get("/Users/violet/Desktop/test/300.jpg")
                .toAbsolutePath().normalize();

        String base64data = Base64.toBase64String(getByteFile(testFile.toFile()));
        AttachmentBase64RequestDto requestDto = AttachmentBase64RequestDto.builder()
                .fieldName("upload")
                .fileType("image/jpg")
                .fileBase64(base64data)
                .originalName("300.jpg")
                .size(testFile.toFile().length())
                .build();
        AttachmentEditorResponseDto responseDto = attachmentService.uploadEditor(requestDto);

        String url = "/api/v1/images/editor/"+responseDto.getUrl();

        //when
        ResponseEntity<byte[]> responseEntity = restTemplate.getForEntity(url, byte[].class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void 새로운_첨부파일_temp파일_목록_저장_정상() throws Exception {
        //given
        List<AttachmentTempSaveRequestDto> saveRequestDtoList = getTempSaveDto(2);

        String url = "/api/v1/attachments/temp";

        //when
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, saveRequestDtoList, String.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void 첨부파일코드로_목록조회() throws Exception {
        //given
        List<AttachmentTempSaveRequestDto> saveRequestDtoList = getTempSaveDto(2);
        String attachmentCode = attachmentService.save(saveRequestDtoList);

        String url = "/api/v1/attachments/"+attachmentCode;

        //when
        ResponseEntity<List<AttachmentResponseDto>> responseEntity =
                restTemplate.exchange(url, HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<AttachmentResponseDto>>() {});

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().size()).isEqualTo(2);
    }

    @Test
    public void 첨부파일코드가_있는_경우_temp파일에대해_새로운파일저장_and_삭제여부_Y_정상() throws Exception {
        //given
        List<AttachmentTempSaveRequestDto> saveRequestDtoList = getTempSaveDto(3);
        String attachmentCode = attachmentService.save(saveRequestDtoList);
        List<AttachmentResponseDto> attachmentList = attachmentService.findByCode(attachmentCode);

        List<AttachmentTempSaveRequestDto> updateRequestDtoList = new ArrayList<>();

        //짝수 index 첨부파일 삭제 = Y
        for (int i = 0; i < attachmentList.size(); i++) {
            AttachmentResponseDto attachmentResponseDto = attachmentList.get(i);
            updateRequestDtoList.add(
                    AttachmentTempSaveRequestDto.builder()
                            .uniqueId(attachmentResponseDto.getId())
                            .physicalFileName(attachmentResponseDto.getPhysicalFileName())
                            .originalName(attachmentResponseDto.getOriginalFileName())
                            .size(attachmentResponseDto.getSize())
                            .entityName(attachmentResponseDto.getEntityName())
                            .entityId(attachmentResponseDto.getEntityId())
                            .isDelete(i%2==0)
                            .build()
            );
        }

        //2개 첨부파일 더하기
        updateRequestDtoList.addAll(getTempSaveDto(2));

        HttpEntity<List<AttachmentTempSaveRequestDto>> requestEntity = new HttpEntity<>(updateRequestDtoList);

        //when
        String url = "/api/v1/attachments/temp/"+attachmentCode;

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void 관리자_첨부파일_목록_조회_정상() throws Exception {
        //given
        List<AttachmentTempSaveRequestDto> saveRequestDtoList1 = getTempSaveDto(2);
        attachmentService.save(saveRequestDtoList1);

        List<AttachmentTempSaveRequestDto> saveRequestDtoList2 = getTempSaveDto(3);
        attachmentService.save(saveRequestDtoList2);

        String url = "/api/v1/attachments/admin";

        //when
        ResponseEntity<RestResponsePage<AttachmentResponseDto>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<RestResponsePage<AttachmentResponseDto>>() {
                }
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        RestResponsePage<AttachmentResponseDto> page = responseEntity.getBody();
        assertThat(page).isNotNull();
        assertThat(page.getTotalElements()).isEqualTo(5);

    }

    @Test
    public void 관리자_첨부파일_목록_검색조회_정상() throws Exception {
        //given
        List<AttachmentTempSaveRequestDto> saveRequestDtoList1 = getTempSaveDto(2);
        attachmentService.save(saveRequestDtoList1);

        List<AttachmentTempSaveRequestDto> saveRequestDtoList2 = getTempSaveDto(3);
        String attachmentCode = attachmentService.save(saveRequestDtoList2);

        String url = "/api/v1/attachments/admin?keywordType=id&keyword="+attachmentCode;

        //when
        ResponseEntity<RestResponsePage<AttachmentResponseDto>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<RestResponsePage<AttachmentResponseDto>>() {
                }
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        RestResponsePage<AttachmentResponseDto> page = responseEntity.getBody();
        assertThat(page).isNotNull();
        assertThat(page.getTotalElements()).isEqualTo(3);

    }

    @Test
    public void 관리자_삭제여부_Y_토글_정상() throws Exception {
        //given
        List<AttachmentTempSaveRequestDto> saveRequestDtoList2 = getTempSaveDto(3);
        String attachmentCode = attachmentService.save(saveRequestDtoList2);

        List<AttachmentResponseDto> results = attachmentService.findByCode(attachmentCode);

        String uniqueId = results.get(1).getId();
        String url = "/api/v1/attachments/admin/"+uniqueId+"/true";

        //when
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, null, String.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<AttachmentResponseDto> saved = attachmentService.findByCode(attachmentCode);
        AttachmentResponseDto updated = saved.stream()
                .filter(attachmentResponseDto -> attachmentResponseDto.getId().equals(uniqueId))
                .findAny().get();
        assertThat(updated.getIsDelete()).isTrue();
    }

    @Test
    public void 관리자_첨부파일_한건_완전삭제_정상() throws Exception {
        //given
        List<AttachmentTempSaveRequestDto> saveRequestDtoList2 = getTempSaveDto(2);
        String attachmentCode = attachmentService.save(saveRequestDtoList2);
        List<AttachmentResponseDto> results = attachmentService.findByCode(attachmentCode);

        String url = "/api/v1/attachments/admin/"+results.get(1).getId();
        //when
        restTemplate.delete(url);

        //then
        List<AttachmentResponseDto> deleted = attachmentService.findByCode(attachmentCode);
        assertThat(deleted.size()).isEqualTo(1);
    }

    @Test
    public void 첨부파일_업로드_저장_정상() throws Exception {
        //given
        String url = "/api/v1/attachments/upload";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("files", getTestFile());
        body.add("files", getTestFile());
        body.add("files", getTestFile());

        AttachmentUploadRequestDto uploadRequestDto =
                AttachmentUploadRequestDto.builder()
                        .entityName("test")
                        .entityId("testid")
                .build();
        body.add("info", uploadRequestDto);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity(body, headers);

        //when
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Attachment> attachmentList = attachmentRepository.findByCode(responseEntity.getBody());
        attachmentList.stream().forEach(attachment -> {
            Path filePath = Paths.get(fileStorageUtils.getFileStorageLocation()+"/" +attachment.getPhysicalFileName())
                    .toAbsolutePath().normalize();
            assertThat(Files.exists(filePath));
        });
    }

    @Test
    public void 첨부파일코드가_있는_경우_새로운파일_업로드_및_저장_and_삭제여부_Y_정상() throws Exception {
        //given
        List<MultipartFile> multipartFiles = getMultipartFileList(3);
        AttachmentUploadRequestDto uploadRequestDto =
                AttachmentUploadRequestDto.builder()
                        .entityName("test")
                        .entityId("testid")
                        .build();
        String attachmentCode = attachmentService.uploadAndSave(multipartFiles, uploadRequestDto);
        List<AttachmentResponseDto> attachmentList = attachmentService.findByCode(attachmentCode);

        List<AttachmentUpdateRequestDto> saveRequestDtoList = new ArrayList<>();

        //모두 삭제 = Y
        for (int i = 0; i < attachmentList.size(); i++) {
            AttachmentResponseDto attachmentResponseDto = attachmentList.get(i);
            saveRequestDtoList.add(
                    AttachmentUpdateRequestDto.builder()
                            .uniqueId(attachmentResponseDto.getId())
                            .isDelete(true)
                            .build()
            );
        }

        saveRequestDtoList.stream().forEach(System.out::println);
        //2개 첨부파일 더하기
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("files", getTestFile());
        body.add("files", getTestFile());
        body.add("info", uploadRequestDto);
        body.add("list", saveRequestDtoList);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity(body, headers);

        //when
        String url = "/api/v1/attachments/upload/"+attachmentCode;

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Attachment> updateAttachments = attachmentRepository.findByCode(responseEntity.getBody());
        updateAttachments.stream().forEach(System.out::println);


    }

    @Test
    public void 첨부파일업로드없이_기존파일삭제여부_Y_정상() throws Exception {
        //given
        List<MultipartFile> multipartFiles = getMultipartFileList(3);
        AttachmentUploadRequestDto uploadRequestDto =
                AttachmentUploadRequestDto.builder()
                        .entityName("test")
                        .entityId("testid")
                        .build();
        String attachmentCode = attachmentService.uploadAndSave(multipartFiles, uploadRequestDto);
        List<AttachmentResponseDto> attachmentList = attachmentService.findByCode(attachmentCode);
        List<AttachmentUpdateRequestDto> saveRequestDtoList = new ArrayList<>();
        //모두 삭제 = Y
        for (int i = 0; i < attachmentList.size(); i++) {
            AttachmentResponseDto attachmentResponseDto = attachmentList.get(i);
            saveRequestDtoList.add(
                    AttachmentUpdateRequestDto.builder()
                            .uniqueId(attachmentResponseDto.getId())
                            .isDelete(true)
                            .build()
            );
        }

        saveRequestDtoList.stream().forEach(System.out::println);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("info", uploadRequestDto);
        body.add("list", saveRequestDtoList);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity(body, headers);

        //when
        String url = "/api/v1/attachments/"+attachmentCode;

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Attachment> updateAttachments = attachmentRepository.findByCode(responseEntity.getBody());
        updateAttachments.stream().forEach(System.out::println);

    }


//
//    @Test
//    public void 첨부파일_다운로드_정상() throws Exception {
//        //given
//        List<AttachmentSaveRequestDto> saveRequestDtoList2 = getTempSaveDto(1);
//        String attachmentCode = attachmentService.save(saveRequestDtoList2);
//
//        List<AttachmentResponseDto> byCode = attachmentService.findByCode(attachmentCode);
//
//        String uniqueId = byCode.get(0).getUniqueId();
//        String url = "/api/v1/download/"+uniqueId;
//
//        //when
//        ResponseEntity<ResponseEntity> responseEntity = restTemplate.getForEntity(url, ResponseEntity.class);
//
//        //then
//        re
//
//    }

}