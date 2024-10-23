# Service Discovery

---

## 1. 의도 / 문제

### 의도

서비스 디스커버리는 분산 시스템 환경에서 서비스의 위치를 자동으로 찾아주는 기능입니다. 이를 사용하면 서비스 간의 상호작용을 원활하게 하여 관리의 복잡성을 줄이고, 동적인 환경에서도 안정적으로 시스템을 운영할 수 있게 도와줍니다.

### 문제

마이크로서비스 기반 애플리케이션 에서는 여러 서비스가 서로를 호출하며, 각 서비스의 인스턴스 수와 위치가 동적으로 변경됩니다. 이러한 환경에서는 기존의 고정된 서비스 호출 방식이 적절하지 않습니다. 특히 가상화되거나 컨테이너화된 환경에서는 서비스 인스턴스의 수와 위치가 끊임없이 변화하기 때문에, 이에 따른 아래와 같은 문제가 발생할 수 있습니다.

- 클라이언트(API 게이트웨이나 다른 서비스)가 필요한 서비스의 현재 위치를 알 수 없습니다.
- 서비스 인스턴스의 변경(추가, 제거, 위치 변경 등)을 실시간으로 추적할 수 없습니다.

![egov_example](./images/MSA%20내에서%20발생하는%20문제점%20그림.png)

이러한 문제점들을 해결하기 위해 각 서비스의 클라이언트 코드에 직접 구현하는 것은 비효율적이며, 복잡한 로직과 중복 코드로 인해 오류가 발생하기 쉽고, 서비스 수가 증가할수록 관리가 더욱 어려워집니다.

따라서, 클라이언트가 동적으로 변화하는 서비스 인스턴스에 대해 효율적으로 요청할 수 있도록 지원하는 매커니즘이 필요합니다. 또한, 이 메커니즘은 변화하는 서비스 구조에 효율적으로 대응하면서도, 향후 서비스가 추가될 때 쉽게 확장할 수 있어야 합니다.



## 2. 해결법

### 2.1. Service registry

- 정의
  - 서비스 레지스트리는 여러 서비스 인스턴스의 메타데이터를 저장하고 관리하는 중앙 집중식 저장소입니다.
  - 각 서비스 인스턴스는 레지스트리에 자신의 정보를 등록하고, 필요할 때 해제할 수 있습니다.

- 기능
  - **등록과 해제**: 서비스 인스턴스는 시작할 때 자신을 레지스트리에 등록하고, 종료할 때 해제합니다. 이 과정을 통해 서비스의 가용성을 관리할 수 있습니다.
  - **상태 모니터링**: 서비스 레지스트리는 서비스 인스턴스의 상태를 주기적으로 확인하여, 비정상적인 인스턴스를 감지하고 자동으로 목록에서 제거할 수 있습니다.
  - **서비스 정보 제공**: 클라이언트는 서비스 레지스트리를 통해 서비스의 현재 위치(호스트, 포트 등)와 상태(정상, 비정상 등)를 조회할 수 있습니다.
- 예시: Netflix Eureka, Consul, Zookeeper 등이 서비스 레지스트리의 예입니다.


### 2.2. Service Client

- 정의
  - 서비스 클라이언트는 특정 서비스에 접근하기 위해 서비스 레지스트리를 조회하고 요청을 보내는 애플리케이션입니다. 클라이언트는 필요한 서비스의 정보를 동적으로 받아와서 사용할 수 있습니다.

- 기능
  - 서비스 조회: 서비스 레지스트리에 접근하여 필요한 서비스의 위치를 확인하고, 그에 따라 요청을 보냅니다.
  - 로드 밸런싱: 클라이언트는 여러 서비스 인스턴스 중 하나를 선택하여 요청을 보낼 수 있으며, 이 과정에서 로드 밸런싱을 수행하여 부하를 분산시킬 수 있습니다.
  - 상태 확인: 클라이언트는 서비스 레지스트리에서 서비스의 상태를 확인하여, 비정상적인 인스턴스에 요청을 보내지 않도록 합니다.
  > 위 기능들은 서비스 디스커버리 패턴 중 클라이언트-사이드 디스커버리 패턴을 적용했을 때 주로 사용됩니다.
  > 서버-사이드 디스커버리 패턴을 적용한 경우에는 API Gateway와 같은 중앙 집중식 컴포넌트가 이 역할을 대신해 줍니다.

