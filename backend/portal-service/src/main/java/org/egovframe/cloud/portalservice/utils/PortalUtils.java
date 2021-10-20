package org.egovframe.cloud.portalservice.utils;

import org.egovframe.cloud.common.config.GlobalConstant;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;


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
		if(isTemp){
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

}
