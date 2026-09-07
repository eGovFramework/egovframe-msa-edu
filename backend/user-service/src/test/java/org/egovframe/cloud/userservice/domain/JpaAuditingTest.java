package org.egovframe.cloud.userservice.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.egovframe.cloud.userservice.domain.role.Role;
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
    void 엔티티를_저장하면_등록일시가_채워진다() {
        Role role = Role.builder()
                .roleId("ROLE_AUDIT_TEST")
                .roleName("감사테스트")
                .roleContent("감사테스트")
                .sortSeq(1)
                .build();

        em.persist(role);
        em.flush();

        assertThat(role.getCreatedDate()).isNotNull();
    }
}
