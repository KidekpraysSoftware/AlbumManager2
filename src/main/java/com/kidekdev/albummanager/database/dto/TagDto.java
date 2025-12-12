package com.kidekdev.albummanager.database.dto;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder(toBuilder = true)
public record TagDto(
        UUID id,
        String name,
        String tagGroup,
        OffsetDateTime createdAt
) {
}
