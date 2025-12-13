package com.kidekdev.albummanager.database;


import com.kidekdev.albummanager.database.entity.ResourceEntity;
import com.kidekdev.albummanager.database.entity.TagGroupEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderingUtils {

    public static void updateTagGroupOrdering(List<TagGroupEntity> entities, List<UUID> orderedIds) {
        if (entities.size() != orderedIds.size()) {
            throw new IllegalArgumentException("TagGroup size mismatch");
        }

        Map<UUID, TagGroupEntity> entityMap = entities.stream()
                .collect(Collectors.toMap(TagGroupEntity::getId, e -> e));

        int size = orderedIds.size();

        for (int i = 0; i < size; i++) {
            UUID id = orderedIds.get(i);
            TagGroupEntity entity = entityMap.get(id);

            if (entity == null) {
                throw new IllegalArgumentException("TagGroup not found: " + id);
            }

            // первый в UI → самый маленький ordering
            entity.setOrdering(-(size - i));
        }
    }

    public static void updateSourceOrdering(List<ResourceEntity> entities, List<UUID> orderedIds) {
        if (entities.size() != orderedIds.size()) {
            throw new IllegalArgumentException("TagGroup size mismatch");
        }

        Map<UUID, ResourceEntity> entityMap = entities.stream()
                .collect(Collectors.toMap(ResourceEntity::getId, e -> e));

        int size = orderedIds.size();

        for (int i = 0; i < size; i++) {
            UUID id = orderedIds.get(i);
            ResourceEntity entity = entityMap.get(id);

            if (entity == null) {
                throw new IllegalArgumentException("TagGroup not found: " + id);
            }

            // первый в UI → самый маленький ordering
            entity.setOrdering(-(size - i));
        }
    }

}
