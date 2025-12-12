package com.kidekdev.albummanager.database.entity;

import com.kidekdev.albummanager.database.type.ResourceExtension;
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
 * Entity that stores metadata of a single resource file previously persisted in YAML.
 */
@Getter
@Setter
@Entity
@Table(name = "resource_file")
public class ResourceEntity {

    /**
     * Unique identifier of the resource entry.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "resource_name")
    private String resourceName;

    private String authorName;

    /**
     * Whether the resource should be used by the application.
     */
    @Column(name = "is_active")
    private Boolean isActive;

    /**
     * Absolute or project relative path of the file on disk.
     */
    private String path;

    /**
     * Indicates that the resource was imported from a project folder scan.
     */
    @Column(name = "is_dynamic")
    private Boolean isDynamic;

    /**
     * SHA-256 hash of the file content.
     */
    @Column(length = 128, nullable = false, unique = true)
    private String hash;

    /**
     * Logical type of the media resource (track, image, midi, video).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type")
    private ResourceType resourceType;

    /**
     * Actual file extension of the stored media.
     */
    @Enumerated(EnumType.STRING)
    private ResourceExtension extension;

    /**
     * Short free form description of the resource.
     */
    @Column(length = 2048)
    private String description;

    /**
     * Moment the resource was added to the system (epoch millis UTC).
     */
    @Column(name = "imported_at", nullable = false)
    @CreationTimestamp
    private OffsetDateTime importedAt;

    /**
     * Original creation time of the file (epoch millis UTC).
     */
    @Column(name = "file_creation_time", nullable = false)
    private OffsetDateTime fileCreationTime;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    LinkedHashSet<UUID> tags;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    LinkedHashSet<UUID> journal;
}
