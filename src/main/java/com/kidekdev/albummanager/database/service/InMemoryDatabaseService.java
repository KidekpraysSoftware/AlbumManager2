package com.kidekdev.albummanager.database.service;

import com.kidekdev.albummanager.database.loader.DatabaseLoadResult;
import com.kidekdev.albummanager.database.model.album.AlbumDto;
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

    private final Map<UUID, Object> database;
    private final Map<String, String> groupByTag;

    public InMemoryDatabaseService(DatabaseLoadResult loadResult) {
        this(Objects.requireNonNull(loadResult, "loadResult").globalDatabase(), loadResult.globalTagGroups());
    }

    public InMemoryDatabaseService(Map<UUID, Object> initialState, GlobalTagGroupsDto tagGroups) {
        this.database = new LinkedHashMap<>();
        if (initialState != null) {
            this.database.putAll(initialState);
        }

        this.groupByTag = new LinkedHashMap<>();
        if (tagGroups != null && tagGroups.getTagGroups() != null) {
            this.groupByTag.putAll(tagGroups.getTagGroups());
        }
    }

    @Override
    public Optional<?> getById(UUID id) {
        return Optional.ofNullable(database.get(id));
    }

    @Override
    public List<ResourceDto> getAllTracks() {
        return getAllOfType(ResourceDto.class);
    }

    @Override
    public List<ProjectDto> getAllProjects() {
        return getAllOfType(ProjectDto.class);
    }

    @Override
    public List<AlbumDto> getAllAlbums() {
        return getAllOfType(AlbumDto.class);
    }

    @Override
    public List<FolderDto> getAllFolders() {
        return getAllOfType(FolderDto.class);
    }

    @Override
    public List<ViewDto> getAllViews() {
        return getAllOfType(ViewDto.class);
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
        return getByIdAndType(id, ResourceDto.class);
    }

    @Override
    public Optional<ProjectDto> getProjectById(UUID id) {
        return getByIdAndType(id, ProjectDto.class);
    }

    @Override
    public Optional<AlbumDto> getAlbumById(UUID id) {
        return getByIdAndType(id, AlbumDto.class);
    }

    @Override
    public Optional<FolderDto> getFolderById(UUID id) {
        return getByIdAndType(id, FolderDto.class);
    }

    @Override
    public Optional<ViewDto> getViewById(UUID id) {
        return getByIdAndType(id, ViewDto.class);
    }

    @Override
    public Optional<JournalDto> getJournalById(UUID id) {
        return getByIdAndType(id, JournalDto.class);
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
        return saveEntity(track, ResourceDto::getId, ResourceDto.class, "track");
    }

    @Override
    public SaveResult saveProject(ProjectDto project) {
        return saveEntity(project, ProjectDto::getId, ProjectDto.class, "project");
    }

    @Override
    public SaveResult saveAlbum(AlbumDto album) {
        return saveEntity(album, AlbumDto::getId, AlbumDto.class, "album");
    }

    @Override
    public SaveResult saveFolder(FolderDto folder) {
        return saveEntity(folder, FolderDto::getId, FolderDto.class, "folder");
    }

    @Override
    public SaveResult saveView(ViewDto view) {
        return saveEntity(view, ViewDto::getId, ViewDto.class, "view");
    }

    private <T> List<T> getAllOfType(Class<T> type) {
        List<T> result = new ArrayList<>();
        for (Object value : database.values()) {
            if (type.isInstance(value)) {
                result.add(type.cast(value));
            }
        }
        return List.copyOf(result);
    }

    private <T> Optional<T> getByIdAndType(UUID id, Class<T> type) {
        if (id == null) {
            return Optional.empty();
        }
        Object entity = database.get(id);
        if (type.isInstance(entity)) {
            return Optional.of(type.cast(entity));
        }
        return Optional.empty();
    }

    private <T> SaveResult saveEntity(T entity, Function<T, UUID> idExtractor, Class<T> type, String entityName) {
        if (entity == null) {
            return new SaveResult(false, entityName + " must not be null");
        }
        UUID id = idExtractor.apply(entity);
        if (id == null) {
            return new SaveResult(false, entityName + " id must not be null");
        }
        Object existing = database.get(id);
        if (existing != null && !type.isInstance(existing)) {
            return new SaveResult(false, "ID %s is already used by %s".formatted(id, existing.getClass().getSimpleName()));
        }
        database.put(id, entity);
        return new SaveResult(true, entityName + " saved");
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
