package com.kidekdev.albummanager.database.service;

import com.kidekdev.albummanager.database.loader.DatabaseLoadResult;
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

import java.util.*;
import java.util.function.Function;

/**
 * Basic in-memory implementation of {@link DatabaseService} backed by a {@link Map}.
 */
public class InMemoryDatabaseService implements DatabaseService {

    private final DataBase database;
    private final Map<String, String> groupByTag;

    public InMemoryDatabaseService(DatabaseLoadResult loadResult) {
        this(Objects.requireNonNull(loadResult, "loadResult").dataBase(), loadResult.globalTagGroups());
    }

    public InMemoryDatabaseService(DataBase initialState, GlobalTagGroupsDto tagGroups) {
        this.database = new DataBase();
        if (initialState != null) {
            this.database.merge(initialState);
        }
        this.groupByTag = new LinkedHashMap<>();
        if (tagGroups != null && tagGroups.getTagGroups() != null) {
            this.groupByTag.putAll(tagGroups.getTagGroups());
        }
    }

    @Override
    public Optional<?> getById(UUID id) {
        return database.findEntity(id);
    }

    @Override
    public List<ResourceDto> getAllTracks() {
        return List.copyOf(database.resources().values());
    }

    @Override
    public List<ProjectDto> getAllProjects() {
        return List.copyOf(database.projects().values());
    }

    @Override
    public List<AlbumDto> getAllAlbums() {
        return List.copyOf(database.albums().values());
    }

    @Override
    public List<FolderDto> getAllFolders() {
        return List.copyOf(database.folders().values());
    }

    @Override
    public List<ImportDto> getAllImports() {
        return List.copyOf(database.imports().values());
    }

    @Override
    public List<ViewDto> getAllViews() {
        return List.copyOf(database.views().values());
    }

    @Override
    public Map<String, List<String>> getAllTagGroups() {
        Map<String, List<String>> result = new LinkedHashMap<>();
        groupByTag.forEach((tag, group) ->
                result.computeIfAbsent(group, key -> new ArrayList<>()).add(tag)
        );
        result.replaceAll((group, tags) -> List.copyOf(tags));
        return Map.copyOf(result);
    }

    @Override
    public List<String> getTagsByGroupName(String groupName) {
        if (groupName == null) {
            return List.of();
        }
        List<String> tags = new ArrayList<>();
        groupByTag.forEach((tag, group) -> {
            if (groupName.equals(group)) {
                tags.add(tag);
            }
        });
        return List.copyOf(tags);
    }

    @Override
    public String getGroupNameByTag(String tag) {
        if (tag == null) {
            return null;
        }
        return groupByTag.get(tag);
    }

    @Override
    public Optional<ResourceDto> getTrackById(UUID id) {
        return getById(database.resources(), id);
    }

    @Override
    public Optional<ProjectDto> getProjectById(UUID id) {
        return getById(database.projects(), id);
    }

    @Override
    public Optional<AlbumDto> getAlbumById(UUID id) {
        return getById(database.albums(), id);
    }

    @Override
    public Optional<FolderDto> getFolderById(UUID id) {
        return getById(database.folders(), id);
    }

    @Override
    public Optional<ImportDto> getImportById(UUID id) {
        return getById(database.imports(), id);
    }

    @Override
    public Optional<ViewDto> getViewById(UUID id) {
        return getById(database.views(), id);
    }

    @Override
    public Optional<JournalDto> getJournalById(UUID id) {
        return getById(database.journals(), id);
    }

    @Override
    public SaveResult addNewMessage(UUID id, JournalMessageDto message) {
        if (id == null) {
            return new SaveResult(false, "journal id must not be null");
        }
        if (message == null) {
            return new SaveResult(false, "journal message must not be null");
        }

        Optional<JournalDto> maybeJournal = getJournalById(id);
        if (maybeJournal.isEmpty()) {
            return new SaveResult(false, "journal %s not found".formatted(id));
        }

        JournalDto journal = maybeJournal.get();
        Map<String, JournalMessageDto> entries = ensureMutableEntries(journal);

        long timestamp = System.currentTimeMillis();
        String key = String.valueOf(timestamp);
        while (entries.containsKey(key)) {
            timestamp++;
            key = String.valueOf(timestamp);
        }

        entries.put(key, message);
        return new SaveResult(true, "journal message added at %s".formatted(key));
    }

