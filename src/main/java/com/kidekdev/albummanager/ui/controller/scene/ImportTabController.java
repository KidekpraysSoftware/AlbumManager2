package com.kidekdev.albummanager.ui.controller.scene;

import com.kidekdev.albummanager.common.OperationResult;
import com.kidekdev.albummanager.database.dto.ImportRuleDto;
import com.kidekdev.albummanager.database.dto.ResourceDto;
import com.kidekdev.albummanager.database.type.ResourceType;
import com.kidekdev.albummanager.ui.context.DatabaseCache;
import com.kidekdev.albummanager.ui.context.DatabaseHolder;
import com.kidekdev.albummanager.ui.exception.AlertUtils;
import com.kidekdev.albummanager.ui.track.TrackRowModule;
import com.kidekdev.albummanager.ui.utils.CollectionUtils;
import com.kidekdev.albummanager.ui.utils.HashUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import lombok.extern.slf4j.Slf4j;
import com.kidekdev.albummanager.ui.utils.FileUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

@Slf4j
public class ImportTabController {
    @FXML
    private Button importButton;

    @FXML
    private VBox importList;

    @FXML
    private VBox newResourceVBox;

//    private final Map<UUID, ImportRuleDto> importRules = new LinkedHashMap<>();

    @FXML
    protected void initialize() {
        log.info("Инициализация ImportTabController");
        importButton.setOnAction(event -> handleCreateAutoImport());
        updateImportResourceList();
    }

    private void updateImportResourceList() {
        importList.getChildren().clear();
        newResourceVBox.getChildren().clear();
        List<ImportRuleDto> foundedRules = DatabaseHolder.importRule.findAll();
        if (foundedRules.isEmpty()) {
            return;
        }
        //заполнение списка путей
        foundedRules.stream()
                .sorted(Comparator.comparing(ImportRuleDto::path))
                .forEach(dto -> importList.getChildren().add(createRuleRow(dto)));

        //Поиск файлов в папках автоимпорта и обновление кеша хешей
        Map<ImportRuleDto, List<Path>> allFounded = new HashMap<>();
        foundedRules.forEach(dto -> {
            Path rootPath = Path.of(dto.path());
            Set<String> extensions = ResourceType.getExtentions(dto.resourceType());
            List<Path> founded = FileUtils.findFilesByExtensions(rootPath, extensions);
            allFounded.put(dto, founded);
        });
        Map<String, Path> hashPathMap = HashUtils.toResourceCache(allFounded);
        DatabaseCache.resourceCache.putAll(hashPathMap);

        //Проверка есть ли такие хеши в базе. Если нет, то добавляем в список претендентов на импорт
        Set<String> hashes = hashPathMap.keySet();
        List<String> foundedHashes = DatabaseHolder.resource.findAllByHash(hashes).stream().map(ResourceDto::hash).toList();
        List<Path> newResources = CollectionUtils.findNotFoundedResources(hashPathMap, foundedHashes);
        newResources.forEach(path -> newResourceVBox.getChildren().add(new TrackRowModule(path)));
    }

    private void handleCreateAutoImport() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Выберите папку для автоимпорта");
        File selectedDirectory = directoryChooser.showDialog(importButton.getScene().getWindow());

        if (selectedDirectory == null) {
            return;
        }

        Optional<ResourceType> selectedType = askForResourceType();
        if (selectedType.isEmpty()) {
            return;
        }

        String path = selectedDirectory.getAbsolutePath();
        ResourceType resourceType = selectedType.get();

        boolean isDuplicate = DatabaseHolder.importRule.findAll().stream()
                .anyMatch(rule -> rule.path().equals(path) && rule.resourceType() == resourceType);

        if (isDuplicate) {
            AlertUtils.showWarnAlert("Такое сочетание пути и типа ресурса уже существует");
            return;
        }

        ImportRuleDto dto = ImportRuleDto.builder()
                .path(path)
                .resourceType(resourceType)
                .isActive(true)
                .build();

        OperationResult result = DatabaseHolder.importRule.save(dto);
        if (!result.isSuccess()) {
            AlertUtils.showErrorAlert(result.message());
            return;
        }

        updateImportResourceList();
    }

    private Optional<ResourceType> askForResourceType() {
        ChoiceDialog<ResourceType> dialog = new ChoiceDialog<>(ResourceType.TRACK, ResourceType.values());
        dialog.setTitle("Тип ресурса");
        dialog.setHeaderText("Выберите тип ресурса для импорта");
        dialog.setContentText("Тип ресурса:");
        return dialog.showAndWait();
    }

    private HBox createRuleRow(ImportRuleDto dto) {

        HBox row = new HBox(20);
        row.getStyleClass().add("import-row");

        Label pathLabel = new Label(dto.path());
        pathLabel.getStyleClass().add("import-path");

        Label typeLabel = new Label(dto.resourceType().name());
        typeLabel.getStyleClass().add("import-type");

        row.getChildren().addAll(pathLabel, typeLabel);

        ContextMenu contextMenu = buildContextMenu(dto, row);
        row.setOnContextMenuRequested(event -> {
            contextMenu.show(row, event.getScreenX(), event.getScreenY());
            event.consume();
        });
        row.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(row, event.getScreenX(), event.getScreenY());
                event.consume();
            } else if (contextMenu.isShowing()) {
                contextMenu.hide();
            }
        });
        return row;
    }

    private ContextMenu buildContextMenu(ImportRuleDto dto, HBox row) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Удалить");
        deleteItem.setOnAction(event -> handleDeleteRule(dto, row));
        contextMenu.getItems().add(deleteItem);
        return contextMenu;
    }

    private void handleDeleteRule(ImportRuleDto dto, HBox row) {
        if (dto.id() != null) {
            OperationResult result = DatabaseHolder.importRule.delete(dto.id());
            if (!result.isSuccess()) {
                AlertUtils.showErrorAlert(result.message());
                updateImportResourceList();
                return;
            }
        }
        updateImportResourceList();
    }
}