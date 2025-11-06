package com.kidekdev.albummanager.ui.controller;

import com.kidekdev.albummanager.database.loader.DatabaseLoader;
import com.kidekdev.albummanager.database.service.DatabaseService;
import com.kidekdev.albummanager.database.service.InMemoryDatabaseService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ImportTabController {
    @FXML
    private Button importButton;

    @FXML
    private VBox importList;

    @FXML
    private Label importPathLabel;

    @FXML
    private TextArea yearsTextAria;


}