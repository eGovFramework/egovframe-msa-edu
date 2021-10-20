package org.egovframe.cloud.reserverequestservice.config;

import lombok.extern.slf4j.Slf4j;
import org.egovframe.cloud.common.config.GlobalConstant;
import org.egovframe.cloud.reserverequestservice.domain.ReserveStatus;
import org.egovframe.cloud.reserverequestservice.service.ReserveService;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

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
    private ReserveService reserveService;
    @Autowired
    private ConnectionFactory connectionFactory;

    /**
     * 예약 신청(실시간) 후 재고 변경에 대한 성공 여부 consumer function
     *
     * @return
     */
    @Bean
    public Consumer<Message<RequestMessage>> inventoryUpdated() {
        return message -> {
            log.info("receive message: {}, headers: {}", message.getPayload(), message.getHeaders());
            if (message.getPayload().getIsItemUpdated()) {
                reserveService.updateStatus(message.getPayload().getReserveId(), ReserveStatus.APPROVE).subscribe();
            }else {
                reserveService.delete(message.getPayload().getReserveId()).subscribe();
            }

            RabbitTemplate rabbitTemplate = rabbitTemplate(connectionFactory);
            rabbitTemplate.convertAndSend(GlobalConstant.SUCCESS_OR_NOT_EX_NAME,
                    message.getPayload().getReserveId(), message.getPayload().getIsItemUpdated());
        };
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