- 예시: API 게이트웨이, 마이크로서비스 애플리케이션 등에서 서비스 클라이언트를 구현하여 사용할 수 있습니다.

### 2.3. Service Discovery

- 정의
  - 서비스 디스커버리는 클라이언트가 서비스 레지스트리를 통해 서비스의 위치를 찾고, 서비스 인스턴스의 상태를 실시간으로 확인할 수 있도록 하는 메커니즘입니다.
- 기능
  - 자동 검색: 클라이언트는 직접 서비스 인스턴스의 위치를 알 필요 없이, 서비스 레지스트리를 통해 필요한 서비스를 자동으로 검색할 수 있습니다.
  - 실시간 업데이트: 서비스 인스턴스가 추가되거나 제거될 때, 서비스 레지스트리는 이를 자동으로 반영하여 클라이언트가 최신 정보를 기반으로 요청을 보낼 수 있게 합니다.
  - 상호작용의 간소화: 서비스 간의 호출 관계를 명시적으로 관리하지 않아도 되므로, 마이크로서비스 아키텍처에서의 서비스 간 상호작용이 간편해집니다.
- 예시
  - Spring Cloud Netflix Eureka의 서비스 디스커버리 기능이 대표적인 예입니다.
  - 클라이언트가 Eureka 서버를 통해 다른 서비스 인스턴스를 쉽게 찾을 수 있도록 도와줍니다.

중요한 점은 모든 서비스의 위치를 클라이언트가 직접 관리할 필요가 없다는 것입니다. 클라이언트는 서비스 레지스트리를 통해 필요한 서비스만 조회하고, 이를 통해 원하는 기능을 수행하는 서비스에 접근할 수 있습니다. 서비스 인스턴스가 변경될 때마다 클라이언트가 이를 인지하고 업데이트할 필요는 없으며, 서비스 레지스트리가 이를 자동으로 관리합니다.

### 2.4. Service Discovery Pattern

#### 📍 `Client-side Service Discovery`

- 서비스에 요청을 할 때, 클라이언트는 모든 서비스 인스턴스의 위치를 알고 있는 서비스 레지스트리를 조회하여 서비스 인스턴스의 위치를 확보합니다.
- 아래 그림은 이 패턴의 구조를 보여줍니다.

![egov_example](./images/클라이언트%20사이드.png)

#### 📍 `Server-side Service Discovery`

- 서비스에 요청할 때, 클라이언트는 정해진 위치에서 실행되는 라우터(또는 로드 밸런서)를 통해 요청을 합니다.
- 라우터는 서비스 레지스트리를 조회하여 사용 가능한 서비스 인스턴스에 요청을 전달합니다.
(이 서비스 레지스트리는 라우터에 내장되어 있을 수도 있습니다.)
- 아래 그림은 이 패턴의 구조를 보여줍니다.

![egov_example](./images/서버%20사이드.png)


---

## 3. 장단점

### 3.1. Client-side vs Server-side Service Discovery

#### 📍 `Client-side Service Discovery`
**Client-side 서비스 디스커버리**에서는 클라이언트가 서비스 레지스트리에서 직접 서비스 위치를 조회하고, 해당 서비스로 직접 요청을 보냅니다.

- **장점**:
  - 단순성: 클라이언트가 직접 서비스를 호출합니다.
  - 유연성: 클라이언트가 로드 밸런싱 로직을 직접 구현할 수 있습니다.
  - 지연시간 감소: 클라이언트와 서비스 간에 중간 단계가 없습니다.

