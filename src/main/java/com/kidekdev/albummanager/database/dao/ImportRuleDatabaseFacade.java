package com.kidekdev.albummanager.database.dao;

import com.kidekdev.albummanager.common.OperationResult;
import com.kidekdev.albummanager.database.dto.ImportRuleDto;

import java.util.List;
import java.util.UUID;

public interface ImportRuleDatabaseFacade {

    OperationResult save(ImportRuleDto dto);

    List<ImportRuleDto> findAll();

    OperationResult deactivate(UUID id);
    OperationResult delete(UUID id);

}
