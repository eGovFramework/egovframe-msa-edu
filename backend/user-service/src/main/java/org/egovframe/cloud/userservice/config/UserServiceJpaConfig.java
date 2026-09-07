package org.egovframe.cloud.userservice.config;

import javax.sql.DataSource;

import org.egovframe.cloud.servlet.config.UserAuditAware;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * JPA Configuration for user-service
 * Includes repositories from both user-service and common module
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "userAuditAware")
@EnableJpaRepositories(
    basePackages = {
        "org.egovframe.cloud.servlet.domain",
        "org.egovframe.cloud.userservice.domain"
    },
    entityManagerFactoryRef = "entityManagerFactory",
    transactionManagerRef = "transactionManager"
)
public class UserServiceJpaConfig {

    @Bean
    AuditorAware<String> userAuditAware() {
        return new UserAuditAware();
    }

    @Primary
    @Bean(name = "entityManagerFactory")
    LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages(
                    "org.egovframe.cloud.servlet.domain",
                    "org.egovframe.cloud.userservice.domain"
                )
                .persistenceUnit("default")
                .build();
    }

    @Primary
    @Bean(name = "transactionManager")
    PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
        return new JPAQueryFactory(entityManager);
    }

}
