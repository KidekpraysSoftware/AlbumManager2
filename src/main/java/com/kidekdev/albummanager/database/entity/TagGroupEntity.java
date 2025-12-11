package com.kidekdev.albummanager.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents mapping between tag name and its group.
 */
@Getter
@Setter
@Entity
@Table(name = "tag_groups")
public class TagGroupEntity {

    /**
     * Surrogate identifier of the mapping row.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * Individual tag value from metadata.
     */
    @Column(nullable = false)
    private String tag;

    /**
     * Logical group the tag belongs to.
     */
    @Column(nullable = false)
    private String groupName;
}
