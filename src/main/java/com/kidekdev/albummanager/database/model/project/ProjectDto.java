package com.kidekdev.albummanager.database.model.project;

import com.kidekdev.albummanager.database.model.common.WorkflowStatus;
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
public class ProjectDto {

    @EqualsAndHashCode.Include
    private UUID id;
    private Boolean isActive;
    private String name;
    private WorkflowStatus status;
    private List<UUID> resources;
    private Long createdAt;
    private String description;
    private UUID view;
}
