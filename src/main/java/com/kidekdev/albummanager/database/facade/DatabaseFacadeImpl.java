package com.kidekdev.albummanager.database.facade;

import com.kidekdev.albummanager.common.OperationResult;
import com.kidekdev.albummanager.database.model.DataBase;
import com.kidekdev.albummanager.database.model.album.AlbumDto;
import com.kidekdev.albummanager.database.model.autoimport.ImportDto;
import com.kidekdev.albummanager.database.model.folder.FolderDto;
import com.kidekdev.albummanager.database.model.journal.JournalDto;
import com.kidekdev.albummanager.database.model.journal.JournalMessageDto;
import com.kidekdev.albummanager.database.model.project.ProjectDto;
import com.kidekdev.albummanager.database.model.resource.ResourceDto;
import com.kidekdev.albummanager.database.model.tag.GlobalTagGroupsDto;
import com.kidekdev.albummanager.database.model.view.ViewDto;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

@RequiredArgsConstructor
public class DatabaseFacadeImpl implements DatabaseFacade {

    final DataBase dataBase;

    @Override
    public Map<String, List<String>> getAllTagGroups() {
        Map<String, List<String>> groups = new LinkedHashMap<>();

        dataBase.globalTagGroups().getTagGroups().forEach((tag, group) -> {
            if (group == null || group.isBlank() || tag == null) {
                return;
            }
            groups.computeIfAbsent(group, key -> new ArrayList<>()).add(tag);
        });

        groups.replaceAll((group, tags) -> List.copyOf(tags));

        return groups;
    }

    @Override
    public List<String> getTagsByGroupName(String groupName) {
        if (groupName == null || groupName.isBlank()) {
            return List.of();
        }

        List<String> tags = getAllTagGroups().get(groupName);
        return tags == null ? List.of() : tags;
    }

    @Override
    public String getGroupNameByTag(String tag) {
        if (tag == null || tag.isBlank()) {
            return null;
        }
        return dataBase.globalTagGroups().getTagGroups().get(tag);
    }

    @Override
    public OperationResult addNewMessage(UUID journalId, JournalMessageDto message) {
        if (journalId == null) {
            return failure("Не указан идентификатор журнала");
        }

        if (message == null) {
            return failure("Сообщение не может быть null");
        }

        JournalDto journal = dataBase.journals().get(journalId);
        if (journal == null) {
            return failure("Журнал не найден");
        }

        Map<String, JournalMessageDto> entries = journal.getEntries();
        if (entries == null) {
            entries = new LinkedHashMap<>();
            journal.setEntries(entries);
        }

        long timestamp = System.currentTimeMillis();
        String key = String.valueOf(timestamp);
        while (entries.containsKey(key)) {
            timestamp++;
            key = String.valueOf(timestamp);
        }

        entries.put(key, message);
        return success(key);
    }

    @Override
    public OperationResult deleteMessage(UUID journalId, Long messageTime) {
        if (journalId == null) {
            return failure("Не указан идентификатор журнала");
        }

        if (messageTime == null) {
            return failure("Время сообщения не указано");
        }

        JournalDto journal = dataBase.journals().get(journalId);
        if (journal == null) {
            return failure("Журнал не найден");
        }

        Map<String, JournalMessageDto> entries = journal.getEntries();
        if (entries == null || entries.isEmpty()) {
            return failure("Сообщение не найдено");
        }

        String key = String.valueOf(messageTime);
        JournalMessageDto removed = entries.remove(key);
        if (removed == null) {
            return failure("Сообщение не найдено");
        }

        return success("Сообщение удалено");
    }

    @Override
    public OperationResult editMessage(UUID journalId, Long messageTime, JournalMessageDto message) {
        if (journalId == null) {
            return failure("Не указан идентификатор журнала");
        }

        if (messageTime == null) {
            return failure("Время сообщения не указано");
        }

        if (message == null) {
            return failure("Сообщение не может быть null");
        }

        JournalDto journal = dataBase.journals().get(journalId);
        if (journal == null) {
            return failure("Журнал не найден");
        }

        Map<String, JournalMessageDto> entries = journal.getEntries();
        if (entries == null || !entries.containsKey(String.valueOf(messageTime))) {
            return failure("Сообщение не найдено");
        }

        entries.put(String.valueOf(messageTime), message);
        return success("Сообщение обновлено");
    }

    @Override
    public OperationResult saveAlbum(AlbumDto album) {
        return saveEntity(album, AlbumDto::getId, dataBase.albums(), "Альбом");
    }

    @Override
    public OperationResult saveFolder(FolderDto folder) {
        return saveEntity(folder, FolderDto::getId, dataBase.folders(), "Папка");
    }

    @Override
    public OperationResult saveImport(ImportDto importDto) {
        return saveEntity(importDto, ImportDto::getId, dataBase.imports(), "Импорт");
    }

    @Override
    public OperationResult saveJournal(JournalDto journal) {
        return saveEntity(journal, JournalDto::getId, dataBase.journals(), "Журнал");
    }

    @Override
    public OperationResult saveProject(ProjectDto project) {
        return saveEntity(project, ProjectDto::getId, dataBase.projects(), "Проект");
    }

    @Override
    public OperationResult saveResource(ResourceDto resource) {
        return saveEntity(resource, ResourceDto::getId, dataBase.resources(), "Ресурс");
    }

    @Override
    public OperationResult saveView(ViewDto view) {
        return saveEntity(view, ViewDto::getId, dataBase.views(), "Представление");
    }

    @Override
    public OperationResult saveGlobalTagGroups(GlobalTagGroupsDto tags) {
        if (tags == null) {
            return failure("Теги не могут быть null");
        }

        dataBase.globalTagGroups().setTagGroups(tags.getTagGroups());
        return success("Глобальные теги обновлены");
    }

    private <T> OperationResult saveEntity(
            T entity,
            Function<T, UUID> idExtractor,
            Map<UUID, T> storage,
            String entityName
    ) {
        if (entity == null) {
            return failure(entityName + " не может быть null");
        }

        UUID id = idExtractor.apply(entity);
        if (id == null) {
            return failure("У сущности %s отсутствует идентификатор".formatted(entityName.toLowerCase()));
        }

        storage.put(id, entity);
        return success(entityName + " сохранен");
    }

    private OperationResult success(String message) {
        return new OperationResult(true, Objects.requireNonNullElse(message, ""));
    }

    private OperationResult failure(String message) {
        return new OperationResult(false, Objects.requireNonNullElse(message, ""));
    }
}
