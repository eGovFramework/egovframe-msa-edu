package org.egovframe.cloud.portalservice.domain.policy;

import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.portalservice.api.policy.dto.PolicyResponseDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles(profiles = "test")
class PolicyRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    PolicyRepository policyRepository;

    /**
     * 단위 테스트가 끝날때마다 수행되는 메소드
     * 테스트 데이터간 침범을 막기 위해 사용
     */
    @AfterEach
    public void cleanUp() {
        policyRepository.deleteAll();
    }


    @Test
    public void 목록조회한다() throws Exception {
        //given
        for (int i = 0; i < 2; i++) {
            String title = "title_"+i;
            String contentStr = "contents " + i;
            String type = "TOS";
            if(i > 0){
                type = "PP";
            }

            policyRepository.save(Policy.builder()
                    .type(type)
                    .title(title)
                    .contents(contentStr)
                    .build());
        }

        //when
        Page<PolicyResponseDto> search = policyRepository.search(RequestDto.builder().build(), PageRequest.of(1, 10));

        //then
        assertThat(search.getTotalElements()).isEqualTo(2);

    }

    @Test
    public void 이용약관_조건부조회한다() {
        //given
        for (int i = 1; i <= 10; i++) {
            String title = "title_"+i;
            String contentStr = "contents " + i;
            String type = "TOS";
            if(i % 2 == 0){
                type = "PP";
            }

            policyRepository.save(Policy.builder()
                    .type(type)
                    .title(title)
                    .contents(contentStr)
                    .build());
        }

        RequestDto requestDto = RequestDto.builder()
                .keywordType("title")
                .keyword("title_2")
                .build();

        //when
        Page<PolicyResponseDto> search = policyRepository.search(requestDto, PageRequest.of(1, 10));

        //then
        assertThat(search.getTotalElements()).isEqualTo(1);

    }

    @Test
    @Transactional
    public void 회원가입시_이용약관_한건조회_정상() throws Exception {
        //given
        for (int i = 1; i <= 10; i++) {
            String title = "title_"+i;
            String contentStr = "contents " + i;
            String type = "TOS";
            if(i % 2 == 0){
                type = "PP";
            }


            policyRepository.save(Policy.builder()
                    .type(type)
                    .title(title)
                    .isUse(true)
                    .regDate(ZonedDateTime.now())
                    .contents(contentStr)
                    .build());
        }

        //when
        PolicyResponseDto responseDto = policyRepository.searchOne("TOS");

        //then
        System.out.println(responseDto.toString());

    }

    @Test
    public void ID로_한건조회() throws Exception {
        Policy save = null;
        //given
        for (int i = 1; i <= 10; i++) {
            String title = "title_"+i;
            String contentStr = "contents " + i;
            String type = "TOS";
            if(i % 2 == 0){
                type = "PP";
            }

            save = policyRepository.save(Policy.builder()
                .type(type)
                .title(title)
                .contents(contentStr)
                .build());
        }

        //when
        Optional<Policy> optionalTerms = policyRepository.findById(save.getId());

        //then
        System.out.println(optionalTerms.get());
        assertThat(optionalTerms.get().getTitle()).isEqualTo(save.getTitle());

    }

    @Test
    @Transactional
    public void 이용약관_수정한다() throws Exception {
        String updateTitle = "update title";
        String updateContents = "update Contents";
        Long id = 1L;
        //given
        for (int i = 1; i <= 10; i++) {
            String title = "title_"+i;
            String contentStr = "contents " + i;
            String type = "TOS";
            if(i % 2 == 0){
                type = "PP";
            }

            id = policyRepository.save(Policy.builder()
                    .type(type)
                    .title(title)
                    .contents(contentStr)
                    .build()).getId();
        }

        //when
        Optional<Policy> optional = policyRepository.findById(id);
        Policy policy = optional.get();

        policy.update(updateTitle, true, updateContents);
        em.persist(policy);

        //then
        Optional<Policy> updated = policyRepository.findById(id);

        assertThat(updated.get().getTitle()).isEqualTo(updateTitle);
        assertThat(updated.get().getContents()).isEqualTo(updateContents);

        System.out.println(updated.get());

    }
}