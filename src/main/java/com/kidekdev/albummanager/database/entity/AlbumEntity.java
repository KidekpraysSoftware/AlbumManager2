package com.kidekdev.albummanager.database.entity;

import com.kidekdev.albummanager.database.type.WorkflowStatus;
import com.kidekdev.albummanager.database.type.AlbumType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.LinkedHashSet;
import java.util.UUID;

/**
 * Album entity containing virtual grouping of resources.
 */
@Getter
@Setter
@Entity
@Table(name = "album")
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

    @CreationTimestamp
    @Column(name = "created_at")
    private Long createdAt;

    /**
     * Optional description of the album content.
     */
    @Column(length = 2048)
    private String description;

    /**
     * Resource used as album wallpaper.
     */
    private UUID wallpaper;

    /**
     * Resource used as preview (audio track).
     */
    private UUID view;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    LinkedHashSet<UUID> items;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    LinkedHashSet<UUID> journal;

}
