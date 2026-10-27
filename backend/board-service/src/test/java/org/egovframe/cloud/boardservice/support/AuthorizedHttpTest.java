package org.egovframe.cloud.boardservice.support;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * 실제 HTTP(RANDOM_PORT)로 컨트롤러를 호출하는 테스트의 공통 기반.
 * 서비스 간 인가 호출은 {@link UserServiceAuthorizationStub} 로 향하고, 기본 요청은 관리자 JWT 를 가진다.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(AuthorizedHttpTestConfig.class)
public abstract class AuthorizedHttpTest {
    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void seedAuthenticatedAuthor() {
        // 댓글/게시글의 created_by FK는 서비스 간 복제된 user 테이블을 참조한다.
        // 실제 JWT 주체와 같은 사용자 행이 있어야 감사 필드와 FK 모두 검증된다.
        jdbc.update("merge into user (user_no, user_id, user_name) key(user_no) values (?, ?, ?)",
                1L, "board-admin", "게시판 관리자");
    }


    @DynamicPropertySource
    static void userServiceStub(DynamicPropertyRegistry registry) {
        UserServiceAuthorizationStub.register(registry);
    }
}
