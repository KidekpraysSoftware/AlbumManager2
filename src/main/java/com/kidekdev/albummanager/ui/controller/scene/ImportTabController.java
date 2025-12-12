package com.kidekdev.albummanager.ui.controller.scene;

import com.kidekdev.albummanager.common.OperationResult;
import com.kidekdev.albummanager.database.dao.ImportRuleDatabaseFacade;
import com.kidekdev.albummanager.database.dao.impl.ImportRuleDatabaseFacadeImpl;
import com.kidekdev.albummanager.database.dto.ImportRuleDto;
import com.kidekdev.albummanager.database.type.ResourceType;
import com.kidekdev.albummanager.ui.exception.AlertUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class ImportTabController {
    @FXML
    private Button importButton;

    @FXML
    private VBox importList;

    private final ImportRuleDatabaseFacade importRuleDatabaseFacade = new ImportRuleDatabaseFacadeImpl();

    private final Map<UUID, ImportRuleDto> importRules = new LinkedHashMap<>();

    @FXML
    protected void initialize() {
        log.info("Инициализация ImportTabController");
        importButton.setOnAction(event -> handleCreateAutoImport());
        loadImportRules();
    }

    private void loadImportRules() {
        importList.getChildren().clear();
        importRules.clear();
        List<ImportRuleDto> rules = AlertUtils.wrapAndHandle(
                importRuleDatabaseFacade::findAll,
                "Не удалось загрузить правила импорта",
                List.of()
        );
        rules.stream()
                .sorted(Comparator.comparing(ImportRuleDto::path))
                .forEach(this::addRuleToView);
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

        boolean isDuplicate = importRules.values().stream()
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

        OperationResult result = importRuleDatabaseFacade.save(dto);
        if (!result.isSuccess()) {
            AlertUtils.showErrorAlert(result.message());
            return;
        }

        loadImportRules();
    }

    private Optional<ResourceType> askForResourceType() {
        ChoiceDialog<ResourceType> dialog = new ChoiceDialog<>(ResourceType.TRACK, ResourceType.values());
        dialog.setTitle("Тип ресурса");
        dialog.setHeaderText("Выберите тип ресурса для импорта");
        dialog.setContentText("Тип ресурса:");
        return dialog.showAndWait();
    }

    private void addRuleToView(ImportRuleDto dto) {
        if (dto.id() != null) {
            importRules.put(dto.id(), dto);
        }

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

        importList.getChildren().add(row);
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
            OperationResult result = importRuleDatabaseFacade.delete(dto.id());
            if (!result.isSuccess()) {
                AlertUtils.showErrorAlert(result.message());
                return;
            }
            importRules.remove(dto.id());
        }

        importList.getChildren().remove(row);
    }
}