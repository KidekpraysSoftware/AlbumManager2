package com.kidekdev.albummanager.ui.utils;

public class DurationFormatter {

    public static String formatDuration(double duration) {
        // Округляем до секунд
        long totalSeconds = Math.round(duration);

        // Переводим в часы, минуты и секунды
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        // Форматируем строку
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds); // даже если minutes = 0, выводим 0:SS
        }
    }
}
