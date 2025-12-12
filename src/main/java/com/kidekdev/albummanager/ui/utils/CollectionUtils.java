package com.kidekdev.albummanager.ui.utils;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CollectionUtils {

    public static List<Path> findNotFoundedResources(
            Map<String, Path> resourceCache,
            List<String> foundedHashes
    ) {
        if (resourceCache == null || resourceCache.isEmpty()) {
            return List.of();
        }

        if (foundedHashes == null || foundedHashes.isEmpty()) {
            return List.copyOf(resourceCache.values());
        }

        Set<String> founded = Set.copyOf(foundedHashes);

        return resourceCache.entrySet().stream()
                .filter(e -> !founded.contains(e.getKey()))
                .map(Map.Entry::getValue)
                .toList();
    }
}
