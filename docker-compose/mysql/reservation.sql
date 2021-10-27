
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

DROP TABLE IF EXISTS `reserve`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS  `reserve`
(
    `reserve_id`               VARCHAR(255)     NOT NULL    COMMENT '예약 id',
    `reserve_item_id`          BIGINT           NULL        COMMENT '예약 물품 id',
    `location_id`              BIGINT           NULL        COMMENT '예약 물품-지역 id',
    `category_id`              VARCHAR(255)     NULL        COMMENT '예약 물품-유형 id',
    `reserve_qty`              BIGINT(18)       NULL        COMMENT '예약 신청인원/수량',
    `reserve_purpose_content`  VARCHAR(4000)    NULL        COMMENT '예약신청 목적',
    `attachment_code`          VARCHAR(255)     NULL        COMMENT '첨부파일 코드',
    `reserve_start_date`       DATETIME         NULL        COMMENT '예약 신청 시작일',
    `reserve_end_date`         DATETIME         NULL        COMMENT '예약 신청 종료일',
    `reserve_status_id`        VARCHAR(20)      NULL        COMMENT '예약상태 - 공통코드(reserve-status)',
    `reason_cancel_content`    VARCHAR(4000)    NULL        COMMENT '예약 취소 사유',
    `user_id`                  VARCHAR(255)     NULL        COMMENT '예약자 id',
    `user_contact_no`          VARCHAR(50)      NULL        COMMENT '예약자 연락처',
    `user_email_addr`          VARCHAR(500)     NULL        COMMENT '예약자 이메일',
    `create_date`              DATETIME         NULL        COMMENT '생성일',
    `created_by`               VARCHAR(255)     NULL        COMMENT '생성자',
    `modified_date`            DATETIME         NULL        COMMENT '수정일',
    `last_modified_by`         VARCHAR(255)     NULL        COMMENT '수정자',
    PRIMARY KEY (reserve_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='예약 신청&확인';
/*!40101 SET character_set_client = @saved_cs_client */;

