package com.kidekdev.albummanager.database.entity;

import com.kidekdev.albummanager.database.model.common.ResourceType;
import com.kidekdev.albummanager.database.model.view.SortBy;
import com.kidekdev.albummanager.database.model.view.SortOrder;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
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
 * Saved filter configuration for selecting resources.
 */
@Getter
@Setter
@Entity
@Table(name = "views")
public class ViewEntity {

    /**
     * Unique identifier of the view.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Whether the view should be accessible in the UI.
     */
    @Column(name = "is_active")
    private Boolean isActive;

    /**
     * User provided view name.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Optional description explaining the selection.
     */
    @Column(length = 2048)
    private String description;

    /**
     * Included filter rules for tags, types, extensions and substrings.
     */
    @Embedded
    private ViewIncludeFilterEmbeddable included = new ViewIncludeFilterEmbeddable();

    /**
     * Excluded filter rules for tags, types, extensions and substrings.
     */
    @Embedded
    private ViewExcludeFilterEmbeddable excluded = new ViewExcludeFilterEmbeddable();

    /**
     * Allowed duration range in seconds.
     */
    @Embedded
    private DurationRangeEmbeddable durationRange = new DurationRangeEmbeddable();

    /**
     * Sorting options of the view.
     */
    @Embedded
    private ViewSortEmbeddable sort = new ViewSortEmbeddable();
}

/**
 * Filter fields reused for included conditions.
 */
@Getter
@Setter
@Embeddable
class ViewIncludeFilterEmbeddable {

    /**
     * Tags that must be present on the resource.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "view_included_tags", joinColumns = @JoinColumn(name = "view_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    /**
     * Allowed resource types.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "view_included_types", joinColumns = @JoinColumn(name = "view_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type")
    private Set<ResourceType> types = new HashSet<>();

    /**
     * Allowed file extensions.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "view_included_extensions", joinColumns = @JoinColumn(name = "view_id"))
    @Column(name = "extension")
    private Set<String> extensions = new HashSet<>();

    /**
     * Substrings expected to be present in the resource name.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "view_included_substrings", joinColumns = @JoinColumn(name = "view_id"))
    @Column(name = "substring")
    private Set<String> nameSubstrings = new HashSet<>();
}

/**
 * Filter fields reused for excluded conditions.
 */
@Getter
@Setter
@Embeddable
class ViewExcludeFilterEmbeddable {

    /**
     * Tags that must be absent in the resource.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "view_excluded_tags", joinColumns = @JoinColumn(name = "view_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    /**
     * Resource types to filter out.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "view_excluded_types", joinColumns = @JoinColumn(name = "view_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type")
    private Set<ResourceType> types = new HashSet<>();

    /**
     * Extensions to exclude.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "view_excluded_extensions", joinColumns = @JoinColumn(name = "view_id"))
    @Column(name = "extension")
    private Set<String> extensions = new HashSet<>();

    /**
     * Name substrings that must not be present.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "view_excluded_substrings", joinColumns = @JoinColumn(name = "view_id"))
    @Column(name = "substring")
    private Set<String> nameSubstrings = new HashSet<>();
}

/**
 * Stores minimal and maximal duration in seconds.
 */
@Getter
@Setter
@Embeddable
class DurationRangeEmbeddable {
    /**
     * Minimal track duration in seconds.
     */
    private Double min;

    /**
     * Maximal track duration in seconds.
     */
    private Double max;
}

/**
 * Sorting preferences of the view.
 */
@Getter
@Setter
@Embeddable
class ViewSortEmbeddable {

    /**
     * Field used for ordering results.
     */
    @Enumerated(EnumType.STRING)
    private SortBy by;

    /**
     * Direction of the sorting.
     */
    @Enumerated(EnumType.STRING)
    private SortOrder order;
}
