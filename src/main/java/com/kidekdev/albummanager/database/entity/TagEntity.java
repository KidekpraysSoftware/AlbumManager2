package com.kidekdev.albummanager.database.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "tags")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TagEntity {

    @Id
    @GeneratedValue
    protected UUID id;

    @Column(name = "name", unique = true, nullable = false)
    String name;

    @Column(name = "tag_group", nullable = false)
    String tagGroup;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    protected OffsetDateTime createdAt;

    public TagEntity(String name, String tagGroup) {
        this.name = name;
        this.tagGroup = tagGroup;
    }
}
