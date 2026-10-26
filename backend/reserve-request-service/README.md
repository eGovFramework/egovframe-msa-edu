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

## 예약 신청 입력 검증

`POST /api/v1/requests/evaluates`와 `POST /api/v1/requests`는
`reserveItemId`, `reservePurposeContent`, `userContactNo`, `userEmail`의 누락 또는
`null`을 저장·메시지 발행 전에 거부합니다. 기존 `@NotNull` 범위만 적용하며 빈 문자열,
이메일 형식, 수량에 대한 새 규칙은 추가하지 않습니다.

`userId`는 요청에서 생략할 수 있습니다. 서비스는 요청에 전달된 값과 관계없이 인증된
사용자로 예약 소유자를 설정합니다. `ClientInput` 검증 그룹은 클라이언트 필드만 검사하며,
기본 검증 그룹의 `userId` 필수 제약은 유지합니다. 교육 예약도 같은 입력 검증 후
`saveForEvent` → `create` 경로로 소유자를 설정합니다.

**병합 순서: [#316](https://github.com/eGovFramework/egovframe-msa-edu/pull/316) 이후에만 병합합니다.**
해당 PR의 reactive advice 등록이 있어야 잘못된 입력에 HTTP 400, 코드 `E001`,
`errors[].field`를 포함한 공통 오류 응답을 반환합니다. 검증만 먼저 적용하면 요청은
조기에 실패하지만 공통 오류 응답 형식은 적용되지 않습니다.

외부 DB·RabbitMQ 없이 실제 임의 포트 WebFlux HTTP 동작과 소유자 설정을 검증합니다.
테스트의 advice 직접 import는 #316 병합 전 전제조건을 보완하며 자동 탐색 검증을 대신하지 않습니다.

```bash
bash gradlew test --tests '*ReserveRequestValidationHttpTest' --no-daemon
```
