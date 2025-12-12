package com.kidekdev.albummanager.database.dao;

import com.kidekdev.albummanager.common.OperationResult;
import com.kidekdev.albummanager.database.dto.TagDto;
import com.kidekdev.albummanager.database.entity.TagEntity;
import com.kidekdev.albummanager.database.mapper.TagMapper;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class TagDatabaseFacadeImplTest {

    private static SessionFactory sessionFactory;
    private static TagDatabaseFacadeImpl facade;

    @BeforeAll
    static void setUp() {
        sessionFactory = new Configuration()
                .configure("hibernate-test.cfg.xml")
                .addAnnotatedClass(TagEntity.class)
                .buildSessionFactory();
        facade = new TagDatabaseFacadeImpl(sessionFactory, TagMapper.INSTANCE);
    }

    @AfterAll
    static void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @Test
    void saveAndFindByNameShouldPersistAndReturnDto() {
        TagDto dto = TagDto.builder()
                .name("tag-1")
                .group("group-1")
                .build();

        OperationResult result = facade.save(dto);
        assertThat(result.isSuccess())
                .withFailMessage(result.message())
                .isTrue();

        TagDto loaded = facade.findByName("tag-1");
        assertThat(loaded)
                .withFailMessage("Saved tag not found: %s".formatted(result.message()))
                .isNotNull();
        assertThat(loaded.name()).isEqualTo("tag-1");
        assertThat(loaded.group()).isEqualTo("group-1");
        assertThat(loaded.id()).isNotNull();
    }

    @Test
    void updateShouldModifyExistingEntity() {
        TagDto original = TagDto.builder()
                .name("tag-2")
                .group("group-2")
                .build();
        facade.save(original);
        TagDto persisted = facade.findByName("tag-2");

        assertThat(persisted)
                .withFailMessage("Tag not persisted for update")
                .isNotNull();

        TagDto updated = persisted.toBuilder()
                .group("group-2-updated")
                .build();
        OperationResult updateResult = facade.update(updated);
        assertThat(updateResult.isSuccess()).isTrue();

        TagDto reloaded = facade.getById(persisted.id());
        assertThat(reloaded.group()).isEqualTo("group-2-updated");
        assertThat(reloaded.name()).isEqualTo(persisted.name());
    }

    @Test
    void deleteShouldRemoveEntity() {
        TagDto dto = TagDto.builder()
                .name("tag-3")
                .group("group-3")
                .build();
        facade.save(dto);
        TagDto persisted = facade.findByName("tag-3");

        assertThat(persisted)
                .withFailMessage("Tag not persisted for delete")
                .isNotNull();

        OperationResult deleteResult = facade.delete(persisted.id());
        assertThat(deleteResult.isSuccess())
                .withFailMessage(deleteResult.message())
                .isTrue();

        TagDto shouldBeNull = facade.findByName("tag-3");
        assertThat(shouldBeNull).isNull();
    }

    @Test
    void getAllShouldReturnAllPersistedEntities() {
        TagDto first = TagDto.builder().name("tag-4").group("group-4").build();
        TagDto second = TagDto.builder().name("tag-5").group("group-5").build();
        facade.save(first);
        facade.save(second);

        List<TagDto> results = facade.getAll();
        assertThat(results)
                .extracting(TagDto::name)
                .contains("tag-4", "tag-5");
    }

    @Test
    void getByIdShouldReturnExactMatch() {
        TagDto dto = TagDto.builder()
                .name("tag-6")
                .group("group-6")
                .build();
        facade.save(dto);
        TagDto persisted = facade.findByName("tag-6");

        assertThat(persisted)
                .withFailMessage("Tag not persisted for getById")
                .isNotNull();

        TagDto loaded = facade.getById(persisted.id());
        assertThat(loaded)
                .extracting(TagDto::id, TagDto::name, TagDto::group)
                .containsExactly(persisted.id(), persisted.name(), persisted.group());
    }
}
