package com.kidekdev.albummanager.database.loader;


import com.kidekdev.albummanager.database.model.tag.GlobalTagGroupsDto;

import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

/**
 * Container for the results produced by {@link DatabaseLoader}.
 */
public record DatabaseLoadResult(
        Map<Path, UUID> pathIndex,
        Map<UUID, Object> globalDatabase,
        GlobalTagGroupsDto globalTagGroups
) {
}

