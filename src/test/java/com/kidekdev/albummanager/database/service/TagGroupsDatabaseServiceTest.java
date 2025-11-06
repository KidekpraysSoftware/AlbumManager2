package com.kidekdev.albummanager.database.service;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TagGroupsDatabaseServiceTest extends BaseDatabaseServiceTest {

    @Test
    void getAllTagGroupsReflectsExampleDatabase() {
        Map<String, List<String>> tagGroups = databaseService.getAllTagGroups();

        assertThat(tagGroups).containsKeys("Применение", "Темы", "Настроения", "Сезоны", "Язык", "Жанры");
        assertThat(tagGroups.get("Темы")).containsExactlyInAnyOrder("Космос", "Лес");
        assertThat(tagGroups.get("Настроения")).containsExactlyInAnyOrder("Грусть", "Радость");
    }

    @Test
    void getTagsByGroupNameReturnsTagsForKnownGroup() {
        List<String> tags = databaseService.getTagsByGroupName("Настроения");

        assertThat(tags).containsExactlyInAnyOrder("Грусть", "Радость");
        assertThat(databaseService.getTagsByGroupName(null)).isEmpty();
    }

    @Test
    void getGroupNameByTagResolvesGroup() {
        assertThat(databaseService.getGroupNameByTag("Инструментал")).isEqualTo("Язык");
        assertThat(databaseService.getGroupNameByTag("Неизвестный тег")).isNull();
    }
}
