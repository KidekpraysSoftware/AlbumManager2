package com.kidekdev.albummanager.database.mapper;

import com.kidekdev.albummanager.database.dto.TagDto;
import com.kidekdev.albummanager.database.entity.TagEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TagMapper {

    TagMapper INSTANCE = Mappers.getMapper(TagMapper.class);

    TagDto toDto(TagEntity entity);

    TagEntity toEntity(TagDto dto);
}
