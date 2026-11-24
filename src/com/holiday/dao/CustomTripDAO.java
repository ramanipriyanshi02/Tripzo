package com.holiday.dao;

import com.holiday.db.DBConnection;
import com.holiday.model.CustomTrip;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomTripDAO {

    public boolean addCustomTrip(CustomTrip trip) {
        String sql = "INSERT INTO custom_trips (customer_id, destination, travel_date, return_date, travelers, duration, "
                   + "hotel, hotel_category, transport, meal_plan, activities, additional_services, additional_req, "
                   + "hotel_cost, transport_cost, meal_cost, activity_cost, additional_services_cost, service_charge, "
                   + "discount, final_price, per_person_cost, budget, status, booking_id) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, trip.getCustomerId());
            pstmt.setString(2, trip.getDestination());
            pstmt.setDate(3, trip.getTravelDate());
            pstmt.setDate(4, trip.getReturnDate());
            pstmt.setInt(5, trip.getTravelers());
            pstmt.setString(6, trip.getDuration());
            pstmt.setString(7, trip.getHotel());
            pstmt.setString(8, trip.getHotelCategory());
            pstmt.setString(9, trip.getTransport());
            pstmt.setString(10, trip.getMealPlan());
            pstmt.setString(11, trip.getActivities());
            pstmt.setString(12, trip.getAdditionalServices());
            pstmt.setString(13, trip.getAdditionalReq());
            pstmt.setDouble(14, trip.getHotelCost());
            pstmt.setDouble(15, trip.getTransportCost());
            pstmt.setDouble(16, trip.getMealCost());
            pstmt.setDouble(17, trip.getActivityCost());
            pstmt.setDouble(18, trip.getAdditionalServicesCost());
            pstmt.setDouble(19, trip.getServiceCharge());
            pstmt.setDouble(20, trip.getDiscount());
            pstmt.setDouble(21, trip.getFinalPrice());
            pstmt.setDouble(22, trip.getPerPersonCost());
            pstmt.setDouble(23, trip.getBudget());
            pstmt.setString(24, trip.getStatus() != null ? trip.getStatus() : "Draft");

            if (trip.getBookingId() != null && trip.getBookingId() > 0) {
                pstmt.setInt(25, trip.getBookingId());
            } else {
                pstmt.setNull(25, Types.INTEGER);
            }

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        trip.setTripId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Add Custom Trip Error: " + e.getMessage());
        }
        return false;
    }

    public boolean updateCustomTrip(CustomTrip trip) {
        String sql = "UPDATE custom_trips SET customer_id = ?, destination = ?, travel_date = ?, return_date = ?, "
                   + "travelers = ?, duration = ?, hotel = ?, hotel_category = ?, transport = ?, meal_plan = ?, "
                   + "activities = ?, additional_services = ?, additional_req = ?, hotel_cost = ?, transport_cost = ?, "
                   + "meal_cost = ?, activity_cost = ?, additional_services_cost = ?, service_charge = ?, discount = ?, "
                   + "final_price = ?, per_person_cost = ?, budget = ?, status = ?, booking_id = ? WHERE trip_id = ?";

        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, trip.getCustomerId());
            pstmt.setString(2, trip.getDestination());
            pstmt.setDate(3, trip.getTravelDate());
            pstmt.setDate(4, trip.getReturnDate());
            pstmt.setInt(5, trip.getTravelers());
            pstmt.setString(6, trip.getDuration());
            pstmt.setString(7, trip.getHotel());
            pstmt.setString(8, trip.getHotelCategory());
            pstmt.setString(9, trip.getTransport());
            pstmt.setString(10, trip.getMealPlan());
            pstmt.setString(11, trip.getActivities());
            pstmt.setString(12, trip.getAdditionalServices());
            pstmt.setString(13, trip.getAdditionalReq());
            pstmt.setDouble(14, trip.getHotelCost());
            pstmt.setDouble(15, trip.getTransportCost());
            pstmt.setDouble(16, trip.getMealCost());
            pstmt.setDouble(17, trip.getActivityCost());
            pstmt.setDouble(18, trip.getAdditionalServicesCost());
            pstmt.setDouble(19, trip.getServiceCharge());
            pstmt.setDouble(20, trip.getDiscount());
            pstmt.setDouble(21, trip.getFinalPrice());
            pstmt.setDouble(22, trip.getPerPersonCost());
            pstmt.setDouble(23, trip.getBudget());
            pstmt.setString(24, trip.getStatus());

            if (trip.getBookingId() != null && trip.getBookingId() > 0) {
                pstmt.setInt(25, trip.getBookingId());
            } else {
                pstmt.setNull(25, Types.INTEGER);
            }
            pstmt.setInt(26, trip.getTripId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update Custom Trip Error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateTripStatus(int tripId, String status, Integer bookingId) {
        String sql = "UPDATE custom_trips SET status = ?, booking_id = ? WHERE trip_id = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            if (bookingId != null && bookingId > 0) {
                pstmt.setInt(2, bookingId);
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setInt(3, tripId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update Trip Status Error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCustomTrip(int tripId) {
        String sql = "DELETE FROM custom_trips WHERE trip_id = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, tripId);
            boolean deleted = pstmt.executeUpdate() > 0;
            if (deleted) {
                DBConnection.syncAutoIncrement(conn, "custom_trips", "trip_id");
            }
            return deleted;
        } catch (SQLException e) {
            System.err.println("Delete Custom Trip Error: " + e.getMessage());
            return false;
        }
    }

    public List<CustomTrip> getAllCustomTrips() {
        List<CustomTrip> list = new ArrayList<>();
        String sql = "SELECT t.*, c.name AS customer_name FROM custom_trips t "
                   + "JOIN customers c ON t.customer_id = c.customer_id "
                   + "ORDER BY t.trip_id DESC";

        Connection conn = DBConnection.getConnection();
        if (conn == null) return list;

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToTrip(rs));
            }
        } catch (SQLException e) {
            System.err.println("Get Custom Trips Error: " + e.getMessage());
        }
        return list;
    }

    public List<CustomTrip> searchCustomTrips(String query) {
        List<CustomTrip> list = new ArrayList<>();
        String sql = "SELECT t.*, c.name AS customer_name FROM custom_trips t "
                   + "JOIN customers c ON t.customer_id = c.customer_id "
                   + "WHERE CAST(t.trip_id AS CHAR) LIKE ? OR c.name LIKE ? OR t.destination LIKE ? OR t.status LIKE ? "
                   + "ORDER BY t.trip_id DESC";

        Connection conn = DBConnection.getConnection();
        if (conn == null) return list;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String pattern = "%" + query + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            pstmt.setString(3, pattern);
            pstmt.setString(4, pattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToTrip(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Search Custom Trips Error: " + e.getMessage());
        }
        return list;
    }

    public CustomTrip getCustomTripById(int tripId) {
        String sql = "SELECT t.*, c.name AS customer_name FROM custom_trips t "
                   + "JOIN customers c ON t.customer_id = c.customer_id "
                   + "WHERE t.trip_id = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, tripId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTrip(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Get Custom Trip By Id Error: " + e.getMessage());
        }
        return null;
    }

    public int getCustomTripCount() {
        String sql = "SELECT COUNT(*) FROM custom_trips";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return 0;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Count Custom Trips Error: " + e.getMessage());
        }
        return 0;
    }

    public Map<String, Object> getCustomTripStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTrips", 0);
        stats.put("draftTrips", 0);
        stats.put("quotedTrips", 0);
        stats.put("approvedTrips", 0);
        stats.put("convertedTrips", 0);
        stats.put("cancelledTrips", 0);
        stats.put("totalEstimatedValue", 0.0);
        stats.put("totalConvertedValue", 0.0);

        Connection conn = DBConnection.getConnection();
        if (conn == null) return stats;

        try {
            try (PreparedStatement s = conn.prepareStatement("SELECT COUNT(*), COALESCE(SUM(final_price), 0) FROM custom_trips");
                 ResultSet rs = s.executeQuery()) {
                if (rs.next()) {
                    stats.put("totalTrips", rs.getInt(1));
                    stats.put("totalEstimatedValue", rs.getDouble(2));
                }
            }

            try (PreparedStatement s = conn.prepareStatement("SELECT status, COUNT(*), COALESCE(SUM(final_price), 0) FROM custom_trips GROUP BY status");
                 ResultSet rs = s.executeQuery()) {
                while (rs.next()) {
                    String st = rs.getString(1);
                    int count = rs.getInt(2);
                    double val = rs.getDouble(3);
                    if ("Draft".equalsIgnoreCase(st)) stats.put("draftTrips", count);
                    else if ("Quotation Generated".equalsIgnoreCase(st) || "Quotation".equalsIgnoreCase(st)) stats.put("quotedTrips", count);
                    else if ("Approved".equalsIgnoreCase(st)) stats.put("approvedTrips", count);
                    else if ("Converted to Booking".equalsIgnoreCase(st)) {
                        stats.put("convertedTrips", count);
                        stats.put("totalConvertedValue", val);
                    }
                    else if ("Cancelled".equalsIgnoreCase(st) || "Rejected".equalsIgnoreCase(st)) stats.put("cancelledTrips", count);
                }
            }
        } catch (SQLException e) {
            System.err.println("Get Custom Trip Stats Error: " + e.getMessage());
        }

        return stats;
    }

    private CustomTrip mapResultSetToTrip(ResultSet rs) throws SQLException {
        Integer bookingId = rs.getInt("booking_id");
        if (rs.wasNull()) bookingId = null;

        CustomTrip t = new CustomTrip(
            rs.getInt("trip_id"),
            rs.getInt("customer_id"),
            rs.getString("destination"),
            rs.getDate("travel_date"),
            rs.getDate("return_date"),
            rs.getInt("travelers"),
            rs.getString("duration"),
            rs.getString("hotel"),
            rs.getString("hotel_category"),
            rs.getString("transport"),
            rs.getString("meal_plan"),
            rs.getString("activities"),
            rs.getString("additional_services"),
            rs.getString("additional_req"),
            rs.getDouble("hotel_cost"),
            rs.getDouble("transport_cost"),
            rs.getDouble("meal_cost"),
            rs.getDouble("activity_cost"),
            rs.getDouble("additional_services_cost"),
            rs.getDouble("service_charge"),
            rs.getDouble("discount"),
            rs.getDouble("final_price"),
            rs.getDouble("per_person_cost"),
            rs.getDouble("budget"),
            rs.getString("status"),
            bookingId
        );
        t.setCustomerName(rs.getString("customer_name"));
        return t;
    }
}
