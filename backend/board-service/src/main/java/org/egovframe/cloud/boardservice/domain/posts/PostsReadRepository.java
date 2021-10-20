package org.egovframe.cloud.boardservice.domain.posts;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * org.egovframe.cloud.boardservice.domain.posts.PostsReadRepository
 * <p>
 * 게시물 조회 레파지토리 인터페이스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/08/02
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/08/02    jooho       최초 생성
 * </pre>
 */
public interface PostsReadRepository extends JpaRepository<PostsRead, PostsReadId>, PostsReadRepositoryCustom {

}
