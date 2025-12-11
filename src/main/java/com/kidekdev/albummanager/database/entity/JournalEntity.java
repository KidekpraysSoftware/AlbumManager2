package com.kidekdev.albummanager.database.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Journal storing chronological messages linked to another entity.
 */
@Getter
@Setter
@Entity
@Table(name = "journals")
public class JournalEntity {

    /**
     * Unique identifier of the journal.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Whether new messages should still be appended.
     */
    @Column(name = "is_active")
    private Boolean isActive;

    /**
     * Identifier of the entity that owns the journal.
     */
    private UUID linkedBy;

    /**
     * Individual entries ordered by creation time.
     */
    @OneToMany(mappedBy = "journal", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<JournalEntryEntity> entries = new ArrayList<>();
}
