package com.kidekdev.albummanager.database.entity;

import com.kidekdev.albummanager.database.model.common.WorkflowStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents DAW project with attached resources.
 */
@Getter
@Setter
@Entity
@Table(name = "projects")
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
     * Resources linked with the project.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "project_resources",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "resource_id"))
    private Set<ResourceEntity> resources = new HashSet<>();

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "view_id")
    private ResourceEntity view;
}
