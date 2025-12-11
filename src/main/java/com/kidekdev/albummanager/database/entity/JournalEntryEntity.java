package com.kidekdev.albummanager.database.entity;

import com.kidekdev.albummanager.database.model.journal.JournalMessageType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Single journal record connected to the parent {@link JournalEntity}.
 */
@Getter
@Setter
@Entity
@Table(name = "journal_entries")
public class JournalEntryEntity {

    /**
     * Surrogate identifier used by Hibernate.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * Epoch millis of the entry creation.
     */
    private Long createdAt;

    /**
     * Type of the journal message (user or system).
     */
    @Enumerated(EnumType.STRING)
    private JournalMessageType type;

    /**
     * Main text of the journal record.
     */
    @Column(length = 4096)
    private String text;

    /**
     * Attachments associated with the message.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "journal_entry_attachments", joinColumns = @JoinColumn(name = "entry_id"))
    @Column(name = "attachment_id")
    private Set<UUID> attachments = new HashSet<>();

    /**
     * Timestamp of another entry that this one comments on.
     */
    private Long commentOn;

    /**
     * Parent journal this entry belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journal_id")
    private JournalEntity journal;
}
