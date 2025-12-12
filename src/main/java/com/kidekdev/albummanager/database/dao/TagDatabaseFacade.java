package com.kidekdev.albummanager.database.dao;

import com.kidekdev.albummanager.common.OperationResult;
import com.kidekdev.albummanager.database.dto.TagDto;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface TagDatabaseFacade {

    OperationResult saveTag(TagDto dto);

    OperationResult saveAllTags(Collection<TagDto> dtos);

    TagDto findById(UUID id);
    TagDto findByName(String tagName);
    List<TagDto> findAllTags();

    List<TagDto> findAllByNames(Collection<String> names);

    List<TagDto> findAllByGroup(String groupName);

    OperationResult renameTag(String oldName, String newName);

    OperationResult updateTag(TagDto dto);
    OperationResult deleteTagByName(String tagName); //тег так же удалить из всех ResourceEntity
    OperationResult deleteTagById(UUID id); //тег так же удалить из всех ResourceEntity
}
