package org.egovframe.cloud.portalservice.domain.content;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * org.egovframe.cloud.portalservice.domain.content.ContentRepository
 * <p>
 * 컨텐츠 레파지토리 인터페이스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/22
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/22    jooho       최초 생성
 * </pre>
 */
public interface ContentRepository extends JpaRepository<Content, Integer>, ContentRepositoryCustom {

}
