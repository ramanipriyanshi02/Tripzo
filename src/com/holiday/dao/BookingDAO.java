package com.holiday.dao;

import com.holiday.db.DBConnection;
import com.holiday.model.Booking;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingDAO {

    public boolean addBooking(Booking booking) {
        String sql = "INSERT INTO bookings (customer_id, package_id, travel_date, travelers, total_amount, booking_status, offer_id, original_amount, discount_amount, booking_date) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, booking.getCustomerId());
            if (booking.getPackageId() > 0) {
                pstmt.setInt(2, booking.getPackageId());
            } else {
                pstmt.setNull(2, java.sql.Types.INTEGER);
            }
            pstmt.setDate(3, booking.getTravelDate());
            pstmt.setInt(4, booking.getTravelers());
            pstmt.setDouble(5, booking.getTotalAmount());
            pstmt.setString(6, booking.getBookingStatus() != null ? booking.getBookingStatus() : "Confirmed");

            if (booking.getOfferId() != null && booking.getOfferId() > 0) {
                pstmt.setInt(7, booking.getOfferId());
            } else {
                pstmt.setNull(7, java.sql.Types.INTEGER);
            }

            double origAmt = booking.getOriginalAmount() != null ? booking.getOriginalAmount() : booking.getTotalAmount();
            pstmt.setDouble(8, origAmt);
            pstmt.setDouble(9, booking.getDiscountAmount());
            pstmt.setDate(10, booking.getBookingDate() != null ? booking.getBookingDate() : new Date(System.currentTimeMillis()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        booking.setBookingId(generatedKeys.getInt(1));
                    }
                }
                // Atomically update usage count on festival offer
                if (booking.getOfferId() != null && booking.getOfferId() > 0) {
                    new FestivalOfferDAO().incrementTimesUsed(booking.getOfferId());
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Add Booking Error: " + e.getMessage());
        }
        return false;
    }

    public boolean updateBooking(Booking booking) {
        String sql = "UPDATE bookings SET customer_id = ?, package_id = ?, travel_date = ?, travelers = ?, "
                   + "total_amount = ?, booking_status = ?, offer_id = ?, original_amount = ?, discount_amount = ?, booking_date = ? "
                   + "WHERE booking_id = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, booking.getCustomerId());
            if (booking.getPackageId() > 0) {
                pstmt.setInt(2, booking.getPackageId());
            } else {
                pstmt.setNull(2, java.sql.Types.INTEGER);
            }
            pstmt.setDate(3, booking.getTravelDate());
            pstmt.setInt(4, booking.getTravelers());
            pstmt.setDouble(5, booking.getTotalAmount());
            pstmt.setString(6, booking.getBookingStatus());

            if (booking.getOfferId() != null && booking.getOfferId() > 0) {
                pstmt.setInt(7, booking.getOfferId());
            } else {
                pstmt.setNull(7, java.sql.Types.INTEGER);
            }

            double origAmt = booking.getOriginalAmount() != null ? booking.getOriginalAmount() : booking.getTotalAmount();
            pstmt.setDouble(8, origAmt);
            pstmt.setDouble(9, booking.getDiscountAmount());
            pstmt.setDate(10, booking.getBookingDate() != null ? booking.getBookingDate() : booking.getTravelDate());
            pstmt.setInt(11, booking.getBookingId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update Booking Error: " + e.getMessage());
            return false;
        }
    }

    public boolean cancelBooking(int bookingId) {
        String sql = "UPDATE bookings SET booking_status = 'Cancelled' WHERE booking_id = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Cancel Booking Error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteBooking(int bookingId) {
        String sql = "DELETE FROM bookings WHERE booking_id = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            boolean deleted = pstmt.executeUpdate() > 0;
            if (deleted) {
                DBConnection.syncAutoIncrement(conn, "bookings", "booking_id");
            }
            return deleted;
        } catch (SQLException e) {
            System.err.println("Delete Booking Error: " + e.getMessage());
            return false;
        }
    }

    public List<Booking> getAllBookings() {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, c.name AS customer_name, c.phone AS customer_phone, "
                   + "COALESCE(p.package_name, 'Custom Trip') AS package_name, "
                   + "COALESCE(p.destination, 'Custom') AS destination, "
                   + "COALESCE(p.price, b.total_amount) AS package_price, "
                   + "COALESCE(p.duration, 'Custom Duration') AS duration, "
                   + "fo.offer_name "
                   + "FROM bookings b "
                   + "JOIN customers c ON b.customer_id = c.customer_id "
                   + "LEFT JOIN packages p ON b.package_id = p.package_id "
                   + "LEFT JOIN festival_offers fo ON b.offer_id = fo.offer_id "
                   + "ORDER BY b.booking_id DESC";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return list;

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException e) {
            System.err.println("Get All Bookings Error: " + e.getMessage());
        }
        return list;
    }

    public List<Booking> searchBookings(String query) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, c.name AS customer_name, c.phone AS customer_phone, "
                   + "COALESCE(p.package_name, 'Custom Trip') AS package_name, "
                   + "COALESCE(p.destination, 'Custom') AS destination, "
                   + "COALESCE(p.price, b.total_amount) AS package_price, "
                   + "COALESCE(p.duration, 'Custom Duration') AS duration, "
                   + "fo.offer_name "
                   + "FROM bookings b "
                   + "JOIN customers c ON b.customer_id = c.customer_id "
                   + "LEFT JOIN packages p ON b.package_id = p.package_id "
                   + "LEFT JOIN festival_offers fo ON b.offer_id = fo.offer_id "
                   + "WHERE CAST(b.booking_id AS CHAR) LIKE ? OR c.name LIKE ? OR p.package_name LIKE ? OR p.destination LIKE ? OR fo.offer_name LIKE ? "
                   + "ORDER BY b.booking_id DESC";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return list;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String pattern = "%" + query + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            pstmt.setString(3, pattern);
            pstmt.setString(4, pattern);
            pstmt.setString(5, pattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToBooking(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Search Bookings Error: " + e.getMessage());
        }
        return list;
    }

    public Booking getBookingById(int bookingId) {
        String sql = "SELECT b.*, c.name AS customer_name, c.phone AS customer_phone, "
                   + "COALESCE(p.package_name, 'Custom Trip') AS package_name, "
                   + "COALESCE(p.destination, 'Custom') AS destination, "
                   + "COALESCE(p.price, b.total_amount) AS package_price, "
                   + "COALESCE(p.duration, 'Custom Duration') AS duration, "
                   + "fo.offer_name "
                   + "FROM bookings b "
                   + "JOIN customers c ON b.customer_id = c.customer_id "
                   + "LEFT JOIN packages p ON b.package_id = p.package_id "
                   + "LEFT JOIN festival_offers fo ON b.offer_id = fo.offer_id "
                   + "WHERE b.booking_id = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBooking(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Get Booking By Id Error: " + e.getMessage());
        }
        return null;
    }

    // Dynamic Dashboard Stats
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPackages", 0);
        stats.put("totalCustomers", 0);
        stats.put("totalBookings", 0);
        stats.put("totalRevenue", 0.0);
        stats.put("pendingPayments", 0);
        stats.put("upcomingTrips", 0);

        Connection conn = DBConnection.getConnection();
        if (conn == null) return stats;

        try {
            // Packages count
            try (PreparedStatement s = conn.prepareStatement("SELECT COUNT(*) FROM packages");
                 ResultSet rs = s.executeQuery()) {
                if (rs.next()) stats.put("totalPackages", rs.getInt(1));
            }
            // Customers count
            try (PreparedStatement s = conn.prepareStatement("SELECT COUNT(*) FROM customers");
                 ResultSet rs = s.executeQuery()) {
                if (rs.next()) stats.put("totalCustomers", rs.getInt(1));
            }
            // Bookings count
            try (PreparedStatement s = conn.prepareStatement("SELECT COUNT(*) FROM bookings");
                 ResultSet rs = s.executeQuery()) {
                if (rs.next()) stats.put("totalBookings", rs.getInt(1));
            }
            // Total Revenue (SUM of Paid amounts in payments table + confirmed total amount)
            try (PreparedStatement s = conn.prepareStatement("SELECT COALESCE(SUM(amount), 0) FROM payments WHERE payment_status = 'Paid'");
                 ResultSet rs = s.executeQuery()) {
                if (rs.next()) stats.put("totalRevenue", rs.getDouble(1));
            }
            // Pending Payments count
            try (PreparedStatement s = conn.prepareStatement("SELECT COUNT(*) FROM payments WHERE payment_status = 'Pending'");
                 ResultSet rs = s.executeQuery()) {
                if (rs.next()) stats.put("pendingPayments", rs.getInt(1));
            }
            // Upcoming Trips count (travel_date >= CURRENT_DATE and status != 'Cancelled')
            try (PreparedStatement s = conn.prepareStatement("SELECT COUNT(*) FROM bookings WHERE travel_date >= CURRENT_DATE AND booking_status != 'Cancelled'");
                 ResultSet rs = s.executeQuery()) {
                if (rs.next()) stats.put("upcomingTrips", rs.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Dashboard Stats Error: " + e.getMessage());
        }

        return stats;
    }

    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        Booking b = new Booking(
            rs.getInt("booking_id"),
            rs.getInt("customer_id"),
            rs.getInt("package_id"),
            rs.getDate("travel_date"),
            rs.getInt("travelers"),
            rs.getDouble("total_amount"),
            rs.getString("booking_status")
        );
        b.setCustomerName(rs.getString("customer_name"));
        b.setCustomerPhone(rs.getString("customer_phone"));
        b.setPackageName(rs.getString("package_name"));
        b.setDestination(rs.getString("destination"));
        b.setPackagePrice(rs.getDouble("package_price"));
        b.setDuration(rs.getString("duration"));

        try {
            int offId = rs.getInt("offer_id");
            if (!rs.wasNull() && offId > 0) {
                b.setOfferId(offId);
            }
            double origAmt = rs.getDouble("original_amount");
            if (!rs.wasNull()) {
                b.setOriginalAmount(origAmt);
            }
            b.setDiscountAmount(rs.getDouble("discount_amount"));
            b.setOfferName(rs.getString("offer_name"));
        } catch (SQLException ignored) {}

        try {
            Date bDate = rs.getDate("booking_date");
            if (bDate != null) {
                b.setBookingDate(bDate);
            }
        } catch (SQLException ignored) {}

        return b;
    }
}
