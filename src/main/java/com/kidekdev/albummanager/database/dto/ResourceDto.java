package com.kidekdev.albummanager.database.dto;

import com.kidekdev.albummanager.database.type.ResourceExtension;
import com.kidekdev.albummanager.database.type.ResourceType;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Flat data transfer object mirroring {@link com.kidekdev.albummanager.database.entity.ResourceEntity} fields.
 */
@Builder(toBuilder = true)
public record ResourceDto(
        UUID id,
        String resourceName,
        String authorName,
        Boolean isActive,
        String path,
        Boolean isDynamic,
        String hash,
        ResourceType resourceType,
        ResourceExtension extension,
        String description,
        OffsetDateTime importedAt,
        OffsetDateTime fileCreationTime,
        Set<TagDto> tags,
        LinkedHashSet<UUID> journal
) {
}
