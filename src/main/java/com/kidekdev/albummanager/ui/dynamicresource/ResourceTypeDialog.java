package com.kidekdev.albummanager.ui.dynamicresource;

import com.kidekdev.albummanager.database.type.ResourceType;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.layout.VBox;

import java.awt.*;

public class ResourceTypeDialog {

    public static Dialog<ResourceType> create() {
        Dialog<ResourceType> dialog = new Dialog<>();
        dialog.setTitle("Импорт ресурса");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        ToggleGroup group = new ToggleGroup();

        RadioButton track = new RadioButton("Трек");
        track.setUserData(ResourceType.TRACK);
        track.setToggleGroup(group);

        RadioButton image = new RadioButton("Изображение");
        image.setUserData(ResourceType.IMAGE);
        image.setToggleGroup(group);

        RadioButton midi = new RadioButton("MIDI");
        midi.setUserData(ResourceType.MIDI);
        midi.setToggleGroup(group);

        RadioButton video = new RadioButton("Видео");
        video.setUserData(ResourceType.VIDEO);
        video.setToggleGroup(group);

        // дефолтное значение
        track.setSelected(true);

        VBox content = new VBox(10, track, image, midi, video);
        dialogPane.setContent(content);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                Toggle selected = group.getSelectedToggle();
                return selected != null
                        ? (ResourceType) selected.getUserData()
                        : null;
            }
            return null;
        });

        return dialog;
    }
}
