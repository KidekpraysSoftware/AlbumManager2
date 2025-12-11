package com.kidekdev.albummanager.ui.track;

import com.kidekdev.albummanager.ui.utils.DurationFormatter;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import lombok.Getter;

@Getter
public class TrackPlayer {

    private final TrackRowModule parent;
    private final MediaPlayer mediaPlayer;
    private final Label timeLabel = new Label();
    private final Timeline timeline;
    private final Media media;
    private final Slider slider = TrackRowModuleUI.createSlider();

    public TrackPlayer(Media media, TrackRowModule parent) {
        this.media = media;
        this.parent = parent;

        this.mediaPlayer = new MediaPlayer(media);
        this.timeline = new Timeline(new KeyFrame(Duration.millis(100), event -> updateTimeView()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeLabel.setText(DurationFormatter.formatDuration(media.getDuration().toSeconds()));
    }

    private void updateTimeView() {
        double sliderValue = slider.getValue(); // от 0 до 100
        double totalSec = media.getDuration().toSeconds(); // допустим, уже в секундах
        double currentSec = totalSec * (sliderValue / 100.0);
        String timeLabelText = DurationFormatter.formatDuration(currentSec);
        parent.getTimeLabel().setText(timeLabelText);
        setActions();
    }

    void setActions() {
        slider.setOnMousePressed(e -> mediaPlayer.pause());
        slider.setOnMouseReleased(e -> {
            if (!mediaPlayer.getTotalDuration().isUnknown()) {
                double seekTo = slider.getValue() / 100 * mediaPlayer.getTotalDuration().toMillis();
                mediaPlayer.seek(Duration.millis(seekTo));
                mediaPlayer.play();
            }
        });

        mediaPlayer.currentTimeProperty().addListener((obs, o, n) -> updateSlider());

        mediaPlayer.setOnEndOfMedia(parent::endOfFile);
    }

    private void updateSlider() {
//        log.info("Слайдер изменен {}, {}", p.getCurrentTime().toMillis(), s.getValue());
        // Избегаем деления на ноль
        if (!mediaPlayer.getTotalDuration().isUnknown()) {
            double percent = mediaPlayer.getCurrentTime().toMillis() / mediaPlayer.getTotalDuration().toMillis() * 100;
            slider.setValue(percent);
        }
    }

    public void pause() {
        mediaPlayer.pause();
        timeline.pause();
    }

    public void stop() {
        mediaPlayer.stop();
        timeline.stop();
        timeLabel.setText(DurationFormatter.formatDuration(media.getDuration().toSeconds()));
    }

    public void play() {
        mediaPlayer.play();
        timeline.play();
    }

    public void disableTrack() {
       mediaPlayer.dispose();
    }
}
