package org.egovframe.cloud.apigateway.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * org.egovframe.cloud.apigateway.filter.GlobalFilterTest
 * <p>
 * 글로벌 필터 단위 테스트 클래스
 *
 * @author 표준프레임워크센터
 * @version 1.0
 * @since 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
class GlobalFilterTest {

    @Test
    @DisplayName("필터 적용 시 Pre 및 Post 로깅 설정이 정상 작동하며 체인이 계속된다")
    void should_executeFilterChain_when_applyFilter() {
        // given
        GlobalFilter filter = new GlobalFilter();
        GlobalFilter.Config config = new GlobalFilter.Config();
        config.setBaseMessage("Base Message");
        config.setPreLogger(true);
        config.setPostLogger(true);

        GatewayFilter gatewayFilter = filter.apply(config);

        ServerWebExchange exchange = mock(ServerWebExchange.class);
        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        ServerHttpResponse response = mock(ServerHttpResponse.class);

        given(exchange.getRequest()).willReturn(request);
        given(exchange.getResponse()).willReturn(response);
        given(chain.filter(exchange)).willReturn(Mono.empty());

        // when
        Mono<Void> result = gatewayFilter.filter(exchange, chain);

        // then
        assertThat(result).isNotNull();
        result.block(); // execute the Mono
        verify(chain).filter(exchange);
    }
}
