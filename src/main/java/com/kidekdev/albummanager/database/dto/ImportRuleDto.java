package com.kidekdev.albummanager.database.dto;

import com.kidekdev.albummanager.database.type.ResourceType;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder(toBuilder = true)
public record ImportRuleDto(
        UUID id,
        String path,
        OffsetDateTime importedAt,
        ResourceType resourceType,
        Boolean isActive
) {
}
