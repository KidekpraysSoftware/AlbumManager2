//package com.kidekdev.albummanager.ui.tag;
//
//import javafx.scene.control.*;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.VBox;
//import lombok.Getter;
//import org.kidekdev.manager.common.dto.tag.CategoryDto;
//import org.kidekdev.manager.common.dto.tag.TagDto;
//
//import java.util.List;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//import static org.kidekdev.manager.frontend.utils.TextUtils.addTagConstraint;
//
//@Getter
//public class AddTagsModule extends VBox {
//    final UUID categoryId;
//    final Label name;
//    final List<CheckBox> tags;
//    final VBox tagList;
//    final int ordering;
//
//    public AddTagsModule(String groupName, List<TagDto> tagList) {
//        super();
//        this.setMinWidth(150);
//        this.categoryId = categoryDto.getCategoryId();
//        this.name = new Label(categoryDto.getName());
//        this.tags = categoryDto.getTags().stream().map(TagDto::getName).map(CheckBox::new).collect(Collectors.toList());
//        this.ordering = categoryDto.getOrdering();
//        tagList = new VBox();
//        tagList.getChildren().addAll(tags);
//        ScrollPane scrollPane = new ScrollPane(tagList);
//        this.getChildren().addAll(name, scrollPane);
//        createAddTagField();
//    }
//
//    public void searchTag(String name) {
//        tags.forEach(c -> {
//            if (name.equals("")) {
//                c.setStyle("-fx-background-color: transparent");
//                return;
//            }
//            if (containsIgnoreCase(c.getText(), name)) {
//                c.setStyle("-fx-background-color: #1f2c3c");
//            } else {
//                c.setStyle("-fx-background-color: transparent");
//            }
//        });
//    }
//
//    public static boolean containsIgnoreCase(String text, String substring) {
//        if (text == null || substring == null) return false;
//        return text.toLowerCase().contains(substring.toLowerCase());
//    }
//
////    public void addNewTag() {
////        TextField textField = new TextField("#");
////        textField.textProperty().addListener((obs, oldText, newText) -> {
////            // Если пользователь удалил # или начал с другого символа — вернуть #
////            if (!newText.startsWith("#")) {
////                newText = "#" + newText.replaceAll("#", "");
////            }
////            String allowed = newText.substring(1).replaceAll("[^a-zA-Zа-яА-Я0-9]", "");
////            if (allowed.length() > 19) { // 1 символ уже занят #
////                allowed = allowed.substring(0, 19);
////            }
////            String finalText = "#" + allowed;
////            if (!finalText.equals(newText)) {
////                textField.setText(finalText);
////            }
////        });
////        Button button = new Button("-");
////        HBox hBox = new HBox();
////        hBox.getChildren().addAll(textField, button);
////        button.setOnAction(e -> tagList.getChildren().remove(hBox));
////        tagList.getChildren().add(hBox);
////    }
//
//
//    void createAddTagField() {
//        HBox hBox = new HBox();
//        Label sharp = new Label("#");
//        TextField textField = new TextField();
//        textField.setMaxWidth(100);
//        Button button = new Button("+");
//        hBox.getChildren().addAll(sharp, textField, button);
//        var tags = tagList.getChildren();
//        button.setOnAction(actionEvent -> {
//            if (textField.getText().equals("")) {
//                return;
//            }
//            CheckBox checkBox = new CheckBox("#" + textField.getText());
//            checkBox.setSelected(true);
//            checkBox.setStyle("-fx-text-fill: #4cf829;");
//
//            tags.add(checkBox);
//            textField.setText("");
//        });
//        textField.setOnAction(actionEvent -> {
//            if (textField.getText().equals("")) {
//                return;
//            }
//            CheckBox checkBox = new CheckBox("#" + textField.getText());
//            checkBox.setSelected(true);
//            checkBox.setStyle("-fx-text-fill: #4cf829;");
//
//            tags.add(checkBox);
//            textField.setText("");
//        });
//
//        textField.textProperty().addListener((observable, oldValue, newValue) -> addTagConstraint(textField, newValue));
//        this.getChildren().add(hBox);
//    }
//
//    public List<String> getSelectedAndNewTags() {
//      return tagList.getChildren().stream()
//                .map(tag->(CheckBox) tag)
//                .filter(CheckBox::isSelected)
//                .map(Labeled::getText)
//                .collect(Collectors.toList());
////        for (Node node : tagList.getChildren()) {
////            if (node instanceof HBox hBox) {
////                for (Node child : hBox.getChildren()) {
////                    // Если CheckBox и он выбран
////                    if (child instanceof CheckBox checkBox && checkBox.isSelected()) {
////                        String text = checkBox.getText();
////                        if (text != null && !text.isBlank()) {
////                            result.add(text);
////                        }
////                    }
////
////                    // Если TextField — это новый тег
////                    if (child instanceof TextField textField) {
////                        String text = textField.getText();
////                        if (text != null && text.length() > 1 && text.startsWith("#")) {
////                            result.add(text);
////                        }
////                    }
////                }
////            }
////        }
//
//    }
//
//    public void selectTags(List<String> trackTags) {
//        trackTags.forEach(trackTag->{
//            tags.forEach(t->{
//                if (t.getText().equals(trackTag)) {
//                    t.setSelected(true);
//                }
//            });
//        });
//
//    }
//}
