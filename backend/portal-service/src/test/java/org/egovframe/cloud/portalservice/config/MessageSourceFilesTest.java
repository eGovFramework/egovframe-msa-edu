package org.egovframe.cloud.portalservice.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

import org.egovframe.cloud.portalservice.domain.message.Message;
import org.egovframe.cloud.portalservice.domain.message.MessageRepository;
import org.egovframe.cloud.portalservice.utils.StorageUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.env.MockEnvironment;

/**
 * {@link MessageSourceFiles}의 메시지 프로퍼티 생성 동작을 검증한다.
 *
 * <p>Spring 컨텍스트 없이 저장소·스토리지를 대역으로 두고 임시 디렉터리에 파일을 만든다.
 */
class MessageSourceFilesTest {

    private static final List<Message> MESSAGES = List.of(
            Message.builder().messageId("common.login").messageKoName("로그인").messageEnName("Login").build());

    private MessageSourceFiles messageSourceFiles(String fileDirectory, StorageUtils storageUtils) {
        MessageRepository messageRepository = mock(MessageRepository.class);
        when(messageRepository.findAll()).thenReturn(MESSAGES);

        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("file.directory", fileDirectory);
        environment.setProperty("ftp.enabled", "true");

        return new MessageSourceFiles(messageRepository, environment, storageUtils);
    }

    @Test
    @DisplayName("file.directory가 아직 없어도 하위 messages 폴더까지 만들고 프로퍼티를 저장한다")
    void createsMessagesDirectoryWhenParentIsAbsent(@TempDir Path tempDir) throws Exception {
        Path fileDirectory = tempDir.resolve("upload").resolve("portal");
        assertThat(Files.exists(fileDirectory)).isFalse();
        StorageUtils storageUtils = mock(StorageUtils.class);

        int count = messageSourceFiles(fileDirectory.toString(), storageUtils).create();

        assertThat(count).isEqualTo(MESSAGES.size());
        Path messagesDirectory = fileDirectory.resolve("messages");
        assertThat(messagesDirectory).exists();
        for (String lang : new String[]{"", "_ko", "_en"}) {
            assertThat(messagesDirectory.resolve("messages" + lang + ".properties")).exists();
        }

        Properties saved = new Properties();
        try (var in = Files.newInputStream(messagesDirectory.resolve("messages_en.properties"))) {
            saved.load(in);
        }
        assertThat(saved.getProperty("common.login")).isEqualTo("Login");
    }

    @Test
    @DisplayName("저장에 실패하면 존재하지 않는 파일을 업로드 대상에 넣지 않는다")
    void doesNotUploadFilesThatWereNotWritten(@TempDir Path tempDir) throws Exception {
        Path fileDirectory = Files.createDirectories(tempDir.resolve("upload"));
        // messages 를 폴더가 아닌 일반 파일로 선점해 폴더 생성과 파일 저장을 모두 실패시킨다
        Files.createFile(fileDirectory.resolve("messages"));
        StorageUtils storageUtils = mock(StorageUtils.class);

        messageSourceFiles(fileDirectory.toString(), storageUtils).create();

        verify(storageUtils, never()).storeFiles(anyList(), anyString());
    }

    @Test
    @DisplayName("업로드 대상에는 실제로 저장된 파일만 담긴다")
    void uploadsOnlyStoredFiles(@TempDir Path tempDir) {
        Path fileDirectory = tempDir.resolve("upload");
        StorageUtils storageUtils = mock(StorageUtils.class);

        messageSourceFiles(fileDirectory.toString(), storageUtils).create();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<File>> captor = ArgumentCaptor.forClass(List.class);
        verify(storageUtils).storeFiles(captor.capture(), anyString());
        assertThat(captor.getValue()).hasSize(3).allSatisfy(file -> assertThat(file).exists());
    }
}
