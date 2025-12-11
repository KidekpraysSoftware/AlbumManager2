package com.kidekdev.albummanager.database.entity;

import com.kidekdev.albummanager.database.model.common.ResourceType;
import com.kidekdev.albummanager.database.model.resource.ResourceExtension;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

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
@Table(name = "resources")
public class ResourceEntity {

    /**
     * Unique identifier of the resource entry.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Whether the resource should be used by the application.
     */
    @Column(name = "is_active")
    private Boolean isActive;

    /**
     * Absolute or project relative path of the file on disk.
     */
    @Column(nullable = false)
    private String path;

    /**
     * Indicates that the resource was imported from a project folder scan.
     */
    private Boolean inFolder;

    /**
     * SHA-256 hash of the file content.
     */
    @Column(length = 128)
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
     * Size of the file in bytes.
     */
    private Long sizeBytes;

    /**
     * Moment the resource was added to the system (epoch millis UTC).
     */
    private Long importedAt;

    /**
     * Original creation time of the file (epoch millis UTC).
     */
    private Long created;

    /**
     * Original modification time of the file (epoch millis UTC).
     */
    private Long modified;

    /**
     * User defined tags attached to the resource.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "resource_tags", joinColumns = @JoinColumn(name = "resource_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    /**
     * Arbitrary metadata key-value pairs extracted from the file.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "resource_metadata", joinColumns = @JoinColumn(name = "resource_id"))
    @MapKeyColumn(name = "meta_key")
    @Column(name = "meta_value")
    private Map<String, String> metadata = new HashMap<>();

    public void setTags(Iterable<String> tags) {
        this.tags.clear();
        if (tags != null) {
            for (String tag : tags) {
                this.tags.add(tag);
            }
        }
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata.clear();
        if (metadata != null) {
            this.metadata.putAll(metadata);
        }
    }
}
