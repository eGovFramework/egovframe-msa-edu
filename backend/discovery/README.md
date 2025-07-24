# Discovery Service

이 서비스는 마이크로서비스들의 서비스 디스커버리를 담당합니다.

- Eureka 등 서비스 레지스트리 역할을 합니다.

## 실행 방법
서비스별 README 또는 루트 README를 참고하세요.

### Docker로 실행하기
```bash
docker build -t discovery .
docker run -d -p 8761:8761 --name discovery discovery
```
설정에 따라 포트와 환경변수는 변경될 수 있습니다.
