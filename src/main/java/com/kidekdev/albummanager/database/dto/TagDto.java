package com.kidekdev.albummanager.database.dto;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Flat data transfer object mirroring {@link com.kidekdev.albummanager.database.entity.TagEntity} fields.
 */
@Builder(toBuilder = true)
public record TagDto(
        UUID id,
        String name,
        String group,
        OffsetDateTime createdAt
) {
}
