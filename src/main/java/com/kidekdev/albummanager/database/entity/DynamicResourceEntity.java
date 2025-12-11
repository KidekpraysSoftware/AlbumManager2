package com.kidekdev.albummanager.database.entity;

import com.kidekdev.albummanager.database.type.ResourceType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.*;

/**
 * Auto-import folder definition used for scanning project resources.
 */
@Getter
@Setter
@Entity
@Table(name = "dynamic_resource")
public class DynamicResourceEntity {

    /**
     * Unique identifier of the folder entry.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Absolute path to the DAW project folder.
     */
    @Column(nullable = false)
    private String path;

    /**
     * Timestamp of the first import (epoch millis UTC).
     */
    private Long importedAt;

    /**
     * Flag indicating whether scanning is currently enabled.
     */
    @Column(name = "is_active")
    private Boolean isActive;

    /**
     * Only resources of this type are imported from the folder.
     */
    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;

    /**
     * Optional resource used as preview for the folder.
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
