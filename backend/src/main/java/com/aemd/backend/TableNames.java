package com.aemd.backend;

import java.util.Set;
import java.util.HashSet;

public class TableNames {

    public static final Set<String> ALLOWED_TABLES = new HashSet<>();

    static {
        ALLOWED_TABLES.add("users");
        ALLOWED_TABLES.add("artwork");
        ALLOWED_TABLES.add("award");
        ALLOWED_TABLES.add("evaluation");
        ALLOWED_TABLES.add("exhibition");
        ALLOWED_TABLES.add("judge");
        ALLOWED_TABLES.add("notification");
        ALLOWED_TABLES.add("organizer");
        ALLOWED_TABLES.add("portfolio");
        ALLOWED_TABLES.add("portfolioartwork");
        ALLOWED_TABLES.add("student");
        ALLOWED_TABLES.add("submission");
        ALLOWED_TABLES.add("userdetails");
        ALLOWED_TABLES.add("venue");
        ALLOWED_TABLES.add("visitor");
        ALLOWED_TABLES.add("appa");
    }

    public static boolean isValid(String name) {
        return ALLOWED_TABLES.contains(name.toLowerCase());
    }
}
