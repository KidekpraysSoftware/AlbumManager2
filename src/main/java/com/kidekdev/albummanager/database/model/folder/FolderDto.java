package com.kidekdev.albummanager.database.model.folder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FolderDto {

    @EqualsAndHashCode.Include
    private UUID id;
    private Boolean isActive;
    private String path;
    private ImportRuleType importRule;
    private Long importedAt;
}
