package com.kidekdev.albummanager.database.service;

import com.kidekdev.albummanager.database.model.common.ResourceType;
import com.kidekdev.albummanager.database.model.view.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ViewDatabaseServiceTest extends BaseDatabaseServiceTest {

    private static final UUID VIEW_ID = UUID.fromString("ae2c9cd6-6cc5-4dd6-91b8-2a3f3c4f1c8f");

    @Test
    void getAllViewsReturnsSingleExampleView() {
        assertThat(databaseService.getAllViews())
                .hasSize(1)
                .first()
                .extracting(ViewDto::getName)
                .isEqualTo("Короткие треки о космосе");
    }

    @Test
    void getViewByIdReturnsExpectedFilters() {
        Optional<ViewDto> maybeView = databaseService.getViewById(VIEW_ID);

        assertThat(maybeView).isPresent();
        ViewDto view = maybeView.orElseThrow();
        assertThat(view.getIncluded().getTags()).contains("Космос");
        assertThat(view.getDurationRange().min()).isEqualTo(0.0);
        assertThat(view.getSort().by()).isEqualTo(SortBy.BY_IMPORTED_AT);
        assertThat(view.getSort().order()).isEqualTo(SortOrder.DESC);
    }

    @Test
    void saveViewPersistsNewView() {
        UUID newViewId = UUID.randomUUID();
        ViewFilterDto included = new ViewFilterDto(List.of("demo"), List.of(ResourceType.TRACK), null, List.of());
        ViewFilterDto excluded = new ViewFilterDto(List.of(), List.of(), null, List.of("skip"));
        ViewDto newView = new ViewDto(
                newViewId,
                true,
                "Demo View",
                "Description",
                included,
                excluded,
                new DurationRangeDto(10.0, 300.0),
                new ViewSortDto(SortBy.BY_NAME, SortOrder.ASC)
        );

        SaveResult result = databaseService.saveView(newView);

        assertThat(result.isSuccess()).isTrue();
        assertThat(databaseService.getViewById(newViewId)).contains(newView);
    }

    @Test
    void saveViewRejectsNullArgument() {
        SaveResult result = databaseService.saveView(null);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.message()).contains("view must not be null");
    }
}
