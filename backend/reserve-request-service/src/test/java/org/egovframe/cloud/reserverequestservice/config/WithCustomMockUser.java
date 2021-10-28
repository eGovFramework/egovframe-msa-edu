package org.egovframe.cloud.reserverequestservice.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.egovframe.cloud.common.domain.Role;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithCustomMockUser {

    String userId() default "user";
    Role role() default Role.ADMIN;

}
