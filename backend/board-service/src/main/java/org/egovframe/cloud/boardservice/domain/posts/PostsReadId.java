package org.egovframe.cloud.boardservice.domain.posts;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * org.egovframe.cloud.boardservice.domain.posts.PostsReadId
 * <p>
 * 게시판 조회 엔티티 복합키 클래스
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
@Getter
@NoArgsConstructor
@Embeddable
public class PostsReadId implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -6710005976442877773L;

    /**
     * 게시판 번호
     */
    @Column(columnDefinition = "int(9)")
    private Integer boardNo;

    /**
     * 게시물 번호
     */
    @Column(columnDefinition = "int(9)")
    private Integer postsNo;

    /**
     * 조회 번호
     */
    @Column(columnDefinition = "int(9)")
    private Integer readNo;

    /**
     * 빌드 패턴 클래스 생성자
     *
     * @param boardNo 게시판 번호
     * @param postsNo 게시물 번호
     * @param readNo  조회 번호
     */
    @Builder
    public PostsReadId(Integer boardNo, Integer postsNo, Integer readNo) {
        this.boardNo = boardNo;
        this.postsNo = postsNo;
        this.readNo = readNo;
    }

    /**
     * Returns a hash code value for the object. This method is supported for the benefit of hash tables such as those provided by java.util.HashMap.
     *
     * @return int a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(boardNo, postsNo, readNo);
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param object the reference object with which to compare.
     * @return {@code true} if this object is the same as the obj
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof PostsReadId)) return false;
        PostsReadId that = (PostsReadId) object;
        return Objects.equals(boardNo, that.getBoardNo()) &&
                Objects.equals(postsNo, that.getPostsNo()) &&
                Objects.equals(readNo, that.getReadNo());
    }

}
