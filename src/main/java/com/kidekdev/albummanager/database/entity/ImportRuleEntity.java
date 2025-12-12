package com.kidekdev.albummanager.database.entity;

import com.kidekdev.albummanager.database.type.ResourceType;
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

import java.time.OffsetDateTime;
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
    @CreationTimestamp
    @Column(name = "imported_at")
    private OffsetDateTime importedAt;

    /**
     * Resource type that should be imported from the path.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type")
    private ResourceType resourceType;

    /**
     * Flag indicating if the rule is active for scanning.
     */
    @Column(name = "is_active")
    private Boolean isActive;
}
