package com.kidekdev.albummanager.ui.dynamicresource;

import com.kidekdev.albummanager.database.type.ResourceType;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.layout.VBox;

public class ResourceTypeDialog {

    public static Dialog<ResourceTypeDialogResult> create() {
        Dialog<ResourceTypeDialogResult> dialog = new Dialog<>();
        dialog.setTitle("Импорт ресурса");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField nameField = new TextField();
        nameField.setPromptText("Название ресурса");

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

        VBox content = new VBox(10, new Label("Название:"), nameField, track, image, midi, video);
        dialogPane.setContent(content);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                Toggle selected = group.getSelectedToggle();
                if (selected == null) {
                    return null;
                }

                return new ResourceTypeDialogResult(nameField.getText(), (ResourceType) selected.getUserData());
            }
            return null;
        });

        return dialog;
    }

    public record ResourceTypeDialogResult(String name, ResourceType type) {
    }
}
