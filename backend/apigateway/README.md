# API Gateway

이 서비스는 egovframe-msa-edu 프로젝트의 API Gateway 역할을 합니다.

- 외부 요청을 각 마이크로서비스로 라우팅합니다.
- 인증, 로깅, 공통 필터 등을 처리합니다.

## 실행 방법
서비스별 README 또는 루트 README를 참고하세요.

### Docker로 실행하기
```bash
docker build -t apigateway .
docker run -d -p 8080:8080 --name apigateway apigateway
```
설정에 따라 포트와 환경변수는 변경될 수 있습니다.
