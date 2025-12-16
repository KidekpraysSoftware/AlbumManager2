package com.kidekdev.albummanager.ui.controller.scene;

import com.kidekdev.albummanager.database.dto.ResourceDto;
import com.kidekdev.albummanager.database.type.ResourceExtension;
import com.kidekdev.albummanager.ui.context.ControllerHolder;
import com.kidekdev.albummanager.ui.context.DatabaseHolder;
import com.kidekdev.albummanager.ui.dispatcher.EventDispatcher;
import com.kidekdev.albummanager.ui.dispatcher.event.AddNewResourceEvent;
import com.kidekdev.albummanager.ui.track.ResourceLocation;
import com.kidekdev.albummanager.ui.track.TrackRowModule;
import javafx.fxml.FXML;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
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
    protected void initialize() {
        ControllerHolder.mainTabController = this;
        log.info("Инициализация MainTabController");
        setupResourceListDD();
        updateMainResourceList();
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
        // подсвечиваем область при перетаскивании файлов
        mainResourceList.setOnDragOver(event -> {
            System.out.println("Файл над листом");
            Dragboard db = event.getDragboard();
            if (event.getGestureSource() != mainResourceList && db.hasFiles()) {
                if (db.getFiles().size() == 1) {  // <<< проверяем, что перетаскивают только один файл
                    event.acceptTransferModes(TransferMode.COPY);
                }
            }
            event.consume();
        });

        // отпустили файлы → добавляем демку
        mainResourceList.setOnDragDropped(event -> {
            System.out.println("Файл был отпущен");
            Dragboard db = event.getDragboard();
            if (db.hasFiles() && db.getFiles().size() == 1) {
                File file = db.getFiles().get(0);
                Path path = file.toPath();
                String name = file.getName().toLowerCase();
                boolean supported = Arrays.stream(ResourceExtension.values())
                        .anyMatch(ext -> name.endsWith("." + ext.name().toLowerCase()));
                if (supported) {
                    EventDispatcher.dispatch(new AddNewResourceEvent(path, false));
                    event.setDropCompleted(true);
                } else {
                    event.setDropCompleted(false);
                }
            } else {
                event.setDropCompleted(false);
            }
            event.consume();
        });

    }
}