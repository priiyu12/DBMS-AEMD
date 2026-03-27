package com.aemd.backend;

import java.sql.*;

public class QueryOperations {

    public static class TableResult {
        public String top10;
        public String bottom10;

        public TableResult(String top10, String bottom10) {
            this.top10 = top10;
            this.bottom10 = bottom10;
        }
    }

    public static TableResult getTopAndBottom10(String tableName) {
        String safe = tableName.replaceAll("[^a-zA-Z0-9_]", "");
        String topJson = executeQuery("SELECT * FROM " + safe + " ORDER BY 1 ASC LIMIT 10");
        String bottomJson = executeQuery("SELECT * FROM " + safe + " ORDER BY 1 DESC LIMIT 10");
        return new TableResult(topJson, bottomJson);
    }

    private static String executeQuery(String sql) {
        StringBuilder sb = new StringBuilder();
        sb.append("[]");
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            StringBuilder arr = new StringBuilder();
            arr.append("[");
            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            boolean firstRow = true;

            while (rs.next()) {
                if (!firstRow) arr.append(",");
                firstRow = false;
                arr.append("{");
                for (int i = 1; i <= cols; i++) {
                    String col = meta.getColumnName(i);
                    String val = rs.getString(i);
                    if (val == null) val = "";
                    arr.append("\"").append(col).append("\":");
                    arr.append("\"").append(val.replace("\"", "\\\"")).append("\"");
                    if (i < cols) arr.append(",");
                }
                arr.append("}");
            }
            arr.append("]");
            sb = arr;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
