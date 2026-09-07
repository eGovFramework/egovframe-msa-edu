package org.egovframe.cloud.portalservice.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.egovframe.cloud.portalservice.domain.code.Code;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

/**
 * BaseEntity 를 상속한 엔티티의 등록일시/등록자가 JPA Auditing 으로 채워지는지 확인한다.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "test")
class JpaAuditingTest {

    @Autowired
    EntityManager em;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("tester", null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @Transactional
    void 엔티티를_저장하면_등록일시와_등록자가_채워진다() {
        Code code = Code.builder()
                .codeId("AUDIT_TEST")
                .codeName("감사테스트")
                .readonly(false)
                .sortSeq(1)
                .useAt(true)
                .build();

        em.persist(code);
        em.flush();

        assertThat(code.getCreatedDate()).isNotNull();
        assertThat(code.getCreatedBy()).isEqualTo("tester");
    }
}
