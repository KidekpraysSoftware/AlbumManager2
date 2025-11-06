module com.kidekdev.albummanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.yaml;
    requires static lombok;
    requires org.slf4j;

    opens com.kidekdev.albummanager to javafx.fxml;
    exports com.kidekdev.albummanager;

    exports com.kidekdev.albummanager.ui.controller;
    opens com.kidekdev.albummanager.ui.controller to javafx.fxml;

    exports com.kidekdev.albummanager.database.loader;
    opens com.kidekdev.albummanager.database.loader to javafx.fxml;

    exports com.kidekdev.albummanager.database.model.album;
    exports com.kidekdev.albummanager.database.model.common;
    exports com.kidekdev.albummanager.database.model.folder;
    exports com.kidekdev.albummanager.database.model.journal;
    exports com.kidekdev.albummanager.database.model.project;
    exports com.kidekdev.albummanager.database.model.resource;
    exports com.kidekdev.albummanager.database.model.tag;
    exports com.kidekdev.albummanager.database.model.view;

    opens com.kidekdev.albummanager.database.model.album to javafx.fxml;
    opens com.kidekdev.albummanager.database.model.common to javafx.fxml;
    opens com.kidekdev.albummanager.database.model.folder to javafx.fxml;
    opens com.kidekdev.albummanager.database.model.journal to javafx.fxml;
    opens com.kidekdev.albummanager.database.model.project to javafx.fxml;
    opens com.kidekdev.albummanager.database.model.resource to javafx.fxml;
    opens com.kidekdev.albummanager.database.model.tag to javafx.fxml;
    opens com.kidekdev.albummanager.database.model.view to javafx.fxml;

    exports com.kidekdev.albummanager.database.service;
    opens com.kidekdev.albummanager.database.service to javafx.fxml;
}