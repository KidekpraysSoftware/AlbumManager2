package com.kidekdev.albummanager.database.entity;

import com.kidekdev.albummanager.database.model.common.ResourceType;
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
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Auto-import rule describing which file types are scanned inside a folder.
 */
@Getter
@Setter
@Entity
@Table(name = "import_rule")
public class ImportRuleEntity {

    /**
     * Unique identifier of the import rule.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Absolute path of the scanned directory.
     */
    @Column(nullable = false)
    private String path;

    /**
     * First time this rule was executed (epoch millis UTC).
     */
    private Long importedAt;

    /**
     * Resource type that should be imported from the path.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type")
    private ResourceType resourceType;
}
