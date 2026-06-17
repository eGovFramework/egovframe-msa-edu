package org.egovframe.cloud.portalservice.utils;

import static org.egovframe.cloud.portalservice.utils.PortalUtils.getPhysicalFileName;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.egovframe.cloud.common.exception.BusinessException;
import org.egovframe.cloud.common.exception.BusinessMessageException;
import org.egovframe.cloud.common.exception.dto.ErrorCode;
import org.egovframe.cloud.common.util.MessageUtil;
import org.egovframe.cloud.portalservice.api.attachment.dto.AttachmentBase64RequestDto;
import org.egovframe.cloud.portalservice.api.attachment.dto.AttachmentImageResponseDto;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.StringUtils;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * org.egovframe.cloud.portalservice.utils.FileStorageUtils
 * <p>
 * 파일 유틸리티 class
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/07/13
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/13    shinmj  최초 생성
 * </pre>
 */
@Slf4j
@Getter
public class FileStorageUtils implements StorageUtils {

    /** 콤마 구분 확장자(소문자, 점 없이). 예: {@code file.storage.allowed-extensions=pdf,png,jpg} */
    public static final String PROPERTY_ALLOWED_EXTENSIONS = "file.storage.allowed-extensions";

    /** 단일 파일 최대 크기. 미설정 시 {@code spring.servlet.multipart.max-file-size}, 그 다음 기본 10MB */
    public static final String PROPERTY_MAX_FILE_SIZE = "file.storage.max-file-size";

    private final Path fileStorageLocation;
    private final Environment environment;
    private final MessageUtil messageUtil;
    private final Set<String> allowedExtensionsLowercase;
    private final long maxFileSizeBytes;

    public FileStorageUtils(Environment environment, MessageUtil messageUtil) {
        this.environment = environment;
        this.fileStorageLocation = Paths.get(environment.getProperty("file.directory")).toAbsolutePath().normalize();
        this.messageUtil = messageUtil;
        this.allowedExtensionsLowercase = parseAllowedExtensions(environment);
        this.maxFileSizeBytes = resolveMaxFileSizeBytes(environment);
    }

    private static Set<String> parseAllowedExtensions(Environment environment) {
        String raw = environment.getProperty(PROPERTY_ALLOWED_EXTENSIONS, "jpg,jpeg,png,gif,bmp,webp,pdf,xlsx,xls,htm,html,txt");
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .map(s -> s.toLowerCase(Locale.ROOT).replaceFirst("^\\.+", ""))
                .collect(Collectors.toUnmodifiableSet());
    }

    private static long resolveMaxFileSizeBytes(Environment environment) {
        String explicit = environment.getProperty(PROPERTY_MAX_FILE_SIZE);
        String source = StringUtils.hasText(explicit)
                ? explicit
                : environment.getProperty("spring.servlet.multipart.max-file-size", "10MB");
        return DataSize.parse(source).toBytes();
    }

    @PostConstruct
    public void init() {
        try {
            String ftpEnabled = environment.getProperty("ftp.enabled");
            if (!StringUtils.hasLength(ftpEnabled) || !"true".equals(ftpEnabled)) {
                log.info("FileStorageUtils createDirectories! ftpEnabled: {}", ftpEnabled);
                Files.createDirectories(this.fileStorageLocation);
                log.info("FileStorageUtils storage policy — maxFileSizeBytes: {}, allowedExtensions: {}",
                        formatMaxFileSizeForLog(), allowedExtensionsLowercase.size());
            }
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Could not create the directory where the uploaded files will be stored.");
        }
    }

    private String formatMaxFileSizeForLog() {
        return maxFileSizeBytes >= 1024 * 1024
                ? (maxFileSizeBytes / (1024 * 1024)) + "MiB"
                : maxFileSizeBytes + " bytes";
    }

    /**
     * 저장 경로(기준 상대 경로만 허용, 디렉터리 트래버설 차단)
     *
     * @param basePath
     * @return
     */
    public Path getStorePath(String basePath) {

        try {
            Path path = resolveDirectoryUnderStorageRoot(basePath);
            Files.createDirectories(path);
            return path;
        } catch (IOException ex) {
            log.error("Could not create file store directory.", ex);
            // 파일을 저장할 수 없습니다. 다시 시도해 주세요.
            throw new BusinessMessageException(messageUtil.getMessage("valid.file.not_saved_try_again"));
        }
    }

