package com.kidekdev.albummanager.database.dao;

import com.kidekdev.albummanager.common.OperationResult;
import com.kidekdev.albummanager.database.dto.DynamicResourceDto;

import java.util.List;
import java.util.UUID;

public interface DynamicResourceDatabaseFacade {

    OperationResult save(DynamicResourceDto dto);

    List<DynamicResourceDto> findAll();

    OperationResult deactivate(UUID id);
    OperationResult delete(UUID id);
}