    @Override
    public SaveResult deleteMessage(UUID id, Long messageTime) {
        if (id == null) {
            return new SaveResult(false, "journal id must not be null");
        }
        if (messageTime == null) {
            return new SaveResult(false, "message time must not be null");
        }

        Optional<JournalDto> maybeJournal = getJournalById(id);
        if (maybeJournal.isEmpty()) {
            return new SaveResult(false, "journal %s not found".formatted(id));
        }

        JournalDto journal = maybeJournal.get();
        Map<String, JournalMessageDto> entries = ensureMutableEntries(journal);
        String key = String.valueOf(messageTime);
        if (!entries.containsKey(key)) {
            return new SaveResult(false, "message %s not found".formatted(messageTime));
        }

        entries.remove(key);
        return new SaveResult(true, "journal message %s deleted".formatted(messageTime));
    }

    @Override
    public SaveResult editMessage(UUID id, Long messageTime, JournalMessageDto message) {
        if (id == null) {
            return new SaveResult(false, "journal id must not be null");
        }
        if (messageTime == null) {
            return new SaveResult(false, "message time must not be null");
        }
        if (message == null) {
            return new SaveResult(false, "journal message must not be null");
        }

        Optional<JournalDto> maybeJournal = getJournalById(id);
        if (maybeJournal.isEmpty()) {
            return new SaveResult(false, "journal %s not found".formatted(id));
        }

        JournalDto journal = maybeJournal.get();
        Map<String, JournalMessageDto> entries = ensureMutableEntries(journal);
        String key = String.valueOf(messageTime);
        if (!entries.containsKey(key)) {
            return new SaveResult(false, "message %s not found".formatted(messageTime));
        }

        entries.put(key, message);
        return new SaveResult(true, "journal message %s updated".formatted(messageTime));
    }

    @Override
    public SaveResult saveTrack(ResourceDto track) {
        return saveEntity(track, ResourceDto::getId, database.resources(), "track");
    }

    @Override
    public SaveResult saveProject(ProjectDto project) {
        return saveEntity(project, ProjectDto::getId, database.projects(), "project");
    }

    @Override
    public SaveResult saveAlbum(AlbumDto album) {
        return saveEntity(album, AlbumDto::getId, database.albums(), "album");
    }

    @Override
    public SaveResult saveFolder(FolderDto folder) {
        return saveEntity(folder, FolderDto::getId, database.folders(), "folder");
    }

    @Override
    public SaveResult saveView(ViewDto view) {
        return saveEntity(view, ViewDto::getId, database.views(), "view");
    }

    @Override
    public SaveResult saveImport(ImportDto importRule) {
        return saveEntity(importRule, ImportDto::getId, database.imports(), "import rule");
    }

    private <T> SaveResult saveEntity(T entity, Function<T, UUID> idExtractor, Map<UUID, T> storage, String entityName) {
        if (entity == null) {
            return new SaveResult(false, entityName + " must not be null");
        }
        UUID id = idExtractor.apply(entity);
        if (id == null) {
            return new SaveResult(false, entityName + " id must not be null");
        }
        Object existing = database.findEntity(id).orElse(null);
        if (existing != null && !storage.containsKey(id)) {
            return new SaveResult(false, "ID %s is already used by %s".formatted(
                    id,
                    existing.getClass().getSimpleName()
            ));
        }
        storage.put(id, entity);
        return new SaveResult(true, entityName + " saved");
    }

    private <T> Optional<T> getById(Map<UUID, T> storage, UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(id));
    }

    private Map<String, JournalMessageDto> ensureMutableEntries(JournalDto journal) {
        Map<String, JournalMessageDto> entries = journal.getEntries();
        if (entries instanceof LinkedHashMap<?, ?> || entries instanceof java.util.HashMap<?, ?>) {
            @SuppressWarnings("unchecked")
            Map<String, JournalMessageDto> casted = (Map<String, JournalMessageDto>) entries;
            return casted;
        }

        Map<String, JournalMessageDto> mutable = new LinkedHashMap<>();
        if (entries != null) {
            mutable.putAll(entries);
        }
        journal.setEntries(mutable);
        return mutable;
    }
}