    private Path resolveDirectoryUnderStorageRoot(String basePath) {
        if (!StringUtils.hasText(basePath)) {
            return fileStorageLocation;
        }
        Path candidate = fileStorageLocation.resolve(StringUtils.cleanPath(basePath)).normalize();
        if (!candidate.startsWith(fileStorageLocation)) {
            log.error("Rejected basePath (escapes storage root): {}", basePath);
            throw new BusinessMessageException(messageUtil.getMessage("valid.file.invalid_name"));
        }
        return candidate;
    }

    /**
     * 저장소 루트 아래 파일만 허용 (업로드된 상대 경로 / 이미지 경로 등).
     */
    private Path resolvePathUnderFileStorage(String relativePathFromStorageRoot) {
        String clean = StringUtils.cleanPath(relativePathFromStorageRoot);
        if (!StringUtils.hasText(clean)) {
            throw new BusinessMessageException(messageUtil.getMessage("valid.file.not_found"));
        }
        Path resolved = fileStorageLocation.resolve(clean).normalize();
        if (!resolved.startsWith(fileStorageLocation)) {
            log.error("Rejected path (escapes storage root): {}", relativePathFromStorageRoot);
            throw new BusinessMessageException(messageUtil.getMessage("valid.file.invalid_name"));
        }
        assertPhysicalStoredNameAllowed(fileNameSegment(resolved));
        return resolved;
    }

    private static String fileNameSegment(Path resolvedFile) {
        Path fileName = resolvedFile.getFileName();
        return fileName != null ? fileName.toString() : "";
    }

    private void assertPhysicalStoredNameAllowed(String storedFileName) {
        String name = storedFileName;
        if (name.endsWith(".temp")) {
            name = name.substring(0, name.length() - ".temp".length());
        }
        assertExtensionAllowed(name);
    }

    private void assertOriginalNamePresent(String originalFilename) {
        if (!StringUtils.hasText(originalFilename)) {
            throw new BusinessMessageException(messageUtil.getMessage("valid.file.invalid_name"));
        }
    }

    private void assertExtensionAllowed(String filename) {
        String ext = StringUtils.getFilenameExtension(filename);
        if (!StringUtils.hasText(ext)) {
            throw new BusinessMessageException(messageUtil.getMessage("valid.file.invalid_name"));
        }
        String extLower = ext.toLowerCase(Locale.ROOT);
        if (!allowedExtensionsLowercase.contains(extLower)) {
            log.warn("Rejected file extension: {}", extLower);
            throw new BusinessMessageException(messageUtil.getMessage("valid.file.invalid_name"));
        }
    }

    private void assertWithinMaxSize(long sizeBytes) {
        if (sizeBytes > maxFileSizeBytes) {
            throw new BusinessMessageException(messageUtil.getMessage("valid.file.too_big"));
        }
    }


    private String probeContentType(Path filePath) {
        String mimeType = null;
        try {
            mimeType = Files.probeContentType(filePath);
        } catch (IOException ex) {
            log.error("Files.probeContentType", ex);
        }

        return mimeType == null ? URLConnection.guessContentTypeFromName(filePath.toString()) : mimeType;
    }

    public String getContentType(String filename) {
        Path filePath = resolvePathUnderFileStorage(filename);
        return probeContentType(filePath);
    }

    /**
     * .temp 제거
     *
     * @param physicalFileName
     * @return
     */
    public String renameTemp(String physicalFileName) {
        String renamedRelative = stripTempSuffixFromRelativePath(physicalFileName);
        Path source = resolvePathUnderFileStorage(physicalFileName);
        Path target = resolvePathUnderFileStorage(renamedRelative);

        File file = source.toFile();
        File renameFile = target.toFile();
        // File.renameTo는 실패 시 예외가 아니라 false를 반환하므로 반환값을 검사한다.
        // (기존 catch(NullPointerException)는 rename 실패를 감지하지 못해 실패가 조용히 무시되어
        //  존재하지 않는 파일 경로가 그대로 저장되는 문제가 있었다.)
        if (!file.renameTo(renameFile)) {
            // 원본 파일을 찾을 수 없거나 이름 변경에 실패한 경우
            throw new BusinessMessageException(messageUtil.getMessage("valid.file.not_found"));
        }
        return renamedRelative;
    }

