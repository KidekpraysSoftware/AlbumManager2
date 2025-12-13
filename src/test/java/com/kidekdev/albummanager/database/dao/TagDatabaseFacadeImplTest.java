package com.kidekdev.albummanager.database.dao;

import com.kidekdev.albummanager.common.OperationResult;
import com.kidekdev.albummanager.database.dao.impl.TagDatabaseFacadeImpl;
import com.kidekdev.albummanager.database.dto.TagDto;
import com.kidekdev.albummanager.database.dto.TagGroupDto;
import com.kidekdev.albummanager.database.entity.TagEntity;
import com.kidekdev.albummanager.database.entity.TagGroupEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class TagDatabaseFacadeImplTest {

    private static SessionFactory sessionFactory;
    private static TagDatabaseFacadeImpl facade;

    @BeforeAll
    static void setUp() {
        sessionFactory = new Configuration()
                .configure("hibernate-test.cfg.xml")
                .addAnnotatedClass(TagGroupEntity.class)
                .addAnnotatedClass(TagEntity.class)
                .buildSessionFactory();
        facade = new TagDatabaseFacadeImpl(sessionFactory);
    }

    @AfterAll
    static void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @BeforeEach
    void clearDatabase() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createMutationQuery("delete from TagEntity").executeUpdate();
            session.createMutationQuery("delete from TagGroupEntity").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    void saveGroupShouldAssignInvertedOrdering() {
        TagGroupDto first = TagGroupDto.builder()
                .name("Group A")
                .tags(List.of(TagDto.builder().name("A1").build()))
                .build();
        TagGroupDto second = TagGroupDto.builder()
                .name("Group B")
                .tags(List.of(TagDto.builder().name("B1").build()))
                .build();

        OperationResult firstResult = facade.saveGroup(first);
        OperationResult secondResult = facade.saveGroup(second);

        assertThat(firstResult.isSuccess()).isTrue();
        assertThat(secondResult.isSuccess()).isTrue();

        Map<String, TagGroupDto> groupsByName = facade.findAllGroups().stream()
                .collect(Collectors.toMap(TagGroupDto::name, g -> g));

        assertThat(groupsByName.get("Group A").ordering()).isZero();
        assertThat(groupsByName.get("Group B").ordering()).isEqualTo(-1);

        List<String> orderedNames = facade.findAllGroups().stream()
                .map(TagGroupDto::name)
                .toList();

        assertThat(orderedNames).containsExactly("Group B", "Group A");
    }

    @Test
    void renameTagShouldUpdateName() {
        TagGroupDto group = TagGroupDto.builder()
                .name("Rename Group")
                .tags(List.of(TagDto.builder().name("OldName").build()))
                .build();
        facade.saveGroup(group);

        OperationResult renameResult = facade.renameTag("OldName", "NewName");
        assertThat(renameResult.isSuccess()).isTrue();

        TagDto renamed = facade.findByName("NewName");
        assertThat(renamed).isNotNull();
        assertThat(renamed.name()).isEqualTo("NewName");
    }

    @Test
    void updateGroupOrderingShouldRewriteOrderingValues() {
        TagGroupDto first = TagGroupDto.builder().name("First").build();
        TagGroupDto second = TagGroupDto.builder().name("Second").build();
        TagGroupDto third = TagGroupDto.builder().name("Third").build();
        facade.saveGroup(first);
        facade.saveGroup(second);
        facade.saveGroup(third);

        List<TagGroupDto> created = facade.findAllGroups();
        List<UUID> orderedIds = List.of(
                created.get(2).id(),
                created.get(1).id(),
                created.get(0).id()
        );

        OperationResult orderingResult = facade.updateGroupOrdering(orderedIds);
        assertThat(orderingResult.isSuccess()).isTrue();

        Map<UUID, Integer> updatedOrdering = facade.findAllGroups().stream()
                .collect(Collectors.toMap(TagGroupDto::id, TagGroupDto::ordering));

        assertThat(updatedOrdering.get(orderedIds.get(0))).isZero();
        assertThat(updatedOrdering.get(orderedIds.get(1))).isEqualTo(-1);
        assertThat(updatedOrdering.get(orderedIds.get(2))).isEqualTo(-2);
    }
}
