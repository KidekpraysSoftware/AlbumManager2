package com.kidekdev.albummanager.database.dao;

import com.kidekdev.albummanager.common.OperationResult;
import com.kidekdev.albummanager.database.dao.impl.ResourceDatabaseFacadeImpl;
import com.kidekdev.albummanager.database.dto.ResourceDto;
import com.kidekdev.albummanager.database.entity.ResourceEntity;
import com.kidekdev.albummanager.database.entity.TagEntity;
import com.kidekdev.albummanager.database.entity.TagGroupEntity;
import com.kidekdev.albummanager.database.mapper.ResourceMapper;
import com.kidekdev.albummanager.database.type.ResourceExtension;
import com.kidekdev.albummanager.database.type.ResourceType;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceDatabaseFacadeImplTest {

    private static SessionFactory sessionFactory;
    private static ResourceDatabaseFacadeImpl facade;

    @BeforeAll
    static void setUp() {
        sessionFactory = new Configuration()
                .configure("hibernate-test.cfg.xml")
                .addAnnotatedClass(ResourceEntity.class)
                .addAnnotatedClass(TagEntity.class)
                .addAnnotatedClass(TagGroupEntity.class)
                .buildSessionFactory();
        facade = new ResourceDatabaseFacadeImpl(sessionFactory, ResourceMapper.INSTANCE);
    }

    @AfterAll
    static void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @Test
    void saveAndFindByHashShouldPersistAndReturnDto() {
        ResourceDto dto = sampleDtoBuilder()
                .hash("hash-1")
                .build();

        OperationResult result = facade.save(dto);
        assertThat(result.isSuccess())
                .withFailMessage(result.message())
                .isTrue();

        ResourceDto loaded = facade.findByHash("hash-1");
        assertThat(loaded)
                .withFailMessage("Saved resource not found: %s".formatted(result.message()))
                .isNotNull();
        assertThat(loaded.hash()).isEqualTo("hash-1");
        assertThat(loaded.resourceName()).isEqualTo(dto.resourceName());
        assertThat(loaded.id()).isNotNull();
    }

    @Test
    void getByIdShouldReturnPersistedDto() {
        ResourceDto dto = sampleDtoBuilder()
                .hash("hash-get-1")
                .build();
        facade.save(dto);
        ResourceDto persisted = facade.findByHash("hash-get-1");

        ResourceDto loaded = facade.getById(persisted.id());

        assertThat(loaded).isNotNull();
        assertThat(loaded.id()).isEqualTo(persisted.id());
        assertThat(loaded.hash()).isEqualTo("hash-get-1");
    }

    @Test
    void getByIdShouldReturnNullWhenMissing() {
        ResourceDto loaded = facade.getById(UUID.randomUUID());

        assertThat(loaded).isNull();
    }

    @Test
    void updateShouldModifyExistingEntity() {
        ResourceDto original = sampleDtoBuilder()
                .hash("hash-2")
                .description("Original")
                .build();
        facade.save(original);
        ResourceDto persisted = facade.findByHash("hash-2");

        assertThat(persisted)
                .withFailMessage("Resource not persisted for update")
                .isNotNull();

        ResourceDto updated = persisted.toBuilder()
                .description("Updated description")
                .build();
        OperationResult updateResult = facade.update(updated);
        assertThat(updateResult.isSuccess()).isTrue();

        ResourceDto reloaded = facade.findByHash("hash-2");
        assertThat(reloaded.description()).isEqualTo("Updated description");
    }

    @Test
    void deactivateShouldSetActiveFlagToFalse() {
        ResourceDto dto = sampleDtoBuilder()
                .hash("hash-3")
                .isActive(true)
                .build();
        facade.save(dto);
        ResourceDto persisted = facade.findByHash("hash-3");

        assertThat(persisted)
                .withFailMessage("Resource not persisted for deactivate")
                .isNotNull();

        OperationResult result = facade.deactivate(persisted.id());
        assertThat(result.isSuccess())
                .withFailMessage(result.message())
                .isTrue();

        ResourceDto reloaded = facade.findByHash("hash-3");
        assertThat(reloaded.isActive()).isFalse();
    }

    @Test
    void deleteShouldRemoveEntity() {
        ResourceDto dto = sampleDtoBuilder()
                .hash("hash-4")
                .build();
        facade.save(dto);
        ResourceDto persisted = facade.findByHash("hash-4");

        assertThat(persisted)
                .withFailMessage("Resource not persisted for delete")
                .isNotNull();

        OperationResult deleteResult = facade.delete(persisted.id());
        assertThat(deleteResult.isSuccess())
                .withFailMessage(deleteResult.message())
                .isTrue();

        ResourceDto shouldBeNull = facade.findByHash("hash-4");
        assertThat(shouldBeNull).isNull();
    }

    @Test
    void findAllByHashShouldReturnMatchingDtos() {
        ResourceDto first = sampleDtoBuilder().hash("hash-5").build();
        ResourceDto second = sampleDtoBuilder().hash("hash-6").build();
        facade.save(first);
        facade.save(second);

        List<ResourceDto> results = facade.findAllByHash(List.of("hash-5", "hash-6"));
        assertThat(results)
                .extracting(ResourceDto::hash)
                .containsExactlyInAnyOrder("hash-5", "hash-6");
    }

    @Test
    void getAllByIdShouldReturnDtosForExistingIds() {
        ResourceDto first = sampleDtoBuilder().hash("hash-all-1").build();
        ResourceDto second = sampleDtoBuilder().hash("hash-all-2").build();

        facade.save(first);
        facade.save(second);

        List<UUID> ids = facade.findAllByHash(List.of("hash-all-1", "hash-all-2"))
                .stream()
                .map(ResourceDto::id)
                .toList();

        List<ResourceDto> loaded = facade.getAllById(ids);

        assertThat(loaded)
                .hasSize(2)
                .extracting(ResourceDto::hash)
                .containsExactlyInAnyOrder("hash-all-1", "hash-all-2");
    }

    @Test
    void getAllByIdShouldReturnEmptyListForEmptyInput() {
        List<ResourceDto> loaded = facade.getAllById(List.of());

        assertThat(loaded).isEmpty();
    }

    private ResourceDto.ResourceDtoBuilder sampleDtoBuilder() {
        return ResourceDto.builder()
                .resourceName("Test Resource")
                .authorName("Tester")
                .isActive(true)
                .path("/tmp/test")
                .isDynamic(false)
                .hash(UUID.randomUUID().toString())
                .resourceType(ResourceType.TRACK)
                .extension(ResourceExtension.MP3)
                .description("desc")
                .importedAt(OffsetDateTime.now())
                .fileCreationTime(OffsetDateTime.now())
                .tags(new LinkedHashSet<>())
                .journal(new LinkedHashSet<>());
    }
}
