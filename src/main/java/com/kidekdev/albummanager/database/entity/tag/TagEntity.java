package com.kidekdev.albummanager.database.entity.tag;

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

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    protected OffsetDateTime createdAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "group_id")
    private TagGroupEntity group;

    public TagEntity(String name, TagGroupEntity group) {
        this.name = name;
        this.group = group;
    }
}
