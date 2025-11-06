package com.kidekdev.albummanager.ui.controller;

import com.kidekdev.albummanager.database.loader.DatabaseLoader;
import com.kidekdev.albummanager.database.service.DatabaseService;
import com.kidekdev.albummanager.database.service.InMemoryDatabaseService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.nio.file.Path;
import java.nio.file.Paths;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        DatabaseLoader loader = new DatabaseLoader();
        Path path = Paths.get("C:\\Users\\Kidek\\Desktop\\metadata");
        DatabaseService db = new InMemoryDatabaseService(loader.loadDatabase(path));
        welcomeText.setText(db.getAllTracks().get(0).getDescription());
    }
}