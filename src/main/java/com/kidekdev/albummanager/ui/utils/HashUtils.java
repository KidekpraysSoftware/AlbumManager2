package com.kidekdev.albummanager.ui.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class HashUtils {
    public static Map<String, Path> toResourceCache(Map<?, List<Path>> source) {
        Map<String, Path> resourceCache = new HashMap<>();

        if (source == null || source.isEmpty()) {
            return resourceCache;
        }

        for (List<Path> paths : source.values()) {
            for (Path path : paths) {
                if (!Files.isRegularFile(path)) {
                    continue;
                }

                String sha256 = sha256(path);
                resourceCache.putIfAbsent(sha256, path);
            }
        }

        return resourceCache;
    }

    private static String sha256(Path path) {
        try (InputStream is = Files.newInputStream(path)) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
            return toHex(digest.digest());
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to calculate SHA-256 for " + path, e);
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
