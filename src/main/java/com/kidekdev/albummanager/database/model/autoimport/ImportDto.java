package com.kidekdev.albummanager.database.model.autoimport;

import com.kidekdev.albummanager.database.model.common.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ImportDto {

    @EqualsAndHashCode.Include
    private UUID id;
    private String path;
    private Long importedAt;
    private List<ResourceType> resourceTypes;
}

