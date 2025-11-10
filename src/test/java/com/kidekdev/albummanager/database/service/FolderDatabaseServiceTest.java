package com.kidekdev.albummanager.database.service;

import com.kidekdev.albummanager.database.model.common.ResourceType;
import com.kidekdev.albummanager.database.model.folder.FolderDto;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FolderDatabaseServiceTest extends BaseDatabaseServiceTest {

    private static final UUID FOLDER_ID = UUID.fromString("4a2c773d-bd29-4329-ac75-871a6838784a");

    @Test
    void getAllFoldersReturnsSingleExample() {
        assertThat(databaseService.getAllFolders())
                .hasSize(1)
                .first()
                .extracting(FolderDto::getPath)
                .isEqualTo("C:\\Users\\Kidek\\Desktop\\Life Holder Project");
    }

    @Test
    void getFolderByIdReturnsExpectedPath() {
        Optional<FolderDto> maybeFolder = databaseService.getFolderById(FOLDER_ID);

        assertThat(maybeFolder).isPresent();
        FolderDto folder = maybeFolder.orElseThrow();
        assertThat(folder.getResourceType()).isEqualTo(ResourceType.TRACK);
        assertThat(folder.getView()).isEqualTo(UUID.fromString("f9136f3e-90cb-4d6f-b1c5-3a2b04a2a4df"));
    }

    @Test
    void saveFolderStoresFolderMetadata() {
        UUID newFolderId = UUID.randomUUID();
        FolderDto newFolder = new FolderDto(
                newFolderId,
                "E:/NewFolder",
                1700000000000L,
                true,
                ResourceType.TRACK,
                null
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
