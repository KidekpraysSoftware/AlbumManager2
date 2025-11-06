package com.kidekdev.albummanager.database.service;

import com.kidekdev.albummanager.database.model.album.AlbumDto;
import com.kidekdev.albummanager.database.model.folder.FolderDto;
import com.kidekdev.albummanager.database.model.journal.JournalDto;
import com.kidekdev.albummanager.database.model.journal.JournalMessageDto;
import com.kidekdev.albummanager.database.model.project.ProjectDto;
import com.kidekdev.albummanager.database.model.resource.ResourceDto;
import com.kidekdev.albummanager.database.model.view.ViewDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface DatabaseService {

    Optional<?> getById(UUID id);

    // Получение всех сущностей
    List<ResourceDto> getAllTracks();

    List<ProjectDto> getAllProjects();

    List<AlbumDto> getAllAlbums();

    List<FolderDto> getAllFolders();

    List<ViewDto> getAllViews();

    Map<String, List<String>> getAllTagGroups(); //key - группа, value - ее теги

    List<String> getTagsByGroupName(String groupName);

    String getGroupNameByTag(String tag);

    // Получение по UUID
    Optional<ResourceDto> getTrackById(UUID id);

    Optional<ProjectDto> getProjectById(UUID id);

    Optional<AlbumDto> getAlbumById(UUID id);

    Optional<FolderDto> getFolderById(UUID id);

    Optional<ViewDto> getViewById(UUID id);

    Optional<JournalDto> getJournalById(UUID id);

    SaveResult addNewMessage(UUID id, JournalMessageDto message);

    SaveResult deleteMessage(UUID id, Long messageTime);

    SaveResult editMessage(UUID id, Long messageTime, JournalMessageDto message);

    // Сохранение сущностей
    SaveResult saveTrack(ResourceDto track);

    SaveResult saveProject(ProjectDto project);

    SaveResult saveAlbum(AlbumDto album);

    SaveResult saveFolder(FolderDto folder);

    SaveResult saveView(ViewDto view);
}

