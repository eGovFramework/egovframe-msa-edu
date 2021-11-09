package org.egovframe.cloud.reactive.service;

import org.egovframe.cloud.common.exception.EntityNotFoundException;
import org.egovframe.cloud.common.service.AbstractService;
import reactor.core.publisher.Mono;

public class ReactiveAbstractService extends AbstractService {

    /**
     * mono error entity not found exception
     *
     * @param id
     * @param <T>
     * @return
     */
    protected  <T> Mono<T> monoResponseStatusEntityNotFoundException(Object id) {
        return Mono.error( new EntityNotFoundException("해당 데이터가 존재하지 않습니다. ID =" + String.valueOf(id)));
    }
}
