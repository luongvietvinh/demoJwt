package com.example.demo.utils;

/**
 * String utility functions.
 * Các hàm tiện ích xử lý chuỗi.
 */
public final class StringUtils {

    private StringUtils() {
        throw new UnsupportedOperationException("Utility class - cannot be instantiated");
    }

    /**
     * Check if string is null or empty.
     * Kiểm tra chuỗi null hoặc rỗng.
     */
    public static boolean isEmpty(String input) {
        return input == null || input.isEmpty();
    }

    /**
     * Empty string constant.
     * Hằng số chuỗi rỗng.
     */
    public static final String EMPTY = "";

}
