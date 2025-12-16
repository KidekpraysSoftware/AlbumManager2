package com.kidekdev.albummanager.ui.controller.event;

import com.kidekdev.albummanager.common.OperationResult;
import com.kidekdev.albummanager.database.dto.ResourceDto;
import com.kidekdev.albummanager.database.dto.TagDto;
import com.kidekdev.albummanager.database.type.ResourceType;
import com.kidekdev.albummanager.ui.context.ControllerHolder;
import com.kidekdev.albummanager.ui.context.DatabaseHolder;
import com.kidekdev.albummanager.ui.controller.scene.EditResourceDialogController;
import com.kidekdev.albummanager.ui.dispatcher.EventHandlerComponent;
import com.kidekdev.albummanager.ui.dispatcher.OnEvent;
import com.kidekdev.albummanager.ui.dispatcher.event.AddNewResourceEvent;
import com.kidekdev.albummanager.ui.dto.PathInfo;
import com.kidekdev.albummanager.ui.utils.FileUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;

import static com.kidekdev.albummanager.ui.exception.AlertUtils.showErrorAlert;
import static com.kidekdev.albummanager.ui.exception.AlertUtils.wrapAndAssert;
import static com.kidekdev.albummanager.ui.utils.FileUtils.getPathInfo;
import static com.kidekdev.albummanager.ui.utils.HashUtils.sha256;

@Slf4j
@EventHandlerComponent
public class ResourceEventController {

    @SneakyThrows
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
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditResourceDialog.fxml"));
        DialogPane dialogPane = loader.load();
        EditResourceDialogController controller = loader.getController();
        controller.getAddTrackArtistTextField().setText(pathInfo.fileName());
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Добавление " + path.getFileName().toString());
        dialog.setDialogPane(dialogPane);

        // Отобразить диалог и ждать ответа
        Optional<ButtonType> result = dialog.showAndWait();

        //Анализ заполненных атрибутов нового трека.
        boolean success = result.isPresent() && result.get() == controller.getAddButtonType();
        if (!success) {
            return;
        }
        EditResourceDialogController.AddResourceResult addResourceResult = controller.getAddTrackResult(pathInfo.fileName());

        OffsetDateTime creationTime = FileUtils.getEarliestFileTime(path.toFile());

        Set<TagDto> resourceTags = DatabaseHolder.tag.mergeNewTags(addResourceResult.selectedTags());
        ResourceDto resourceDto = ResourceDto.builder()
                .resourceName(addResourceResult.resourceName())
                .authorName(addResourceResult.authorName())
                .isActive(true)
                .path(path.toString())
                .isDynamic(event.isDynamic())
                .hash(resourceFileHash)
                .resourceType(ResourceType.resolveType(pathInfo.extension()))
                .extension(pathInfo.extension())
                .fileCreationTime(creationTime)
                .tags(resourceTags)
                .build();
        DatabaseHolder.resource.save(resourceDto);
        ControllerHolder.importTabController.updateImportResourceList();
        ControllerHolder.mainTabController.updateMainResourceList();
    }
}
