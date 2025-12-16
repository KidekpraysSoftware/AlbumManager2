package com.kidekdev.albummanager.database.entity;

import com.kidekdev.albummanager.database.type.ResourceType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
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

    @Column(nullable = false)
    private String name;

    /**
     * Absolute path to the DAW project folder.
     */
    @Column(nullable = false)
    private String path;

    /**
     * Timestamp of the first import (epoch millis UTC).
     */
    @Column(name = "imported_at", nullable = false)
    @CreationTimestamp
    private OffsetDateTime importedAt;

    /**
     * Flag indicating whether scanning is currently enabled.
     */
    @Column(name = "is_active")
    private Boolean isActive;

    /**
     * Only resources of this type are imported from the folder.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type")
    private ResourceType resourceType;

    /**
     * Optional resource used as preview for the folder.
     */
    private UUID view;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    LinkedHashSet<UUID> journal;

}
