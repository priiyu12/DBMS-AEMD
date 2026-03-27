package com.aemd.backend;

import java.sql.*;

public class CRUDOperations {

    // INSERT artwork (returns artworkid if success, -1 otherwise)
    public static int insertArtwork(String title, String medium, String dimensions, Integer yearcreated, String description, String imageurl, int studentid, boolean isforsale) {
        int newId = -1;
        String sql = "INSERT INTO artwork (title, medium, dimensions, yearcreated, description, imageurl, studentid, isforsale) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING artworkid";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, medium);
            ps.setString(3, dimensions);
            if (yearcreated != null) ps.setInt(4, yearcreated); else ps.setNull(4, Types.INTEGER);
            ps.setString(5, description);
            ps.setString(6, imageurl);
            ps.setInt(7, studentid);
            ps.setBoolean(8, isforsale);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                newId = rs.getInt("artworkid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newId;
    }

    // INSERT visitor (returns visitor id)
    public static int insertVisitor(String tickettype, String visitdate, String feedback, Integer feedbackratings) {
        int newId = -1;
        String sql = "INSERT INTO visitor (tickettype, visitdate, feedback, feedbackratings) VALUES (?, ?, ?, ?) RETURNING userid";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tickettype);
            if (visitdate != null && !visitdate.trim().isEmpty()) ps.setDate(2, Date.valueOf(visitdate)); else ps.setNull(2, Types.DATE);
            ps.setString(3, feedback);
            if (feedbackratings != null) ps.setInt(4, feedbackratings); else ps.setNull(4, Types.INTEGER);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) newId = rs.getInt("userid");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newId;
    }

    // UPDATE notification status
    public static boolean updateNotificationStatus(int notificationid, String status) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE notification SET status = ? WHERE notificationid = ?")) {
            ps.setString(1, status);
            ps.setInt(2, notificationid);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // DELETE notification
    public static boolean deleteNotification(int notificationid) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM notification WHERE notificationid = ?")) {
            ps.setInt(1, notificationid);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
