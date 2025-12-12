package com.kidekdev.albummanager.database.mapper;

import com.kidekdev.albummanager.database.dto.ResourceDto;
import com.kidekdev.albummanager.database.entity.ResourceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ResourceMapper {

    ResourceMapper INSTANCE = Mappers.getMapper(ResourceMapper.class);

    ResourceDto toDto(ResourceEntity entity);

    ResourceEntity toEntity(ResourceDto dto);
}
