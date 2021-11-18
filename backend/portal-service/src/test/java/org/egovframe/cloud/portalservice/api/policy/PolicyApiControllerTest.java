package org.egovframe.cloud.portalservice.api.policy;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.egovframe.cloud.portalservice.api.policy.dto.PolicyResponseDto;
import org.egovframe.cloud.portalservice.api.policy.dto.PolicySaveRequestDto;
import org.egovframe.cloud.portalservice.api.policy.dto.PolicyUpdateRequestDto;
import org.egovframe.cloud.portalservice.domain.policy.Policy;
import org.egovframe.cloud.portalservice.domain.policy.PolicyRepository;
import org.egovframe.cloud.portalservice.util.RestResponsePage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles(profiles = "test")
class PolicyApiControllerTest {

    @LocalServerPort
    private int port;

    private static final String API_URL = "/api/v1/policies";

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    private PolicyRepository policyRepository;

    @BeforeEach
    public void setUp() {
        //given
        for (int i = 1; i <= 10; i++) {
            String title = "title_"+i;
            String contents = "contents " + i;
            String type = "TOS";
            if(i % 2 == 0){
                type = "PP";
            }

            policyRepository.save(Policy.builder()
                    .type(type)
                    .title(title)
                    .isUse(true)
                    .regDate(ZonedDateTime.now())
                    .contents(contents)
                    .build());
        }
    }

    @AfterEach
    public void teardown() {
        policyRepository.deleteAll();
    }

    @Test
    public void 이용약관_등록_정상() throws Exception {
        //given
        String type = "PP";
        String title= "test title";
        String contents = "test contents";

        PolicySaveRequestDto requestDto = PolicySaveRequestDto.builder()
                .type(type)
                .title(title)
                .isUse(true)
                .regDate(ZonedDateTime.now())
                .contents(contents)
                .build();


        //when
        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(API_URL, requestDto, Long.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Optional<Policy> policy = policyRepository.findById(responseEntity.getBody().longValue());

        System.out.println(policy.get().toString());

    }

    @Test
    public void 목록조회한다() throws Exception {

        String url = API_URL+"?size=3%page=0";
        //when
        ResponseEntity<RestResponsePage<PolicyResponseDto>> responseEntity =
                restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<RestResponsePage<PolicyResponseDto>>() {});

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        RestResponsePage<PolicyResponseDto> body = responseEntity.getBody();
        body.stream().forEach(System.out::println);

    }

    @Test
    public void 회원가입시_가장최근_이용약관_조회_된다() throws Exception {

        String url = "http://localhost:"+port+API_URL + "/latest/TOS";
        //when
        ResponseEntity<PolicyResponseDto> responseEntity = restTemplate.getForEntity(url, PolicyResponseDto.class);

        //then
        System.out.println(responseEntity.getBody().toString());
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getTitle()).isEqualTo("title_9");


    }

    @Test
    @Order(1)
    public void ID로_한건조회_정상() throws Exception {

        String url = API_URL +"/9";

        //when
        ResponseEntity<PolicyResponseDto> responseEntity = restTemplate.getForEntity(url, PolicyResponseDto.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        System.out.println(responseEntity.getBody().toString());
    }

    @Test
    public void 이용약관_수정_된다() throws Exception {
        //given
        Long id = policyRepository.save(Policy.builder()
                .type("TOS")
                .title("title")
                .contents("contents!!!!")
                .build()
        ).getId();
        String url = API_URL +"/"+id;

        PolicyUpdateRequestDto requestDto = PolicyUpdateRequestDto.builder()
                .title("update title")
                .contents("update Details")
                .build();

        //when
        HttpEntity<PolicyUpdateRequestDto> requestEntity = new HttpEntity<>(requestDto);

        //when
        ResponseEntity<Long> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Long.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(id);
        Policy result = policyRepository.findById(id).get();
        System.out.println(result);

    }

    @Test
    public void 이용약관_삭제_한다() {
        //given
        Long id = policyRepository.save(Policy.builder()
                .type("TOS")
                .title("title")
                .contents("contents!!!!")
                .build()
        ).getId();
        String url = API_URL +"/"+id;

        //when
        restTemplate.delete(url);

        //then
        Optional<Policy> terms = policyRepository.findById(id);
        assertThat(terms.isPresent()).isFalse();
    }

    @Test
    public void 사용여부_수정_한다() throws Exception {
        //given
        Long id = policyRepository.save(Policy.builder()
                .type("TOS")
                .title("title")
                .isUse(true)
                .contents("contents!!!")
                .build()
        ).getId();
        String url = API_URL +"/"+id+"/"+false;

        //when
        ResponseEntity<Long> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, null, Long.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(id);
        Policy result = policyRepository.findById(id).get();
        System.out.println(result);
        assertThat(result.getIsUse()).isFalse();
    }
}