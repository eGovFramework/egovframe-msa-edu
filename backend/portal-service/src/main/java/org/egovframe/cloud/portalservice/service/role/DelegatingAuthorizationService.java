package org.egovframe.cloud.portalservice.service.role;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service("authorizationService")
public class DelegatingAuthorizationService {

    private static final String SERVICE_PATH_PREFIX = "/portal-service";

    private final RestTemplate loadBalancedRestTemplate;

    public Boolean isAuthorization(HttpServletRequest request, Authentication authentication) {
        String path = SERVICE_PATH_PREFIX + request.getRequestURI();
        String method = request.getMethod();
        HttpHeaders headers = new HttpHeaders();
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(authHeader)) {
            headers.set(HttpHeaders.AUTHORIZATION, authHeader);
        }
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        String url = UriComponentsBuilder.fromUriString("http://user-service/api/v1/authorizations/check")
                .queryParam("httpMethod", method)
                .queryParam("requestPath", path)
                .build(true)
                .toUriString();
        return Boolean.TRUE.equals(
                loadBalancedRestTemplate.exchange(url, HttpMethod.GET, entity, Boolean.class).getBody());
    }

}
