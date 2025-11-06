package com.kidekdev.albummanager.database.model.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ViewDto {

    @EqualsAndHashCode.Include
    private UUID id;
    private Boolean isActive;
    private String name;
    private String description;
    private ViewFilterDto included;
    private ViewFilterDto excluded;
    private DurationRangeDto durationRange;
    private ViewSortDto sort;
}
