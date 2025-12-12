package com.kidekdev.albummanager.database.mapper;

import com.kidekdev.albummanager.database.dto.ImportRuleDto;
import com.kidekdev.albummanager.database.entity.ImportRuleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ImportRuleMapper {

    ImportRuleMapper INSTANCE = Mappers.getMapper(ImportRuleMapper.class);

    ImportRuleDto toDto(ImportRuleEntity entity);

    ImportRuleEntity toEntity(ImportRuleDto dto);
}
