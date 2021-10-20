package org.egovframe.cloud.portalservice.client;

import org.egovframe.cloud.portalservice.client.dto.BoardResponseDto;
import org.egovframe.cloud.portalservice.config.CustomFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * org.egovframe.cloud.portalservice.client.BoardServiceClient
 * <p>
 * 게시판 서비스와 통신하는 feign client interface
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/08/19
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/08/19    shinmj  최초 생성
 * </pre>
 */
@FeignClient(value = "board-service", configuration = CustomFeignConfiguration.class)
public interface BoardServiceClient {
    /**
     * 게시판 한건 조회
     *
     * @param boardNo
     * @return
     */
    @GetMapping("/api/v1/boards/{boardNo}")
    BoardResponseDto findById(@PathVariable("boardNo") Integer boardNo);
}
