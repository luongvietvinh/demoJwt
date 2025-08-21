package com.example.demo.utils;

/**
 * Constants used across the project.
 * Các hằng số dùng chung trong toàn bộ project.
 */
public final class Constants {

    private Constants() {
        throw new UnsupportedOperationException("Utility class - cannot be instantiated");
    }

    // Percent values
    public static final int TAX_IN_PERCENT = 10; // Thuế VAT 10%
    public static final int MAX_HUNDRED_PERCENT = 100; // 100%

    // Storage units
    public static final long ONE_KILOBYTE = 1024L;

}
