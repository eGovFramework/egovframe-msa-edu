package org.egovframe.cloud.servlet.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.persistence.EntityManager;

/**
 * org.egovframe.cloud.servlet.config.JpaConfig
 *
 * JPA 설정 클래스
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
 * </pre>
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "userAuditAware") // JPA Auditing 활성화
@EnableJpaRepositories(basePackages = "org.egovframe.cloud.*.domain")
public class JpaConfig {

    /**
     * JpaQueryFactory 빈 등록
     *
     * @param entityManager 엔티티 매니저
     * @return JPAQueryFactory 쿼리 및 DML 절 생성을 위한 팩토리 클래스
     */
    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
        return new JPAQueryFactory(entityManager);
    }

}
