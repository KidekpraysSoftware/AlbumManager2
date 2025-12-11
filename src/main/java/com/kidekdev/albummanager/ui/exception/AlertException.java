package com.kidekdev.albummanager.ui.exception;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

import java.io.PrintWriter;
import java.io.StringWriter;

public class AlertException extends RuntimeException{
    public AlertException(String message) {
        super(message);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(message);
        alert.setGraphic(null);
        StringWriter sw = new StringWriter();
        this.printStackTrace(new PrintWriter(sw));
        TextArea textArea = new TextArea(sw.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        alert.getDialogPane().setExpandableContent(textArea);
        alert.showAndWait();
    }
    public AlertException(String message, Exception e) {
        super(message);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(message);
        alert.setGraphic(null);
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        TextArea textArea = new TextArea(sw.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        alert.getDialogPane().setExpandableContent(textArea);
        alert.showAndWait();
    }


}