- **단점**:
  - 클라이언트 복잡성 증가: 각 클라이언트가 서비스 디스커버리 로직을 구현해야 합니다.
  - 강한 결합: 클라이언트가 디스커버리 과정과 강하게 결합됩니다.
  - 확장성 제한: 서비스가 많아지면 각 클라이언트의 디스커버리 로직 관리가 어려워질 수 있습니다.

  <br>

#### 📍 `Server-side Service Discovery`
**Server-side 서비스 디스커버리**에서는 클라이언트가 로드 밸런서나 프록시 서버에 요청을 보내면, 이 서버가 서비스 레지스트리에 질의하고 적절한 서비스로 요청을 전달합니다.

- **장점**:
  - 클라이언트 단순화: 클라이언트가 디스커버리 로직을 처리하지 않아도 됩니다.
  - 중앙 집중 제어: 로드 밸런싱 및 라우팅 결정이 중앙에서 이루어집니다.
  - 확장성 용이: 클라이언트 전반에 걸쳐 디스커버리 로직을 관리하는 복잡성을 줄여줍니다.

- **단점**:
  - 추가적인 중간 단계: 요청이 중간 서버를 거치면서 지연 시간이 증가할 수 있습니다.
  - 단일 실패 지점: 로드 밸런서나 프록시 서버가 실패하면 서비스 요청이 전달되지 않을 수 있습니다.
  - 인프라 구성: 로드 밸런서 같은 추가적인 인프라 구성이 필요합니다. 로드 밸런서 설정 및 관리에 추가적인 작업이 필요합니다.


<br>

#### 📍 `비교 정리`

| **특징** | **Client-side Discovery** | **Server-side Discovery** |
| --- | --- | --- |
| **로드 밸런싱 위치** | 클라이언트가 직접 수행 | 서버 측 로드 밸런서가 수행 |
| **클라이언트 복잡성** | 클라이언트가 서비스 위치 및 로드 밸런싱 알고리즘을 관리 | 클라이언트는 단순화, 로드 밸런서는 서버가 처리 |
| **서비스 위치 정보 관리** | 클라이언트가 직접 서비스 레지스트리에서 조회 | 서버 측 로드 밸런서가 서비스 레지스트리에서 조회 |
| **추가 인프라 요구 사항** | 추가 인프라 필요 없음 | 서버 측 로드 밸런서 또는 프록시 서버 필요 |
| **유연성** | 클라이언트가 다양한 로드 밸런싱 전략을 적용 가능 | 서버 측에서 다양한 트래픽 관리 기능을 제공 |

<br>

---

### 3.2. 3rd Party vs Self-hosted Service Registry

#### 📍 `3rd Party Service Registry`
**3rd party 서비스 레지스트리**는 외부에서 제공되는 관리형 서비스 레지스트리를 사용합니다. 3rd party란 이름 그대로 다른 회사나 서비스 제공자가 제공하는 서비스를 의미합니다. 사용자는 레지스트리를 직접 운영하지 않고, 이들이 제공하는 API를 통해 서비스 정보를 등록하고 조회할 수 있습니다. 이 방식의 장점은 인프라 관리에 대한 부담을 줄이고, 빠르게 디스커버리 기능을 구현할 수 있다는 점입니다.

- **장점**:
  - 유지보수 부담 감소: 서비스 제공자가 인프라와 확장성을 관리합니다.
  - 빠른 설정: 인프라를 신경 쓸 필요 없이 빠르게 구현 가능합니다.
  - 높은 가용성: 보통 내장된 중복성과 높은 가용성을 제공합니다.

- **단점**:
  - 벤더 종속성: 특정 서비스 제공자에 의존하게 됩니다.
  - 커스터마이징 제한: 제공자가 정한 기능과 설정 내에서만 사용할 수 있습니다.
  - 잠재적 지연시간: 인터넷을 통해 서비스 레지스트리에 접근할 경우 지연이 발생할 수 있습니다.

