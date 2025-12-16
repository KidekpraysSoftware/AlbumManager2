package com.kidekdev.albummanager.database.dto;

import com.kidekdev.albummanager.database.type.ResourceType;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.UUID;

@Builder(toBuilder = true)
public record DynamicResourceDto(
        UUID id,
        String name,
        String path,
        OffsetDateTime importedAt,
        Boolean isActive,
        ResourceType resourceType,
        UUID view,
        LinkedHashSet<UUID> journal
) {
}
