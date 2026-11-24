package com.holiday.dao;

import com.holiday.db.DBConnection;
import com.holiday.model.FestivalOffer;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FestivalOfferDAO {

    public boolean addOffer(FestivalOffer offer) {
        String sql = "INSERT INTO festival_offers (offer_name, festival_name, description, image_path, "
                + "discount_type, discount_value, max_discount, min_booking_amount, start_date, end_date, "
                + "applicable_package_id, status, usage_limit, times_used) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, offer.getOfferName());
            pstmt.setString(2, offer.getFestivalName());
            pstmt.setString(3, offer.getDescription());
            pstmt.setString(4, offer.getImagePath());
            pstmt.setString(5, offer.getDiscountType());
            pstmt.setDouble(6, offer.getDiscountValue());

            if (offer.getMaxDiscount() != null && offer.getMaxDiscount() > 0) {
                pstmt.setDouble(7, offer.getMaxDiscount());
            } else {
                pstmt.setNull(7, java.sql.Types.DECIMAL);
            }

            pstmt.setDouble(8, offer.getMinBookingAmount());
            pstmt.setDate(9, offer.getStartDate());
            pstmt.setDate(10, offer.getEndDate());

            if (offer.getApplicablePackageId() != null && offer.getApplicablePackageId() > 0) {
                pstmt.setInt(11, offer.getApplicablePackageId());
            } else {
                pstmt.setNull(11, java.sql.Types.INTEGER);
            }

            pstmt.setString(12, offer.getStatus() != null ? offer.getStatus() : "Active");

            if (offer.getUsageLimit() != null && offer.getUsageLimit() > 0) {
                pstmt.setInt(13, offer.getUsageLimit());
            } else {
                pstmt.setNull(13, java.sql.Types.INTEGER);
            }

            pstmt.setInt(14, offer.getTimesUsed());

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        offer.setOfferId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Add Festival Offer Error: " + e.getMessage());
        }
        return false;
    }

    public boolean updateOffer(FestivalOffer offer) {
        String sql = "UPDATE festival_offers SET offer_name = ?, festival_name = ?, description = ?, "
                + "image_path = ?, discount_type = ?, discount_value = ?, max_discount = ?, "
                + "min_booking_amount = ?, start_date = ?, end_date = ?, applicable_package_id = ?, "
                + "status = ?, usage_limit = ? WHERE offer_id = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, offer.getOfferName());
            pstmt.setString(2, offer.getFestivalName());
            pstmt.setString(3, offer.getDescription());
            pstmt.setString(4, offer.getImagePath());
            pstmt.setString(5, offer.getDiscountType());
            pstmt.setDouble(6, offer.getDiscountValue());

            if (offer.getMaxDiscount() != null && offer.getMaxDiscount() > 0) {
                pstmt.setDouble(7, offer.getMaxDiscount());
            } else {
                pstmt.setNull(7, java.sql.Types.DECIMAL);
            }

            pstmt.setDouble(8, offer.getMinBookingAmount());
            pstmt.setDate(9, offer.getStartDate());
            pstmt.setDate(10, offer.getEndDate());

            if (offer.getApplicablePackageId() != null && offer.getApplicablePackageId() > 0) {
                pstmt.setInt(11, offer.getApplicablePackageId());
            } else {
                pstmt.setNull(11, java.sql.Types.INTEGER);
            }

            pstmt.setString(12, offer.getStatus());

            if (offer.getUsageLimit() != null && offer.getUsageLimit() > 0) {
                pstmt.setInt(13, offer.getUsageLimit());
            } else {
                pstmt.setNull(13, java.sql.Types.INTEGER);
            }

            pstmt.setInt(14, offer.getOfferId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update Festival Offer Error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateOfferStatus(int offerId, String newStatus) {
        String sql = "UPDATE festival_offers SET status = ? WHERE offer_id = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, offerId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update Offer Status Error: " + e.getMessage());
            return false;
        }
    }

    public boolean incrementTimesUsed(int offerId) {
        String sql = "UPDATE festival_offers SET times_used = times_used + 1 WHERE offer_id = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, offerId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Increment Offer Usage Error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteOffer(int offerId) {
        String sql = "DELETE FROM festival_offers WHERE offer_id = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, offerId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Delete Festival Offer Error: " + e.getMessage());
            return false;
        }
    }

    public FestivalOffer getOfferById(int offerId) {
        String sql = "SELECT f.*, p.package_name AS applicable_package_name "
                + "FROM festival_offers f "
                + "LEFT JOIN packages p ON f.applicable_package_id = p.package_id "
                + "WHERE f.offer_id = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, offerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToOffer(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Get Offer By Id Error: " + e.getMessage());
        }
        return null;
    }

    public List<FestivalOffer> getAllOffers() {
        List<FestivalOffer> list = new ArrayList<>();
        String sql = "SELECT f.*, p.package_name AS applicable_package_name "
                + "FROM festival_offers f "
                + "LEFT JOIN packages p ON f.applicable_package_id = p.package_id "
                + "ORDER BY f.offer_id DESC";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return list;

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToOffer(rs));
            }
        } catch (SQLException e) {
            System.err.println("Get All Offers Error: " + e.getMessage());
        }
        return list;
    }

    public List<FestivalOffer> getActiveOffers(Date targetDate) {
        List<FestivalOffer> list = new ArrayList<>();
        String sql = "SELECT f.*, p.package_name AS applicable_package_name "
                + "FROM festival_offers f "
                + "LEFT JOIN packages p ON f.applicable_package_id = p.package_id "
                + "WHERE f.status = 'Active' "
                + "AND (? IS NULL OR (? BETWEEN f.start_date AND f.end_date)) "
                + "AND (f.usage_limit IS NULL OR f.times_used < f.usage_limit) "
                + "ORDER BY f.discount_value DESC";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return list;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, targetDate);
            pstmt.setDate(2, targetDate);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToOffer(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Get Active Offers Error: " + e.getMessage());
        }
        return list;
    }

    public List<FestivalOffer> getEligibleOffersForBooking(int packageId, double amount, Date travelDate) {
        List<FestivalOffer> eligible = new ArrayList<>();
        List<FestivalOffer> activeOffers = getActiveOffers(travelDate);
        for (FestivalOffer offer : activeOffers) {
            if (offer.isEligible(packageId, amount, travelDate)) {
                eligible.add(offer);
            }
        }
        return eligible;
    }

    public Map<String, Object> getOfferStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOffers", 0);
        stats.put("activeOffers", 0);
        stats.put("scheduledOffers", 0);
        stats.put("expiredOffers", 0);
        stats.put("pausedOffers", 0);
        stats.put("totalDiscountsGiven", 0.0);
        stats.put("totalOfferBookings", 0);
        stats.put("totalOfferBookingValue", 0.0);

        Connection conn = DBConnection.getConnection();
        if (conn == null) return stats;

        try {
            // Count totals by status
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT "
                         + "COUNT(*) AS total, "
                         + "SUM(CASE WHEN status = 'Active' THEN 1 ELSE 0 END) AS active_cnt, "
                         + "SUM(CASE WHEN status = 'Scheduled' THEN 1 ELSE 0 END) AS scheduled_cnt, "
                         + "SUM(CASE WHEN status = 'Expired' OR end_date < CURRENT_DATE THEN 1 ELSE 0 END) AS expired_cnt, "
                         + "SUM(CASE WHEN status = 'Paused' THEN 1 ELSE 0 END) AS paused_cnt "
                         + "FROM festival_offers")) {
                if (rs.next()) {
                    stats.put("totalOffers", rs.getInt("total"));
                    stats.put("activeOffers", rs.getInt("active_cnt"));
                    stats.put("scheduledOffers", rs.getInt("scheduled_cnt"));
                    stats.put("expiredOffers", rs.getInt("expired_cnt"));
                    stats.put("pausedOffers", rs.getInt("paused_cnt"));
                }
            }

            // Bookings financial impact with applied offers
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT "
                         + "COALESCE(SUM(discount_amount), 0.0) AS total_discounts, "
                         + "COUNT(booking_id) AS offer_bookings, "
                         + "COALESCE(SUM(total_amount), 0.0) AS offer_booking_value "
                         + "FROM bookings "
                         + "WHERE offer_id IS NOT NULL AND booking_status != 'Cancelled'")) {
                if (rs.next()) {
                    stats.put("totalDiscountsGiven", rs.getDouble("total_discounts"));
                    stats.put("totalOfferBookings", rs.getInt("offer_bookings"));
                    stats.put("totalOfferBookingValue", rs.getDouble("offer_booking_value"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Get Offer Statistics Error: " + e.getMessage());
        }

        return stats;
    }

    private FestivalOffer mapResultSetToOffer(ResultSet rs) throws SQLException {
        FestivalOffer o = new FestivalOffer();
        o.setOfferId(rs.getInt("offer_id"));
        o.setOfferName(rs.getString("offer_name"));
        o.setFestivalName(rs.getString("festival_name"));
        o.setDescription(rs.getString("description"));
        o.setImagePath(rs.getString("image_path"));
        o.setDiscountType(rs.getString("discount_type"));
        o.setDiscountValue(rs.getDouble("discount_value"));

        double maxD = rs.getDouble("max_discount");
        if (!rs.wasNull()) {
            o.setMaxDiscount(maxD);
        } else {
            o.setMaxDiscount(null);
        }

        o.setMinBookingAmount(rs.getDouble("min_booking_amount"));
        o.setStartDate(rs.getDate("start_date"));
        o.setEndDate(rs.getDate("end_date"));

        int pkgId = rs.getInt("applicable_package_id");
        if (!rs.wasNull() && pkgId > 0) {
            o.setApplicablePackageId(pkgId);
        } else {
            o.setApplicablePackageId(null);
        }

        o.setStatus(rs.getString("status"));

        int limit = rs.getInt("usage_limit");
        if (!rs.wasNull() && limit > 0) {
            o.setUsageLimit(limit);
        } else {
            o.setUsageLimit(null);
        }

        o.setTimesUsed(rs.getInt("times_used"));
        o.setCreatedAt(rs.getTimestamp("created_at"));

        try {
            o.setApplicablePackageName(rs.getString("applicable_package_name"));
        } catch (SQLException ignored) {}

        return o;
    }
}
