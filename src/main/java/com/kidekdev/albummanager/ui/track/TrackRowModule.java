package com.kidekdev.albummanager.ui.track;

import com.kidekdev.albummanager.ui.context.ActiveTrackHolder;
import com.kidekdev.albummanager.ui.utils.DurationFormatter;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import lombok.Getter;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Getter
public class TrackRowModule extends AnchorPane implements PlayableTrack {

    private final UUID trackId;
    private final Path trackPath;
    private final Media media;

    private final Button playButton = TrackRowModuleUI.createPlayButton();
    private final Button pauseButton = TrackRowModuleUI.createPauseButton();
    private final StackPane buttonStackPane = TrackRowModuleUI.createButtonStackPane(playButton, pauseButton);

    private final HBox markerHBox = TrackRowModuleUI.createMarkerBox();

    private final Label titleLabel;
    private final Label subtitleLabel;
    private final Label timeLabel;
    private final VBox trackInfoBox;
    private TrackPlayer trackPlayer;

    private final int SLIDER_INDEX = 2;
    private final int TIME_LABEL_INDEX = 3;

    public TrackRowModule(Path trackPath) {
       this(trackPath, null, trackPath.getFileName().toString(), "Импорт режим");
    }

    public TrackRowModule(Path trackPath, UUID trackId, String trackName, String authorName) {
        this.trackId = trackId;
        this.trackPath = trackPath;
        this.media = new Media(trackPath.toUri().toString());
        this.timeLabel = new Label(DurationFormatter.formatDuration(media.getDuration().toSeconds()));
        titleLabel = TrackRowModuleUI.createTitleLabel(trackName);
        subtitleLabel = TrackRowModuleUI.createSubtitleLabel(authorName);
        trackInfoBox = TrackRowModuleUI.createTrackInfoBox(titleLabel, subtitleLabel);
        getChildren().addAll(buttonStackPane, trackInfoBox, /*slider индекс [2]*/ timeLabel, markerHBox);
        setupRoot();
        initActions();
    }

    private void setupRoot() {
        setPrefWidth(332.0);
        setMinHeight(80.0);
        setPrefHeight(80.0);
        setMaxHeight(80.0);
        getStyleClass().add("track-player-item");
        getStylesheets().add(getClass().getResource("/css/module/TrackRowModule.css").toExternalForm());
    }

    private void initActions() {
        playButton.setOnAction(e -> {
            play();
        });
        pauseButton.setOnAction(e -> {
            pause();
        });
        this.setOnMousePressed(e -> play());
    }

    @Override
    public void play() {
        playButton.setVisible(false);
        pauseButton.setVisible(true);
        if (trackPlayer == null) {
            trackPlayer = new TrackPlayer(media, this);
            getChildren().add(SLIDER_INDEX, trackPlayer.getSlider());
//            getChildren().add(TIME_LABEL_INDEX, trackPlayer.getTimeLabel());
        }
        if (ActiveTrackHolder.activeTrack == null) {
            ActiveTrackHolder.activeTrack = this;
        }
        if (ActiveTrackHolder.activeTrack != this) {
            ActiveTrackHolder.activeTrack.stop();
            ActiveTrackHolder.activeTrack = this;
        }
        trackPlayer.play();

    }

    //полное выключение трека
    @Override
    public void stop() {
        pauseButton.setVisible(false);
        playButton.setVisible(true);
        if (trackPlayer != null) {
            trackPlayer.stop();
            getChildren().remove(SLIDER_INDEX);
//            getChildren().remove(TIME_LABEL_INDEX);
            trackPlayer.disableTrack();
            trackPlayer = null;
        }
    }

    @Override
    public void pause() {
        pauseButton.setVisible(false);
        playButton.setVisible(true);
        trackPlayer.pause();
    }

    public void endOfFile() {
        stop();
        Parent parent = this.getParent();
        if (parent instanceof Pane pane) {
            //Убираем из списка все ресурсы, которые не являются треками
            List<? extends PlayableTrack> playList = pane.getChildren().stream()
                    .filter(item -> item instanceof PlayableTrack)
                    .map(track -> (PlayableTrack) track)
                    .toList();
            int index = playList.indexOf(this); //получаем индекс этого трека
            if (playList.size() != index + 1) {
                playList.get(index + 1).play();
            }
        }
    }
}
