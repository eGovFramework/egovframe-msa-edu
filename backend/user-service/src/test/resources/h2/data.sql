INSERT INTO `authorization` (authorization_name,url_pattern_value,http_method_code,sort_seq,created_by,created_date,last_modified_by,modified_date) VALUES
     ('사용자 목록 조회','/user-service/api/v1/users','GET',101,'65a00f65-8460-49af-98ec-042977e56f4b',now(),'65a00f65-8460-49af-98ec-042977e56f4b',now()),
     ('사용자 단건 조회','/user-service/api/v1/users/?*','GET',102,'65a00f65-8460-49af-98ec-042977e56f4b',now(),'65a00f65-8460-49af-98ec-042977e56f4b',now()),
     ('사용자 등록','/user-service/api/v1/users','POST',103,'65a00f65-8460-49af-98ec-042977e56f4b',now(),'65a00f65-8460-49af-98ec-042977e56f4b',now()),
     ('사용자 수정','/user-service/api/v1/users/?*','PUT',104,'65a00f65-8460-49af-98ec-042977e56f4b',now(),'65a00f65-8460-49af-98ec-042977e56f4b',now()),
     ('사용자 토큰 갱신','/user-service/api/v1/users/token/refresh','GET',105,'65a00f65-8460-49af-98ec-042977e56f4b',now(),'65a00f65-8460-49af-98ec-042977e56f4b',now()),
     ('권한 페이지 목록 조회','/user-service/api/v1/roles','GET',106,'65a00f65-8460-49af-98ec-042977e56f4b',now(),'65a00f65-8460-49af-98ec-042977e56f4b',now()),
     ('권한 전체 목록 조회','/user-service/api/v1/roles/all','GET',107,'65a00f65-8460-49af-98ec-042977e56f4b',now(),'65a00f65-8460-49af-98ec-042977e56f4b',now()),
     ('인가 페이지 목록 조회','/user-service/api/v1/authorizations','GET',108,'65a00f65-8460-49af-98ec-042977e56f4b',now(),'65a00f65-8460-49af-98ec-042977e56f4b',now()),
     ('인가 단건 조회','/user-service/api/v1/authorizations/?*','GET',109,'65a00f65-8460-49af-98ec-042977e56f4b',now(),'65a00f65-8460-49af-98ec-042977e56f4b',now()),
     ('인가 다음 정렬 순서 조회','/user-service/api/v1/authorizations/sort-seq/next','GET',110,'65a00f65-8460-49af-98ec-042977e56f4b',now(),'65a00f65-8460-49af-98ec-042977e56f4b',now()),
     ('인가 등록','/user-service/api/v1/authorizations','POST',111,'65a00f65-8460-49af-98ec-042977e56f4b',now(),'65a00f65-8460-49af-98ec-042977e56f4b',now()),
     ('인가 수정','/user-service/api/v1/authorizations/?*','PUT',112,'65a00f65-8460-49af-98ec-042977e56f4b',now(),'65a00f65-8460-49af-98ec-042977e56f4b',now()),
     ('인가 삭제','/user-service/api/v1/authorizations/?*','DELETE',113,'65a00f65-8460-49af-98ec-042977e56f4b',now(),'65a00f65-8460-49af-98ec-042977e56f4b',now()),
     ('인가 여부 확인','/user-service/api/v1/authorizations/check','GET',114,'65a00f65-8460-49af-98ec-042977e56f4b',now(),'65a00f65-8460-49af-98ec-042977e56f4b',now()),
     ('권한 인가 페이지 목록 조회','/user-service/api/v1/role-authorizations','GET',115,'65a00f65-8460-49af-98ec-042977e56f4b',now(),'65a00f65-8460-49af-98ec-042977e56f4b',now()),
     ('권한 인가 다건 등록','/user-service/api/v1/role-authorizations','POST',116,'65a00f65-8460-49af-98ec-042977e56f4b',now(),'65a00f65-8460-49af-98ec-042977e56f4b',now()),
     ('권한 인가 다건 삭제','/user-service/api/v1/role-authorizations','PUT',117,'65a00f65-8460-49af-98ec-042977e56f4b',now(),'65a00f65-8460-49af-98ec-042977e56f4b',now()),
     ('사용자 이메일 중복 확인','/user-service/api/v1/users/exists','POST',118,'65a00f65-8460-49af-98ec-042977e56f4b',now(),'65a00f65-8460-49af-98ec-042977e56f4b',now()),
     ('사용자 회원 가입','/user-service/api/v1/users/join','POST',119,'65a00f65-8460-49af-98ec-042977e56f4b',now(),'65a00f65-8460-49af-98ec-042977e56f4b',now()),
     ('사용자 비밀번호 찾기','/user-service/api/v1/users/password/find','POST',120,'65a00f65-8460-49af-98ec-042977e56f4b',now(),'65a00f65-8460-49af-98ec-042977e56f4b',now()),
     ('사용자 비밀번호 찾기 유효성 확인','/user-service/api/v1/users/password/valid/?*','GET',121,'65a00f65-8460-49af-98ec-042977e56f4b',now(),'65a00f65-8460-49af-98ec-042977e56f4b',now()),
     ('사용자 비밀번호 찾기 변경','/user-service/api/v1/users/password/change','PUT',122,'65a00f65-8460-49af-98ec-042977e56f4b',now(),'65a00f65-8460-49af-98ec-042977e56f4b',now()),
     ('사용자 비밀번호 변경','/user-service/api/v1/users/password/update','PUT',123,'65a00f65-8460-49af-98ec-042977e56f4b',now(),'65a00f65-8460-49af-98ec-042977e56f4b',now()),
     ('사용자 비밀번호 확인','/user-service/api/v1/users/password/match','POST',124,'65a00f65-8460-49af-98ec-042977e56f4b',now(),'65a00f65-8460-49af-98ec-042977e56f4b',now()),
     ('예약지역 사용여부 토글','/reserve-item-service/api/v1/locations/?*/?*','PUT',125,'87638675-11fa-49e5-9bd1-d2524bf6fa45',now(),'87638675-11fa-49e5-9bd1-d2524bf6fa45',now()),
     ('사용자 정보 수정','/user-service/api/v1/users/info/?*','PUT',126,'65a00f65-8460-49af-98ec-042977e56f4b',now(),'65a00f65-8460-49af-98ec-042977e56f4b',now()),
     ('사용자 회원탈퇴','/user-service/api/v1/users/leave','POST',127,'65a00f65-8460-49af-98ec-042977e56f4b',now(),'65a00f65-8460-49af-98ec-042977e56f4b',now()),
     ('사용자 삭제','/user-service/api/v1/users/delete/?*','DELETE',128,'65a00f65-8460-49af-98ec-042977e56f4b',now(),'65a00f65-8460-49af-98ec-042977e56f4b',now()),
     ('사용자 소셜 정보 조회','/user-service/api/v1/users/social','POST',129,'65a00f65-8460-49af-98ec-042977e56f4b',now(),'65a00f65-8460-49af-98ec-042977e56f4b',now());

