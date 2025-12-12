package com.kidekdev.albummanager.database.mapper;

import com.kidekdev.albummanager.database.dto.DynamicResourceDto;
import com.kidekdev.albummanager.database.entity.DynamicResourceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DynamicResourceMapper {

    DynamicResourceMapper INSTANCE = Mappers.getMapper(DynamicResourceMapper.class);

    DynamicResourceDto toDto(DynamicResourceEntity entity);

    DynamicResourceEntity toEntity(DynamicResourceDto dto);
}
