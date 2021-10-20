package org.egovframe.cloud.portalservice.config;

import lombok.extern.slf4j.Slf4j;
import org.egovframe.cloud.common.util.MessageUtil;
import org.egovframe.cloud.portalservice.utils.FileStorageUtils;
import org.egovframe.cloud.portalservice.utils.FtpStorageUtils;
import org.egovframe.cloud.portalservice.utils.StorageUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * org.egovframe.cloud.portalservice.config.StorageConfig
 * <p>
 * StorageConfig Config 클래스
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
@Slf4j
@Configuration
public class StorageConfig {

    @Bean
    public StorageUtils storageUtils(Environment environment, MessageUtil messageUtil) {
        String ftpEnabled = environment.getProperty("ftp.enabled");
        if (StringUtils.hasLength(ftpEnabled) && "true".equals(ftpEnabled)) {
            log.info("ftpEnabled: {} StorageUtils -> FtpStorageUtils", ftpEnabled);
            return new FtpStorageUtils(environment, messageUtil);
        }
        log.info("ftpEnabled: {} StorageUtils -> FileStorageUtils", ftpEnabled);
        return new FileStorageUtils(environment, messageUtil);
    }
}
