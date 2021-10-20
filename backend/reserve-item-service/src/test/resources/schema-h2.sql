CREATE TABLE IF NOT EXISTS location(
    location_id BIGINT AUTO_INCREMENT,
    location_name VARCHAR(200),
    use_at tinyint(1) default 1 null,
    sort_seq smallint(3) null,
    create_date DATE null,
    modified_date DATE null,
    created_by VARCHAR(255) null,
    last_modified_by VARCHAR(255) null,
    CONSTRAINT PERSON_PK PRIMARY KEY (location_id)
);


-- reserve_item Table Create SQL
CREATE TABLE IF NOT EXISTS reserve_item
(
    reserve_item_id       BIGINT            NOT NULL    AUTO_INCREMENT COMMENT '예약 물품 id',
    reserve_item_name     VARCHAR(200)      NULL        COMMENT '예약 물품 이름',
    location_id           BIGINT            NULL        COMMENT '지역 id',
    category_id           VARCHAR(20)       NULL        COMMENT '예약유형 - 공통코드 reserve-category',
    capacity_count        MEDIUMINT(5)      NULL        COMMENT '재고/수용인원 수',
    operation_start_date  DATETIME          NULL        COMMENT '운영 시작 일',
    operation_end_date    DATETIME          NULL        COMMENT '운영 종료 일',
    reserve_method_id     VARCHAR(20)       NULL        COMMENT '예약 방법 - 공통코드 reserve-method',
    reserve_means_id      VARCHAR(20)       NULL        COMMENT '예약 구분 (인터넷 예약 시) - 공통코드 reserve-means',
    request_start_date    DATETIME          NULL        COMMENT '예약 신청 시작 일시',
    request_end_date      DATETIME          NULL        COMMENT '예약 신청 종료 일시',
    period_at             TINYINT(1)        NULL        DEFAULT 0 COMMENT '기간 지정 가능 여부 - true: 지정 가능, false: 지정 불가',
    period_max_count      SMALLINT(3)       NULL        COMMENT '최대 예약 가능 일 수',
    external_url          VARCHAR(500)      NULL        COMMENT '외부링크',
    selection_means_id    VARCHAR(20)       NULL        COMMENT '선별 방법 - 공통코드 reserve-selection-means',
    free_at               TINYINT(1)        NULL        DEFAULT 1 COMMENT '유/무료 - true: 무료, false: 유료',
    usage_cost            DECIMAL(18, 0)    NULL        COMMENT '이용 요금',
    use_at                TINYINT(1)        NULL        DEFAULT 1 COMMENT '사용 여부',
    purpose_content       VARCHAR(4000)     NULL        COMMENT '용도',
    item_addr             VARCHAR(500)      NULL        COMMENT '주소',
    target_id             VARCHAR(20)       NULL        COMMENT '이용 대상 - 공통코드 reserve-target',
    excluded_content      VARCHAR(2000)     NULL        COMMENT '사용허가 제외대상',
    homepage_url          VARCHAR(500)      NULL        COMMENT '홈페이지 url',
    contact_no            VARCHAR(50)       NULL        COMMENT '문의처',
    manager_dept_name     VARCHAR(200)      NULL        COMMENT '담당자 소속',
    manager_name          VARCHAR(200)      NULL        COMMENT '담당자 이름',
    manager_contact_no    VARCHAR(50)       NULL        COMMENT '담당자 연락처',
    create_date           DATETIME          NULL        COMMENT '생성일',
    created_by            VARCHAR(255)      NULL        COMMENT '생성자',
    modified_date         DATETIME          NULL        COMMENT '수정일',
    last_modified_by      VARCHAR(255)      NULL        COMMENT '수정자',
    PRIMARY KEY (reserve_item_id)
    );
