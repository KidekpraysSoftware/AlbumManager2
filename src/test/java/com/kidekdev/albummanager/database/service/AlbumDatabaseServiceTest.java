package com.kidekdev.albummanager.database.service;

import com.kidekdev.albummanager.database.model.album.AlbumDto;
import com.kidekdev.albummanager.database.model.album.AlbumType;
import com.kidekdev.albummanager.database.model.common.WorkflowStatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AlbumDatabaseServiceTest extends BaseDatabaseServiceTest {

    private static final UUID ALBUM_ID = UUID.fromString("bb75ba42-0257-422f-a90e-cd1066fdd9f2");

    @Test
    void getAllAlbumsReturnsExampleAlbum() {
        List<AlbumDto> albums = databaseService.getAllAlbums();

        assertThat(albums)
                .hasSize(1)
                .first()
                .extracting(AlbumDto::getName)
                .isEqualTo("Space Music");
    }

    @Test
    void getAlbumByIdReturnsPopulatedDto() {
        Optional<AlbumDto> maybeAlbum = databaseService.getAlbumById(ALBUM_ID);

        assertThat(maybeAlbum).isPresent();
        AlbumDto album = maybeAlbum.orElseThrow();
        assertThat(album.getType()).isEqualTo(AlbumType.DRAFT);
        assertThat(album.getStatus()).isEqualTo(WorkflowStatus.INPROGRESS);
        assertThat(album.getResources()).hasSize(3);
        assertThat(album.getWallpaper()).isEqualTo(UUID.fromString("cff92b59-d23c-4cb9-bb73-04635a1ef03a"));
    }

    @Test
    void saveAlbumAllowsAddingNewAlbum() {
        UUID newAlbumId = UUID.randomUUID();
        AlbumDto newAlbum = new AlbumDto(
                newAlbumId,
                true,
                "Demo Album",
                AlbumType.RELEASE,
                "/demo",
                WorkflowStatus.CREATED,
                List.of(),
                1700000000000L,
                "Test album",
                null,
                null
        );

        SaveResult result = databaseService.saveAlbum(newAlbum);

        assertThat(result.isSuccess()).isTrue();
        assertThat(databaseService.getAlbumById(newAlbumId)).contains(newAlbum);
    }

    @Test
    void saveAlbumRejectsNullArgument() {
        SaveResult result = databaseService.saveAlbum(null);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.message()).contains("album must not be null");
    }
}
