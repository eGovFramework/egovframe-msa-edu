# eGovFrame Cloud Module Common

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-5.0.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Gradle](https://img.shields.io/badge/Gradle-8.x-blue.svg)](https://gradle.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

eGovFrame Cloud 마이크로서비스 아키텍처에서 여러 서비스에서 공통으로 사용하는 코드와 라이브러리를 제공하는 공통 모듈입니다.

## 📋 목차

- [주요 기능](#주요-기능)
- [기술 스택](#기술-스택)
- [프로젝트 구조](#프로젝트-구조)
- [시작하기](#시작하기)
- [사용 방법](#사용-방법)
- [모듈 구성](#모듈-구성)
- [빌드 및 배포](#빌드-및-배포)
- [기여하기](#기여하기)

## ✨ 주요 기능

### 공통 기능
- **예외 처리**: 비즈니스 예외 및 글로벌 예외 처리
- **DTO**: 공통 요청/응답 DTO 및 메시지 객체
- **유틸리티**: 로깅, 메시지 처리 유틸리티
- **설정**: OpenAPI 문서화, 메시지 소스, 컨트롤러 어드바이스

### Servlet 기반 (전통적인 Spring MVC)
- **도메인 엔티티**: BaseEntity, BaseTimeEntity (JPA Auditing)
- **인증/인가**: JWT 기반 인증 필터
- **API 로깅**: 요청/응답 로깅 인터셉터 및 서비스
- **예외 처리**: Servlet 환경 전용 예외 핸들러

### Reactive 기반 (Spring WebFlux)
- **도메인 엔티티**: BaseEntity, BaseTimeEntity (R2DBC)
- **인증/인가**: Reactive Security 설정
- **데이터베이스**: R2DBC 설정 및 구성
- **예외 처리**: Reactive 환경 전용 예외 핸들러

## 🛠 기술 스택

### 핵심 기술
- **Java 17**: 최신 LTS 버전
- **Spring Boot 5.0.0**: 애플리케이션 프레임워크
- **Spring Cloud**: 마이크로서비스 지원
- **Gradle**: 빌드 도구

### 데이터베이스
- **Spring Data JPA**: Servlet 환경용 ORM
- **Spring Data R2DBC**: Reactive 환경용 비동기 데이터베이스 접근
- **QueryDSL 5.0.0**: 타입 안전한 쿼리 작성

### 보안
- **Spring Security**: 인증 및 인가
- **JWT (jjwt 0.12.6)**: 토큰 기반 인증

### 문서화
- **Springdoc OpenAPI 1.8.0**: API 문서 자동 생성

### 기타
- **Lombok**: 보일러플레이트 코드 제거
- **eGovFrame RTE**: 전자정부 표준프레임워크 공통 컴포넌트

## 📁 프로젝트 구조

```
src/main/java/org/egovframe/cloud/
├── common/              # 공통 기능
│   ├── config/         # 공통 설정 (ControllerAdvice, OpenAPI 등)
│   ├── domain/         # 공통 도메인 (Role 등)
│   ├── dto/            # 공통 DTO
│   ├── exception/      # 공통 예외 처리
│   ├── service/        # 공통 서비스 추상 클래스
│   └── util/           # 유틸리티 클래스
├── servlet/            # Servlet 기반 기능
│   ├── config/         # JPA, Security, WebMvc 설정
│   ├── domain/         # JPA 엔티티 (BaseEntity, BaseTimeEntity)
│   ├── exception/      # Servlet 예외 처리
│   ├── interceptor/    # API 로깅 인터셉터
│   └── service/        # Servlet 서비스
└── reactive/           # Reactive 기반 기능
    ├── config/         # R2DBC, Security 설정
    ├── domain/         # R2DBC 엔티티 (BaseEntity, BaseTimeEntity)
    ├── exception/      # Reactive 예외 처리
    └── service/        # Reactive 서비스
```

## 🚀 시작하기

### 사전 요구사항

- Java 17 이상
- Gradle 8.x 이상 (또는 Gradle Wrapper 사용)

### 설치

1. 저장소 클론
```bash
git clone <repository-url>
cd egovframe-cloud-module-common
```

2. 로컬 Maven 저장소에 설치
```bash
./gradlew publishToMavenLocal
```

## 📖 사용 방법

### 의존성 추가

다른 프로젝트에서 이 모듈을 사용하려면 `build.gradle`에 다음을 추가하세요:

```gradle
dependencies {
    implementation 'org.egovframe.cloud:egovframe-cloud-module-common:5.0.0'
}
```

### Maven 저장소 설정

`build.gradle`에 다음 저장소를 추가해야 합니다:

```gradle
repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://maven.egovframe.go.kr/maven/") }
}
```

## 📦 모듈 구성

### Common 모듈

#### 예외 처리
- `BusinessException`: 비즈니스 로직 예외
- `EntityNotFoundException`: 엔티티 미발견 예외
- `InvalidValueException`: 유효하지 않은 값 예외
- `ErrorCode`, `ErrorResponse`: 에러 코드 및 응답 DTO

#### 유틸리티
- `MessageUtil`: 다국어 메시지 처리
- `LogUtil`: 로깅 유틸리티

#### 설정
- `ApiControllerAdvice`: 전역 컨트롤러 어드바이스
- `OpenApiDocsConfig`: OpenAPI 문서 설정
- `MessageSourceConfig`: 메시지 소스 설정

### Servlet 모듈

#### 도메인
- `BaseEntity`: 생성자/수정자 정보 포함 엔티티
- `BaseTimeEntity`: 생성일시/수정일시 포함 엔티티

#### 인증/인가
- `AuthenticationFilter`: JWT 기반 인증 필터
- `JpaConfig`: JPA 설정
- `UserAuditAware`: Auditing 사용자 정보 제공

#### 로깅
- `ApiLogInterceptor`: API 요청/응답 로깅
- `ApiLogService`: API 로그 서비스

### Reactive 모듈

#### 도메인
- `BaseEntity`: Reactive 환경용 엔티티
- `BaseTimeEntity`: Reactive 환경용 시간 추적 엔티티

#### 설정
- `R2dbcConfig`: R2DBC 설정
- `SecurityConfig`: Reactive Security 설정
- `AuthenticationConverter`: 인증 정보 변환기
- `UserAuditAware`: Reactive Auditing 사용자 정보 제공

## 🔨 빌드 및 배포

### 로컬 빌드

```bash
./gradlew build
```

### JAR 파일 생성

```bash
./gradlew jar
```

생성된 JAR 파일은 `build/libs/` 디렉토리에 위치합니다.

### 로컬 Maven 저장소에 배포

```bash
./gradlew publishToMavenLocal
```

### Javadoc 생성

```bash
./gradlew javadoc
```

생성된 문서는 `build/docs/javadoc/` 디렉토리에 위치합니다.

### QueryDSL 클래스 정리

```bash
./gradlew cleanQuerydsl
```

## 💡 사용 예제

### BaseEntity 사용

```java
@Entity
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    // createdBy, lastModifiedBy, createdDate, modifiedDate는 자동으로 포함됨
}
```

### BusinessException 사용

```java
if (user == null) {
    throw new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND, "사용자를 찾을 수 없습니다.");
}
```

### MessageUtil 사용

```java
@Autowired
private MessageUtil messageUtil;

String message = messageUtil.getMessage("error.user.notfound");
```

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 라이선스

이 프로젝트는 Apache License 2.0 하에 배포됩니다. 자세한 내용은 `LICENSE` 파일을 참조하세요.

## 📞 문의

프로젝트 관련 문의사항이 있으시면 이슈를 등록해 주세요.

## 🔗 관련 링크

- [eGovFrame 공식 사이트](https://www.egovframe.go.kr/)
- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)
- [Spring Cloud 공식 문서](https://spring.io/projects/spring-cloud)

---

**개발**: 표준프레임워크센터  
**버전**: 5.0.0  
**최종 업데이트**: 2024
