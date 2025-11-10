package com.kidekdev.albummanager.ui.controller;

import com.kidekdev.albummanager.database.facade.DatabaseFacade;
import com.kidekdev.albummanager.database.facade.DatabaseFacadeImpl;
import com.kidekdev.albummanager.database.loader.DatabaseLoader;
import com.kidekdev.albummanager.database.model.DataBase;
import com.kidekdev.albummanager.database.model.album.AlbumDto;
import com.kidekdev.albummanager.database.model.project.ProjectDto;
import com.kidekdev.albummanager.database.model.resource.ResourceDto;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class ImportTabController {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
            .withZone(ZoneId.systemDefault());

    private final DatabaseLoader databaseLoader = new DatabaseLoader();

    @FXML
    private Button loadDatabaseButton;

    @FXML
    private VBox importList;

    @FXML
    private Label importPathLabel;

    @FXML
    private Label statusLabel;

    private Path databasePath;
    private DataBase loadedDatabase;
    private DatabaseFacade databaseFacade;

    @FXML
    private void initialize() {
        databasePath = Path.of(System.getProperty("java.io.tmpdir"), "album-manager-demo-database");
        importPathLabel.setText(databasePath.toAbsolutePath().toString());
        importList.getChildren().clear();

        if (Files.exists(databasePath)) {
            statusLabel.setText("Обнаружена существующая база. Можно сразу загрузить данные.");
            loadDatabaseButton.setDisable(false);
        } else {
            statusLabel.setText("Нажмите \"Создать демо-базу\", чтобы подготовить структуру.");
            loadDatabaseButton.setDisable(true);
        }
    }

    @FXML
    private void handleCreateDatabase() {
        try {
            recreateDemoDatabase();
            loadDatabaseButton.setDisable(false);
            statusLabel.setText("База создана и заполнена демонстрационными данными. Теперь её можно загрузить.");
            importList.getChildren().setAll(createInfoLabel("Структура каталога пересоздана. Готово к загрузке."));
        } catch (Exception e) {
            statusLabel.setText("Ошибка при создании базы: " + readableMessage(e));
            loadDatabaseButton.setDisable(true);
            importList.getChildren().setAll(createInfoLabel("Не удалось подготовить демо-базу."));
        }
    }

    @FXML
    private void handleLoadDatabase() {
        try {
            DataBase database = databaseLoader.loadDatabase(databasePath);
            this.loadedDatabase = database;
            this.databaseFacade = new DatabaseFacadeImpl(database);
            renderDatabaseOverview();
            statusLabel.setText("База успешно загружена.");
        } catch (Exception e) {
            statusLabel.setText("Ошибка при загрузке базы: " + readableMessage(e));
            importList.getChildren().setAll(createInfoLabel("Попробуйте пересоздать демо-базу."));
        }
    }

    private void renderDatabaseOverview() {
        importList.getChildren().clear();

        importList.getChildren().add(createSectionLabel("Общая информация"));
        importList.getChildren().add(createInfoLabel("Ресурсов: " + loadedDatabase.resources().size()));
        importList.getChildren().add(createInfoLabel("Проектов: " + loadedDatabase.projects().size()));
        importList.getChildren().add(createInfoLabel("Альбомов: " + loadedDatabase.albums().size()));
        importList.getChildren().add(createInfoLabel("Импортов: " + loadedDatabase.imports().size()));
        importList.getChildren().add(createInfoLabel("Журналов: " + loadedDatabase.journals().size()));

        Map<String, List<String>> tagGroups = databaseFacade.getAllTagGroups();
        importList.getChildren().add(createSectionLabel("Группы тегов (" + tagGroups.size() + ")"));
        if (tagGroups.isEmpty()) {
            importList.getChildren().add(createInfoLabel("Глобальные теги не найдены."));
        } else {
            tagGroups.forEach((group, tags) ->
                    importList.getChildren().add(createInfoLabel(group + ": " + String.join(", ", tags))));
        }

        importList.getChildren().add(createSectionLabel("Проекты"));
        if (loadedDatabase.projects().isEmpty()) {
            importList.getChildren().add(createInfoLabel("Нет проектов для отображения."));
        } else {
            loadedDatabase.projects().values().forEach(project ->
                    importList.getChildren().add(createInfoLabel(describeProject(project))));
        }

        importList.getChildren().add(createSectionLabel("Альбомы"));
        if (loadedDatabase.albums().isEmpty()) {
            importList.getChildren().add(createInfoLabel("Нет альбомов для отображения."));
        } else {
            loadedDatabase.albums().values().forEach(album ->
                    importList.getChildren().add(createInfoLabel(describeAlbum(album))));
        }

        importList.getChildren().add(createSectionLabel("Ресурсы"));
        if (loadedDatabase.resources().isEmpty()) {
            importList.getChildren().add(createInfoLabel("Нет ресурсов для отображения."));
        } else {
            loadedDatabase.resources().values().stream().limit(5)
                    .forEach(resource -> importList.getChildren().add(createInfoLabel(describeResource(resource))));
            if (loadedDatabase.resources().size() > 5) {
                importList.getChildren().add(createInfoLabel("… и ещё "
                        + (loadedDatabase.resources().size() - 5) + " ресурсов"));
            }
        }
    }

    private String describeProject(ProjectDto project) {
        int attachedResources = project.getResources() == null ? 0 : project.getResources().size();
        String status = project.getStatus() != null ? project.getStatus().name() : "UNKNOWN";
        String name = nullToPlaceholder(project.getName(), "Без названия");
        return "• " + name + " (" + status + "), ресурсов: " + attachedResources;
    }

    private String describeAlbum(AlbumDto album) {
        int attachedResources = album.getResources() == null ? 0 : album.getResources().size();
        String status = album.getStatus() != null ? album.getStatus().name() : "UNKNOWN";
        String type = album.getType() != null ? album.getType().name() : "UNKNOWN";
        String name = nullToPlaceholder(album.getName(), "Без названия");
        return "• " + name + " [" + type + "] (" + status + "), треков: " + attachedResources;
    }

    private String describeResource(ResourceDto resource) {
        String type = resource.getResourceType() != null ? resource.getResourceType().name() : "UNKNOWN";
        String createdAt = resource.getCreated() == null
                ? "—"
                : DATE_FORMATTER.format(Instant.ofEpochMilli(resource.getCreated()));
        return "• " + resource.getId() + " (" + type + "), создан: " + createdAt;
    }

    private void recreateDemoDatabase() throws IOException, URISyntaxException {
        deleteIfExists(databasePath);
        databaseLoader.createDatabase(databasePath);
        copyDemoDatabase(databasePath);
    }

    private void deleteIfExists(Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }
        try (Stream<Path> walk = Files.walk(path)) {
            walk.sorted(Comparator.reverseOrder())
                    .forEach(current -> {
                        try {
                            Files.deleteIfExists(current);
                        } catch (IOException e) {
                            throw new RuntimeException("Не удалось удалить " + current + ": " + e.getMessage(), e);
                        }
                    });
        }
    }

    private void copyDemoDatabase(Path target) throws IOException, URISyntaxException {
        URL resourceUrl = Objects.requireNonNull(getClass().getResource("/database-example"),
                "Не найден каталог database-example в ресурсах");
        Path sourceRoot = Path.of(resourceUrl.toURI());
        try (Stream<Path> walk = Files.walk(sourceRoot)) {
            walk.forEach(source -> {
                Path relative = sourceRoot.relativize(source);
                Path destination = target.resolve(relative.toString());
                try {
                    if (Files.isDirectory(source)) {
                        Files.createDirectories(destination);
                    } else {
                        if (destination.getParent() != null) {
                            Files.createDirectories(destination.getParent());
                        }
                        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Не удалось скопировать " + source + ": " + e.getMessage(), e);
                }
            });
        }
    }

    private Label createSectionLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: bold; -fx-padding: 8 0 0 0;");
        return label;
    }

    private Label createInfoLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setStyle("-fx-padding: 2 0 0 12;");
        return label;
    }

    private String nullToPlaceholder(String value, String placeholder) {
        return value == null || value.isBlank() ? placeholder : value;
    }

    private String readableMessage(Exception e) {
        String message = e.getMessage();
        if (message == null || message.isBlank()) {
            return e.getClass().getSimpleName();
        }
        return message;
    }
}
