package org.egovframe.cloud.userservice.domain.role;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * org.egovframe.cloud.userservice.domain.role.AuthorizationRepository
 * <p>
 * 인가 레파지토리 인터페이스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/08
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/08    jooho       최초 생성
 * </pre>
 */
public interface AuthorizationRepository extends JpaRepository<Authorization, Integer>, AuthorizationRepositoryCustom {

    /**
     * 정렬 순서로 인가 단건 조회
     *
     * @param sortSeq 정렬 순서
     * @return Optional<Authorization> 인가 엔티티
     */
    Optional<Authorization> findBySortSeq(Integer sortSeq);

}
