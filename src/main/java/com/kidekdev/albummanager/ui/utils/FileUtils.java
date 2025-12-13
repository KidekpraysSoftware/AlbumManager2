package com.kidekdev.albummanager.ui.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class FileUtils {

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
