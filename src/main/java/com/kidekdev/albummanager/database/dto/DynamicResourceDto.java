package com.kidekdev.albummanager.database.dto;

import com.kidekdev.albummanager.database.type.ResourceType;
import lombok.Builder;

import java.util.LinkedHashSet;
import java.util.UUID;

@Builder(toBuilder = true)
public record DynamicResourceDto(
        UUID id,
        String path,
        Long importedAt,
        Boolean isActive,
        ResourceType resourceType,
        UUID view,
        LinkedHashSet<UUID> items,
        LinkedHashSet<UUID> journal
) {
}
