package com.kidekdev.albummanager.database.service;

import com.kidekdev.albummanager.database.loader.DatabaseLoadResult;
import com.kidekdev.albummanager.database.loader.DatabaseLoader;
import com.kidekdev.albummanager.database.model.album.AlbumDto;
import com.kidekdev.albummanager.database.model.folder.FolderDto;
import com.kidekdev.albummanager.database.model.journal.JournalDto;
import com.kidekdev.albummanager.database.model.journal.JournalMessageDto;
import com.kidekdev.albummanager.database.model.project.ProjectDto;
import com.kidekdev.albummanager.database.model.resource.ResourceDto;
import com.kidekdev.albummanager.database.model.tag.GlobalTagGroupsDto;
import com.kidekdev.albummanager.database.model.view.ViewDto;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * File-backed implementation of {@link DatabaseService} that is able to load and initialize
 * database folders from the filesystem.
 */
public class FileDatabaseService implements DatabaseService {

    private DatabaseService delegate;
    private Path databasePath;

    public FileDatabaseService() {
        this.delegate = createEmptyDelegate();
    }

    public Optional<Path> getDatabasePath() {
        return Optional.ofNullable(databasePath);
    }

    public OperationResult loadDatabase(Path path) {
        if (path == null) {
            return new OperationResult(false, "Database path must not be null");
        }
        if (!Files.exists(path)) {
            return new OperationResult(false, "Database path does not exist: " + path.toAbsolutePath());
        }
        if (!Files.isDirectory(path)) {
            return new OperationResult(false, "Database path is not a directory: " + path.toAbsolutePath());
        }

        DatabaseLoader loader = new DatabaseLoader();
        try {
            DatabaseLoadResult loadResult = loader.loadDatabase(path);
            this.delegate = new InMemoryDatabaseService(loadResult);
            this.databasePath = path;
            return new OperationResult(true, "Database loaded from " + path.toAbsolutePath());
        } catch (RuntimeException ex) {
            this.delegate = createEmptyDelegate();
            this.databasePath = null;
            return new OperationResult(false, "Failed to load database from %s: %s".formatted(
                    path.toAbsolutePath(), ex.getMessage()
            ));
        }
    }

    public OperationResult initNewDatabase(Path path) {
        if (path == null) {
            return new OperationResult(false, "Database path must not be null");
        }
        DatabaseLoader loader = new DatabaseLoader();
        try {
            DatabaseLoadResult loadResult = loader.createDatabase(path);
            this.delegate = new InMemoryDatabaseService(loadResult);
            this.databasePath = path;
            return new OperationResult(true, "Initialized new database at " + path.toAbsolutePath());
        } catch (RuntimeException ex) {
            return new OperationResult(false, "Failed to initialize database at %s: %s".formatted(
                    path.toAbsolutePath(), ex.getMessage()
            ));
        }
    }

    private DatabaseService createEmptyDelegate() {
        return new InMemoryDatabaseService(Map.of(), new GlobalTagGroupsDto(Map.of()));
    }

    @Override
    public Optional<?> getById(UUID id) {
        return delegate.getById(id);
    }

    @Override
    public List<ResourceDto> getAllTracks() {
        return delegate.getAllTracks();
    }

    @Override
    public List<ProjectDto> getAllProjects() {
        return delegate.getAllProjects();
    }

    @Override
    public List<AlbumDto> getAllAlbums() {
        return delegate.getAllAlbums();
    }

    @Override
    public List<FolderDto> getAllFolders() {
        return delegate.getAllFolders();
    }

    @Override
    public List<ViewDto> getAllViews() {
        return delegate.getAllViews();
    }

    @Override
    public Map<String, List<String>> getAllTagGroups() {
        return delegate.getAllTagGroups();
    }

    @Override
    public List<String> getTagsByGroupName(String groupName) {
        return delegate.getTagsByGroupName(groupName);
    }

    @Override
    public String getGroupNameByTag(String tag) {
        return delegate.getGroupNameByTag(tag);
    }

    @Override
    public Optional<ResourceDto> getTrackById(UUID id) {
        return delegate.getTrackById(id);
    }

    @Override
    public Optional<ProjectDto> getProjectById(UUID id) {
        return delegate.getProjectById(id);
    }

    @Override
    public Optional<AlbumDto> getAlbumById(UUID id) {
        return delegate.getAlbumById(id);
    }

    @Override
    public Optional<FolderDto> getFolderById(UUID id) {
        return delegate.getFolderById(id);
    }

    @Override
    public Optional<ViewDto> getViewById(UUID id) {
        return delegate.getViewById(id);
    }

    @Override
    public Optional<JournalDto> getJournalById(UUID id) {
        return delegate.getJournalById(id);
    }

    @Override
    public SaveResult addNewMessage(UUID id, JournalMessageDto message) {
        return delegate.addNewMessage(id, message);
    }

    @Override
    public SaveResult deleteMessage(UUID id, Long messageTime) {
        return delegate.deleteMessage(id, messageTime);
    }

    @Override
    public SaveResult editMessage(UUID id, Long messageTime, JournalMessageDto message) {
        return delegate.editMessage(id, messageTime, message);
    }

    @Override
    public SaveResult saveTrack(ResourceDto track) {
        return delegate.saveTrack(track);
    }

    @Override
    public SaveResult saveProject(ProjectDto project) {
        return delegate.saveProject(project);
    }

    @Override
    public SaveResult saveAlbum(AlbumDto album) {
        return delegate.saveAlbum(album);
    }

    @Override
    public SaveResult saveFolder(FolderDto folder) {
        return delegate.saveFolder(folder);
    }

    @Override
    public SaveResult saveView(ViewDto view) {
        return delegate.saveView(view);
    }
}
