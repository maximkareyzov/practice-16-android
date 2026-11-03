package com.example.practice16;

public final class Transform {
    private Transform() { }
    /** Преобразует строку в число или возвращает предыдущее значение. */
    public static int parseIntOrDefault(String value, int defaultValue) {
        try { return Integer.parseInt(value); }
        catch (NumberFormatException e) { return defaultValue; }
    }
}
