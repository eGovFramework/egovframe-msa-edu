package org.egovframe.cloud.portalservice.support;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.util.AntPathMatcher;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/**
 * user-service 의 {@code GET /api/v1/authorizations/check} 계약만 대신하는 테스트 전용 HTTP 서버.
 * portal-service 의 운영 코드({@code DelegatingAuthorizationService} → {@code @LoadBalanced RestTemplate})는
 * 그대로 두고 서비스 디스커버리가 이 서버로 {@code user-service} 를 해석하도록 한다.
 *
 * <p>user-service 와 같이 호출자가 보낸 {@code Authorization} 헤더의 JWT {@code authorities} 클레임을
 * 권한 목록으로 보고, 권한별 허용 규칙(HTTP 메서드 + Ant 경로)과 대조하여 {@code true}/{@code false} 를 응답한다.
 * 헤더가 없으면 user-service 와 같이 ROLE_ANONYMOUS 의 규칙을 적용하고, 해석할 수 없는 헤더는 어떤 규칙에도 해당하지 않아 {@code false} 이다.
 * 서명 검증은 portal-service 의 {@code AuthenticationFilter} 가 수행하므로 여기서는 하지 않는다.
 */
public final class UserServiceAuthorizationStub {

    private static final String P = "/portal-service/api/v1/";

    /**
     * 허용 규칙. user-service 인가 테이블의 (권한, HTTP 메서드, URL 패턴) 에 대응하며,
     * 관리자 외 권한의 규칙은 docker-compose/mysql/init/init.sql 의 role_authorization 시드(portal-service 항목)를 그대로 옮긴 것이다.
     * Authorization 헤더가 없는 호출자는 user-service 와 같이 ROLE_ANONYMOUS 로 취급한다.
     */
    private static final Map<String, List<String[]>> RULES = Map.of(
            "ROLE_ADMIN", List.<String[]>of(new String[] {"*", "/portal-service/**"}),
            "ROLE_ANONYMOUS", List.<String[]>of(
                    new String[] {"GET", P + "images/editor/**"},
                    new String[] {"GET", P + "images/?*"},
                    new String[] {"GET", P + "download/?*"},
                    new String[] {"GET", P + "attachments/?*"},
                    new String[] {"GET", P + "policies/latest/?*"},
                    new String[] {"GET", P + "contents/?*"},
                    new String[] {"GET", P + "privacies/all/use"},
                    new String[] {"GET", P + "menu-roles/?*"},
                    new String[] {"GET", P + "?*/banners/?*/?*"},
                    new String[] {"POST", P + "statistics/?*"}),
            "ROLE_USER", List.<String[]>of(
                    new String[] {"GET", P + "code-details/?*"},
                    new String[] {"POST", P + "upload"},
                    new String[] {"POST", P + "upload/editor"},
                    new String[] {"GET", P + "images/editor/**"},
                    new String[] {"GET", P + "images/?*"},
                    new String[] {"GET", P + "download/?*"},
                    new String[] {"GET", P + "attachments/?*"},
                    new String[] {"POST", P + "attachments/upload"},
                    new String[] {"PUT", P + "attachments/upload/?*"},
                    new String[] {"PUT", P + "attachments/?*"},
                    new String[] {"GET", P + "policies/?*"},
                    new String[] {"GET", P + "code-details/?*/codes"},
                    new String[] {"GET", P + "policies/latest/?*"},
                    new String[] {"GET", P + "contents/?*"},
                    new String[] {"GET", P + "privacies/all/use"},
                    new String[] {"GET", P + "menu-roles/?*"},
                    new String[] {"GET", P + "sites"},
                    new String[] {"GET", P + "code-details/?*/codes/?*"},
                    new String[] {"GET", P + "code-details/?*/parent"},
                    new String[] {"GET", P + "?*/banners/?*/?*"},
                    new String[] {"POST", P + "statistics/?*"}));

    /** 호출 기록 한 건. */
    public record Call(String httpMethod, String requestPath, String authorization, boolean allowed) {
    }

    private static final AntPathMatcher MATCHER = new AntPathMatcher();
    private static final List<Call> CALLS = new CopyOnWriteArrayList<>();
    private static HttpServer server;

    private UserServiceAuthorizationStub() {
    }

    /**
     * 서버를 (JVM 당 한 번) 기동하고 {@code user-service} 를 이 서버로 해석하는 프로퍼티를 등록한다.
     * {@code @DynamicPropertySource} 메서드에서 호출한다.
     */
    public static synchronized void register(DynamicPropertyRegistry registry) {
        if (server == null) {
            try {
                server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
            server.createContext("/api/v1/authorizations/check", UserServiceAuthorizationStub::handle);
            server.start();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stop(0)));
        }
        String uri = "http://" + server.getAddress().getHostString() + ":" + server.getAddress().getPort();
        registry.add("spring.cloud.discovery.client.simple.instances.user-service[0].uri", () -> uri);
    }

    /** 지금까지 받은 인가 확인 요청 (오래된 순). */
    public static List<Call> calls() {
        return Collections.unmodifiableList(new ArrayList<>(CALLS));
    }

    public static void clearCalls() {
        CALLS.clear();
    }

    private static void handle(HttpExchange exchange) throws IOException {
        Map<String, String> query = parseQuery(exchange.getRequestURI().getRawQuery());
        String authorization = exchange.getRequestHeaders().getFirst("Authorization");
        boolean allowed = "GET".equals(exchange.getRequestMethod())
                && isAllowed(authoritiesOf(authorization), query.get("httpMethod"), query.get("requestPath"));
        CALLS.add(new Call(query.get("httpMethod"), query.get("requestPath"), authorization, allowed));

        byte[] body = String.valueOf(allowed).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, body.length);
        exchange.getResponseBody().write(body);
        exchange.close();
    }

    private static boolean isAllowed(List<String> authorities, String httpMethod, String requestPath) {
        if (httpMethod == null || requestPath == null) {
            return false;
        }
        for (String authority : authorities) {
            for (String[] rule : RULES.getOrDefault(authority, List.of())) {
                if (("*".equals(rule[0]) || rule[0].equals(httpMethod)) && MATCHER.match(rule[1], requestPath)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static List<String> authoritiesOf(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            return List.of("ROLE_ANONYMOUS");
        }
        String token = authorization.trim();
        if (token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }
        String[] parts = token.split("\\.");
        if (parts.length < 2) {
            return List.of();
        }
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        int key = payload.indexOf("\"authorities\"");
        if (key < 0) {
            return List.of();
        }
        int start = payload.indexOf('"', payload.indexOf(':', key) + 1);
        int end = payload.indexOf('"', start + 1);
        if (start < 0 || end < 0) {
            return List.of();
        }
        return Arrays.stream(payload.substring(start + 1, end).split(",")).map(String::trim).filter(s -> !s.isEmpty())
                .toList();
    }

    private static Map<String, String> parseQuery(String rawQuery) {
        Map<String, String> result = new HashMap<>();
        if (rawQuery == null) {
            return result;
        }
        for (String pair : rawQuery.split("&")) {
            int eq = pair.indexOf('=');
            if (eq > 0) {
                result.put(URLDecoder.decode(pair.substring(0, eq), StandardCharsets.UTF_8),
                        URLDecoder.decode(pair.substring(eq + 1), StandardCharsets.UTF_8));
            }
        }
        return result;
    }
}
