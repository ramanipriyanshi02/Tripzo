package com.holiday.model;

import java.sql.Date;

public class CustomTrip {
    private int tripId;
    private int customerId;
    private String customerName;
    private String destination;
    private Date travelDate;
    private Date returnDate;
    private int travelers;
    private String duration;
    private String hotel;
    private String hotelCategory;
    private String transport;
    private String mealPlan;
    private String activities;
    private String additionalServices;
    private String additionalReq;
    private double hotelCost;
    private double transportCost;
    private double mealCost;
    private double activityCost;
    private double additionalServicesCost;
    private double serviceCharge;
    private double discount;
    private double finalPrice;
    private double perPersonCost;
    private double budget;
    private String status;
    private Integer bookingId;

    public CustomTrip() {}

    public CustomTrip(int tripId, int customerId, String destination, Date travelDate, Date returnDate, int travelers,
                      String duration, String hotel, String hotelCategory, String transport,
                      String mealPlan, String activities, String additionalServices, String additionalReq,
                      double hotelCost, double transportCost, double mealCost, double activityCost,
                      double additionalServicesCost, double serviceCharge, double discount,
                      double finalPrice, double perPersonCost, double budget, String status, Integer bookingId) {
        this.tripId = tripId;
        this.customerId = customerId;
        this.destination = destination;
        this.travelDate = travelDate;
        this.returnDate = returnDate;
        this.travelers = travelers;
        this.duration = duration;
        this.hotel = hotel;
        this.hotelCategory = hotelCategory;
        this.transport = transport;
        this.mealPlan = mealPlan;
        this.activities = activities;
        this.additionalServices = additionalServices;
        this.additionalReq = additionalReq;
        this.hotelCost = hotelCost;
        this.transportCost = transportCost;
        this.mealCost = mealCost;
        this.activityCost = activityCost;
        this.additionalServicesCost = additionalServicesCost;
        this.serviceCharge = serviceCharge;
        this.discount = discount;
        this.finalPrice = finalPrice;
        this.perPersonCost = perPersonCost;
        this.budget = budget;
        this.status = status;
        this.bookingId = bookingId;
    }

    // Overloaded Constructor for Backwards Compatibility
    public CustomTrip(int tripId, int customerId, String destination, Date travelDate, int travelers,
                      String duration, String hotel, String hotelCategory, String transport,
                      String mealPlan, String activities, String additionalReq,
                      double hotelCost, double transportCost, double mealCost, double activityCost,
                      double serviceCharge, double discount, double finalPrice, String status) {
        this(tripId, customerId, destination, travelDate, null, travelers, duration, hotel, hotelCategory,
             transport, mealPlan, activities, "", additionalReq, hotelCost, transportCost, mealCost,
             activityCost, 0.0, serviceCharge, discount, finalPrice,
             travelers > 0 ? finalPrice / travelers : finalPrice, 0.0, status, null);
    }

    // Getters and Setters
    public int getTripId() { return tripId; }
    public void setTripId(int tripId) { this.tripId = tripId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public Date getTravelDate() { return travelDate; }
    public void setTravelDate(Date travelDate) { this.travelDate = travelDate; }

    public Date getReturnDate() { return returnDate; }
    public void setReturnDate(Date returnDate) { this.returnDate = returnDate; }

    public int getTravelers() { return travelers; }
    public void setTravelers(int travelers) { this.travelers = travelers; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getHotel() { return hotel; }
    public void setHotel(String hotel) { this.hotel = hotel; }

    public String getHotelCategory() { return hotelCategory; }
    public void setHotelCategory(String hotelCategory) { this.hotelCategory = hotelCategory; }

    public String getTransport() { return transport; }
    public void setTransport(String transport) { this.transport = transport; }

    public String getMealPlan() { return mealPlan; }
    public void setMealPlan(String mealPlan) { this.mealPlan = mealPlan; }

    public String getActivities() { return activities; }
    public void setActivities(String activities) { this.activities = activities; }

    public String getAdditionalServices() { return additionalServices; }
    public void setAdditionalServices(String additionalServices) { this.additionalServices = additionalServices; }

    public String getAdditionalReq() { return additionalReq; }
    public void setAdditionalReq(String additionalReq) { this.additionalReq = additionalReq; }

    public double getHotelCost() { return hotelCost; }
    public void setHotelCost(double hotelCost) { this.hotelCost = hotelCost; }

    public double getTransportCost() { return transportCost; }
    public void setTransportCost(double transportCost) { this.transportCost = transportCost; }

    public double getMealCost() { return mealCost; }
    public void setMealCost(double mealCost) { this.mealCost = mealCost; }

    public double getActivityCost() { return activityCost; }
    public void setActivityCost(double activityCost) { this.activityCost = activityCost; }

    public double getAdditionalServicesCost() { return additionalServicesCost; }
    public void setAdditionalServicesCost(double additionalServicesCost) { this.additionalServicesCost = additionalServicesCost; }

    public double getServiceCharge() { return serviceCharge; }
    public void setServiceCharge(double serviceCharge) { this.serviceCharge = serviceCharge; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

    public double getFinalPrice() { return finalPrice; }
    public void setFinalPrice(double finalPrice) { this.finalPrice = finalPrice; }

    public double getPerPersonCost() { return perPersonCost; }
    public void setPerPersonCost(double perPersonCost) { this.perPersonCost = perPersonCost; }

    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getBookingId() { return bookingId; }
    public void setBookingId(Integer bookingId) { this.bookingId = bookingId; }
}
