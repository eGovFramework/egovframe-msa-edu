package org.egovframe.cloud.portalservice.utils;

import org.egovframe.cloud.portalservice.api.attachment.dto.AttachmentBase64RequestDto;
import org.egovframe.cloud.portalservice.api.attachment.dto.AttachmentImageResponseDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * org.egovframe.cloud.portalservice.utils.StorageUtils
 * <p>
 * StorageUtils
 * ftp 서버 사용 여부에 따라 StorageUtils 에 주입하는 빈이 달라진다.
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/09/08
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/08    jaeyeolkim  최초 생성
 * </pre>
 */
public interface StorageUtils {

    /**
     * MultipartFile -> 물리적 파일 저장
     *
     * @param file     MultipartFile
     * @param basePath 기본 root외 파일이 저장될 경로
     * @param isTemp   .temp 파일 생성 여부
     * @return
     */
    String storeFile(MultipartFile file, String basePath, boolean isTemp);

    /**
     * file 저장
     *
     * @param files
     * @param basePath
     * @return
     */
    void storeFiles(List<File> files, String basePath);


    /**
     * base64 encoding 된 파일 저장
     *
     * @param requestDto
     * @param basePath
     * @return
     */
    String storeBase64File(AttachmentBase64RequestDto requestDto, String basePath);

    /**
     * image 태그에서 호출 시 byte 배열로 return
     *
     * @param imagename
     * @return
     * @throws IOException
     */
    AttachmentImageResponseDto loadImage(String imagename);

    /**
     * download file
     *
     * @param filename
     * @return
     */
    Resource downloadFile(String filename);

    /**
     * .temp 제거
     *
     * @param physicalFileName
     * @return
     */
    String renameTemp(String physicalFileName);

    /**
     * 물리적 파일 삭제
     *
     * @param filename
     * @return
     */
    boolean deleteFile(String filename);

    /**
     * 저장 경로
     *
     * @param basePath
     * @return
     */
    public Path getStorePath(String basePath);


}