INSERT INTO `role` (role_id,role_name,role_content,sort_seq,created_date) VALUES
     ('ROLE_ADMIN','시스템 관리자','시스템 관리자 권한',101,'2021-10-20 13:39:15'),
     ('ROLE_ANONYMOUS','손님','손님 권한',104,'2021-10-20 13:39:15'),
     ('ROLE_EMPLOYEE','내부 사용자','내부 사용자 권한',102,'2021-10-20 13:39:15'),
     ('ROLE_USER','일반 사용자','일반 사용자 권한',103,'2021-10-20 13:39:15');

INSERT INTO role_authorization (role_id,authorization_no,created_by,created_date)
select 'ROLE_ADMIN', authorization_no, '65a00f65-8460-49af-98ec-042977e56f4b', now() from `authorization`

union all
select 'ROLE_USER', authorization_no, '65a00f65-8460-49af-98ec-042977e56f4b', now() from `authorization`
where url_pattern_value = '/user-service/api/v1/users/?*' and http_method_code = 'GET'
union all
select 'ROLE_USER', authorization_no, '65a00f65-8460-49af-98ec-042977e56f4b', now() from `authorization`
where url_pattern_value = '/user-service/api/v1/users/token/refresh' and http_method_code = 'GET'
union all
select 'ROLE_USER', authorization_no, '65a00f65-8460-49af-98ec-042977e56f4b', now() from `authorization`
where url_pattern_value = '/user-service/api/v1/authorizations/check' and http_method_code = 'GET'
union all
select 'ROLE_USER', authorization_no, '65a00f65-8460-49af-98ec-042977e56f4b', now() from `authorization`
where url_pattern_value = '/user-service/api/v1/users/exists' and http_method_code = 'POST'
union all
select 'ROLE_USER', authorization_no, '65a00f65-8460-49af-98ec-042977e56f4b', now() from `authorization`
where url_pattern_value = '/user-service/api/v1/users/password/update' and http_method_code = 'PUT'
union all
select 'ROLE_USER', authorization_no, '65a00f65-8460-49af-98ec-042977e56f4b', now() from `authorization`
where url_pattern_value = '/user-service/api/v1/users/password/match' and http_method_code = 'POST'
union all
select 'ROLE_USER', authorization_no, '65a00f65-8460-49af-98ec-042977e56f4b', now() from `authorization`
where url_pattern_value = '/user-service/api/v1/users/info/?*' and http_method_code = 'PUT'
union all
select 'ROLE_USER', authorization_no, '65a00f65-8460-49af-98ec-042977e56f4b', now() from `authorization`
where url_pattern_value = '/user-service/api/v1/users/leave' and http_method_code = 'POST'

