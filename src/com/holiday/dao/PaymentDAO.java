package com.holiday.dao;

import com.holiday.db.DBConnection;
import com.holiday.model.Payment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    public boolean addPayment(Payment payment) {
        String sql = "INSERT INTO payments (booking_id, payment_method, payment_status, payment_date, amount) VALUES (?, ?, ?, ?, ?)";
        Connection conn = DBConnection.getConnection();
        if (conn == null)
            return false;

        try (PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, payment.getBookingId());
            pstmt.setString(2, payment.getPaymentMethod());
            pstmt.setString(3, payment.getPaymentStatus());
            pstmt.setDate(4, payment.getPaymentDate());
            pstmt.setDouble(5, payment.getAmount());
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        payment.setPaymentId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Add Payment Error: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePayment(Payment payment) {
        String sql = "UPDATE payments SET booking_id = ?, payment_method = ?, payment_status = ?, payment_date = ?, amount = ? WHERE payment_id = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null)
            return false;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, payment.getBookingId());
            pstmt.setString(2, payment.getPaymentMethod());
            pstmt.setString(3, payment.getPaymentStatus());
            pstmt.setDate(4, payment.getPaymentDate());
            pstmt.setDouble(5, payment.getAmount());
            pstmt.setInt(6, payment.getPaymentId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update Payment Error: " + e.getMessage());
            return false;
        }
    }

    public boolean deletePayment(int paymentId) {
        String sql = "DELETE FROM payments WHERE payment_id = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null)
            return false;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, paymentId);
            boolean deleted = pstmt.executeUpdate() > 0;
            if (deleted) {
                DBConnection.syncAutoIncrement(conn, "payments", "payment_id");
            }
            return deleted;
        } catch (SQLException e) {
            System.err.println("Delete Payment Error: " + e.getMessage());
            return false;
        }
    }

    public List<Payment> getAllPayments() {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT p.*, c.name AS customer_name, COALESCE(pkg.package_name, 'Custom Trip') AS package_name, b.total_amount AS total_booking_amount "
                + "FROM payments p "
                + "JOIN bookings b ON p.booking_id = b.booking_id "
                + "JOIN customers c ON b.customer_id = c.customer_id "
                + "LEFT JOIN packages pkg ON b.package_id = pkg.package_id "
                + "ORDER BY p.payment_id DESC";
        Connection conn = DBConnection.getConnection();
        if (conn == null)
            return list;

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToPayment(rs));
            }
        } catch (SQLException e) {
            System.err.println("Get All Payments Error: " + e.getMessage());
        }
        return list;
    }

    public List<Payment> searchPayments(String query) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT p.*, c.name AS customer_name, COALESCE(pkg.package_name, 'Custom Trip') AS package_name, b.total_amount AS total_booking_amount "
                + "FROM payments p "
                + "JOIN bookings b ON p.booking_id = b.booking_id "
                + "JOIN customers c ON b.customer_id = c.customer_id "
                + "LEFT JOIN packages pkg ON b.package_id = pkg.package_id "
                + "WHERE CAST(p.payment_id AS CHAR) LIKE ? OR CAST(p.booking_id AS CHAR) LIKE ? OR c.name LIKE ? OR p.payment_status LIKE ? "
                + "ORDER BY p.payment_id DESC";
        Connection conn = DBConnection.getConnection();
        if (conn == null)
            return list;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String pattern = "%" + query + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            pstmt.setString(3, pattern);
            pstmt.setString(4, pattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPayment(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Search Payments Error: " + e.getMessage());
        }
        return list;
    }

    public Payment getPaymentByBookingId(int bookingId) {
        String sql = "SELECT p.*, c.name AS customer_name, COALESCE(pkg.package_name, 'Custom Trip') AS package_name, b.total_amount AS total_booking_amount "
                + "FROM payments p "
                + "JOIN bookings b ON p.booking_id = b.booking_id "
                + "JOIN customers c ON b.customer_id = c.customer_id "
                + "LEFT JOIN packages pkg ON b.package_id = pkg.package_id "
                + "WHERE p.booking_id = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null)
            return null;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPayment(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Get Payment By Booking Id Error: " + e.getMessage());
        }
        return null;
    }

    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment p = new Payment(
                rs.getInt("payment_id"),
                rs.getInt("booking_id"),
                rs.getString("payment_method"),
                rs.getString("payment_status"),
                rs.getDate("payment_date"),
                rs.getDouble("amount"));
        p.setCustomerName(rs.getString("customer_name"));
        p.setPackageName(rs.getString("package_name"));
        p.setTotalBookingAmount(rs.getDouble("total_booking_amount"));
        return p;
    }
}
