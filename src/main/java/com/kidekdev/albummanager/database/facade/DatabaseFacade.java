package com.kidekdev.albummanager.database.facade;

import com.kidekdev.albummanager.common.OperationResult;
import com.kidekdev.albummanager.database.model.album.AlbumDto;
import com.kidekdev.albummanager.database.model.autoimport.ImportDto;
import com.kidekdev.albummanager.database.model.folder.FolderDto;
import com.kidekdev.albummanager.database.model.journal.JournalDto;
import com.kidekdev.albummanager.database.model.journal.JournalMessageDto;
import com.kidekdev.albummanager.database.model.project.ProjectDto;
import com.kidekdev.albummanager.database.model.resource.ResourceDto;
import com.kidekdev.albummanager.database.model.tag.GlobalTagGroupsDto;
import com.kidekdev.albummanager.database.model.view.ViewDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface DatabaseFacade {

    //Теги
    Map<String, List<String>> getAllTagGroups(); //key - группа, value - ее теги

    List<String> getTagsByGroupName(String groupName);

    String getGroupNameByTag(String tag);

    //Сохранение
    OperationResult addNewMessage(UUID journalId, JournalMessageDto message);

    OperationResult deleteMessage(UUID journalId, Long messageTime);

    OperationResult editMessage(UUID journalId, Long messageTime, JournalMessageDto message);

    // Сохранение сущностей
    OperationResult saveAlbum(AlbumDto album);

    OperationResult saveFolder(FolderDto folder);

    OperationResult saveImport(ImportDto importDto);

    OperationResult saveJournal(JournalDto journal);

    OperationResult saveProject(ProjectDto project);

    OperationResult saveResource(ResourceDto resource);

    OperationResult saveView(ViewDto view);

    //Сохранение глобальных параметров
    OperationResult saveGlobalTagGroups(GlobalTagGroupsDto tags);

}