- **예시**:
  - Consul Cloud (HashiCorp)
  - AWS Cloud Map
  - Azure Service Fabric
  - Google Cloud Service Directory

  <br>

#### 📍 `Self-hosted Service Registry`
**Self-hosted 서비스 레지스트리**는 조직이 직접 서비스 레지스트리를 설치하고 운영하는 방식입니다. 필요한 소프트웨어를 직접 관리하고, 클러스터 내에서 동작하는 모든 서비스의 정보를 직접 등록하고 관리하게 됩니다. 이는 특정 클라우드 제공자에 종속되지 않으며, 서비스 레지스트리를 보다 세밀하게 제어할 수 있다는 장점이 있습니다. 그러나 직접 설치하고 관리하는 부담이 따릅니다.

- **장점**:
  - 완전한 제어: 레지스트리의 구성과 운영을 완벽하게 제어할 수 있습니다.
  - 커스터마이징 가능: 조직의 필요에 맞게 자유롭게 설정할 수 있습니다.
  - 벤더 종속성 없음: 외부 제공자에 의존하지 않습니다.

- **단점**:
  - 유지보수 부담: 레지스트리의 유지보수, 확장 및 업데이트를 직접 담당해야 합니다.
  - 인프라 비용: 호스팅 및 유지 관리를 위한 하드웨어나 클라우드 자원이 필요합니다.
  - 복잡성 증가: 자체 운영 레지스트리를 관리하고 확장하는 데 복잡성이 추가됩니다.

- **예시**:
  - Consul (HashiCorp)
  - etcd (CoreOS)
  - Zookeeper (Apache)
  - Eureka (Netflix)

<br>

#### 📍 `정리`

| **레지스트리** | **주요 사용 사례** | **특징** | **장점** | **단점** |
| --- | --- | --- | --- | --- |
| **Eureka** | Spring Cloud, 클라이언트 로드 밸런싱 | Client-Side Discovery, Spring Cloud와 통합 | Spring Cloud와의 강력한 통합 | 복잡한 설정 및 확장성 이슈 |
| **Consul** | 헬스 체크, 설정 관리, 다중 데이터센터 | Client-Side Discovery, Key-Value 저장소 지원 | 헬스 체크 및 설정 관리 기능, 다중 데이터센터 지원 | 설정 복잡성, 에이전트 배포 필요 |
| **Zookeeper** | 분산 시스템 동기화 및 구성 관리 | 일관성 중심, 분산 락, 리더 선출 등 지원 | 강력한 일관성 보장, 다양한 분산 시스템 기능 제공 | 복잡한 설정 및 운영, 성능 이슈 |
| **Etcd** | Kubernetes 설정 관리, Key-Value 저장소 | Raft 알고리즘 기반 일관성 보장, 분산 환경에서 내구성 제공 | Kubernetes와의 강력한 통합 | 제한된 기능, 성능 및 확장성 이슈 |
| **AWS Cloud Map** | AWS 기반 서비스 디스커버리 | AWS 서비스와 깊은 통합, DNS 기반 검색 | AWS 생태계와 완벽한 통합 | AWS 종속성, 멀티 클라우드 지원 부족 |


<br>

---
---


## 4. 구현 예시

### 4.1. egovframe-msa-edu

- 디렉토리 구조

![egov_example](./images/egov_service_discovery.png)

- 아키텍처

![egov_example](./images/egov%20아키텍처.png)

이 그림은 egov 프로젝트 내 service-discovery 관련 아키텍처를 간략하게 나타낸 그림입니다. 각 클라이언트 서비스들이 직접 레지스트리를 조회하여 서비스 인스턴스를 찾는 형태가 아닌 API Gateway를 통하고 있습니다. 즉 클라이언트는 API Gateway를 통해 요청을 하고 API Gateway에서는 Eureka Server의 Service Registry를 조회하여 클라이언트가 요청한 서비스 인스턴스의 정보를 찾습니다.


