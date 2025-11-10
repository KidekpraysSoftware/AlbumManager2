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

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class DatabaseFacadeImpl implements DatabaseFacade {

    final DataBase dataBase;

    @Override
    public Map<String, List<String>> getAllTagGroups() {
        return null;
    }

    @Override
    public List<String> getTagsByGroupName(String groupName) {
        return null;
    }

    @Override
    public String getGroupNameByTag(String tag) {
        return null;
    }

    @Override
    public OperationResult addNewMessage(UUID journalId, JournalMessageDto message) {
        return null;
    }

    @Override
    public OperationResult deleteMessage(UUID journalId, Long messageTime) {
        return null;
    }

    @Override
    public OperationResult editMessage(UUID journalId, Long messageTime, JournalMessageDto message) {
        return null;
    }

    @Override
    public OperationResult saveAlbum(AlbumDto album) {
        return null;
    }

    @Override
    public OperationResult saveFolder(FolderDto folder) {
        return null;
    }

    @Override
    public OperationResult saveImport(ImportDto importDto) {
        return null;
    }

    @Override
    public OperationResult saveJournal(JournalDto journal) {
        return null;
    }

    @Override
    public OperationResult saveProject(ProjectDto project) {
        return null;
    }

    @Override
    public OperationResult saveResource(ResourceDto resource) {
        return null;
    }

    @Override
    public OperationResult saveView(ViewDto view) {
        return null;
    }

    @Override
    public OperationResult saveGlobalTagGroups(GlobalTagGroupsDto tags) {
        return null;
    }
}