    private static String stripTempSuffixFromRelativePath(String relativePath) {
        String clean = StringUtils.cleanPath(relativePath);
        if (clean.endsWith(".temp")) {
            return clean.substring(0, clean.length() - ".temp".length());
        }
        return clean.replace(".temp", "");
    }

    /**
     * base64 encoding 된 파일 저장
     *
     * @param requestDto
     * @param basePath
     * @return
     */
    public String storeBase64File(AttachmentBase64RequestDto requestDto, String basePath) {
        assertOriginalNamePresent(requestDto.getOriginalName());
        assertExtensionAllowed(requestDto.getOriginalName());

        String filename = getPhysicalFileName(requestDto.getOriginalName(), false);

        try {
            Path path = getStorePath(basePath);
            Path target = path.resolve(filename).normalize();
            if (!target.startsWith(path)) {
                throw new BusinessMessageException(messageUtil.getMessage("valid.file.invalid_name"));
            }
            File file = target.toFile();

            Base64.Decoder decoder = Base64.getDecoder();
            byte[] decodeBytes = decoder.decode(requestDto.getFileBase64().getBytes());
            assertWithinMaxSize(decodeBytes.length);

            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                outputStream.write(decodeBytes);
            }

            return filename;

        } catch (IllegalArgumentException ex) {
            log.error("Invalid base64 payload.", ex);
            throw new BusinessMessageException(messageUtil.getMessage("valid.file.not_saved_try_again"));
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
     * MultipartFile -> 물리적 파일 저장
     *
     * @param file     MultipartFile
     * @param basePath 기본 root외 파일이 저장될 경로
     * @param isTemp   .temp 파일 생성 여부
     * @return
     */
    public String storeFile(MultipartFile file, String basePath, boolean isTemp) {
        assertOriginalNamePresent(file.getOriginalFilename());
        assertExtensionAllowed(file.getOriginalFilename());
        assertWithinMaxSize(file.getSize());

        String filename = getPhysicalFileName(file.getOriginalFilename(), isTemp);

        try {
            Path path = getStorePath(basePath);
            Path target = path.resolve(filename).normalize();
            if (!target.startsWith(path)) {
                log.error("Filename resolves outside store directory : {}", filename);
                throw new BusinessMessageException(messageUtil.getMessage("valid.file.invalid_name") + " : " + filename);
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            }

            return filename;
        } catch (IOException ex) {
            log.error("Could not stored file", ex);
            // 파일을 저장할 수 없습니다. 다시 시도해 주세요.
            throw new BusinessMessageException(messageUtil.getMessage("valid.file.not_saved_try_again"));
        }
    }

    @Override
    public void storeFiles(List<File> files, String basePath) {
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
     * download file
     *
     * @param filename
     * @return
     */
    public Resource downloadFile(String filename) {
        try {
            Path path = resolvePathUnderFileStorage(filename);
            Resource resource = new UrlResource(path.toUri());

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
     * @return public String getContentType(String filename) {
     * @throws IOException
     */
    public AttachmentImageResponseDto loadImage(String imagename) {
        try {
            Path imagePath = resolvePathUnderFileStorage(imagename);
            try (InputStream is = new FileInputStream(imagePath.toFile())) {
                try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
                    int read;
                    byte[] data = new byte[(int) imagePath.toFile().length()];
                    while ((read = is.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, read);
                    }

                    return AttachmentImageResponseDto.builder()
                            .mimeType(probeContentType(imagePath))
                            .data(data)
                            .build();
                }
            }

        } catch (FileNotFoundException | NoSuchFileException ex) {
            // 파일을 찾을 수 없습니다.
            throw new BusinessMessageException(messageUtil.getMessage("valid.file.not_found"));
        } catch (IOException iex) {
            log.error("Could not read file.", iex);
            // 파일을 찾을 수 없습니다.
            throw new BusinessMessageException(messageUtil.getMessage("valid.file.not_found"));
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
            Path path = resolvePathUnderFileStorage(filename);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("Could not deleted file.", e);
            return false;
        }
    }
}
