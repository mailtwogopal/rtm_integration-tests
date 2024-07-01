package com.booking.utils;

import java.util.HashMap;
import java.util.Map;

public class TestDataStore {
    private static Map<String, String> dataStore = new HashMap<>();

    public static void storeData(String key, String value) {
        dataStore.put(key, value);
    }

    public static String retrieveData(String key) {
        return dataStore.getOrDefault(key, null);
    }

    public static int cleanup() {
        dataStore.clear();
        return dataStore.size();
    }
}
