package com.kidekdev.albummanager.ui.utils;

import javafx.scene.control.TextField;

public class TextUtils {

    public static void addTagConstraint(TextField textField, String newValue) {
        // Здесь ты пишешь, что должно происходить при изменении текста
        // Удаляем символы # и пробелы
        String cleaned = newValue.replaceAll("[#\\s]", "");

        // Ограничиваем длину до 20 символов
        if (cleaned.length() > 20) {
            cleaned = cleaned.substring(0, 20);
        }

        // Делаем первую букву заглавной, остальное оставляем как есть
        if (!cleaned.isEmpty()) {
            cleaned = cleaned.substring(0, 1).toUpperCase() + cleaned.substring(1);
        }

        // Устанавливаем отфильтрованный текст, если он отличается от введённого
        if (!cleaned.equals(newValue)) {
            textField.setText(cleaned);
        }
    }
}
