package org.egovframe.cloud.portalservice.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.egovframe.cloud.common.util.MessageUtil;
import org.egovframe.cloud.portalservice.api.attachment.dto.AttachmentImageResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.env.MockEnvironment;

class FileStorageUtilsTest {

    @TempDir
    Path storageDirectory;

    private FileStorageUtils fileStorageUtils;

    @BeforeEach
    void setUp() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("file.directory", storageDirectory.toString())
                .withProperty(FileStorageUtils.PROPERTY_ALLOWED_EXTENSIONS, "png");
        fileStorageUtils = new FileStorageUtils(environment, mock(MessageUtil.class));
    }

    @Test
    void loadImageReturnsEveryStoredByte() throws IOException {
        byte[] imageBytes = new byte[] {
                (byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a,
                0x01, 0x02, 0x03, 0x04
        };
        Files.write(storageDirectory.resolve("image.png"), imageBytes);

        AttachmentImageResponseDto image = fileStorageUtils.loadImage("image.png");

        assertThat(image.getMimeType()).startsWith("image/");
        assertThat(image.getData()).containsExactly(imageBytes);
    }

    @Test
    void loadImageReturnsImmediatelyForEmptyImage() throws IOException {
        Files.createFile(storageDirectory.resolve("empty.png"));

        AttachmentImageResponseDto image = fileStorageUtils.loadImage("empty.png");

        assertThat(image.getMimeType()).startsWith("image/");
        assertThat(image.getData()).isEmpty();
    }
}
