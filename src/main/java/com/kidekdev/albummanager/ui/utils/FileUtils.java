package com.kidekdev.albummanager.ui.utils;

import com.kidekdev.albummanager.database.type.ResourceExtension;
import com.kidekdev.albummanager.ui.dto.PathInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class FileUtils {



    public static PathInfo getPathInfo(Path path) {
        Objects.requireNonNull(path, "path");

        String fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex <= 0 || dotIndex == fileName.length() - 1) {
            throw new IllegalArgumentException("Файл без расширения или с некорректным именем: " + fileName);
        }

        String nameWithoutExtension = fileName.substring(0, dotIndex);
        String ext = fileName.substring(dotIndex + 1).toUpperCase(Locale.ROOT);

        ResourceExtension extension;
        try {
            extension = ResourceExtension.valueOf(ext);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неподдерживаемое расширение: " + ext);
        }

        return new PathInfo(nameWithoutExtension, extension);
    }

    private FileUtils() {
    }

    public static List<Path> findFilesByExtensions(Path root, Set<String> extensions) {
        if (root == null || extensions == null || extensions.isEmpty()) {
            return List.of();
        }

        if (!Files.exists(root)) {
            return List.of();
        }

        try (Stream<Path> stream = Files.walk(root)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(p -> hasExtension(p, extensions))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to scan directory: " + root, e);
        }
    }

    private static boolean hasExtension(Path path, Set<String> extensions) {
        String name = path.getFileName().toString().toLowerCase();
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex == -1) {
            return false;
        }
        String ext = name.substring(dotIndex + 1);
        return extensions.contains(ext);
    }

    public static void revealFileInExplorer(Path path) {
        if (path == null || !Files.exists(path)) return;

        try {
            String command = "explorer.exe /select,\"" + path.toAbsolutePath() + "\"";
            Runtime.getRuntime().exec(command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
