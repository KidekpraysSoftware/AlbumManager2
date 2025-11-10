package com.kidekdev.albummanager.database.service;

import com.kidekdev.albummanager.database.model.autoimport.ImportDto;
import com.kidekdev.albummanager.database.model.common.ResourceType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ImportDatabaseServiceTest extends BaseDatabaseServiceTest {

    private static final UUID IMPORT_ID = UUID.fromString("2ab741c4-e336-48fd-a1f3-d4055df1f264");

    @Test
    void getAllImportsReturnsConfiguredRules() {
        List<ImportDto> imports = databaseService.getAllImports();

        assertThat(imports)
                .hasSize(1)
                .first()
                .extracting(ImportDto::getPath)
                .isEqualTo("C:\\Users\\Kidek\\Desktop\\Сессии разработка");
    }

    @Test
    void getImportByIdReturnsDto() {
        Optional<ImportDto> maybeImport = databaseService.getImportById(IMPORT_ID);

        assertThat(maybeImport).isPresent();
        ImportDto importDto = maybeImport.orElseThrow();
        assertThat(importDto.getResourceTypes())
                .containsExactlyInAnyOrder(ResourceType.TRACK, ResourceType.IMAGE);
    }

    @Test
    void saveImportStoresRule() {
        UUID newImportId = UUID.randomUUID();
        ImportDto newImport = new ImportDto(
                newImportId,
                "D:/Sessions",
                1700000000000L,
                List.of(ResourceType.TRACK, ResourceType.MIDI)
        );

        SaveResult result = databaseService.saveImport(newImport);

        assertThat(result.isSuccess()).isTrue();
        assertThat(databaseService.getImportById(newImportId)).contains(newImport);
    }

    @Test
    void saveImportRejectsNullArgument() {
        SaveResult result = databaseService.saveImport(null);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.message()).contains("import rule must not be null");
    }
}

