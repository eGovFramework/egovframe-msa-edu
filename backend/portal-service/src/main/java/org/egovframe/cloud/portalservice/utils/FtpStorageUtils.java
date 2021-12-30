package org.egovframe.cloud.portalservice.utils;

import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.egovframe.cloud.common.exception.BusinessException;
import org.egovframe.cloud.common.exception.BusinessMessageException;
import org.egovframe.cloud.common.exception.dto.ErrorCode;
import org.egovframe.cloud.common.util.MessageUtil;
import org.egovframe.cloud.portalservice.api.attachment.dto.AttachmentBase64RequestDto;
import org.egovframe.cloud.portalservice.api.attachment.dto.AttachmentImageResponseDto;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static org.egovframe.cloud.portalservice.utils.PortalUtils.getPhysicalFileName;

/**
 * org.egovframe.cloud.portalservice.utils.FtpStorageUtils
 * <p>
 * FTP 서버에 파일을 관리한다.
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
@Slf4j
@Getter
@Component
public class FtpStorageUtils implements StorageUtils {

    private final Environment environment;
    private final MessageUtil messageUtil;

    public FtpStorageUtils(Environment environment, MessageUtil messageUtil) {
        this.environment = environment;
        this.messageUtil = messageUtil;
    }

    /**
     * FTP 서버에 최상위 디렉토리 생성
     */
    @PostConstruct
    public void init() {
        try {
            String ftpEnabled = environment.getProperty("ftp.enabled");
            // ftp server 사용하는 환경에서만 처리
            if (StringUtils.hasLength(ftpEnabled) && "true".equals(ftpEnabled)) {
                FtpClientDto ftpClientDto = new FtpClientDto(environment);
                this.connect(ftpClientDto);
                FTPClient ftpClient = ftpClientDto.getFtpClient();

                // 업로드 기본 디렉토리 생성 및 권한 부여
                String rootDir = ftpClientDto.getDirectory();
                makePermissionDirectory(ftpClient, rootDir);

                // editor, messages 디렉토리 생성 및 권한 부여
                makePermissionDirectory(ftpClient, rootDir + "/editor");
                makePermissionDirectory(ftpClient, rootDir + "/messages");

                this.disconnect(ftpClient);
            }
        } catch (IOException ex) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Could not create the directory where the uploaded files will be stored.");
        }
    }

    private void makePermissionDirectory(FTPClient ftpClient, String directory) throws IOException {
        // 디렉토리 생성
        ftpClient.makeDirectory(directory);
        ftpClient.sendSiteCommand("chmod " + "755 " + directory);
    }

    private void setPermission(FTPClient ftpClient, String path) throws IOException {
        ftpClient.sendSiteCommand("chmod " + "644 " + path);
    }

    /**
     * FTP 서버에 접속한다
     *
     * @param ftpClientDto
     * @return
     */
    private FtpClientDto connect(FtpClientDto ftpClientDto) {
        FTPClient ftpClient = ftpClientDto.getFtpClient();

        try {
            // connect
            ftpClient.setControlEncoding(StandardCharsets.UTF_8.name());
            ftpClient.setConnectTimeout(3000);
            ftpClient.connect(ftpClientDto.getHostname(), ftpClientDto.getPort());

            boolean login = ftpClient.login(ftpClientDto.getUsername(), ftpClientDto.getPassword());
            log.info("FTPClient login: {}", login);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();

        } catch (SocketException e) {
            log.error("FTPClient SocketException connect", e);
            throw new BusinessMessageException(messageUtil.getMessage("valid.file.not_saved_try_again"));
        } catch (IOException e) {
            log.error("FTPClient IOException connect", e);
            throw new BusinessMessageException(messageUtil.getMessage("valid.file.not_saved_try_again"));
        }

        return ftpClientDto;
    }

    /**
     * FTP 서버 접속을 종료한다
     *
     * @param ftpClient
     */
    private void disconnect(FTPClient ftpClient) {
        // logout & disconnect
        if (ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException e) {
                log.error("FTPClient IOException disconnect", e);
                throw new BusinessMessageException(messageUtil.getMessage("valid.file.not_saved_try_again"));
            }
        }
    }

    /**
     * FTP 서버에 접속하여 파일을 저장한 후 접속을 종료한다
     *
     * @param ftpClientDto
     * @param isList
     */
    public void storeFile(FtpClientDto ftpClientDto, boolean isList) {
        FTPClient ftpClient = ftpClientDto.getFtpClient();
        try {
            // connect
            this.connect(ftpClientDto);

            // 디렉토리 생성 및 권한 부여
            this.makePermissionDirectory(ftpClient, ftpClientDto.getPathname());

            // upload
            if (isList) {
                List<File> files = ftpClientDto.getFiles();
                for (File file : files) {
                    try (InputStream inputStream = new FileInputStream(file)) {
                        String remote = ftpClientDto.getPathname() + "/" + file.getName();
//                        String remote = "/mnt/messages/" + file.getName();
                        boolean storeFile = ftpClient.storeFile(remote, inputStream);
                        // 파일 권한 부여
                        this.setPermission(ftpClient, remote);
                        log.info("FTPClient storeFile '{}' SUCCESS? {}", remote, storeFile);
                    }
                }
            } else {
                MultipartFile file = ftpClientDto.getFile();
                try (InputStream inputStream = file.getInputStream()) {
                    String remote = ftpClientDto.getRemote();
                    boolean storeFile = ftpClient.storeFile(remote, inputStream);
                    // 파일 권한 부여
                    this.setPermission(ftpClient, remote);
                    log.info("FTPClient storeFile remote={}, SUCCESS? {}", remote, storeFile);
                }
            }

        } catch (IOException e) {
            log.error("FTPClient Exception", e);
            throw new BusinessMessageException(messageUtil.getMessage("valid.file.not_saved_try_again"));
        } finally {
            // logout & disconnect
            this.disconnect(ftpClient);
        }
    }

    /**
     * MultipartFile -> 물리적 파일 저장
     *
     * @param file     MultipartFile
     * @param basePath 기본 root외 파일이 저장될 경로
     * @param isTemp   .temp 파일 생성 여부
     * @return
     */
    public String storeFile(MultipartFile file, String basePath, boolean isTemp) throws BusinessMessageException {
        String filename = getPhysicalFileName(file.getOriginalFilename(), isTemp);

        if (filename.contains("..")) {
            log.error("Filename contains invalid path sequence : " + filename);
            // 파일명이 잘못되었습니다.
            throw new BusinessMessageException(messageUtil.getMessage("valid.file.invalid_name") + " : " + filename);
        }

        FtpClientDto ftpClientDto = new FtpClientDto(environment);
        ftpClientDto.addFile(file, basePath, filename);
        this.storeFile(ftpClientDto, false);

        return filename;
    }

    /**
     * 여러 파일 업로드
     *
     * @param files
     * @param basePath
     */
    @Override
    public void storeFiles(List<File> files, String basePath) {
        FtpClientDto ftpClientDto = new FtpClientDto(environment);
        ftpClientDto.addFiles(files, basePath);
        this.storeFile(ftpClientDto, true);
    }

    /**
     * .temp 파일 생성하여 MultipartFile 저장
     *
     * @param file
     * @return
     */
    public String storeFileTemp(MultipartFile file) {
        return storeFile(file, "", true);
    }

    /**
     * .temp 파일 생성하여 MultipartFile 저장
     *
     * @param file
     * @param basePath
     * @return
     */
    public String storeFileTemp(MultipartFile file, String basePath) {
        return storeFile(file, basePath, true);
    }

    /**
     * MultipartFile 저장.
     *
     * @param file
     * @return
     */
    public String storeFile(MultipartFile file) {
        return storeFile(file, "", false);
    }

    /**
     * MultipartFile 저장.
     *
     * @param file
     * @param basePath
     * @return
     */
    public String storeFile(MultipartFile file, String basePath) {
        return storeFile(file, basePath, false);
    }


    /**
     * base64 encoding 된 파일 저장
     *
     * @param requestDto
     * @param basePath
     * @return
     */
    public String storeBase64File(AttachmentBase64RequestDto requestDto, String basePath) {
        try {
            // ftp connect
            FtpClientDto ftpClientDto = new FtpClientDto(environment);
            this.connect(ftpClientDto);
            FTPClient ftpClient = ftpClientDto.getFtpClient();

            String filename = getPhysicalFileName(requestDto.getOriginalName(), false);
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] decodeBytes = decoder.decode(requestDto.getFileBase64().getBytes());

//            FileOutputStream outputStream = new FileOutputStream(file);
            // 디렉토리 생성 및 권한 부여
            String directory = ftpClientDto.getDirectory() + StringUtils.cleanPath("/" + basePath);
            this.makePermissionDirectory(ftpClient, directory);

            String remote = directory + StringUtils.cleanPath("/" + filename);
            OutputStream outputStream = ftpClient.storeFileStream(remote);
            outputStream.write(decodeBytes);
            outputStream.close();

            // 파일 권한 부여
            this.setPermission(ftpClient, remote);
            // 접속 종료
            this.disconnect(ftpClient);

            return filename;

        } catch (IOException ex) {
            log.error("Could not stored base 64 file.", ex);
            // 파일을 저장할 수 없습니다. 다시 시도해 주세요.
            throw new BusinessMessageException(messageUtil.getMessage("valid.file.not_saved_try_again"));
        }
    }

    /**
     * base64 encoding 된 파일 저장
     *
     * @param requestDto
     * @return
     */
    public String storeBase64File(AttachmentBase64RequestDto requestDto) {
        return storeBase64File(requestDto, "");
    }


    /**
     * download file
     *
     * @param filename
     * @return
     */
    public Resource downloadFile(String filename) {
        try {
            Resource resource = new UrlResource(environment.getProperty("file.url") + StringUtils.cleanPath("/" + filename));

            if (resource.exists()) {
                return resource;
            } else {
                log.error("Could not found resource");
                // 파일을 찾을 수 없습니다.
                throw new BusinessMessageException(messageUtil.getMessage("valid.file.not_found"));
            }
        } catch (MalformedURLException ex) {
            log.error("Could not found file.", ex);
            // 파일을 찾을 수 없습니다.
            throw new BusinessMessageException(messageUtil.getMessage("valid.file.not_found"));
        }
    }

    /**
     * image 태그에서 호출 시 byte 배열로 return
     *
     * @param imagename
     * @return
     * @throws IOException
     */
    public AttachmentImageResponseDto loadImage(String imagename) {
        InputStream inputStream = null;
        try {
            String paths = environment.getProperty("file.url")+StringUtils.cleanPath("/"+ imagename);
            Resource resource = new UrlResource(paths);
            inputStream = resource.getInputStream();

            byte[] data = IOUtils.toByteArray(inputStream);
            String contentType = URLConnection.guessContentTypeFromName(resource.getFilename());

            inputStream.close();

            return AttachmentImageResponseDto.builder()
                .mimeType(contentType)
                .data(data)
                .build();
        } catch (FileNotFoundException | NoSuchFileException ex) {
            // 파일을 찾을 수 없습니다.
            throw new BusinessMessageException(messageUtil.getMessage("valid.file.not_found"));
        } catch (IOException iex) {
            log.error("Could not read file.", iex);
            // 파일을 찾을 수 없습니다.
            throw new BusinessMessageException(messageUtil.getMessage("valid.file.not_found"));
        }finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error(e.getLocalizedMessage());
                }
            }
        }
    }

    /**
     * 물리적 파일 삭제
     *
     * @param filename
     * @return
     */
    public boolean deleteFile(String filename) {
        try {
            FtpClientDto ftpClientDto = new FtpClientDto(environment);
            this.connect(ftpClientDto);
            FTPClient ftpClient = ftpClientDto.getFtpClient();
            // 삭제
            ftpClient.deleteFile(ftpClientDto.getDirectory() + StringUtils.cleanPath("/" + filename));
            this.disconnect(ftpClient);
            return true;
        } catch (IOException e) {
            log.error("Could not deleted file.", e);
            return false;
        }
    }

    /**
     * .temp 제거
     *
     * @param physicalFileName
     * @return
     */
    @Deprecated
    public String renameTemp(String physicalFileName) {
        return physicalFileName;
    }

    /**
     * 저장 경로
     *
     * @param basePath
     * @return
     */
    @Override
    public Path getStorePath(String basePath) {
        try {
            FtpClientDto ftpClientDto = new FtpClientDto(environment);
            this.connect(ftpClientDto);
            FTPClient ftpClient = ftpClientDto.getFtpClient();
            // 디렉토리 생성 및 권한 부여
            Path directory = Paths.get(ftpClientDto.getDirectory()).toAbsolutePath().normalize();
            directory.resolve(basePath);
            makePermissionDirectory(ftpClient, directory.toString());

            this.disconnect(ftpClient);

            return directory;
        } catch (IOException ex) {
            log.error("Could not create file store directory.", ex);
            // 파일을 저장할 수 없습니다. 다시 시도해 주세요.
            throw new BusinessMessageException(messageUtil.getMessage("valid.file.not_saved_try_again"));
        }
    }
}
