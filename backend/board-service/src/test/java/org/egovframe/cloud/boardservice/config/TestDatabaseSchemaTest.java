package org.egovframe.cloud.boardservice.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TestDatabaseSchemaTest {
    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void userTableCanBeQueriedWithoutQuotingAsInMysql() {
        assertThat(jdbc.queryForObject("select count(*) from user", Long.class))
                .isNotNull();
    }
}
