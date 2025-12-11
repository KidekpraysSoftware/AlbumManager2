package com.kidekdev.albummanager.database.entity;

import com.kidekdev.albummanager.database.model.common.ResourceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Auto-import folder definition used for scanning project resources.
 */
@Getter
@Setter
@Entity
@Table(name = "folders")
public class FolderEntity {

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "view_id")
    private ResourceEntity view;
}
