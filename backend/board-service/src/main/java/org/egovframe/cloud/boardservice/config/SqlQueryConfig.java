package org.egovframe.cloud.boardservice.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.querydsl.sql.MySQLTemplates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.spring.SpringConnectionProvider;
import com.querydsl.sql.spring.SpringExceptionTranslator;

/**
 * org.egovframe.cloud.common.config.SqlQueryConfig
 *
 * Native SQL 설정 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/07
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/07    jooho       최초 생성
 *  2026/06/26    이백행         [2026년 컨트리뷰션] @Bean 메서드의 불필요한 public 접근제어자 제거
 * </pre>
 */
@Configuration
public class SqlQueryConfig {

    /**
     * SQLQueryFactory 빈 등록
     *
     * @param dataSource 데이터 소스
     * @return SQLQueryFactory 쿼리 및 DML 절 생성을 위한 팩토리 클래스
     */
    @Bean
    SQLQueryFactory queryFactory(DataSource dataSource) {
        return new SQLQueryFactory(querydslConfiguration(), new SpringConnectionProvider(dataSource));
    }

    /**
     * querydsl 설정
     *
     * @return Configuration 설정
     */
    public com.querydsl.sql.Configuration querydslConfiguration() {
        SQLTemplates templates = MySQLTemplates.builder().build(); // MySQL
        com.querydsl.sql.Configuration configuration = new com.querydsl.sql.Configuration(templates);
        configuration.setExceptionTranslator(new SpringExceptionTranslator());
        return configuration;
    }

}
