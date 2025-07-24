# Reserve Request Service

예약 요청 관련 기능을 담당하는 서비스입니다.

## 실행 방법
서비스별 README 또는 루트 README를 참고하세요.

### Docker로 실행하기
```bash
docker build -t reserve-request-service .
docker run -d -p 8085:8080 --name reserve-request-service reserve-request-service
```
설정에 따라 포트와 환경변수는 변경될 수 있습니다.
