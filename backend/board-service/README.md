# Board Service

이 서비스는 egovframe-msa-edu 프로젝트의 게시판 서비스입니다.

- 게시글 CRUD 기능을 제공합니다.
- 댓글, 파일 첨부 등 게시판 관련 기능을 담당합니다.

## 실행 방법
서비스별 README 또는 루트 README를 참고하세요.

### Docker로 실행하기
```bash
docker build -t board-service .
docker run -d -p 8081:8080 --name board-service board-service
```
설정에 따라 포트와 환경변수는 변경될 수 있습니다.
