package com.kidekdev.albummanager.ui.controller.scene;

import com.kidekdev.albummanager.database.model.resource.ResourceExtension;
import com.kidekdev.albummanager.ui.dispatcher.EventDispatcher;
import com.kidekdev.albummanager.ui.dispatcher.event.AddNewResourceEvent;
import com.kidekdev.albummanager.ui.track.TrackRowModule;
import javafx.fxml.FXML;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.UUID;


@Slf4j
public class MainTabController {

    @FXML
    private VBox mainResourceList;

    @FXML
    private VBox projectTrackListVBox;

    @FXML
    protected void initialize() {
        log.info("Инициализация MainTabController");
        setupResourceListDD();
        TrackRowModule track1 = new TrackRowModule(Path.of("D:/Green Zone/Музыкальный проект/Действующие проекты/Сессии/S1/216.mp3"), UUID.randomUUID(), "Трек 216", "Неизвестен");
        TrackRowModule track2 = new TrackRowModule(Path.of("D:/Green Zone/Музыкальный проект/Действующие проекты/Сессии/S1/217.mp3"), UUID.randomUUID(), "Трек 217", "Неизвестен");
        TrackRowModule track3 = new TrackRowModule(Path.of("D:/Green Zone/Музыкальный проект/Действующие проекты/Сессии/S1/218.mp3"), UUID.randomUUID(), "Трек 218", "Неизвестен");
//        mainResourceList.getChildren().add(track1);
//        mainResourceList.getChildren().add(track2);
//        mainResourceList.getChildren().add(track3);
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
                    EventDispatcher.dispatch(new AddNewResourceEvent(path));
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