package com.kidekdev.albummanager.ui.track;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class TrackRowModuleUI {

    public static Button createPlayButton() {
        Button button = new Button();
        button.setPrefSize(27, 32);
        button.getStyleClass().add("play-button");
//        AnchorPane.setLeftAnchor(button, 10.0);
//        AnchorPane.setTopAnchor(button, 10.0);
        button.setVisible(true);
        return button;
    }

    public static Button createPauseButton() {
        Button button = new Button();
        button.setPrefSize(27, 32);
        button.getStyleClass().add("pause-button");
        button.setVisible(false);
//        AnchorPane.setLeftAnchor(button, 45.0); // рядом с play
//        AnchorPane.setTopAnchor(button, 10.0);
        button.setVisible(false);
        return button;
    }

    public static Label createTitleLabel(String title) {
        Label label = new Label(title);
        label.getStyleClass().add("track-title");
        return label;
    }

    public static Label createSubtitleLabel(String subtitle) {
        Label label = new Label(subtitle);
        label.getStyleClass().add("track-subtitle");
        return label;
    }

    public static VBox createTrackInfoBox(Label title, Label subtitle) {
        VBox vbox = new VBox(2, title, subtitle);
        vbox.getStyleClass().add("track-info-box");
        vbox.setMaxWidth(Double.MAX_VALUE);
        AnchorPane.setLeftAnchor(vbox, 50.0);
        AnchorPane.setTopAnchor(vbox, 6.0);
        return vbox;
    }

    public static Slider createSlider() {
        Slider slider = new Slider();
        slider.setPrefWidth(267);
        slider.getStyleClass().add("playback-slider");
        AnchorPane.setLeftAnchor(slider, 50.0);
        AnchorPane.setBottomAnchor(slider, 10.0);
        return slider;
    }

//    public static Label createTimeLabel() {
//        Label label = new Label("00:00");
//        label.getStyleClass().add("time-label");
//        return label;
//    }

    public static VBox createTimeBox(Label timeLabel) {
        VBox timeBox = new VBox(timeLabel);
        timeBox.setAlignment(Pos.TOP_RIGHT);
        AnchorPane.setRightAnchor(timeBox, 10.0);
        AnchorPane.setTopAnchor(timeBox, 39.0);
        return timeBox;
    }

    public static HBox createMarkerBox() {
        HBox box = new HBox(3);
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setPrefHeight(21);
        box.setPrefWidth(75);
        AnchorPane.setRightAnchor(box, 10.0);
        AnchorPane.setTopAnchor(box, 9.0);
        return box;
    }

    public static StackPane createButtonStackPane(Button playButton, Button pauseButton) {
        StackPane stackPane = new StackPane(playButton, pauseButton);
        AnchorPane.setLeftAnchor(stackPane, 10.0);
        AnchorPane.setTopAnchor(stackPane, 20.0);
        return stackPane;
    }
}
