package com.kidekdev.albummanager.ui.exception;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;
import java.util.function.Supplier;

public class AlertUtils {

    public static void wrap(String message, Runnable runnable) {
        try {
            runnable.run();
        } catch (AlertException e) {
            throw new RuntimeException();
        } catch (Exception e) {
            throw new AlertException(message, e);
        }
    }

    public static <T> T wrapAndReturn(String message, Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (AlertException e) {
            throw new RuntimeException();
        } catch (Exception e) {
            throw new AlertException(message, e);
        }
    }

    public static <T> T wrapAndHandle(Supplier<T> supplier, String message, T defaultValue) {
        try {
            return supplier.get();
        } catch (Exception e) {
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
            return defaultValue;
        }
    }

    public static void wrapAndAssert(String message, boolean assertion) {
        if (!assertion) {
            throw new AlertException(message);
        }
    }

    public static void showErrorAlert(String message) {
        var alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showWarnAlert(String message) {
        var alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Предупреждение");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

//    public static void confirmOrThrow(String message, String title) {
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setTitle(title);
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//        alert.setGraphic(null);
//
//        ButtonType yesButton = new ButtonType("Да", ButtonBar.ButtonData.YES);
//        ButtonType noButton = new ButtonType("Нет", ButtonBar.ButtonData.NO);
//        alert.getButtonTypes().setAll(yesButton, noButton);
//
//        Optional<ButtonType> result = alert.showAndWait();
//
//        if (result.isEmpty() || result.get() == noButton) {
//            throw new RuntimeException("Пользователь отменил операцию");
//        }
//    }

    public static boolean confirm(String message, String title, String canselMessage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.setGraphic(null);

        ButtonType yesButton = new ButtonType("Продолжить", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType(canselMessage, ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = alert.showAndWait();

        return result.isPresent() && result.get() == yesButton;
    }

}
