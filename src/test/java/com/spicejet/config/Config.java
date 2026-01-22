package com.spicejet.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Config {
    private static final String DEFAULT_CONFIG = "config.properties";
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream is = Config.class.getClassLoader().getResourceAsStream(DEFAULT_CONFIG)) {
            if (is == null) {
                throw new IllegalStateException("Missing " + DEFAULT_CONFIG + " on classpath (src/test/resources).");
            }
            PROPS.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + DEFAULT_CONFIG, e);
        }
    }

    private Config() {}

    public static String get(String key) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) return sys;
        String env = System.getenv(key);
        if (env != null && !env.isBlank()) return env;
        return PROPS.getProperty(key);
    }

    public static String getRequired(String key) {
        String v = get(key);
        if (v == null || v.isBlank()) {
            throw new IllegalStateException("Missing required config: " + key);
        }
        return v;
    }

    public static boolean getBoolean(String key, boolean defaultVal) {
        String v = get(key);
        if (v == null) return defaultVal;
        return Boolean.parseBoolean(v.trim());
    }

    public static int getInt(String key, int defaultVal) {
        String v = get(key);
        if (v == null) return defaultVal;
        try {
            return Integer.parseInt(v.trim());
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }
}

