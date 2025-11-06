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
}