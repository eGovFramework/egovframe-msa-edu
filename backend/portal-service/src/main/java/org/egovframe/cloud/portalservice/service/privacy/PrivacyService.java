package org.egovframe.cloud.portalservice.service.privacy;

import lombok.RequiredArgsConstructor;
import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.common.exception.EntityNotFoundException;
import org.egovframe.cloud.common.service.AbstractService;
import org.egovframe.cloud.portalservice.api.privacy.dto.PrivacyListResponseDto;
import org.egovframe.cloud.portalservice.api.privacy.dto.PrivacyResponseDto;
import org.egovframe.cloud.portalservice.api.privacy.dto.PrivacySaveRequestDto;
import org.egovframe.cloud.portalservice.api.privacy.dto.PrivacyUpdateRequestDto;
import org.egovframe.cloud.portalservice.domain.privacy.Privacy;
import org.egovframe.cloud.portalservice.domain.privacy.PrivacyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * org.egovframe.cloud.portalservice.service.privacy.PrivacyService
 * <p>
 * 개인정보처리방침 서비스 클래스
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
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PrivacyService extends AbstractService {

    /**
     * 개인정보처리방침 레파지토리 인터페이스
     */
    private final PrivacyRepository privacyRepository;

    /**
     * 조회 조건에 일치하는 개인정보처리방침 페이지 목록 조회
     *
     * @param requestDto 요청 DTO
     * @param pageable   페이지 정보
     * @return Page<PrivacyListResponseDto> 페이지 개인정보처리방침 목록 응답 DTO
     */
    public Page<PrivacyListResponseDto> findPage(RequestDto requestDto, Pageable pageable) {
        return privacyRepository.findPage(requestDto, pageable);
    }

    /**
     * 사용여부로 개인정보처리방침 내림차순 전체 목록 조회
     *
     * @param useAt 사용 여부
     * @return List<PrivacyResponseDto> 개인정보처리방침 상세 응답 DTO List
     */
    public List<PrivacyResponseDto> findAllByUseAt(Boolean useAt) {
        return privacyRepository.findAllByUseAt(useAt);
    }

    /**
     * 개인정보처리방침 단건 조회
     *
     * @param privacyNo 개인정보처리방침 번호
     * @return PrivacyResponseDto 개인정보처리방침 응답 DTO
     */
    public PrivacyResponseDto findById(Integer privacyNo) {
        Privacy entity = findPrivacy(privacyNo);

        return new PrivacyResponseDto(entity);
    }

    /**
     * 개인정보처리방침 등록
     *
     * @param requestDto 개인정보처리방침 등록 요청 DTO
     * @return PrivacyResponseDto 개인정보처리방침 응답 DTO
     */
    @Transactional
    public PrivacyResponseDto save(PrivacySaveRequestDto requestDto) {
        Privacy entity = privacyRepository.save(requestDto.toEntity());

        return new PrivacyResponseDto(entity);
    }

    /**
     * 개인정보처리방침 수정
     *
     * @param privacyNo  개인정보처리방침 번호
     * @param requestDto 개인정보처리방침 수정 요청 DTO
     * @return PrivacyResponseDto 개인정보처리방침 응답 DTO
     */
    @Transactional
    public PrivacyResponseDto update(Integer privacyNo, PrivacyUpdateRequestDto requestDto) {
        Privacy entity = findPrivacy(privacyNo);

        // 수정
        entity.update(requestDto.getPrivacyTitle(), requestDto.getPrivacyContent(), requestDto.getUseAt());

        return new PrivacyResponseDto(entity);
    }

    /**
     * 개인정보처리방침 사용 여부 수정
     *
     * @param privacyNo 개인정보처리방침 번호
     * @param useAt     사용 여부
     * @return PrivacyResponseDto 개인정보처리방침 응답 DTO
     */
    @Transactional
    public PrivacyResponseDto updateUseAt(Integer privacyNo, Boolean useAt) {
        Privacy entity = findPrivacy(privacyNo);

        // 수정
        entity.updateUseAt(useAt);

        return new PrivacyResponseDto(entity);
    }

    /**
     * 개인정보처리방침 삭제
     *
     * @param privacyNo 개인정보처리방침 번호
     */
    @Transactional
    public void delete(Integer privacyNo) {
        Privacy entity = findPrivacy(privacyNo);

        // 삭제
        privacyRepository.delete(entity);
    }

    /**
     * 개인정보처리방침 번호로 개인정보처리방침 엔티티 조회
     *
     * @param privacyNo 개인정보처리방침 번호
     * @return Privacy 개인정보처리방침 엔티티
     */
    private Privacy findPrivacy(Integer privacyNo) {
        return privacyRepository.findById(privacyNo)
                .orElseThrow(() -> new EntityNotFoundException(getMessage("valid.notexists.format", new Object[]{getMessage("privacy")})));
    }

}
