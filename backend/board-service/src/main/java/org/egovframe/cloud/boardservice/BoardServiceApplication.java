package org.egovframe.cloud.boardservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * org.egovframe.cloud.boardservice.BoardServiceApplication
 * <p>
 * 게시판 서비스 어플리케이션 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/28
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/28    jooho       최초 생성
 * </pre>
 */
@ComponentScan(basePackages={"org.egovframe.cloud.common", "org.egovframe.cloud.servlet", "org.egovframe.cloud.boardservice"}) // org.egovframe.cloud.common package 포함하기 위해
@EntityScan({"org.egovframe.cloud.servlet.domain", "org.egovframe.cloud.boardservice.domain"})
@SpringBootApplication
public class BoardServiceApplication {

    /**
     * 메인
     *
     * @param args 매개변수
     */
    public static void main(String[] args) {
        SpringApplication.run(BoardServiceApplication.class, args);
    }

}
