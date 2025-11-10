package com.kidekdev.albummanager.database.model;

import com.kidekdev.albummanager.database.model.album.AlbumDto;
import com.kidekdev.albummanager.database.model.autoimport.ImportDto;
import com.kidekdev.albummanager.database.model.folder.FolderDto;
import com.kidekdev.albummanager.database.model.journal.JournalDto;
import com.kidekdev.albummanager.database.model.project.ProjectDto;
import com.kidekdev.albummanager.database.model.resource.ResourceDto;
import com.kidekdev.albummanager.database.model.view.ViewDto;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Strongly typed in-memory representation of the YAML database.
 */
public class DataBase {

    private final Map<UUID, ResourceDto> resources;
    private final Map<UUID, AlbumDto> albums;
    private final Map<UUID, FolderDto> folders;
    private final Map<UUID, ProjectDto> projects;
    private final Map<UUID, JournalDto> journals;
    private final Map<UUID, ViewDto> views;
    private final Map<UUID, ImportDto> imports;

    public DataBase() {
        this(null, null, null, null, null, null, null);
    }

    public DataBase(
            Map<UUID, ResourceDto> resources,
            Map<UUID, AlbumDto> albums,
            Map<UUID, FolderDto> folders,
            Map<UUID, ProjectDto> projects,
            Map<UUID, JournalDto> journals,
            Map<UUID, ViewDto> views,
            Map<UUID, ImportDto> imports
    ) {
        this.resources = new LinkedHashMap<>();
        this.albums = new LinkedHashMap<>();
        this.folders = new LinkedHashMap<>();
        this.projects = new LinkedHashMap<>();
        this.journals = new LinkedHashMap<>();
        this.views = new LinkedHashMap<>();
        this.imports = new LinkedHashMap<>();

        if (resources != null) {
            this.resources.putAll(resources);
        }
        if (albums != null) {
            this.albums.putAll(albums);
        }
        if (folders != null) {
            this.folders.putAll(folders);
        }
        if (projects != null) {
            this.projects.putAll(projects);
        }
        if (journals != null) {
            this.journals.putAll(journals);
        }
        if (views != null) {
            this.views.putAll(views);
        }
        if (imports != null) {
            this.imports.putAll(imports);
        }
    }

    public Map<UUID, ResourceDto> resources() {
        return resources;
    }

    public Map<UUID, AlbumDto> albums() {
        return albums;
    }

    public Map<UUID, FolderDto> folders() {
        return folders;
    }

    public Map<UUID, ProjectDto> projects() {
        return projects;
    }

    public Map<UUID, JournalDto> journals() {
        return journals;
    }

    public Map<UUID, ViewDto> views() {
        return views;
    }

    public Map<UUID, ImportDto> imports() {
        return imports;
    }

    public Optional<Object> findEntity(UUID id) {
        if (id == null) {
            return Optional.empty();
        }

        Object entity = resources.get(id);
        if (entity != null) {
            return Optional.of(entity);
        }

        entity = albums.get(id);
        if (entity != null) {
            return Optional.of(entity);
        }

        entity = folders.get(id);
        if (entity != null) {
            return Optional.of(entity);
        }

        entity = projects.get(id);
        if (entity != null) {
            return Optional.of(entity);
        }

        entity = journals.get(id);
        if (entity != null) {
            return Optional.of(entity);
        }

        entity = views.get(id);
        if (entity != null) {
            return Optional.of(entity);
        }

        entity = imports.get(id);
        return Optional.ofNullable(entity);
    }

    public void merge(DataBase other) {
        if (other == null) {
            return;
        }
        resources.putAll(Objects.requireNonNull(other).resources);
        albums.putAll(other.albums);
        folders.putAll(other.folders);
        projects.putAll(other.projects);
        journals.putAll(other.journals);
        views.putAll(other.views);
        imports.putAll(other.imports);
    }
}

