package org.egovframe.cloud.reserveitemservice.config;


import lombok.extern.slf4j.Slf4j;
import org.egovframe.cloud.reserveitemservice.api.reserveItem.dto.ReserveSaveRequestDto;
import org.egovframe.cloud.reserveitemservice.service.reserveItem.ReserveItemService;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * org.egovframe.cloud.reserverequestservice.config.ReserveEventConfig
 *
 * event stream 설정 class
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/09/16
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/16    shinmj       최초 생성
 * </pre>
 */
@Slf4j
@Configuration
public class ReserveEventConfig {

    @Autowired
    private ReserveItemService reserveItemService;

    /**
     * 예약 신청 후 재고 변경에 대한 consumer
     *
     * @return
     */
    @Bean
    public Consumer<ReserveSaveRequestDto> reserveRequest() {
        return reserveSaveRequestDto -> {
            log.info("receive data => {}", reserveSaveRequestDto);
            reserveItemService.updateInventoryThenSendMessage(
                    reserveSaveRequestDto.getReserveItemId(),
                    reserveSaveRequestDto.getReserveQty(),
                    reserveSaveRequestDto.getReserveId())
                    .subscribe();
        };
    }

}
