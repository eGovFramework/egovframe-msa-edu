package org.egovframe.cloud.common.util;

import org.egovframe.cloud.common.config.GlobalConstant;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;


/**
 * org.egovframe.cloud.common.util.LogUtil
 * <p>
 * 로그인, 접속 로그 입력 시 필요한 정보 제공
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/09/02
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/02    jaeyeolkim  최초 생성
 * </pre>
 */
public class LogUtil {

	/**
	 * 클라이언트 사용자의 IP 가져오기
	 *
	 * @return
	 */
	public static String getUserIp() {

		String ip = null;
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();

		ip = request.getHeader("X-Forwarded-For");

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Real-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-RealIP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("REMOTE_ADDR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}

		return ip;
	}

	/**
	 * 접속 사이트 정보를 넘긴다.
	 *
	 * @param request
	 * @return
	 */
	public static Long getSiteId(HttpServletRequest request) {
		String header = request.getHeader(GlobalConstant.HEADER_SITE_ID);
		if (!StringUtils.hasLength(header)) {
			return null;
		}
		return Long.valueOf(header);
	}

}
