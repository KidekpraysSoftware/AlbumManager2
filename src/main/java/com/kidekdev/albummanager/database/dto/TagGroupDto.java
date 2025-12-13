package com.kidekdev.albummanager.database.dto;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record TagGroupDto(
        UUID id,
        String name,
        OffsetDateTime createdAt,
        int ordering,
        List<TagDto> tags
) {
}
