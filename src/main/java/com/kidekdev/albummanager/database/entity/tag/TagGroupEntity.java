package com.kidekdev.albummanager.database.entity.tag;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TagGroupEntity {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false, unique = true)
    private int ordering;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<TagEntity> tags = new ArrayList<>();
}
