# Getting Started

### OAUTH2 설정
> 각 사이트에서 애플리케이션 API 이용을 신청하여 Client ID를 발급 받아야 한다.<br/>
> 현재 구글과 네이버를 지원한다.
- [Google](https://console.cloud.google.com)
- [Naver](https://developers.naver.com/apps/#/register?api=nvlogin)
- Kakao - @todo
  
### application-oauth.yml
- resources/application-oauth.yml 파일을 생성한다.
- 아래 내용을 넣고 각 client-id, client-secret 를 입력한다.
```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: @TODO https://console.cloud.google.com
            client-secret: @TODO
            scope: profile,email
          # 네이버는 Spring Security를 공식 지원하지 않기 때문에 CommonOAuth2Provider 에서 해주는 값들을 수동으로 입력한다.
          naver:
            client-id: @TODO https://developers.naver.com/apps/#/register?api=nvlogin
            client-secret: @TODO
            redirect_uri_template: "{baseUrl}/{action}/oauth2/code/{registrationId}"
            authorization_grant_type: authorization_code
            scope: name,email,profile_image
            client-name: Naver
        provider:
          naver:
            authorization_uri: https://nid.naver.com/oauth2.0/authorize
            token_uri: https://nid.naver.com/oauth2.0/token
            user-info-uri: https://openapi.naver.com/v1/nid/me
            # 기준이 되는 user_name 의 이름을 네이버에서는 response로 지정해야한다. (네이버 회원 조회시 반환되는 JSON 형태 때문이다)
            # response를 user_name으로 지정하고 이후 자바 코드로 response의 id를 user_name으로 지정한다. (스프링 시큐리티에서 하위 필드를 명시할 수 없기 때문)
            user_name_attribute: response
```