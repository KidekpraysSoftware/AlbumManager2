package com.kidekdev.albummanager.database.type;

import java.util.Set;

public enum ResourceType {
    TRACK,
    IMAGE,
    MIDI,
    VIDEO;

    public static Set<String> getExtensions(ResourceType type) {
        if (type == null) {
            throw new RuntimeException("Не указан тип ресурса");
        }

        return switch (type) {
            case TRACK -> Set.of(
                    "wav",
                    "mp3",
                    "flac",
                    "aiff",
                    "ogg"
            );
            case IMAGE -> Set.of(
                    "png",
                    "jpeg",
                    "jpg"
            );
            case MIDI -> Set.of(
                    "mid",
                    "midi"
            );
            case VIDEO -> Set.of(
                    "mp4",
                    "mov",
                    "mkv"
            );
        };
    }

    public static ResourceType resolveType(ResourceExtension ext) {
        return switch (ext) {
            case WAV, MP3, FLAC, AIFF, OGG -> ResourceType.TRACK;
            case PNG, JPEG, JPG           -> ResourceType.IMAGE;
            case MID, MIDI                -> ResourceType.MIDI;
            case MP4, MOV, MKV            -> ResourceType.VIDEO;
        };
    }
}


