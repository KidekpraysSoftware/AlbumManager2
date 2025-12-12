package com.kidekdev.albummanager.database.dao;

import com.kidekdev.albummanager.common.OperationResult;
import com.kidekdev.albummanager.database.dao.impl.TagDatabaseFacadeImpl;
import com.kidekdev.albummanager.database.dto.ResourceDto;
import com.kidekdev.albummanager.database.dto.TagDto;
import com.kidekdev.albummanager.database.entity.ResourceEntity;
import com.kidekdev.albummanager.database.entity.TagEntity;
import com.kidekdev.albummanager.database.mapper.TagMapper;
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

class TagDatabaseFacadeImplTest {

    private static SessionFactory sessionFactory;
    private static TagDatabaseFacadeImpl facade;

    @BeforeAll
    static void setUp() {
        sessionFactory = new Configuration()
                .configure("hibernate-test.cfg.xml")
                .addAnnotatedClass(TagEntity.class)
                .addAnnotatedClass(ResourceEntity.class)
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
    void saveAndFindByNameShouldPersistTag() {
        TagDto tagDto = sampleTagBuilder()
                .name("rock")
                .build();

        OperationResult result = facade.saveTag(tagDto);
        assertThat(result.isSuccess())
                .withFailMessage(result.message())
                .isTrue();

        TagDto loaded = facade.findByName("rock");
        assertThat(loaded)
                .withFailMessage("Saved tag not found")
                .isNotNull();
        assertThat(loaded.name()).isEqualTo("rock");
        assertThat(loaded.id()).isNotNull();
    }

    @Test
    void findByIdShouldReturnPersistedTag() {
        TagDto tagDto = sampleTagBuilder()
                .name("jazz")
                .build();
        facade.saveTag(tagDto);

        TagDto persisted = facade.findByName("jazz");
        TagDto byId = facade.findById(persisted.id());

        assertThat(byId).isNotNull();
        assertThat(byId.id()).isEqualTo(persisted.id());
    }

    @Test
    void findAllTagsShouldReturnAllPersistedTags() {
        facade.saveAllTags(List.of(
                sampleTagBuilder().name("pop").build(),
                sampleTagBuilder().name("metal").build()
        ));

        List<TagDto> all = facade.findAllTags();
        assertThat(all)
                .extracting(TagDto::name)
                .contains("pop", "metal");
    }

    @Test
    void findAllByNamesShouldFilterTags() {
        facade.saveAllTags(List.of(
                sampleTagBuilder().name("hip-hop").build(),
                sampleTagBuilder().name("lofi").build()
        ));

        List<TagDto> filtered = facade.findAllByNames(List.of("hip-hop"));
        assertThat(filtered)
                .singleElement()
                .extracting(TagDto::name)
                .isEqualTo("hip-hop");
    }

    @Test
    void findAllByGroupShouldReturnMatchingTags() {
        facade.saveAllTags(List.of(
                sampleTagBuilder().name("piano").tagGroup("instrument").build(),
                sampleTagBuilder().name("guitar").tagGroup("instrument").build(),
                sampleTagBuilder().name("calm").tagGroup("mood").build()
        ));

        List<TagDto> instruments = facade.findAllByGroup("instrument");
        assertThat(instruments)
                .extracting(TagDto::name)
                .containsExactlyInAnyOrder("piano", "guitar");
    }

    @Test
    void renameTagShouldUpdateName() {
        facade.saveTag(sampleTagBuilder().name("electro").build());

        OperationResult result = facade.renameTag("electro", "edm");
        assertThat(result.isSuccess())
                .withFailMessage(result.message())
                .isTrue();

        TagDto renamed = facade.findByName("edm");
        assertThat(renamed).isNotNull();
        assertThat(renamed.name()).isEqualTo("edm");
    }

    @Test
    void updateTagShouldModifyExistingRecord() {
        facade.saveTag(sampleTagBuilder().name("mellow").tagGroup("vibe").build());
        TagDto persisted = facade.findByName("mellow");

        TagDto updated = persisted.toBuilder()
                .tagGroup("updated-vibe")
                .build();

        OperationResult updateResult = facade.updateTag(updated);
        assertThat(updateResult.isSuccess())
                .withFailMessage(updateResult.message())
                .isTrue();

        TagDto reloaded = facade.findById(persisted.id());
        assertThat(reloaded.tagGroup()).isEqualTo("updated-vibe");
    }

    @Test
    void deleteTagByNameShouldRemoveTagAndReferences() {
        facade.saveTag(sampleTagBuilder().name("tempo").build());
        TagDto tag = facade.findByName("tempo");

        persistResourceWithTag(tag.id());

        OperationResult result = facade.deleteTagByName("tempo");
        assertThat(result.isSuccess())
                .withFailMessage(result.message())
                .isTrue();

        TagDto shouldBeNull = facade.findByName("tempo");
        assertThat(shouldBeNull).isNull();
    }

    @Test
    void deleteTagByIdShouldRemoveTag() {
        facade.saveTag(sampleTagBuilder().name("oldie").build());
        TagDto tag = facade.findByName("oldie");

        OperationResult result = facade.deleteTagById(tag.id());
        assertThat(result.isSuccess())
                .withFailMessage(result.message())
                .isTrue();

        TagDto shouldBeNull = facade.findById(tag.id());
        assertThat(shouldBeNull).isNull();
    }

    @Test
    void saveAllTagsShouldHandleEmptyCollection() {
        OperationResult result = facade.saveAllTags(List.of());

        assertThat(result.isSuccess()).isTrue();
    }

    private TagDto.TagDtoBuilder sampleTagBuilder() {
        return TagDto.builder()
                .name("tag")
                .tagGroup("default");
    }

    private void persistResourceWithTag(UUID tagId) {
        ResourceDto dto = ResourceDto.builder()
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
                .tags(new LinkedHashSet<>(List.of(tagId)))
                .journal(new LinkedHashSet<>())
                .build();

        new com.kidekdev.albummanager.database.dao.impl.ResourceDatabaseFacadeImpl(sessionFactory, ResourceMapper.INSTANCE)
                .save(dto);
    }
}
