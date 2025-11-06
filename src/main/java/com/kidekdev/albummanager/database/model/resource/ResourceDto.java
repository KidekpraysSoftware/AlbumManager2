package com.kidekdev.albummanager.database.model.resource;

import com.kidekdev.albummanager.database.model.common.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ResourceDto {

    @EqualsAndHashCode.Include
    private UUID id;
    private Boolean isActive;
    private String hash;
    private ResourceType resourceType;
    private ResourceExtension extension;
    private String description;
    private long sizeBytes;
    private Long importedAt;
    private Long created;
    private Long modified;
    private List<String> tags;
    private Map<String, String> metadata;
}
