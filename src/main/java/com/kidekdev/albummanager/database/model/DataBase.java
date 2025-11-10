package com.kidekdev.albummanager.database.model;

import com.kidekdev.albummanager.database.model.album.AlbumDto;
import com.kidekdev.albummanager.database.model.autoimport.ImportDto;
import com.kidekdev.albummanager.database.model.folder.FolderDto;
import com.kidekdev.albummanager.database.model.journal.JournalDto;
import com.kidekdev.albummanager.database.model.project.ProjectDto;
import com.kidekdev.albummanager.database.model.resource.ResourceDto;
import com.kidekdev.albummanager.database.model.tag.GlobalTagGroupsDto;
import com.kidekdev.albummanager.database.model.view.ViewDto;
import lombok.Builder;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Strongly typed in-memory representation of the YAML database.
 */
@Builder
public record DataBase(
        Map<Path, UUID> pathIndex,
        Map<UUID, ResourceDto> resources,
        Map<UUID, AlbumDto> albums,
        Map<UUID, FolderDto> folders,
        Map<UUID, ProjectDto> projects,
        Map<UUID, JournalDto> journals,
        Map<UUID, ViewDto> views,
        Map<UUID, ImportDto> imports,
        GlobalTagGroupsDto globalTagGroups
) {

    public DataBase {
        pathIndex = toLinkedHashMap(pathIndex);
        resources = toLinkedHashMap(resources);
        albums = toLinkedHashMap(albums);
        folders = toLinkedHashMap(folders);
        projects = toLinkedHashMap(projects);
        journals = toLinkedHashMap(journals);
        views = toLinkedHashMap(views);
        imports = toLinkedHashMap(imports);
        globalTagGroups = globalTagGroups != null ? globalTagGroups : new GlobalTagGroupsDto(Map.of());
    }

    public static DataBase empty() {
        return DataBase.builder().build();
    }

    private static <K, V> Map<K, V> toLinkedHashMap(Map<K, V> source) {
        if (source == null) {
            return new LinkedHashMap<>();
        }
        return new LinkedHashMap<>(source);
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

    public DataBase merge(DataBase other) {
        if (other == null) {
            return this;
        }

        Map<Path, UUID> mergedPathIndex = new LinkedHashMap<>(pathIndex);
        mergedPathIndex.putAll(other.pathIndex());

        Map<UUID, ResourceDto> mergedResources = new LinkedHashMap<>(resources);
        mergedResources.putAll(other.resources());

        Map<UUID, AlbumDto> mergedAlbums = new LinkedHashMap<>(albums);
        mergedAlbums.putAll(other.albums());

        Map<UUID, FolderDto> mergedFolders = new LinkedHashMap<>(folders);
        mergedFolders.putAll(other.folders());

        Map<UUID, ProjectDto> mergedProjects = new LinkedHashMap<>(projects);
        mergedProjects.putAll(other.projects());

        Map<UUID, JournalDto> mergedJournals = new LinkedHashMap<>(journals);
        mergedJournals.putAll(other.journals());

        Map<UUID, ViewDto> mergedViews = new LinkedHashMap<>(views);
        mergedViews.putAll(other.views());

        Map<UUID, ImportDto> mergedImports = new LinkedHashMap<>(imports);
        mergedImports.putAll(other.imports());

        GlobalTagGroupsDto mergedTagGroups =
                other.globalTagGroups() != null ? other.globalTagGroups() : globalTagGroups;

        return DataBase.builder()
                .pathIndex(mergedPathIndex)
                .resources(mergedResources)
                .albums(mergedAlbums)
                .folders(mergedFolders)
                .projects(mergedProjects)
                .journals(mergedJournals)
                .views(mergedViews)
                .imports(mergedImports)
                .globalTagGroups(mergedTagGroups)
                .build();
    }
}

