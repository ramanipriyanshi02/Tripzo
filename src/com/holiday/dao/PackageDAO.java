package com.holiday.dao;

import com.holiday.db.DBConnection;
import com.holiday.model.Package;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PackageDAO {

    public boolean addPackage(Package pkg) {
        String sql = "INSERT INTO packages (package_name, destination, duration, price, hotel, transport, description) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, pkg.getPackageName());
            pstmt.setString(2, pkg.getDestination());
            pstmt.setString(3, pkg.getDuration());
            pstmt.setDouble(4, pkg.getPrice());
            pstmt.setString(5, pkg.getHotel());
            pstmt.setString(6, pkg.getTransport());
            pstmt.setString(7, pkg.getDescription());
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        pkg.setPackageId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Add Package Error: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePackage(Package pkg) {
        String sql = "UPDATE packages SET package_name = ?, destination = ?, duration = ?, price = ?, hotel = ?, transport = ?, description = ? WHERE package_id = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pkg.getPackageName());
            pstmt.setString(2, pkg.getDestination());
            pstmt.setString(3, pkg.getDuration());
            pstmt.setDouble(4, pkg.getPrice());
            pstmt.setString(5, pkg.getHotel());
            pstmt.setString(6, pkg.getTransport());
            pstmt.setString(7, pkg.getDescription());
            pstmt.setInt(8, pkg.getPackageId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update Package Error: " + e.getMessage());
            return false;
        }
    }

    public boolean deletePackage(int packageId) {
        String sql = "DELETE FROM packages WHERE package_id = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, packageId);
            boolean deleted = pstmt.executeUpdate() > 0;
            if (deleted) {
                DBConnection.syncAutoIncrement(conn, "packages", "package_id");
            }
            return deleted;
        } catch (SQLException e) {
            System.err.println("Delete Package Error: " + e.getMessage());
            return false;
        }
    }

    public List<Package> getAllPackages() {
        List<Package> list = new ArrayList<>();
        String sql = "SELECT * FROM packages ORDER BY package_id DESC";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return list;

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToPackage(rs));
            }
        } catch (SQLException e) {
            System.err.println("Get All Packages Error: " + e.getMessage());
        }
        return list;
    }

    public List<Package> searchPackages(String query) {
        List<Package> list = new ArrayList<>();
        String sql = "SELECT * FROM packages WHERE package_name LIKE ? OR destination LIKE ? ORDER BY package_id DESC";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return list;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + query + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPackage(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Search Packages Error: " + e.getMessage());
        }
        return list;
    }

    public Package getPackageById(int packageId) {
        String sql = "SELECT * FROM packages WHERE package_id = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, packageId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPackage(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Get Package By Id Error: " + e.getMessage());
        }
        return null;
    }

    private Package mapResultSetToPackage(ResultSet rs) throws SQLException {
        return new Package(
            rs.getInt("package_id"),
            rs.getString("package_name"),
            rs.getString("destination"),
            rs.getString("duration"),
            rs.getDouble("price"),
            rs.getString("hotel"),
            rs.getString("transport"),
            rs.getString("description")
        );
    }
}
