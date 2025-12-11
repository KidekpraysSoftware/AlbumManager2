package com.kidekdev.albummanager.ui.track;


import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

@Slf4j
public class CentralizedAudioPlayer {

    private static CentralizedAudioPlayer instance;

    private MediaPlayer mediaPlayer;
    private Path currentPath;

    private final ReadOnlyObjectWrapper<Duration> currentTime = new ReadOnlyObjectWrapper<>(Duration.ZERO);
    private final ReadOnlyObjectWrapper<Duration> totalDuration = new ReadOnlyObjectWrapper<>(Duration.ZERO);

    private CentralizedAudioPlayer() {}

    public static CentralizedAudioPlayer getInstance() {
        if (instance == null) {
            instance = new CentralizedAudioPlayer();
        }
        return instance;
    }

    /**
     * Создает новый MediaPlayer для указанного файла
     */
    public MediaPlayer init(Path path) {
        // Если уже открыт этот же файл — возвращаем текущий плеер
        if (mediaPlayer != null && path.equals(currentPath)) {
            return mediaPlayer;
        }

        // Если был другой плеер — освобождаем
        if (mediaPlayer != null) {
            mediaPlayer.dispose();
        }

        currentPath = path;
        Media media = new Media(path.toUri().toString());
        mediaPlayer = new MediaPlayer(media);

        // Настраиваем события
        mediaPlayer.setOnReady(() -> {
            totalDuration.set(mediaPlayer.getMedia().getDuration());
            log.info("Track ready: {} ({} sec)", path.getFileName(), totalDuration.get().toSeconds());
        });

        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            currentTime.set(newTime);
        });

        mediaPlayer.setOnEndOfMedia(() -> {
            log.info("Track ended: {}", path.getFileName());
            currentTime.set(Duration.ZERO);
        });

        return mediaPlayer;
    }

    /**
     * Запускает воспроизведение, если плеер инициализирован
     */
    public void play() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void seek(Duration duration) {
        if (mediaPlayer != null) {
            mediaPlayer.seek(duration);
        }
    }

    public ReadOnlyObjectProperty<Duration> currentTimeProperty() {
        return currentTime.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<Duration> totalDurationProperty() {
        return totalDuration.getReadOnlyProperty();
    }

    public Duration getCurrentTime() {
        return currentTime.get();
    }

    public Duration getTotalDuration() {
        return totalDuration.get();
    }
}