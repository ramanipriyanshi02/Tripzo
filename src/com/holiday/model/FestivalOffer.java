package com.holiday.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class FestivalOffer {

    private int offerId;
    private String offerName;
    private String festivalName;
    private String description;
    private String imagePath;
    private String discountType; // "Percentage" or "Fixed Amount"
    private double discountValue;
    private Double maxDiscount; // nullable limit for percentage discounts
    private double minBookingAmount;
    private Date startDate;
    private Date endDate;
    private Integer applicablePackageId; // null = all packages
    private String applicablePackageName; // joined field for display
    private String status; // "Active", "Scheduled", "Paused", "Draft", "Expired"
    private Integer usageLimit; // nullable
    private int timesUsed;
    private Timestamp createdAt;

    public FestivalOffer() {
        this.status = "Active";
        this.minBookingAmount = 0.0;
        this.timesUsed = 0;
    }

    public FestivalOffer(int offerId, String offerName, String festivalName, String description,
                         String imagePath, String discountType, double discountValue,
                         Double maxDiscount, double minBookingAmount, Date startDate, Date endDate,
                         Integer applicablePackageId, String status, Integer usageLimit) {
        this.offerId = offerId;
        this.offerName = offerName;
        this.festivalName = festivalName;
        this.description = description;
        this.imagePath = imagePath;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.maxDiscount = maxDiscount;
        this.minBookingAmount = minBookingAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.applicablePackageId = applicablePackageId;
        this.status = status;
        this.usageLimit = usageLimit;
        this.timesUsed = 0;
    }

    // Business Logic: Eligibility check
    public boolean isEligible(int packageId, double bookingAmount, Date targetDate) {
        if (!"Active".equalsIgnoreCase(this.status)) {
            return false;
        }

        // Check dates
        if (targetDate != null) {
            if (startDate != null && targetDate.before(startDate)) return false;
            if (endDate != null && targetDate.after(endDate)) return false;
        }

        // Check package eligibility
        if (applicablePackageId != null && applicablePackageId > 0) {
            if (applicablePackageId != packageId) {
                return false;
            }
        }

        // Check minimum booking amount
        if (bookingAmount < minBookingAmount) {
            return false;
        }

        // Check usage limit
        if (usageLimit != null && usageLimit > 0 && timesUsed >= usageLimit) {
            return false;
        }

        return true;
    }

    // Business Logic: Precise discount calculation using BigDecimal
    public double calculateDiscount(double originalAmount) {
        if (originalAmount <= 0.0) return 0.0;
        if (originalAmount < minBookingAmount) return 0.0;

        BigDecimal original = BigDecimal.valueOf(originalAmount);
        BigDecimal discount = BigDecimal.ZERO;

        if ("Percentage".equalsIgnoreCase(discountType)) {
            BigDecimal rate = BigDecimal.valueOf(discountValue).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            discount = original.multiply(rate).setScale(2, RoundingMode.HALF_UP);

            if (maxDiscount != null && maxDiscount > 0.0) {
                BigDecimal max = BigDecimal.valueOf(maxDiscount);
                if (discount.compareTo(max) > 0) {
                    discount = max;
                }
            }
        } else {
            // Fixed Amount
            discount = BigDecimal.valueOf(discountValue).setScale(2, RoundingMode.HALF_UP);
            if (discount.compareTo(original) > 0) {
                discount = original;
            }
        }

        return discount.doubleValue();
    }

    public String getDiscountBadgeText() {
        if ("Percentage".equalsIgnoreCase(discountType)) {
            return String.format("%.0f%% OFF", discountValue);
        } else {
            return String.format("₹%,.0f OFF", discountValue);
        }
    }

    public String getFormattedDateRange() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        String s = startDate != null ? sdf.format(startDate) : "Any";
        String e = endDate != null ? sdf.format(endDate) : "Any";
        return s + " - " + e;
    }

    // Getters and Setters
    public int getOfferId() { return offerId; }
    public void setOfferId(int offerId) { this.offerId = offerId; }

    public String getOfferName() { return offerName; }
    public void setOfferName(String offerName) { this.offerName = offerName; }

    public String getFestivalName() { return festivalName; }
    public void setFestivalName(String festivalName) { this.festivalName = festivalName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }

    public double getDiscountValue() { return discountValue; }
    public void setDiscountValue(double discountValue) { this.discountValue = discountValue; }

    public Double getMaxDiscount() { return maxDiscount; }
    public void setMaxDiscount(Double maxDiscount) { this.maxDiscount = maxDiscount; }

    public double getMinBookingAmount() { return minBookingAmount; }
    public void setMinBookingAmount(double minBookingAmount) { this.minBookingAmount = minBookingAmount; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public Integer getApplicablePackageId() { return applicablePackageId; }
    public void setApplicablePackageId(Integer applicablePackageId) { this.applicablePackageId = applicablePackageId; }

    public String getApplicablePackageName() { return applicablePackageName; }
    public void setApplicablePackageName(String applicablePackageName) { this.applicablePackageName = applicablePackageName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getUsageLimit() { return usageLimit; }
    public void setUsageLimit(Integer usageLimit) { this.usageLimit = usageLimit; }

    public int getTimesUsed() { return timesUsed; }
    public void setTimesUsed(int timesUsed) { this.timesUsed = timesUsed; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return offerName + " (" + getDiscountBadgeText() + ")";
    }
}
