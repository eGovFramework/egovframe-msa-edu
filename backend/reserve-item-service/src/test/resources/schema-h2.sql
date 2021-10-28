CREATE TABLE IF NOT EXISTS  `code` (
    `code_id` varchar(20) NOT NULL COMMENT '코드 id',
    `code_name` varchar(500) NOT NULL COMMENT '코드 명',
    `parent_code_id` varchar(20) DEFAULT NULL COMMENT '부모 코드 id',
    use_at            BOOLEAN         NULL        DEFAULT TRUE COMMENT '사용 여부',
    PRIMARY KEY (`code_id`)
) ;

-- location Table Create SQL
CREATE TABLE IF NOT EXISTS location
(
    location_id       BIGINT          NOT NULL    AUTO_INCREMENT COMMENT '지역 id',
    location_name     VARCHAR(200)    NULL        COMMENT '지역 이름',
    sort_seq          SMALLINT(3)     NULL        COMMENT '정렬 순서',
    use_at            BOOLEAN         NULL        DEFAULT TRUE COMMENT '사용 여부',
    created_by        VARCHAR(255)    NULL        COMMENT '생성자',
    create_date       DATETIME        NULL        COMMENT '생성일',
    last_modified_by  VARCHAR(255)    NULL        COMMENT '수정자',
    modified_date     DATETIME        NULL        COMMENT '수정일',
    PRIMARY KEY (location_id)
) ;



-- reserve_item Table Create SQL
CREATE TABLE IF NOT EXISTS reserve_item
(
    reserve_item_id       BIGINT            NOT NULL    AUTO_INCREMENT COMMENT '예약 물품 id',
    reserve_item_name     VARCHAR(200)      NULL        COMMENT '예약 물품 이름',
    location_id           BIGINT            NULL        COMMENT '지역 id',
    category_id           VARCHAR(20)       NULL        COMMENT '예약유형 - 공통코드 reserve-category',
    total_qty             BIGINT(18)        NULL        COMMENT '총 재고/수용인원 수',
    inventory_qty         BIGINT(18)        NULL        COMMENT '현재 남은 재고/수용인원 수',
    operation_start_date  DATETIME          NULL        COMMENT '운영 시작 일',
    operation_end_date    DATETIME          NULL        COMMENT '운영 종료 일',
    reserve_method_id     VARCHAR(20)       NULL        COMMENT '예약 방법 - 공통코드 reserve-method',
    reserve_means_id      VARCHAR(20)       NULL        COMMENT '예약 구분 (인터넷 예약 시) - 공통코드 reserve-means',
    request_start_date    DATETIME          NULL        COMMENT '예약 신청 시작 일시',
    request_end_date      DATETIME          NULL        COMMENT '예약 신청 종료 일시',
    period_at             BOOLEAN           NULL        DEFAULT FALSE COMMENT '기간 지정 가능 여부 - true: 지정 가능, false: 지정 불가',
    period_max_count      SMALLINT(3)       NULL        COMMENT '최대 예약 가능 일 수',
    external_url          VARCHAR(500)      NULL        COMMENT '외부링크',
    selection_means_id    VARCHAR(20)       NULL        COMMENT '선별 방법 - 공통코드 reserve-selection-means',
    paid_at               BOOLEAN           NULL        DEFAULT FALSE COMMENT '유/무료 - false: 무료, true: 유료',
    usage_cost            DECIMAL(18, 0)    NULL        COMMENT '이용 요금',
    use_at                BOOLEAN           NULL        DEFAULT TRUE COMMENT '사용 여부',
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
    PRIMARY KEY (reserve_item_id),
    CONSTRAINT FK_reserve_item_location_id FOREIGN KEY (location_id)
        REFERENCES location (location_id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ;



