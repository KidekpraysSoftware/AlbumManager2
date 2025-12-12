package com.kidekdev.albummanager.database.dao;

import com.kidekdev.albummanager.common.OperationResult;
import com.kidekdev.albummanager.database.dto.TagDto;

import java.util.List;
import java.util.UUID;

public interface TagDatabaseFacade {

    OperationResult save(TagDto entity);

    OperationResult update(TagDto entity);

    OperationResult delete(UUID id);

    TagDto getById(UUID id);

    List<TagDto> getAll();

    TagDto findByName(String name);
}
