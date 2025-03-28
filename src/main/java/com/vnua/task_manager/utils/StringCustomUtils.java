package com.vnua.task_manager.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class StringCustomUtils {
    public static String convertToSnakeCase(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        String noDiacritics = removeAccents(input);

        // Chuyển đổi thành chữ thường, loại bỏ khoảng trắng dư thừa và thay bằng dấu "_"
        return noDiacritics.toLowerCase()
                .replaceAll("[^a-z0-9\s]", "") // Loại bỏ ký tự đặc biệt, chỉ giữ lại chữ cái và số
                .trim()
                .replaceAll("\s+", "_"); // Thay thế khoảng trắng bằng "_"
    }

    private static String removeAccents(String input) {
        String temp = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("");
    }

    public static String convertToSnakeCaseKeepDot(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        // Bỏ dấu tiếng Việt
        String noDiacritics = removeAccentsKeepDot(input);

        // Chuyển đổi thành chữ thường và chỉ giữ lại chữ cái, số, dấu chấm và khoảng trắng
        noDiacritics = noDiacritics.toLowerCase()
                .replaceAll("[^a-z0-9.\\s]", "") // Giữ lại dấu chấm
                .trim()
                .replaceAll("\\s+", "_"); // Thay thế khoảng trắng bằng "_"

        return noDiacritics;
    }

    private static String removeAccentsKeepDot(String input) {
        String temp = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("");
    }
}
