package com.aemd.backend;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/temp";
    private static final String USER = "postgres";
    private static final String PASS = "1212";

    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            e.printStackTrace();
            return null;  // ApiServer checks for null
        }
    }
}
