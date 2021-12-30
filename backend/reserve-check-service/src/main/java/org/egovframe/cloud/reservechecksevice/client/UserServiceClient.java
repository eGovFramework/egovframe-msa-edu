package org.egovframe.cloud.reservechecksevice.client;

import org.egovframe.cloud.reservechecksevice.client.dto.UserResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

/**
 * org.egovframe.cloud.reservechecksevice.client.UserServiceClient
 * <p>
 * 사용자 서비스와 통신하는 feign client interface
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/09/30
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/30    shinmj  최초 생성
 * </pre>
 */
@ReactiveFeignClient(value = "user-service")
public interface UserServiceClient {


    /**
     * 사용자 단 건 조회
     *
     * @param userId
     * @return
     */
    @GetMapping("/api/v1/users/{userId}")
    Mono<UserResponseDto> findByUserId(@PathVariable("userId") String userId);
}
