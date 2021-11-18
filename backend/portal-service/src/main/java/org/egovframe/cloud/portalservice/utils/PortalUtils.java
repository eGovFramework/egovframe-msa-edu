package org.egovframe.cloud.portalservice.utils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.apache.commons.io.FilenameUtils;
import org.springframework.util.StringUtils;


/**
 * org.egovframe.cloud.portalservice.utils.PortalUtils
 * <p>
 * 포털 서비스 사용하는 유틸 클래스
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/09/09
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/09    jaeyeolkim  최초 생성
 * </pre>
 */
public class PortalUtils {

    /**
     * '-'을 제거한 uuid 생성
     *
     * @return
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 물리적 파일 이름 생성
     *
     * @param originalFileName
     * @param isTemp
     * @return
     */
    public static String getPhysicalFileName(String originalFileName, boolean isTemp) {
        String ext = StringUtils.getFilenameExtension(originalFileName);
        StringBuffer sb = new StringBuffer();
        sb.append(getUUID());
        sb.append(".");
        sb.append(ext);
        if (isTemp) {
            sb.append(".temp");
        }
        return StringUtils.cleanPath(sb.toString());
    }

    /**
     * 물리적 파일 이름 생성 (.temp)
     *
     * @param originalFileName
     * @return
     */
    public static String getPhysicalFileName(String originalFileName) {
        return getPhysicalFileName(originalFileName, true);
    }

	/**
	 * SecureRandom을 활용한 랜덤 생성
	 *
	 * @param count
	 * @return
	 */
	public static String randomAlphanumeric(int count) {
        char[] charSet = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C',
            'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
            'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

        StringBuffer sb = new StringBuffer();
		SecureRandom sr = new SecureRandom();
		sr.setSeed(LocalDateTime.now().getNano());

		int idx = 0;
		int len = charSet.length;
		for (int i = 0; i < count; i++) {
			idx = sr.nextInt(len);
			sb.append(charSet[idx]);
		}

        return sb.toString();
    }

}
