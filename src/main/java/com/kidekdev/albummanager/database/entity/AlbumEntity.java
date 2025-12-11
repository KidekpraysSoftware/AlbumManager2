package com.kidekdev.albummanager.database.entity;

import com.kidekdev.albummanager.database.model.album.AlbumType;
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
 * Album entity containing virtual grouping of resources.
 */
@Getter
@Setter
@Entity
@Table(name = "albums")
public class AlbumEntity {

    /**
     * Unique identifier of the album.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Flag indicating whether album is visible and selectable.
     */
    @Column(name = "is_active")
    private Boolean isActive;

    /**
     * Human friendly album name.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Mode of the album lifecycle (draft or release).
     */
    @Enumerated(EnumType.STRING)
    private AlbumType type;

    /**
     * Virtual path of the album within the library.
     */
    private String path;

    /**
     * Workflow status of the album creation process.
     */
    @Enumerated(EnumType.STRING)
    private WorkflowStatus status;

    /**
     * Resources that belong to the album.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "album_resources",
            joinColumns = @JoinColumn(name = "album_id"),
            inverseJoinColumns = @JoinColumn(name = "resource_id"))
    private Set<ResourceEntity> resources = new HashSet<>();

    /**
     * Creation timestamp stored as epoch millis UTC.
     */
    private Long createdAt;

    /**
     * Optional description of the album content.
     */
    @Column(length = 2048)
    private String description;

    /**
     * Resource used as album wallpaper.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallpaper_id")
    private ResourceEntity wallpaper;

    /**
     * Resource used as preview (audio track).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "view_id")
    private ResourceEntity view;
}
