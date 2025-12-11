package com.kidekdev.albummanager.database.entity;

import com.kidekdev.albummanager.database.entity.tag.TagEntity;
import com.kidekdev.albummanager.database.model.common.ResourceType;
import com.kidekdev.albummanager.database.model.resource.ResourceExtension;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Entity that stores metadata of a single resource file previously persisted in YAML.
 */
@Getter
@Setter
@Entity
@Table(name = "resource")
public class ResourceEntity {

    /**
     * Unique identifier of the resource entry.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String resourceName;

    @Column(nullable = false)
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
    @Column(nullable = false)
    @CreationTimestamp
    private OffsetDateTime importedAt;

    /**
     * Original creation time of the file (epoch millis UTC).
     */
    @Column(nullable = false)
    private OffsetDateTime fileCreationTime;

    /* ---------- Теги ---------- */

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "resource_tag_link",
            joinColumns = @JoinColumn(name = "resource_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    Set<TagEntity> tags = new HashSet<>();
}
