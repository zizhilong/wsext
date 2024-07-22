package com.example.demo2.Tools;

public class StringHelper {
    public static String trimQuotes(String str) {
        if (str == null || str.length() < 2) {
            return str;
        }

        char firstChar = str.charAt(0);
        char lastChar = str.charAt(str.length() - 1);

        if ((firstChar == '"' && lastChar == '"') || (firstChar == '\'' && lastChar == '\'')) {
            return str.substring(1, str.length() - 1);
        }

        return str;
    }
}
