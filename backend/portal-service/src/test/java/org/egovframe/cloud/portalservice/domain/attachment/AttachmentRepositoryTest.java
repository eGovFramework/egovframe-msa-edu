package org.egovframe.cloud.portalservice.domain.attachment;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles(profiles = "test")
class AttachmentRepositoryTest {
    @Autowired
    EntityManager em;

    @Autowired
    AttachmentRepository attachmentRepository;

    /**
     * 단위 테스트가 끝날때마다 수행되는 메소드
     * 테스트 데이터간 침범을 막기 위해 사용
     */
    @AfterEach
    public void cleanUp() {
        attachmentRepository.deleteAll();
    }

    @Test
    public void 첨부파일_등록() throws Exception {
        //given
        AttachmentId attachmentId = AttachmentId.builder()
                .code("testAttachmentCode")
                .seq(1L).build();

        Attachment attachment = Attachment.builder()
                .attachmentId(attachmentId)
                .uniqueId(UUID.randomUUID().toString())
                .originalFileName("test.png")
                .physicalFileName(UUID.randomUUID().toString())
                .size(1232L)
                .build();

        //when
        Attachment save = attachmentRepository.save(attachment);

        //then
        System.out.println(save);
        assertThat(save.getAttachmentId().getSeq()).isEqualTo(1);

    }

    @Test
    public void 여러건_등록() throws Exception {
        //given
        String code = "testAttachmentCode";
        AttachmentId attachmentId1 = AttachmentId.builder()
                .code(code)
                .seq(1L).build();
        Attachment attachment1 = Attachment.builder()
                .attachmentId(attachmentId1)
                .uniqueId(UUID.randomUUID().toString())
                .originalFileName("test1.png")
                .physicalFileName(UUID.randomUUID().toString())
                .size(1232L)
                .build();

        AttachmentId attachmentId2 = AttachmentId.builder()
                .code(code)
                .seq(2L)
                .build();
        Attachment attachment2 = Attachment.builder()
                .attachmentId(attachmentId2)
                .uniqueId(UUID.randomUUID().toString())
                .originalFileName("test2.png")
                .physicalFileName(UUID.randomUUID().toString())
                .size(1232L)
                .build();

        //when
        attachmentRepository.save(attachment1);
        attachmentRepository.save(attachment2);

        //then
        List<Attachment> all = attachmentRepository.findAll();
        all.stream().forEach(System.out::println);

    }

    @Test
    public void code로다건조회() throws Exception {
        //given
        String code = "testAttachmentCode";
        for (Long i = 1L; i <= 5L; i++) {
            AttachmentId attachmentId = AttachmentId.builder()
                    .code(code)
                    .seq(i)
                    .build();
            attachmentRepository.save(
                    Attachment.builder()
                    .attachmentId(attachmentId)
                    .uniqueId(UUID.randomUUID().toString())
                    .physicalFileName(UUID.randomUUID().toString())
                    .originalFileName("test_"+i+".txt")
                    .size(123L)
                    .build()
            );
        }
        //when
        List<Attachment> attachments = attachmentRepository.findByCode(code);

        //then
        assertThat(attachments.size()).isEqualTo(5);
        attachments.stream().forEach(System.out::println);
    }

    @Test
    public void 대체키로한건조회() throws Exception {
        //given
        String code = "testAttachmentCode";
        String id = "";
        for (Long i = 1L; i <= 5L; i++) {
            AttachmentId attachmentId = AttachmentId.builder()
                    .code(code)
                    .seq(i)
                    .build();
            id = UUID.randomUUID().toString();

            attachmentRepository.save(
                    Attachment.builder()
                            .attachmentId(attachmentId)
                            .uniqueId(id)
                            .physicalFileName(UUID.randomUUID().toString())
                            .originalFileName("test_"+i+".txt")
                            .size(123L)
                            .build()
            );
        }

        //when
        Optional<Attachment> byId = attachmentRepository.findAllByUniqueId(id);

        //then
        assertThat(byId.isPresent()).isTrue();
        assertThat(byId.get().getAttachmentId().getCode()).isEqualTo(code);
        System.out.println(byId.get());
    }


}