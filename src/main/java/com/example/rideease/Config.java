package com.example.rideease;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Config {
    private static final Map<String, String> env = new HashMap<>();

    static {
        loadDotenv();
    }

    private static void loadDotenv() {
        File f = new File(".env");
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                int idx = line.indexOf('=');
                if (idx > 0) {
                    String k = line.substring(0, idx).trim();
                    String v = line.substring(idx + 1).trim();
                    if ((v.startsWith("\"") && v.endsWith("\"")) || (v.startsWith("'") && v.endsWith("'"))) {
                        v = v.substring(1, v.length() - 1);
                    }
                    env.put(k, v);
                }
            }
        } catch (IOException ignored) {
        }
    }

    public static String get(String key, String def) {
        String v = System.getenv(key);
        if (v != null) return v;
        v = System.getProperty(key);
        if (v != null) return v;
        v = env.get(key);
        return v != null ? v : def;
    }
}
