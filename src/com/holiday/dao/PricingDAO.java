package com.holiday.dao;

import com.holiday.db.DBConnection;
import com.holiday.model.Activity;
import com.holiday.model.DestinationPricing;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PricingDAO {

    public DestinationPricing getPricingForDestination(String destination) {
        String sql = "SELECT * FROM destination_pricing WHERE LOWER(destination) = LOWER(?)";
        Connection conn = DBConnection.getConnection();

        if (conn != null) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, destination != null ? destination.trim() : "");
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return mapResultSetToPricing(rs);
                    }
                }
            } catch (SQLException e) {
                System.err.println("Get Destination Pricing Error: " + e.getMessage());
            }

            // Fallback to 'Default' destination pricing in DB
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM destination_pricing WHERE destination = 'Default'");
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPricing(rs);
                }
            } catch (SQLException e) {
                System.err.println("Get Default Pricing Error: " + e.getMessage());
            }
        }

        // Hardcoded Fallback if DB is unavailable
        return new DestinationPricing(0, "Default", 1500.0, 2000.0, 3800.0, 6800.0, 1000.0, 1500.0, 3000.0, 6000.0, 10000.0, 300.0, 800.0, 1200.0, 2000.0);
    }

    public List<Activity> getAllActivities() {
        List<Activity> list = new ArrayList<>();
        String sql = "SELECT * FROM activities ORDER BY activity_name ASC";
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            list.add(new Activity(1, "Sightseeing", 1000.0));
            list.add(new Activity(2, "Adventure", 2500.0));
            list.add(new Activity(3, "Water Sports", 3000.0));
            list.add(new Activity(4, "City Tour", 1500.0));
            list.add(new Activity(5, "Museum Visit", 800.0));
            list.add(new Activity(6, "Trekking", 2000.0));
            list.add(new Activity(7, "Local Experience", 1200.0));
            return list;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Activity(
                    rs.getInt("activity_id"),
                    rs.getString("activity_name"),
                    rs.getDouble("price_per_person")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Get All Activities Error: " + e.getMessage());
        }
        return list;
    }

    private DestinationPricing mapResultSetToPricing(ResultSet rs) throws SQLException {
        return new DestinationPricing(
            rs.getInt("pricing_id"),
            rs.getString("destination"),
            rs.getDouble("base_cost"),
            rs.getDouble("hotel_3_star"),
            rs.getDouble("hotel_4_star"),
            rs.getDouble("hotel_5_star"),
            rs.getDouble("transport_bus"),
            rs.getDouble("transport_train"),
            rs.getDouble("transport_shared_cab"),
            rs.getDouble("transport_private_cab"),
            rs.getDouble("transport_flight"),
            rs.getDouble("meal_breakfast"),
            rs.getDouble("meal_map"),
            rs.getDouble("meal_ap"),
            rs.getDouble("service_charge")
        );
    }
}
