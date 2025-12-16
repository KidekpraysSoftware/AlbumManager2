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
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tag")
public class TagEntity {

    @Id
    @GeneratedValue
    protected UUID id;

    @Column(name = "name", unique = true, nullable = false)
    String name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "group_id")
    private TagGroupEntity group;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    protected OffsetDateTime createdAt;
}
