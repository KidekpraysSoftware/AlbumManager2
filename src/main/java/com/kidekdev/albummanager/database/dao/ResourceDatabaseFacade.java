package com.kidekdev.albummanager.database.dao;

import com.kidekdev.albummanager.common.OperationResult;
import com.kidekdev.albummanager.database.dto.ResourceDto;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ResourceDatabaseFacade {

    OperationResult save(ResourceDto dto);

    ResourceDto getById(UUID id);

    List<ResourceDto> getAllById(Collection<UUID> ids);

    OperationResult update(ResourceDto dto);

    OperationResult deactivate(UUID id);

    OperationResult delete(UUID id);

    ResourceDto findByHash(String hash);

    List<ResourceDto> findAllByHash(Collection<String> hashList);

    OperationResult updateResourceOrdering(List<UUID> orderedIds); //Use OrderingUtils.updateSourceOrdering()

    OperationResult isExist(String resourceFileHash); //если true, то написать в message "Такой ресурс уже есть: <Автор - Название>"
}
