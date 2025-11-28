package com.asynctide.turnbridge.service.upload;

import java.util.Locale;

/**
 * Helper for extracting the base message type (e.g., C0401, C0501) from raw Type strings.
 */
public final class MessageTypeUtils {

    private static final Locale LOCALE = Locale.ROOT;

    private MessageTypeUtils() {}

    public static String baseType(String raw) {
        if (raw == null) {
            return "";
        }
        String trimmed = removeFormatChars(raw.trim());
        int delimiter = trimmed.indexOf('|');
        if (delimiter >= 0) {
            trimmed = trimmed.substring(0, delimiter);
        }
        return trimmed.toUpperCase(LOCALE);
    }

    private static String removeFormatChars(String value) {
        if (value.isEmpty()) {
            return value;
        }
        StringBuilder builder = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (Character.getType(c) == Character.FORMAT) {
                continue;
            }
            builder.append(c);
        }
        return builder.toString();
    }
}
