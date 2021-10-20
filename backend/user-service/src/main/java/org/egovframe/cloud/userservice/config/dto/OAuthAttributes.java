package org.egovframe.cloud.userservice.config.dto;

import lombok.Builder;
import lombok.Getter;
import org.egovframe.cloud.common.domain.Role;
import org.egovframe.cloud.userservice.domain.user.User;

import java.util.HashMap;
import java.util.Map;

@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String userName;
    private String email;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes,
                           String nameAttributeKey, String userName, String email) {
        // public으로 선언된 데이터가 private 선언된 배열에 저장되지 않도록 한다.(reference가 아닌, “값”을 할당함으로써 private 멤버로서의 접근권한을 유지 시켜준다.)
        this.attributes = new HashMap<>();
        attributes.forEach((k, v) -> this.attributes.put(k, attributes.get(k)));
        this.nameAttributeKey = nameAttributeKey;
        this.userName = userName;
        this.email = email;
    }

    // OAuth2User에서 반환하는 사용자 정보는 Map이기 때문에 값 하나하나를 변환해야만 한다.
    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if ("naver".equals(registrationId)) {
            return ofNaver("id", attributes);
        }
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .userName((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return OAuthAttributes.builder()
                .userName((String) response.get("name"))
                .email((String) response.get("email"))
                .attributes(response)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    // User 엔티티 생성
    // OAuthAttributes에서 엔티티를 생성하는 시점은 처음 가입할 때이다.
    // 가입할 때의 기본 권한을 USER 변경함
    public User toEntity() {
        return User.builder()
                .userName(userName)
                .email(email)
                .role(Role.ANONYMOUS)
                .build();
    }
}
