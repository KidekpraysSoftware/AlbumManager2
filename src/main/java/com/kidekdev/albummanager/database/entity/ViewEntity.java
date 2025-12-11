package com.kidekdev.albummanager.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Saved filter configuration for selecting resources.
 */
@Getter
@Setter
@Entity
@Table(name = "view")
public class ViewEntity {

    /**
     * Unique identifier of the view.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Whether the view should be accessible in the UI.
     */
    @Column(name = "is_active")
    private Boolean isActive;

    /**
     * User provided view name.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Optional description explaining the selection.
     */
    @Column(length = 2048)
    private String description;

}