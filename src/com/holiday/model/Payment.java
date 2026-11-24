package com.holiday.model;

import java.sql.Date;

public class Payment {
    private int paymentId;
    private int bookingId;
    private String paymentMethod;
    private String paymentStatus;
    private Date paymentDate;
    private double amount;

    // Joined Display Fields
    private String customerName;
    private String packageName;
    private double totalBookingAmount;

    public Payment() {}

    public Payment(int paymentId, int bookingId, String paymentMethod, String paymentStatus, Date paymentDate, double amount) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.paymentDate = paymentDate;
        this.amount = amount;
    }

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public Date getPaymentDate() { return paymentDate; }
    public void setPaymentDate(Date paymentDate) { this.paymentDate = paymentDate; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public double getTotalBookingAmount() { return totalBookingAmount; }
    public void setTotalBookingAmount(double totalBookingAmount) { this.totalBookingAmount = totalBookingAmount; }
}
