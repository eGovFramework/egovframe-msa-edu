package org.egovframe.cloud.portalservice.domain.code;

import org.egovframe.cloud.portalservice.api.code.dto.*;
import org.egovframe.cloud.portalservice.service.code.CodeDetailService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * org.egovframe.cloud.portalservice.api.code.CodeRepositoryTest
 * <p>
 * 공통코드 CRUD 요청을 처리하는 JPA 테스트
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/07/12
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/12    jaeyeolkim  최초 생성
 * </pre>
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles(profiles = "test")
class CodeRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    CodeRepository codeRepository;

    @Autowired
    CodeDetailService codeDetailService;

    private static final String PARENT_CODE_ID = "TEST";
    private static final String PARENT_CODE_NAME = "테스트";

    @Test
    @Order(Integer.MIN_VALUE)
    public void given() throws Exception {
        codeRepository.save(
                Code.builder()
                        .codeId(PARENT_CODE_ID)
                        .codeName(PARENT_CODE_NAME)
                        .readonly(false)
                        .sortSeq(1)
                        .useAt(true)
                        .build()
        );

        for (int i = 1; i <= 30; i++) {
            codeRepository.save(
                    Code.builder()
                            .parentCodeId(PARENT_CODE_ID)
                            .codeId(PARENT_CODE_ID + "_" + i)
                            .codeName(PARENT_CODE_NAME + "_" + i)
                            .readonly(false)
                            .sortSeq(i)
                            .useAt(true)
                            .build()
            );
        }
    }

    @Test
    @Order(Integer.MAX_VALUE)
    public void cleanup() throws Exception {
        codeRepository.deleteAll();
    }

    @Test
    public void 공통코드상세_목록_조회된다() throws Exception {
        // given
        CodeDetailRequestDto requestDto = CodeDetailRequestDto.builder()
                .parentCodeId(PARENT_CODE_ID)
                .keywordType("codeId")
                .keyword(PARENT_CODE_ID)
                .build();

        // when
        Page<CodeDetailListResponseDto> results = codeRepository.findAllDetailByKeyword(requestDto, PageRequest.of(1, 5));
        for (CodeDetailListResponseDto result : results) {
            System.out.println("result = " + result.getCodeId());
        }

        // then
        assertThat(results.getTotalPages()).isEqualTo(30/5);
        assertThat(results.getTotalElements()).isEqualTo(30);
    }

    @Test
    public void 공통코드상세_단건_조회된다() throws Exception {
        // when
        Code code = codeRepository.findByCodeId(PARENT_CODE_ID + "_1").get();

        // then
        assertThat(code.getParentCodeId()).isEqualTo(PARENT_CODE_ID);
    }

    @Transactional
    @Test
    public void 공통코드상세_수정된다() throws Exception {
        // given

        // when
        Code code = codeRepository.findByCodeId(PARENT_CODE_ID + "_1").get();
        code.updateDetail(code.getParentCodeId(), "수정", code.getCodeDescription(), 100, true);
        em.persist(code);

        Code updateCode = codeRepository.findByCodeId(PARENT_CODE_ID + "_1").get();

        // then
        assertThat(updateCode.getCodeName()).isEqualTo("수정");
        assertThat(updateCode.getSortSeq()).isEqualTo(100);
        assertThat(updateCode.getUseAt()).isTrue();
    }
}