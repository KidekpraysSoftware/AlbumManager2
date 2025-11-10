package com.kidekdev.albummanager.database.facade;

import com.kidekdev.albummanager.common.OperationResult;
import com.kidekdev.albummanager.database.model.DataBase;
import com.kidekdev.albummanager.database.model.album.AlbumDto;
import com.kidekdev.albummanager.database.model.autoimport.ImportDto;
import com.kidekdev.albummanager.database.model.folder.FolderDto;
import com.kidekdev.albummanager.database.model.journal.JournalDto;
import com.kidekdev.albummanager.database.model.journal.JournalMessageDto;
import com.kidekdev.albummanager.database.model.journal.JournalMessageType;
import com.kidekdev.albummanager.database.model.project.ProjectDto;
import com.kidekdev.albummanager.database.model.resource.ResourceDto;
import com.kidekdev.albummanager.database.model.tag.GlobalTagGroupsDto;
import com.kidekdev.albummanager.database.model.view.ViewDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseFacadeImplTest {

    private DataBase dataBase;
    private DatabaseFacadeImpl facade;

    @BeforeEach
    void setUp() {
        dataBase = DataBase.builder().build();
        facade = new DatabaseFacadeImpl(dataBase);
    }

    @Test
    void getAllTagGroupsGroupsTagsByGroupName() {
        Map<String, String> source = new LinkedHashMap<>();
        source.put("tag-1", "group-A");
        source.put("tag-2", "group-A");
        source.put("tag-3", "group-B");
        dataBase.globalTagGroups().setTagGroups(source);
        dataBase.globalTagGroups().addTagGroup("ignored", null);

        Map<String, List<String>> groups = facade.getAllTagGroups();

        assertThat(groups).containsOnlyKeys("group-A", "group-B");
        assertThat(groups.get("group-A")).containsExactlyInAnyOrder("tag-1", "tag-2");
        assertThat(groups.get("group-B")).containsExactly("tag-3");
    }

    @Test
    void getTagsByGroupNameReturnsTagsOrEmptyList() {
        Map<String, String> source = new LinkedHashMap<>();
        source.put("tag-1", "group-A");
        source.put("tag-2", "group-B");
        dataBase.globalTagGroups().setTagGroups(source);

        assertThat(facade.getTagsByGroupName("group-A")).containsExactlyInAnyOrder("tag-1");
        assertThat(facade.getTagsByGroupName("group-B")).containsExactlyInAnyOrder("tag-2");
        assertThat(facade.getTagsByGroupName("missing")).isEmpty();
        assertThat(facade.getTagsByGroupName(null)).isEmpty();
    }

    @Test
    void getGroupNameByTagReturnsGroupOrNull() {
        Map<String, String> source = new LinkedHashMap<>();
        source.put("tag-1", "group-A");
        dataBase.globalTagGroups().setTagGroups(source);

        assertThat(facade.getGroupNameByTag("tag-1")).isEqualTo("group-A");
        assertThat(facade.getGroupNameByTag("missing")).isNull();
        assertThat(facade.getGroupNameByTag(null)).isNull();
    }

    @Test
    void addNewMessageCreatesEntryAndReturnsTimestamp() {
        UUID journalId = UUID.randomUUID();
        JournalDto journal = new JournalDto();
        journal.setId(journalId);
        journal.setEntries(new LinkedHashMap<>());
        dataBase.journals().put(journalId, journal);

        JournalMessageDto message = new JournalMessageDto(JournalMessageType.USER, "text", List.of(), null);

        OperationResult result = facade.addNewMessage(journalId, message);

        assertThat(result.isSuccess()).isTrue();
        assertThat(journal.getEntries()).hasSize(1);
        assertThat(journal.getEntries()).containsEntry(result.message(), message);
    }

    @Test
    void addNewMessageFailsWhenJournalMissing() {
        OperationResult result = facade.addNewMessage(UUID.randomUUID(), new JournalMessageDto());

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.message()).isEqualTo("Журнал не найден");
    }

    @Test
    void deleteMessageRemovesExistingEntry() {
        UUID journalId = UUID.randomUUID();
        JournalDto journal = new JournalDto();
        journal.setId(journalId);
        journal.setEntries(new LinkedHashMap<>());
        journal.getEntries().put("123", new JournalMessageDto());
        dataBase.journals().put(journalId, journal);

        OperationResult result = facade.deleteMessage(journalId, 123L);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.message()).isEqualTo("Сообщение удалено");
        assertThat(journal.getEntries()).isEmpty();
    }

    @Test
    void deleteMessageReturnsFailureWhenEntryMissing() {
        UUID journalId = UUID.randomUUID();
        JournalDto journal = new JournalDto();
        journal.setId(journalId);
        journal.setEntries(new LinkedHashMap<>());
        dataBase.journals().put(journalId, journal);

        OperationResult result = facade.deleteMessage(journalId, 456L);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.message()).isEqualTo("Сообщение не найдено");
    }

    @Test
    void editMessageUpdatesExistingEntry() {
        UUID journalId = UUID.randomUUID();
        JournalDto journal = new JournalDto();
        journal.setId(journalId);
        journal.setEntries(new LinkedHashMap<>());
        journal.getEntries().put("789", new JournalMessageDto(JournalMessageType.SYSTEM, "old", List.of(), null));
        dataBase.journals().put(journalId, journal);

        JournalMessageDto updated = new JournalMessageDto(JournalMessageType.USER, "new", List.of(), null);

        OperationResult result = facade.editMessage(journalId, 789L, updated);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.message()).isEqualTo("Сообщение обновлено");
        assertThat(journal.getEntries()).containsEntry("789", updated);
    }

    @Test
    void editMessageFailsWhenJournalMissing() {
        OperationResult result = facade.editMessage(UUID.randomUUID(), 1L, new JournalMessageDto());

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.message()).isEqualTo("Журнал не найден");
    }

    @Test
    void saveAlbumStoresEntity() {
        UUID albumId = UUID.randomUUID();
        AlbumDto album = new AlbumDto();
        album.setId(albumId);

        OperationResult result = facade.saveAlbum(album);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.message()).isEqualTo("Альбом сохранен");
        assertThat(dataBase.albums()).containsEntry(albumId, album);
    }

    @Test
    void saveAlbumFailsWhenIdMissing() {
        AlbumDto album = new AlbumDto();

        OperationResult result = facade.saveAlbum(album);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.message()).isEqualTo("У сущности альбом отсутствует идентификатор");
    }

    @Test
    void saveDifferentEntitiesShareGenericValidation() {
        UUID folderId = UUID.randomUUID();
        FolderDto folder = new FolderDto();
        folder.setId(folderId);

        UUID importId = UUID.randomUUID();
        ImportDto importDto = new ImportDto();
        importDto.setId(importId);

        UUID projectId = UUID.randomUUID();
        ProjectDto project = new ProjectDto();
        project.setId(projectId);

        UUID resourceId = UUID.randomUUID();
        ResourceDto resource = new ResourceDto();
        resource.setId(resourceId);

        UUID viewId = UUID.randomUUID();
        ViewDto view = new ViewDto();
        view.setId(viewId);

        UUID journalId = UUID.randomUUID();
        JournalDto journal = new JournalDto();
        journal.setId(journalId);

        assertThat(facade.saveFolder(folder).isSuccess()).isTrue();
        assertThat(facade.saveImport(importDto).isSuccess()).isTrue();
        assertThat(facade.saveProject(project).isSuccess()).isTrue();
        assertThat(facade.saveResource(resource).isSuccess()).isTrue();
        assertThat(facade.saveView(view).isSuccess()).isTrue();
        assertThat(facade.saveJournal(journal).isSuccess()).isTrue();

        assertThat(dataBase.folders()).containsEntry(folderId, folder);
        assertThat(dataBase.imports()).containsEntry(importId, importDto);
        assertThat(dataBase.projects()).containsEntry(projectId, project);
        assertThat(dataBase.resources()).containsEntry(resourceId, resource);
        assertThat(dataBase.views()).containsEntry(viewId, view);
        assertThat(dataBase.journals()).containsEntry(journalId, journal);
    }

    @Test
    void saveGlobalTagGroupsUpdatesDatabase() {
        Map<String, String> source = new LinkedHashMap<>();
        source.put("tag-1", "group-A");
        GlobalTagGroupsDto dto = new GlobalTagGroupsDto(source);

        OperationResult result = facade.saveGlobalTagGroups(dto);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.message()).isEqualTo("Глобальные теги обновлены");
        assertThat(dataBase.globalTagGroups().getTagGroups()).containsEntry("tag-1", "group-A");
    }

    @Test
    void saveGlobalTagGroupsFailsWhenNull() {
        OperationResult result = facade.saveGlobalTagGroups(null);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.message()).isEqualTo("Теги не могут быть null");
    }
}

