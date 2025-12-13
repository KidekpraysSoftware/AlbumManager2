package com.kidekdev.albummanager.database.mapper;

import com.kidekdev.albummanager.database.dto.ResourceDto;
import com.kidekdev.albummanager.database.dto.TagDto;
import com.kidekdev.albummanager.database.entity.ResourceEntity;
import com.kidekdev.albummanager.database.entity.TagEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ResourceMapper {

    ResourceMapper INSTANCE = Mappers.getMapper(ResourceMapper.class);

    @Mapping(target = "tagGroup", source = "group.name")
    TagDto toTagDto(TagEntity entity);

    @Mapping(target = "tags", source = "tags")
    ResourceDto toDto(ResourceEntity entity);

    @Mapping(target = "tags", ignore = true)
    ResourceEntity toEntity(ResourceDto dto);
}
