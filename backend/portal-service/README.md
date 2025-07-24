# Portal Service

egovframe-msa-edu 프로젝트의 포탈(메인) 서비스입니다.

- 사용자 포털 관련 기능 제공

## 실행 방법
서비스별 README 또는 루트 README를 참고하세요.

### Docker로 실행하기
```bash
docker build -t portal-service .
docker run -d -p 8082:8080 --name portal-service portal-service
```
설정에 따라 포트와 환경변수는 변경될 수 있습니다.
