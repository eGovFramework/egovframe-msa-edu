package org.egovframe.cloud.portalservice.domain.code;

import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.portalservice.api.code.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * org.egovframe.cloud.portalservice.domain.code.CodeRepositoryCustom
 * <p>
 * 공통코드 Querydsl interface
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/07/12
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/12    jaeyeolkim  최초 생성
 * </pre>
 */
public interface CodeRepositoryCustom {

    /**
     * 공통코드 목록
     *
     * @param requestDto
     * @param pageable
     * @return
     */
    Page<CodeListResponseDto> findAllByKeyword(RequestDto requestDto, Pageable pageable);

    /**
     * 공통코드 상세 목록
     *
     * @param requestDto
     * @param pageable
     * @return
     */
    Page<CodeDetailListResponseDto> findAllDetailByKeyword(CodeDetailRequestDto requestDto, Pageable pageable);

    /**
     * 공통코드 목록 - parentCodeId 가 없는 상위공통코드
     *
     * @return
     */
    List<CodeResponseDto> findAllParent();

    /**
     * 공통코드 상세 목록 - parentCodeId 에 해당하는 사용중인 공통코드 목록
     *
     * @param parentCodeId
     * @return
     */
    List<CodeDetailResponseDto> findDetailsByParentCodeIdUseAt(String parentCodeId);

    /**
     * 공통코드 상세 목록 - parentCodeId 에 해당하는 사용중인 공통코드 목록
     * 사용여부가 false 로 변경된 경우에도 인자로 받은 공통코드를 목록에 포함되도록 한다
     *
     * @param parentCodeId
     * @param codeId
     * @return
     */
    List<CodeDetailResponseDto> findDetailsUnionCodeIdByParentCodeId(String parentCodeId, String codeId);

    /**
     * 부모 공통 코드 단건 조회
     *
     * @param codeId
     * @return
     */
    CodeResponseDto findParentByCodeId(String codeId);

    /**
     * 공통코드 parentCodeId 에 해당코드가 존재하는지 여부를 알기 위해 건수를 카운트한다
     * @param codeId
     * @return
     */
    long countByParentCodeId(String codeId);
}
