package com.kidekdev.albummanager.database.entity;

import com.kidekdev.albummanager.database.model.common.ResourceType;
import com.kidekdev.albummanager.database.model.common.WorkflowStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents DAW project with attached resources.
 */
@Getter
@Setter
@Entity
@Table(name = "project")
public class ProjectEntity {

    /**
     * Unique identifier of the project.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Whether the project is visible and available for processing.
     */
    @Column(name = "is_active")
    private Boolean isActive;

    /**
     * Project display name.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Workflow status of the project (created, in progress, released, etc.).
     */
    @Enumerated(EnumType.STRING)
    private WorkflowStatus status;

    /**
     * Creation timestamp (epoch millis UTC).
     */
    private Long createdAt;

    /**
     * Optional description of the project.
     */
    @Column(length = 2048)
    private String description;

    /**
     * Primary resource used as preview or main track.
     */
    private UUID view;

    /**
     * Resources linked with the project.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "items")
    LinkedHashSet<UUID> items;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "journal")
    LinkedHashSet<UUID> journal;
}
