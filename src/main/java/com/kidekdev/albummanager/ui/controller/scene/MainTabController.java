package com.kidekdev.albummanager.ui.controller.scene;

import com.kidekdev.albummanager.database.dto.ResourceDto;
import com.kidekdev.albummanager.database.type.ResourceExtension;
import com.kidekdev.albummanager.ui.context.ControllerHolder;
import com.kidekdev.albummanager.ui.context.DatabaseHolder;
import com.kidekdev.albummanager.ui.dispatcher.EventDispatcher;
import com.kidekdev.albummanager.ui.dispatcher.event.AddNewDynamicResourceEvent;
import com.kidekdev.albummanager.ui.dispatcher.event.AddNewResourceEvent;
import com.kidekdev.albummanager.ui.dispatcher.event.RefreshDynamicResourceEvent;
import com.kidekdev.albummanager.ui.track.ResourceLocation;
import com.kidekdev.albummanager.ui.track.TrackRowModule;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static com.kidekdev.albummanager.database.type.ResourceType.TRACK;


@Slf4j
public class MainTabController {

    @FXML
    private VBox mainResourceList;

    @FXML
    private VBox projectTrackListVBox;

    @FXML
    private Button refreshDynamicResourceStateButton;

    @FXML
    protected void initialize() {
        ControllerHolder.mainTabController = this;
        log.info("Инициализация MainTabController");
        setupResourceListDD();
        updateMainResourceList();
        refreshDynamicResourceStateButton.setOnAction(event -> {
            EventDispatcher.dispatch(new RefreshDynamicResourceEvent());
        });

    }

    public void updateMainResourceList() {
        mainResourceList.getChildren().clear();
        List<ResourceDto> resourceDtoList = DatabaseHolder.resource.findAllActive();
        resourceDtoList.stream()
                .filter(dto -> dto.resourceType().equals(TRACK))
                .forEach(dto -> {
                    TrackRowModule track =
                            new TrackRowModule(
                                    Path.of(dto.path()),
                                    ResourceLocation.RESOURCE_LIST,
                                    dto.id(),
                                    dto.resourceName(),
                                    dto.authorName()
                            );
                    mainResourceList.getChildren().add(track);
                });
    }

    private void setupResourceListDD() {
        // drag over
        mainResourceList.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (event.getGestureSource() != mainResourceList && db.hasFiles() && db.getFiles().size() == 1) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        // drop
        mainResourceList.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();

            if (!db.hasFiles() || db.getFiles().size() != 1) {
                event.setDropCompleted(false);
                event.consume();
                return;
            }

            File file = db.getFiles().get(0);
            Path path = file.toPath();

            // 1) если папка
            if (Files.isDirectory(path)) {
                EventDispatcher.dispatch(new AddNewDynamicResourceEvent(path));
                event.setDropCompleted(true);
                event.consume();
                return;
            }

            // 2) если файл — проверяем расширение
            String name = file.getName().toLowerCase();
            boolean supported = Arrays.stream(ResourceExtension.values())
                    .anyMatch(ext -> name.endsWith("." + ext.name().toLowerCase()));

            if (supported) {
                EventDispatcher.dispatch(new AddNewResourceEvent(path, false));
                event.setDropCompleted(true);
            } else {
                event.setDropCompleted(false);
            }

            event.consume();
        });
    }
}