package com.example.demo.utils;

import java.util.Map;

/**
 * Map utility functions.
 * Các hàm tiện ích xử lý Map.
 */
public final class MapUtils {

    private MapUtils() {
        throw new UnsupportedOperationException("Utility class - cannot be instantiated");
    }

    /**
     * Check if a map is empty or null.
     * Kiểm tra Map null hoặc rỗng.
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * Check if a map is not empty.
     * Kiểm tra Map không null và không rỗng.
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }
}
