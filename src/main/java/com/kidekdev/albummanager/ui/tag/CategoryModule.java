//package com.kidekdev.albummanager.ui.tag;
//
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.control.ScrollPane;
//import javafx.scene.layout.VBox;
//import lombok.Getter;
//import org.kidekdev.manager.common.dto.tag.CategoryDto;
//import org.kidekdev.manager.common.dto.tag.TagDto;
//import org.kidekdev.manager.frontend.dispatcher.EventDispatcher;
//import org.kidekdev.manager.frontend.event.dispatcher.FiltersChangedEvent;
//
//import java.util.*;
//
//@Getter
//public class CategoryModule extends VBox {
//
//    private final UUID categoryId;
//    private final String name;
//    private List<FilterRow> filterRows = new ArrayList<>();
//
//
//    public CategoryModule(CategoryDto categoryDto) {
//        categoryId = categoryDto.getCategoryId();
//        name = categoryDto.getName();
//        List<TagDto> tags = categoryDto.getTags();
//        Optional.ofNullable(tags)
//                .orElse(Collections.emptyList())
//                .stream()
//                .map(tag->new FilterRow(tag, categoryDto.getOrdering()))
//                .forEach(filterRows::add);
//
//        Button resetButton = new Button("Очистить");
//        Label categoryLabel = new Label(name);
//        ScrollPane scrollPane = new ScrollPane();
//        scrollPane.getStyleClass().add("filter-scroll-pane");
//        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
//        this.getChildren().add(categoryLabel);
//        this.getChildren().add(scrollPane);
//        this.getChildren().add(resetButton);
//
//        VBox tagList = new VBox(10);
//        tagList.getChildren().addAll(filterRows);
//        scrollPane.setContent(tagList);
//        resetButton.setOnAction(e -> {
//            tagList.getChildren().forEach(row -> {
//                if (row instanceof FilterRow filterRow) {
//                    filterRow.resetAll();
//                }
//            });
//            EventDispatcher.get().dispatch(new FiltersChangedEvent());
//        });
//    }
//
//
////   public CategoryModule(List<String> tags, List<String> included, List<String> excluded, String labelText) {
////        super(10);
////        Label categoryLabel = new Label(labelText);
////        this.getStyleClass().add("filter-module");
////
////        if (groups != null) {
////            List<ChoiceBox> choiceBoxList = new ArrayList<>();
////            groups.forEach(g -> {
////                ChoiceBox<String> choiceBox = new ChoiceBox<>();
////                List<String> groupTags = g.getTags();
////                choiceBox.getItems().addAll(groupTags);
////                choiceBox.getItems().add("Все");
////                choiceBox.setValue(g.getSelected());
////                choiceBox.setMaxWidth(Double.MAX_VALUE);
////                choiceBox.setOnAction(actionEvent -> {
////                    EventDispatcher.get().dispatch(new FiltersChangedEvent());
////                });
////                choiceBoxList.add(choiceBox);
////            });
////            this.getChildren().addAll(choiceBoxList);
////        }
////
////        ScrollPane scrollPane = new ScrollPane();
////        scrollPane.getStyleClass().add("filter-scroll-pane");
////        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
////        Button resetButton = new Button("Очистить");
////        this.getChildren().add(categoryLabel);
////        this.getChildren().add(scrollPane);
////        this.getChildren().add(resetButton);
////
////        VBox tagList = new VBox(10);
////        scrollPane.setContent(tagList);
////
//////        if (groups != null) {
//////            groups.forEach(g -> {
//////                HashSet<String> tagsSet = new HashSet<>(g.getTags());
//////                tagsSet.forEach(tag->filterRows.add(new FilterRow(tag)));
//////            });
//////        }
////        tagList.getChildren().addAll(filterRows);
////
////        //Заполнение choiceBox, если они есть
////        tags.forEach(tag -> {
////            FilterRow filterRow = new FilterRow(tag, included, excluded);
////            tagList.getChildren().add(filterRow);
////            filterRows.add(filterRow);
////        });
////
////
////        resetButton.setOnAction(e -> {
////            tagList.getChildren().forEach(row -> {
////                if (row instanceof FilterRow filterRow) {
////                    filterRow.resetAll();
////                }
////            });
////            EventDispatcher.get().dispatch(new FiltersChangedEvent());
////        });
////    }
//
//    public List<String> getIncludeTags() {
//        return filterRows.stream().filter(r -> r.getIncludeTagCheckBox().isSelected()).map(FilterRow::getTagName).toList();
//    }
//
//    public List<String> getExcludeTags() {
//        return filterRows.stream().filter(r -> r.getExcludeTagCheckBox().isSelected()).map(FilterRow::getTagName).toList();
//    }
//}
