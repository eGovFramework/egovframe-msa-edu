package org.egovframe.cloud.portalservice.utils;

import java.util.ArrayList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.core.env.Environment;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

/**
 * org.egovframe.cloud.portalservice.utils.FtpClientDto
 * <p>
 * FTP Client dto
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/09/07
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/07    jaeyeolkim  최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
public class FtpClientDto {
    private String hostname;
    private int port;
    private String username;
    private String password;
    private String directory; // 기본 상위 저장 경로
    private String path; // 하위 저장 경로

    private FTPClient ftpClient;
    private MultipartFile file;
    private List<File> files;
    private String filename;

    public FtpClientDto(Environment env) {
        this.ftpClient = new FTPClient();
        this.hostname = env.getProperty("ftp.hostname");
        this.port = Integer.parseInt(env.getProperty("ftp.port", ""));
        this.username = env.getProperty("ftp.username");
        this.password = env.getProperty("ftp.password");
        this.directory = env.getProperty("ftp.directory");
    }

    public FtpClientDto addFile(MultipartFile file, String path, String filename) {
        this.filename = filename;
        this.path = path;
        this.file = file;
        return this;
    }

    public FtpClientDto addFiles(List<File> files, String path) {
        this.files = new ArrayList<>(files);
        this.path = path;
        return this;
    }

    /**
     * 파일 저장 경로
     *
     * @return
     */
    public String getPathname() {
        return this.directory + "/" + this.path;
    }

    /**
     * 파일명을 포함한 원격 경로
     *
     * @return
     */
    public String getRemote() {
        return getPathname() + "/" + this.filename;
    }

    @Override
    public String toString() {
        return "FtpClientDto{" +
                "hostname='" + hostname + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", directory='" + directory + '\'' +
                '}';
    }
}
