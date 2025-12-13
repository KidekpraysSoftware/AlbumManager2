package com.kidekdev.albummanager.ui.controller.event;

import com.kidekdev.albummanager.common.OperationResult;
import com.kidekdev.albummanager.database.type.ResourceType;
import com.kidekdev.albummanager.ui.context.DatabaseHolder;
import com.kidekdev.albummanager.ui.dispatcher.EventHandlerComponent;
import com.kidekdev.albummanager.ui.dispatcher.OnEvent;
import com.kidekdev.albummanager.ui.dispatcher.event.AddNewResourceEvent;
import com.kidekdev.albummanager.ui.dto.PathInfo;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static com.kidekdev.albummanager.ui.exception.AlertUtils.showErrorAlert;
import static com.kidekdev.albummanager.ui.exception.AlertUtils.wrapAndAssert;
import static com.kidekdev.albummanager.ui.utils.FileUtils.getPathInfo;
import static com.kidekdev.albummanager.ui.utils.HashUtils.sha256;

@Slf4j
@EventHandlerComponent
public class ResourceEventController {

    @OnEvent(AddNewResourceEvent.class)
    public void addNewResource(AddNewResourceEvent event) {
        Path path = event.path();
        PathInfo pathInfo = getPathInfo(path);
        wrapAndAssert("Длина названия файла вместе с расширением должна быть не более 212 символов",
                pathInfo.fileName().length() <= 212);
        log.info("Добавление нового ресурса {}", path);
        String resourceFileHash = sha256(path);
        log.info("Началось добавление ресурса fileName = {}, sha256 = {} ", pathInfo.fileName(), resourceFileHash);
        OperationResult existCheck = DatabaseHolder.resource.isExist(resourceFileHash);
        if (!existCheck.isSuccess()) {
            showErrorAlert(existCheck.message());
        }
    }
}
