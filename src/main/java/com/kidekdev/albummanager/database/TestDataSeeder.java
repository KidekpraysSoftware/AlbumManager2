package com.kidekdev.albummanager.database;

import com.kidekdev.albummanager.database.dao.TagDatabaseFacade;
import com.kidekdev.albummanager.database.dao.impl.TagDatabaseFacadeImpl;
import com.kidekdev.albummanager.database.dto.TagDto;
import com.kidekdev.albummanager.database.dto.TagGroupDto;

import java.util.List;

public class TestDataSeeder {

    private final TagDatabaseFacade tagDatabaseFacade;

    public TestDataSeeder() {
        this(new TagDatabaseFacadeImpl());
    }

    public TestDataSeeder(TagDatabaseFacade tagDatabaseFacade) {
        this.tagDatabaseFacade = tagDatabaseFacade;
    }

    public void fillTags() {
        List<TagGroupDto> groups = List.of(
                TagGroupDto.builder()
                        .name("Жанры")
                        .tags(createTags(List.of(
                                "Рок", "Поп", "Джаз", "Блюз", "Электроника",
                                "Хип-хоп", "Классика", "Фолк", "Металл", "Инди"
                        )))
                        .build(),
                TagGroupDto.builder()
                        .name("Настроение")
                        .tags(createTags(List.of(
                                "Энергичный", "Спокойный", "Грустный", "Вдохновляющий", "Романтичный",
                                "Меланхоличный", "Бодрый", "Драматичный", "Медитативный", "Мрачный"
                        )))
                        .build(),
                TagGroupDto.builder()
                        .name("Инструменты")
                        .tags(createTags(List.of(
                                "Гитара", "Фортепиано", "Ударные", "Синтезатор", "Бас",
                                "Струнные", "Духовые", "Вокал", "Перкуссия", "Медные"
                        )))
                        .build()
        );

        groups.forEach(tagDatabaseFacade::saveGroup);
    }

    private List<TagDto> createTags(List<String> names) {
        return names.stream()
                .map(name -> TagDto.builder().name(name).build())
                .toList();
    }
}
