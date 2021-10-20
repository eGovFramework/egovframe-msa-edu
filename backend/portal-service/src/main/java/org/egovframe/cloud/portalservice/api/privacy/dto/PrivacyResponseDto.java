package org.egovframe.cloud.portalservice.api.privacy.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egovframe.cloud.portalservice.domain.privacy.Privacy;

/**
 * org.egovframe.cloud.portalservice.api.privacy.dto.PrivacyResponseDto
 * <p>
 * 개인정보처리방침 상세 응답 DTO 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/23
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/23    jooho       최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
public class PrivacyResponseDto {

    /**
     * 개인정보처리방침 번호
     */
    private Integer privacyNo;

    /**
     * 개인정보처리방침 제목
     */
    private String privacyTitle;

    /**
     * 개인정보처리방침 내용
     */
    private String privacyContent;

    /**
     * 사용 여부
     */
    private Boolean useAt;

    /**
     * 개인정보처리방침 목록 응답 DTO 생성자
     *
     * @param privacyNo      개인정보처리방침 번호
     * @param privacyTitle   개인정보처리방침 제목
     * @param privacyContent 개인정보처리방침 내용
     * @param useAt          사용 여부
     */
    @QueryProjection
    public PrivacyResponseDto(Integer privacyNo, String privacyTitle, String privacyContent, Boolean useAt) {
        this.privacyNo = privacyNo;
        this.privacyTitle = privacyTitle;
        this.privacyContent = privacyContent;
        this.useAt = useAt;
    }

    /**
     * 개인정보처리방침 엔티티를 생성자로 주입 받아서 개인정보처리방침 상세 응답 DTO 속성 값 세팅
     *
     * @param entity 개인정보처리방침 엔티티
     */
    public PrivacyResponseDto(Privacy entity) {
        this.privacyNo = entity.getPrivacyNo();
        this.privacyTitle = entity.getPrivacyTitle();
        this.privacyContent = entity.getPrivacyContent();
        this.useAt = entity.getUseAt();
    }

    /**
     * 개인정보처리방침 상세 응답 DTO 속성 값으로 개인정보처리방침 엔티티 빌더를 사용하여 객체 생성
     *
     * @return Privacy 개인정보처리방침 엔티티
     */
    public Privacy toEntity() {
        return Privacy.builder()
                .privacyNo(privacyNo)
                .privacyTitle(privacyTitle)
                .privacyContent(privacyContent)
                .useAt(useAt)
                .build();
    }

}
