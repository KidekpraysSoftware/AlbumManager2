package com.kidekdev.albummanager.database.service;

import com.kidekdev.albummanager.database.model.folder.FolderDto;
import com.kidekdev.albummanager.database.model.folder.ImportRuleType;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FolderDatabaseServiceTest extends BaseDatabaseServiceTest {

    private static final UUID FOLDER_ID = UUID.fromString("2ab741c4-e336-48fd-a1f3-d4055df1f264");

    @Test
    void getAllFoldersReturnsSingleExample() {
        assertThat(databaseService.getAllFolders())
                .hasSize(1)
                .first()
                .extracting(FolderDto::getPath)
                .isEqualTo("D:/MusicProjects/LifeHolder");
    }

    @Test
    void getFolderByIdReturnsExpectedPath() {
        Optional<FolderDto> maybeFolder = databaseService.getFolderById(FOLDER_ID);

        assertThat(maybeFolder).isPresent();
        assertThat(maybeFolder.orElseThrow().getImportRule()).isEqualTo(ImportRuleType.UPDATABLE_RESOURCE);
    }

    @Test
    void saveFolderStoresFolderMetadata() {
        UUID newFolderId = UUID.randomUUID();
        FolderDto newFolder = new FolderDto(
                newFolderId,
                true,
                "E:/NewFolder",
                ImportRuleType.RESOURCE_FOLDER,
                1700000000000L
        );

        SaveResult result = databaseService.saveFolder(newFolder);

        assertThat(result.isSuccess()).isTrue();
        assertThat(databaseService.getFolderById(newFolderId)).contains(newFolder);
    }

    @Test
    void saveFolderRejectsNullArgument() {
        SaveResult result = databaseService.saveFolder(null);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.message()).contains("folder must not be null");
    }
}
