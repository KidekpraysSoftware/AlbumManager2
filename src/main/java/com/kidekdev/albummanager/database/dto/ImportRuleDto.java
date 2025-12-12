package com.kidekdev.albummanager.database.dto;

import com.kidekdev.albummanager.database.type.ResourceType;
import lombok.Builder;

import java.util.UUID;

@Builder(toBuilder = true)
public record ImportRuleDto(
        UUID id,
        String path,
        Long importedAt,
        ResourceType resourceType,
        Boolean isActive
) {
}
