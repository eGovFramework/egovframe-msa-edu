# Config

공통 설정 파일 및 환경설정 정보를 관리하는 디렉토리입니다.

- 공통 프로퍼티, 환경 변수 등을 포함합니다.

## 참고
각 서비스에서 이 설정을 참조합니다.

## 자격증명 환경 변수 오버라이드
DB·Eureka 접속 자격증명은 설정 파일에 평문으로 두지 않고 환경 변수로 주입할 수 있습니다.
값을 지정하지 않으면 기존 로컬 개발용 기본값(`msaportal`, `admin`)이 그대로 사용되므로 기존 구동 방식에는 영향이 없습니다.

| 환경 변수 | 적용 대상 | 기본값 |
|---|---|---|
| `DB_USERNAME` | 각 서비스 datasource/r2dbc 사용자 | `msaportal` |
| `DB_PASSWORD` | 각 서비스 datasource/r2dbc 비밀번호 | `msaportal` |
| `EUREKA_USERNAME` | Eureka 클라이언트 접속 사용자 | `admin` |
| `EUREKA_PASSWORD` | Eureka 클라이언트 접속 비밀번호 | `admin` |

운영 환경에서는 컨테이너/배포 환경 변수로 위 값을 주입하여 평문 자격증명 노출을 피하는 것을 권장합니다.