union all
select 'ROLE_ANONYMOUS', authorization_no, '65a00f65-8460-49af-98ec-042977e56f4b', now() from `authorization`
where url_pattern_value = '/user-service/api/v1/users/?*' and http_method_code = 'GET'
union all
select 'ROLE_ANONYMOUS', authorization_no, '65a00f65-8460-49af-98ec-042977e56f4b', now() from `authorization`
where url_pattern_value = '/user-service/api/v1/users/token/refresh' and http_method_code = 'GET'
union all
select 'ROLE_ANONYMOUS', authorization_no, '65a00f65-8460-49af-98ec-042977e56f4b', now() from `authorization`
where url_pattern_value = '/user-service/api/v1/authorizations/check' and http_method_code = 'GET'
union all
select 'ROLE_ANONYMOUS', authorization_no, '65a00f65-8460-49af-98ec-042977e56f4b', now() from `authorization`
where url_pattern_value = '/user-service/api/v1/users/exists' and http_method_code = 'POST'
union all
select 'ROLE_ANONYMOUS', authorization_no, '65a00f65-8460-49af-98ec-042977e56f4b', now() from `authorization`
where url_pattern_value = '/user-service/api/v1/users/join' and http_method_code = 'POST'
union all
select 'ROLE_ANONYMOUS', authorization_no, '65a00f65-8460-49af-98ec-042977e56f4b', now() from `authorization`
where url_pattern_value = '/user-service/api/v1/users/password/find' and http_method_code = 'POST'
union all
select 'ROLE_ANONYMOUS', authorization_no, '65a00f65-8460-49af-98ec-042977e56f4b', now() from `authorization`
where url_pattern_value = '/user-service/api/v1/users/password/valid/?*' and http_method_code = 'GET'
union all
select 'ROLE_ANONYMOUS', authorization_no, '65a00f65-8460-49af-98ec-042977e56f4b', now() from `authorization`
where url_pattern_value = '/user-service/api/v1/users/password/change' and http_method_code = 'PUT'
union all
select 'ROLE_ANONYMOUS', authorization_no, '65a00f65-8460-49af-98ec-042977e56f4b', now() from `authorization`
where url_pattern_value = '/user-service/api/v1/users/social' and http_method_code = 'POST';
