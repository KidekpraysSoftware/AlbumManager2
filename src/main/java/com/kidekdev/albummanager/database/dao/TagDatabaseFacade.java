package com.kidekdev.albummanager.database.dao;

import com.kidekdev.albummanager.common.OperationResult;
import com.kidekdev.albummanager.database.dto.TagDto;
import com.kidekdev.albummanager.database.dto.TagGroupDto;

import java.util.*;

public interface TagDatabaseFacade {

    // ===== CREATE / UPDATE =====
    OperationResult saveGroup(TagGroupDto dto);
    Set<TagDto> mergeNewTags(Map<UUID, List<String>> tags);


    // ===== READ: single =====
    TagDto findTagById(UUID id);
    TagGroupDto findGroupById(UUID id);

    TagDto findByName(String tagName);

    // ===== READ: collections =====
    List<TagDto> findAllTags();
    List<TagGroupDto> findAllGroups();

    List<TagDto> findAllByNames(Collection<String> names);
    List<TagDto> findAllByGroupName(String groupName);

    // ===== UPDATE =====
    OperationResult renameTag(String oldName, String newName);
    OperationResult renameGroup(String oldName, String newName);
    OperationResult updateGroups(List<TagGroupDto> groups);
    OperationResult updateGroup(TagGroupDto group);

    OperationResult updateGroupOrdering(List<UUID> orderedIds); //Use OrderingUtils.updateTagGroupOrdering()

    // ===== DELETE =====
    OperationResult deleteTagById(UUID id);
    OperationResult deleteTagByName(String name);

    OperationResult deleteGroupById(UUID id);
    OperationResult deleteGroupByName(String name);
}
