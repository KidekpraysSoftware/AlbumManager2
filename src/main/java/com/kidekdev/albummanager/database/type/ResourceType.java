package com.kidekdev.albummanager.database.type;

import java.util.Set;

public enum ResourceType {
    TRACK,
    IMAGE,
    MIDI,
    VIDEO;

    public static Set<String> getExtentions(ResourceType type) {
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

}


