package com.kidekdev.albummanager.database.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.kidekdev.albummanager.database.model.DataBase;
import com.kidekdev.albummanager.database.model.album.AlbumDto;
import com.kidekdev.albummanager.database.model.autoimport.ImportDto;
import com.kidekdev.albummanager.database.model.folder.FolderDto;
import com.kidekdev.albummanager.database.model.journal.JournalDto;
import com.kidekdev.albummanager.database.model.project.ProjectDto;
import com.kidekdev.albummanager.database.model.resource.ResourceDto;
import com.kidekdev.albummanager.database.model.tag.GlobalTagGroupsDto;
import com.kidekdev.albummanager.database.model.view.ViewDto;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * Loads YAML entities from a file system folder into the in-memory database representation.
 */
@Slf4j
public class DatabaseLoader {

    private static final Set<String> REQUIRED_FOLDERS = Set.of(
            "album",
            "folder",
            "import",
            "journal",
            "project",
            "resource",
            "view",
            "global"
    );

    private final ObjectMapper objectMapper;

    public DatabaseLoader() {
        this.objectMapper = new ObjectMapper(new YAMLFactory());
        this.objectMapper.findAndRegisterModules();
    }

    public DataBase loadDatabase(Path rootPath) {
        GlobalTagGroupsDto globalTagGroups = GlobalTagGroupsDto.load(rootPath);
        DataBase mutable = DataBase.builder()
                .globalTagGroups(globalTagGroups)
                .build();

        scanForEntities(rootPath, mutable.pathIndex());
        ensureUniqueIdentifiers(mutable.pathIndex());
        loadEntities(rootPath, mutable);

        return new DataBase(
                mutable.pathIndex(),
                mutable.resources(),
                mutable.albums(),
                mutable.folders(),
                mutable.projects(),
                mutable.journals(),
                mutable.views(),
                mutable.imports(),
                mutable.globalTagGroups()
        );
    }

    public DataBase createDatabase(Path rootPath) {
        if (rootPath == null) {
            throw new IllegalArgumentException("Database path must not be null");
        }

        try {
            Files.createDirectories(rootPath);
            for (String folder : REQUIRED_FOLDERS) {
                Files.createDirectories(rootPath.resolve(folder));
            }
            initializeTagGroups(rootPath.resolve("global").resolve("taggroups.yaml"));
            return loadDatabase(rootPath);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to create database at " + rootPath.toAbsolutePath(), e);
        }
    }

    private void initializeTagGroups(Path file) throws IOException {
        if (Files.exists(file)) {
            return;
        }

        Files.createDirectories(file.getParent());
        Files.writeString(
                file,
                "tagGroups: {}\n",
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE_NEW
        );
    }

    private void scanForEntities(Path rootPath, Map<Path, UUID> pathIndex) {
        if (!Files.exists(rootPath)) {
            throw new IllegalStateException("Database root does not exist: " + rootPath.toAbsolutePath());
        }

        try {
            Files.walk(rootPath)
                    .filter(Files::isRegularFile)
                    .filter(file -> file.getFileName().toString().endsWith(".yaml"))
                    .forEach(file -> registerYamlFile(rootPath, pathIndex, file));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to scan database folder: " + rootPath.toAbsolutePath(), e);
        }
    }

    private void registerYamlFile(Path rootPath, Map<Path, UUID> pathIndex, Path file) {
        String fileName = file.getFileName().toString();
        String uuidCandidate = fileName.substring(0, fileName.length() - ".yaml".length());
        UUID uuid = parseUuid(uuidCandidate, file);

        if (uuid == null) {
            // просто пропускаем невалидные UUID
            return;
        }

        Path relativePath = rootPath.relativize(file);
        pathIndex.put(relativePath, uuid);
    }

    private UUID parseUuid(String value, Path file) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            log.warn("Пропущен yaml файл без UUID в названии. {} ", file.toAbsolutePath());
            return null;
        }
    }

    private void ensureUniqueIdentifiers(Map<Path, UUID> pathIndex) {
        Set<UUID> identifiers = new HashSet<>();
        for (UUID uuid : pathIndex.values()) {
            if (!identifiers.add(uuid)) {
                throw new IllegalStateException("Duplicate UUID detected during database load: " + uuid);
            }
        }
    }

    private void loadEntities(Path rootPath, DataBase dataBase) {
        dataBase.pathIndex().forEach((path, uuid) -> loadEntity(rootPath, dataBase, path, uuid));
    }

    private void loadEntity(Path rootPath, DataBase dataBase, Path relativePath, UUID uuid) {
        if (relativePath.getNameCount() == 0) {
            throw new IllegalStateException("Invalid path without parent directory: " + relativePath);
        }

        String folderName = relativePath.getName(0).toString();
        Path absolutePath = rootPath.resolve(relativePath);

        switch (folderName) {
            case "resource" -> loadResource(dataBase, absolutePath, uuid);
            case "album" -> loadAlbum(dataBase, absolutePath, uuid);
            case "folder" -> loadFolder(dataBase, absolutePath, uuid);
            case "import" -> loadImport(dataBase, absolutePath, uuid);
            case "journal" -> loadJournal(dataBase, absolutePath, uuid);
            case "project" -> loadProject(dataBase, absolutePath, uuid);
            case "view" -> loadView(dataBase, absolutePath, uuid);
            default -> throw new IllegalStateException(
                    "Unsupported entity folder '%s' for file %s".formatted(folderName, relativePath));
        }
    }

    private void loadResource(DataBase dataBase, Path file, UUID uuid) {
        ResourceDto resource = readYaml(file, ResourceDto.class);
        dataBase.resources().put(uuid, resource);
        log.debug("Loaded resource {} from {}", uuid, file);
    }

    private void loadAlbum(DataBase dataBase, Path file, UUID uuid) {
        AlbumDto album = readYaml(file, AlbumDto.class);
        dataBase.albums().put(uuid, album);
        log.debug("Loaded album {} from {}", uuid, file);
    }

    private void loadFolder(DataBase dataBase, Path file, UUID uuid) {
        FolderDto folder = readYaml(file, FolderDto.class);
        dataBase.folders().put(uuid, folder);
        log.debug("Loaded folder {} from {}", uuid, file);
    }

    private void loadImport(DataBase dataBase, Path file, UUID uuid) {
        ImportDto importRule = readYaml(file, ImportDto.class);
        dataBase.imports().put(uuid, importRule);
        log.debug("Loaded import {} from {}", uuid, file);
    }

    private void loadJournal(DataBase dataBase, Path file, UUID uuid) {
        JournalDto journal = readYaml(file, JournalDto.class);
        dataBase.journals().put(uuid, journal);
        log.debug("Loaded journal {} from {}", uuid, file);
    }

    private void loadProject(DataBase dataBase, Path file, UUID uuid) {
        ProjectDto project = readYaml(file, ProjectDto.class);
        dataBase.projects().put(uuid, project);
        log.debug("Loaded project {} from {}", uuid, file);
    }

    private void loadView(DataBase dataBase, Path file, UUID uuid) {
        ViewDto view = readYaml(file, ViewDto.class);
        dataBase.views().put(uuid, view);
        log.debug("Loaded view {} from {}", uuid, file);
    }

    private <T> T readYaml(Path file, Class<T> type) {
        try {
            return objectMapper.readValue(file.toFile(), type);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read YAML file: " + file.toAbsolutePath(), e);
        }
    }
}

