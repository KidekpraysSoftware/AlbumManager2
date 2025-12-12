package com.kidekdev.albummanager.database.dao;

import com.kidekdev.albummanager.common.OperationResult;
import com.kidekdev.albummanager.database.dto.ResourceDto;

import java.util.List;
import java.util.UUID;

public interface ResourceDatabaseFacade {

    OperationResult save(ResourceDto entity);

    ResourceDto getById(UUID entity);

    List<ResourceDto> getAllById(List<UUID> entityList);

    OperationResult update(ResourceDto entity);

    OperationResult deactivate(UUID entity);

    OperationResult delete(UUID entity);

    ResourceDto findByHash(String hash);

    List<ResourceDto> findAllByHash(List<String> hashList);

}
