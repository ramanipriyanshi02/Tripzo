package com.holiday.model;

import java.sql.Date;

public class Booking {
    private int bookingId;
    private int customerId;
    private int packageId;
    private Date travelDate;
    private int travelers;
    private double totalAmount;
    private String bookingStatus;

    // Joined Display Fields
    private String customerName;
    private String customerPhone;
    private String packageName;
    private String destination;
    private double packagePrice;
    private String duration;

    private Integer offerId;
    private Double originalAmount;
    private double discountAmount;
    private String offerName;
    private Date bookingDate;

    public Booking() {}

    public Booking(int bookingId, int customerId, int packageId, Date travelDate, int travelers, double totalAmount, String bookingStatus) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.packageId = packageId;
        this.travelDate = travelDate;
        this.travelers = travelers;
        this.totalAmount = totalAmount;
        this.bookingStatus = bookingStatus;
        this.originalAmount = totalAmount;
        this.discountAmount = 0.0;
    }

    public Booking(int bookingId, int customerId, int packageId, Date travelDate, int travelers,
                   double totalAmount, String bookingStatus, Integer offerId, Double originalAmount, double discountAmount) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.packageId = packageId;
        this.travelDate = travelDate;
        this.travelers = travelers;
        this.totalAmount = totalAmount;
        this.bookingStatus = bookingStatus;
        this.offerId = offerId;
        this.originalAmount = originalAmount != null ? originalAmount : totalAmount;
        this.discountAmount = discountAmount;
    }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getPackageId() { return packageId; }
    public void setPackageId(int packageId) { this.packageId = packageId; }

    public Date getTravelDate() { return travelDate; }
    public void setTravelDate(Date travelDate) { this.travelDate = travelDate; }

    public int getTravelers() { return travelers; }
    public void setTravelers(int travelers) { this.travelers = travelers; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(String bookingStatus) { this.bookingStatus = bookingStatus; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public double getPackagePrice() { return packagePrice; }
    public void setPackagePrice(double packagePrice) { this.packagePrice = packagePrice; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public Integer getOfferId() { return offerId; }
    public void setOfferId(Integer offerId) { this.offerId = offerId; }

    public Double getOriginalAmount() { return originalAmount != null ? originalAmount : totalAmount; }
    public void setOriginalAmount(Double originalAmount) { this.originalAmount = originalAmount; }

    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }

    public String getOfferName() { return offerName; }
    public void setOfferName(String offerName) { this.offerName = offerName; }

    public Date getBookingDate() { return bookingDate != null ? bookingDate : travelDate; }
    public void setBookingDate(Date bookingDate) { this.bookingDate = bookingDate; }

    @Override
    public String toString() {
        return "Booking #" + bookingId + " - " + (customerName != null ? customerName : "Customer #" + customerId) 
                + " (" + (packageName != null ? packageName : "Pkg #" + packageId) + " @ ₹" + totalAmount + ")";
    }
}
