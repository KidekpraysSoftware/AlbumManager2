package com.kidekdev.albummanager.database.utils;

import lombok.SneakyThrows;

import java.nio.file.*;
import java.util.Set;
import java.util.stream.Collectors;

public class DatabaseUtils {

    @SneakyThrows
    public static Set<String> getDatabaseFolders() {
        Path databasePath = Paths.get("src/main/resources/database-example");

        if (!Files.exists(databasePath) || !Files.isDirectory(databasePath)) {
            throw new RuntimeException("Папка database-example не найдена: " + databasePath.toAbsolutePath());
        }

        try (var stream = Files.list(databasePath)) {
            return stream
                    .filter(Files::isDirectory)
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toSet());
        }
    }
}