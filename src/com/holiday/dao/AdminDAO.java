package com.holiday.dao;

import com.holiday.db.DBConnection;
import com.holiday.model.Admin;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {

    public Admin authenticate(String username, String password) {
        String sql = "SELECT * FROM admin WHERE username = ? AND password = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            // Fallback Demo Login when database server is unreachable
            if ("admin".equalsIgnoreCase(username) && "admin123".equals(password)) {
                return new Admin(1, "admin", "admin123");
            }
            return null;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Admin(
                        rs.getInt("admin_id"),
                        rs.getString("username"),
                        rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Admin Authentication Error: " + e.getMessage());
        }
        return null;
    }

    public boolean updatePassword(int adminId, String newPassword) {
        String sql = "UPDATE admin SET password = ? WHERE admin_id = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, adminId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update Password Error: " + e.getMessage());
            return false;
        }
    }
}
