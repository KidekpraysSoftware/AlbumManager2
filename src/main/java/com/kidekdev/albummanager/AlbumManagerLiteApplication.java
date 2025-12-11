package com.kidekdev.albummanager;

import com.kidekdev.albummanager.database.HibernateUtil;
import com.kidekdev.albummanager.database.entity.ResourceEntity;
import com.kidekdev.albummanager.database.type.ResourceExtension;
import com.kidekdev.albummanager.database.type.ResourceType;
import com.kidekdev.albummanager.ui.dispatcher.EventDispatcher;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

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
        createResource();
//        Path databaseRoot = Path.of("C:/Users/Kidek/Desktop/AMMetadata");
//        DatabaseHolder.database = new DatabaseLoader().loadDatabase(databaseRoot);
//        DatabaseHolder.databaseFacade = new DatabaseFacadeImpl(DatabaseHolder.database);
    }

    public static void main(String[] args) {
        launch();
    }

    private static void createResource() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            ResourceEntity entity = new ResourceEntity();
            entity.setIsActive(true);
            entity.setPath("/demo/path.wav");
            entity.setHash("demo-hash");
            entity.setResourceType(ResourceType.TRACK);
            entity.setExtension(ResourceExtension.WAV);
            entity.setDescription("Demo resource created in example");
            entity.setFileCreationTime(OffsetDateTime.now());
            entity.setTags(new LinkedHashSet<>(List.of(UUID.randomUUID(),UUID.randomUUID())));
            session.persist(entity);

            transaction.commit();
        }
    }
}