#### Java 파일 (`DiscoveryApplication.java`)


```java
package org.egovframe.cloud.discovery;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class DiscoveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscoveryApplication.class, args);
    }
}
```

이 파일은 Spring Boot 애플리케이션의 진입점이며, Eureka 서버를 활성화하는 애플리케이션입니다.

- **`@EnableEurekaServer`**: 이 애노테이션은 이 애플리케이션이 **Eureka 서버**로 동작하게 합니다. Eureka 서버는 마이크로서비스 환경에서 서비스 레지스트리 역할을 합니다. 여러 서비스들이 Eureka 서버에 자신의 상태를 등록하고, 이를 통해 서비스 간의 통신 및 로드 밸런싱을 할 수 있습니다.

<br>

#### YAML 파일 (`application.yml`)


```yaml
server:
  port: 8761

spring:
  application:
    name: discovery
  security:
    user:
      name: admin
      password: admin

# eureka가 포함되면 eureka server도 등록되므로 해제한다.
eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://${spring.security.user.name}:${spring.security.user.password}@${eureka.instance.hostname:localhost}:8761/eureka
  server:
    peer-node-read-timeout-ms: 10000
```

이 파일은 Eureka 서버와 Spring Boot 애플리케이션에 대한 설정을 정의합니다.

1. **spring.application.name: discovery**
    - 애플리케이션 이름을 discovery로 설정합니다. 이 이름은 다른 마이크로서비스들이 이 서비스를 인식하는 데 사용됩니다.
2. **spring.security.user.name 및 spring.security.user.password**
    - 기본 인증 정보를 설정합니다. admin/admin이 사용자 이름과 비밀번호로 설정되어 있습니다. 이 정보는 Eureka 대시보드에 접근할 때 사용됩니다.
3. **eureka.client.register-with-eureka: false 및 eureka.client.fetch-registry: false**
    - Eureka 서버 자체가 다른 Eureka 서버에 **자신을 등록하지 않도록** 설정합니다. 이 경우 Eureka 서버가 스스로를 레지스트리에 등록하지 않으며, 다른 서버로부터 레지스트리를 가져오지 않습니다.
4. **eureka.client.service-url.defaultZone**
    - Eureka 클라이언트가 Eureka 서버와 통신할 때 사용할 기본 URL을 정의합니다.
    `${spring.security.user.name}:${spring.security.user.password}`는 인증을 위한 사용자 이름과 비밀번호입니다.
    `${eureka.instance.hostname:localhost}`는 호스트 이름을 정의하며, 기본값은 localhost입니다.
    전체적으로 http://admin:admin@localhost:8761/eureka 형태의 URL이 됩니다.
5. **eureka.server.peer-node-read-timeout-ms**
    - Eureka 서버 간 통신 시 다른 노드로부터 응답을 기다리는 시간을 10초로 설정합니다.이 설정은 다중 Eureka 서버 클러스터 구성에서 유용합니다.

<br>

### 4.2. Spring io - service registration and discovery

- repository: https://github.com/spring-guides/gs-service-registration-and-discovery
- 가이드 문서: https://spring.io/guides/gs/service-registration-and-discovery

#### eureka-server 파일

`EurekaServerApplication.java`
```java
package com.example.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaServerApplication.class, args);
	}

}
```
`application.yml`
```yaml
spring:
  application:
    name: eureka-server
server:
  port: 8761
eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
logging:
  level:
    com.netflix.eureka: OFF
    com.netflix.discovery: OFF
```

<br>

#### service 파일

`ServiceaApplication.java`
```java
package com.example.servicea;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServiceaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceaApplication.class, args);
	}

}
```
`application.yml`

```yaml
spring:
  application:
    name: servicea
server:
  port: 8081
```

<br>


## 5. 참고 자료
- https://microservices.io/tags/service%20discovery
- https://spring.io/guides/gs/service-registration-and-discovery
- https://enjoy-dev.tistory.com/2
- https://ksh-coding.tistory.com/137
