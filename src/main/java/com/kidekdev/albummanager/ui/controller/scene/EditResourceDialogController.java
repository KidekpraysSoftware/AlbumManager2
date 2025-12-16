package com.kidekdev.albummanager.ui.controller.scene;

import com.kidekdev.albummanager.database.dto.TagGroupDto;
import com.kidekdev.albummanager.ui.context.ControllerHolder;
import com.kidekdev.albummanager.ui.context.DatabaseHolder;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import com.kidekdev.albummanager.ui.tag.AddTagsModule;


import java.util.*;

import static com.kidekdev.albummanager.ui.utils.TextUtils.addTagConstraint;


@Data
public class EditResourceDialogController {

    @FXML
    private ToggleGroup TrackTypeToggleGroup;

    @FXML
    private ButtonType addButtonType;

    @FXML
    private TextField addTrackArtistTextField;

    @FXML
    private TextField addTrackNameTextField;

    @FXML
    private HBox categoryListHbox;

    private List<AddTagsModule> categoryList = new ArrayList<>();

    @FXML
    private AnchorPane settingsPane;

    @FXML
    private TextField searchTagTextField;

    @SneakyThrows
    @FXML
    public void initialize() {
        ControllerHolder.editResourceDialogController = this;
        categoryListHbox.getChildren().clear();
        List<TagGroupDto> groups = DatabaseHolder.tag.findAllGroups();


        groups.stream().map(AddTagsModule::new).forEach(group -> {
            if (group.getOrdering() == 0) {
                return;
            }
            categoryList.add(group);
            categoryListHbox.getChildren().add(group);
            categoryListHbox.getChildren().add(new Separator(Orientation.VERTICAL));
        });

        searchTagTextField.textProperty().addListener((obs, oldText, newText) -> {
            categoryList.forEach(c -> c.searchTag(newText));
            addTagConstraint(searchTagTextField, newText);
        });
    }

    public Map<UUID, List<String>> getAllSelectedTags() {
        Map<UUID, List<String>> result = new HashMap<>();
        for (AddTagsModule module : categoryList) {
            result.put(module.getCategoryId(), module.getSelectedAndNewTags());
        }
        return result;
    }

    public EditResourceResult getAddTrackResult(String fileName) {
        String trackName = addTrackNameTextField.getText();
        String authorName = addTrackArtistTextField.getText();
        return EditResourceResult.builder()
                .resourceName(trackName.isEmpty() ? "Без названия" : trackName)
                .authorName(authorName.isEmpty() ? "KidekDemo " + fileName : authorName)
                .selectedTags(getAllSelectedTags())
                .build();
    }

    public void selectCheckBox(List<String> tags) {
        categoryList.forEach(c -> c.selectTags(tags));
    }

    @Builder
    public record EditResourceResult(
            String resourceName,
            String authorName,
            Map<UUID, List<String>> selectedTags
    ) {
    }
}
