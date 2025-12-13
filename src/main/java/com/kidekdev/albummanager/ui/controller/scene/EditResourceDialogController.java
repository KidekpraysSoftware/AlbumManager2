package com.kidekdev.albummanager.ui.controller.scene;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.kidekdev.albummanager.database.dto.TagDto;
//import com.kidekdev.albummanager.ui.context.ControllerHolder;
//import com.kidekdev.albummanager.ui.context.DatabaseHolder;
//import javafx.fxml.FXML;
//import javafx.geometry.Orientation;
//import javafx.scene.control.*;
//import javafx.scene.layout.AnchorPane;
//import javafx.scene.layout.HBox;
//import lombok.Data;
//import lombok.SneakyThrows;
//import org.kidekdev.manager.common.OperationResponse;
//import org.kidekdev.manager.common.dto.tag.CategoryDto;
//import org.kidekdev.manager.common.dto.track.TrackType;
//import org.kidekdev.manager.frontend.context.ControllerContext;
//import org.kidekdev.manager.frontend.module.tag.AddTagsModule;
//import org.kidekdev.manager.frontend.module.track.AddTrackResultV2;
//import org.kidekdev.manager.frontend.service.ApiClient;
//
//import java.util.*;
//
//import static org.kidekdev.manager.frontend.utils.TextUtils.addTagConstraint;
//
//@Data
public class EditResourceDialogController {
//
//    @FXML
//    private ToggleGroup TrackTypeToggleGroup;
//
//    @FXML
//    private ButtonType addButtonType;
//
//    @FXML
//    private TextField addTrackArtistTextField;
//
//    @FXML
//    private TextField addTrackNameTextField;
//
//    @FXML
//    private HBox categoryListHbox;
//
//    private List<AddTagsModule> categoryList = new ArrayList<>();
//
//    @FXML
//    private RadioButton radioButtonDemo;
//
//    @FXML
//    private RadioButton radioButtonFragment;
//
//    @FXML
//    private RadioButton radioButtonShard;
//
//    @FXML
//    private AnchorPane settingsPane;
//
//    @FXML
//    private TextField searchTagTextField;
//
//    @SneakyThrows
//    @FXML
//    public void initialize() {
//        ControllerHolder.editResourceDialogController = this;
//        categoryListHbox.getChildren().clear();
//        Map<String, List<TagDto>> groups = DatabaseHolder.tag.findAllGroups();
//
//
//        groups.forEach((groupName,tagList) -> {
//            if (c.getOrdering() == 0){
//                return;
//            }
//            categoryList.add(c);
//            categoryListHbox.getChildren().add(c);
//            categoryListHbox.getChildren().add(new Separator(Orientation.VERTICAL));
//        });
//
//        searchTagTextField.textProperty().addListener((obs, oldText, newText) -> {
//            categoryList.forEach(c -> c.searchTag(newText));
//            addTagConstraint(searchTagTextField, newText);
//        });
//    }
//
//    public TrackType getTrackType() {
//        if (radioButtonDemo.isSelected()) {
//            return TrackType.DEMO;
//        }
//        if (radioButtonFragment.isSelected()) {
//            return TrackType.FRAGMENT;
//        }
//        if (radioButtonShard.isSelected()) {
//            return TrackType.SHARD;
//        }
//        throw new RuntimeException();
//    }
//
//    public ButtonType getAddButtonType() {
//        return addButtonType;
//    }
//
//    public Map<UUID, List<String>> getAllSelectedTags() {
//        Map<UUID, List<String>> result = new HashMap<>();
//        for (AddTagsModule module : categoryList) {
//            result.put(module.getCategoryId(), module.getSelectedAndNewTags());
//        }
//        return result;
//    }
//
//    public AddTrackResultV2 getAddTrackResult(String fileName) {
//        AddTrackResultV2 result = new AddTrackResultV2();
//
//        String trackName = addTrackNameTextField.getText();
//        String authorName = addTrackArtistTextField.getText();
//
//        result.setTrackName(trackName.equals("") ? "Без названия" : trackName);
//        result.setAuthorName(authorName.equals("") ? "KidekDemo " + fileName : authorName);
//        result.setSelectedTags(getAllSelectedTags());
//        result.setType(getTrackType());
//        return result;
//    }
//
//    public void selectCheckBox(List<String> tags) {
//
//        categoryList.forEach(c->c.selectTags(tags));
//    }
//
//    public void setTrackType(TrackType type) {
//        radioButtonDemo.setSelected(false);
//        radioButtonFragment.setSelected(false);
//        radioButtonShard.setSelected(false);
//        if (type.equals(TrackType.DEMO)) {
//            radioButtonDemo.setSelected(true);
//        }
//        if (type.equals(TrackType.FRAGMENT)) {
//            radioButtonFragment.setSelected(true);
//        }
//        if (type.equals(TrackType.SHARD)) {
//            radioButtonShard.setSelected(true);
//        }
//    }
}
