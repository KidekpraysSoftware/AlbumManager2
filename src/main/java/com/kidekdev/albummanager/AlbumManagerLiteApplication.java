package com.kidekdev.albummanager;

import com.kidekdev.albummanager.common.OperationResult;
import com.kidekdev.albummanager.database.facade.DatabaseFacadeImpl;
import com.kidekdev.albummanager.database.loader.DatabaseLoader;
import com.kidekdev.albummanager.database.model.DataBase;
import com.kidekdev.albummanager.ui.context.DatabaseHolder;
import com.kidekdev.albummanager.ui.dispatcher.EventDispatcher;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

@Slf4j
public class AlbumManagerLiteApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException, URISyntaxException {
        initBackend();
        FXMLLoader fxmlLoader = new FXMLLoader(AlbumManagerLiteApplication.class.getResource("/fxml/Application.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("Album manager lite");
        stage.setScene(scene);
        stage.show();
    }

    private void initBackend(){
        EventDispatcher.scanAndRegisterHandlers("com.kidekdev.albummanager.ui");

        Path databaseRoot = Path.of("C:/Users/Kidek/Desktop/AMMetadata");
        DatabaseHolder.database = new DatabaseLoader().loadDatabase(databaseRoot);
        DatabaseHolder.databaseFacade = new DatabaseFacadeImpl(DatabaseHolder.database);
    }

    public static void main(String[] args) {
        launch();
    }
}