package com.kidekdev.albummanager.database.service;

import com.kidekdev.albummanager.database.model.common.ResourceType;
import com.kidekdev.albummanager.database.model.resource.ResourceDto;
import com.kidekdev.albummanager.database.model.resource.ResourceExtension;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceDatabaseServiceTest extends BaseDatabaseServiceTest {

    private static final UUID MIDI_TRACK_ID = UUID.fromString("f2a6e6e4-3c77-44e5-b33c-73d72c84d49c");

    @Test
    void getAllTracksReturnsEveryResourceFromExampleDatabase() {
        List<ResourceDto> tracks = databaseService.getAllTracks();

        assertThat(tracks)
                .hasSize(4)
                .extracting(ResourceDto::getId)
                .containsExactlyInAnyOrder(
                        UUID.fromString("71acaf40-81d3-4a37-bbc5-708f9adcb83b"),
                        UUID.fromString("9a1d01c7-43ff-4a3b-8bc8-f6bde7626a73"),
                        UUID.fromString("f2a6e6e4-3c77-44e5-b33c-73d72c84d49c"),
                        UUID.fromString("f9136f3e-90cb-4d6f-b1c5-3a2b04a2a4df")
                );
    }

    @Test
    void getTrackByIdReturnsMatchingDto() {
        Optional<ResourceDto> maybeTrack = databaseService.getTrackById(MIDI_TRACK_ID);

        assertThat(maybeTrack).isPresent();
        ResourceDto track = maybeTrack.orElseThrow();
        assertThat(track.getResourceType()).isEqualTo(ResourceType.MIDI);
        assertThat(track.getTags()).containsExactly("piano");
        assertThat(track.getMetadata())
                .containsEntry("title", "Sketch_01")
                .containsEntry("tempo bpm", "120");
    }

    @Test
    void saveTrackPersistsNewResource() {
        UUID newTrackId = UUID.randomUUID();
        ResourceDto newTrack = new ResourceDto(
                newTrackId,
                true,
                false,
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                ResourceType.TRACK,
                ResourceExtension.MP3,
                "Demo track",
                4242L,
                1700000000000L,
                1700000000000L,
                1700000005000L,
                List.of("demo", "test"),
                Map.of("bpm", "90")
        );

        SaveResult result = databaseService.saveTrack(newTrack);

        assertThat(result.isSuccess()).isTrue();
        assertThat(databaseService.getTrackById(newTrackId)).contains(newTrack);
    }

    @Test
    void saveTrackRejectsNullResource() {
        SaveResult result = databaseService.saveTrack(null);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.message()).contains("track must not be null");
    }
}
