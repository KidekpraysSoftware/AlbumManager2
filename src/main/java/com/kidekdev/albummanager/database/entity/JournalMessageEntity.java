package com.kidekdev.albummanager.database.entity;

import com.kidekdev.albummanager.database.type.JournalMessageType;
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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "journal_message")
public class JournalMessageEntity {

    /**
     * Surrogate identifier used by Hibernate.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * Epoch millis of the entry creation.
     */
    @CreationTimestamp
    @Column(name = "created_at")
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
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    LinkedHashSet<UUID> attachments;

    /**
     * Timestamp of another entry that this one comments on.
     */
    @CreationTimestamp
    private OffsetDateTime commentOn;

}
