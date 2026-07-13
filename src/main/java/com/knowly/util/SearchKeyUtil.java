package com.knowly.util;

public final class SearchKeyUtil {

    private SearchKeyUtil() {
    }

    public static String generate(String text) {

        if (text == null || text.isBlank()) {
            return null;
        }

        return text
                .trim()
                .toLowerCase()
                .replaceAll("\\s+", " ")
                .replaceAll("[^a-z0-9 ]", "")
                .replace(" ", "");
    }
}