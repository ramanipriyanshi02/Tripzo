package com.holiday.model;

public class DestinationPricing {
    private int pricingId;
    private String destination;
    private double baseCost;
    private double hotel3Star;
    private double hotel4Star;
    private double hotel5Star;
    private double transportBus;
    private double transportTrain;
    private double transportSharedCab;
    private double transportPrivateCab;
    private double transportFlight;
    private double mealBreakfast;
    private double mealMap;
    private double mealAp;
    private double serviceCharge;

    public DestinationPricing() {}

    public DestinationPricing(int pricingId, String destination, double baseCost, double hotel3Star, double hotel4Star, double hotel5Star, double transportBus, double transportTrain, double transportSharedCab, double transportPrivateCab, double transportFlight, double mealBreakfast, double mealMap, double mealAp, double serviceCharge) {
        this.pricingId = pricingId;
        this.destination = destination;
        this.baseCost = baseCost;
        this.hotel3Star = hotel3Star;
        this.hotel4Star = hotel4Star;
        this.hotel5Star = hotel5Star;
        this.transportBus = transportBus;
        this.transportTrain = transportTrain;
        this.transportSharedCab = transportSharedCab;
        this.transportPrivateCab = transportPrivateCab;
        this.transportFlight = transportFlight;
        this.mealBreakfast = mealBreakfast;
        this.mealMap = mealMap;
        this.mealAp = mealAp;
        this.serviceCharge = serviceCharge;
    }

    public int getPricingId() { return pricingId; }
    public void setPricingId(int pricingId) { this.pricingId = pricingId; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public double getBaseCost() { return baseCost; }
    public void setBaseCost(double baseCost) { this.baseCost = baseCost; }

    public double getHotel3Star() { return hotel3Star; }
    public void setHotel3Star(double hotel3Star) { this.hotel3Star = hotel3Star; }

    public double getHotel4Star() { return hotel4Star; }
    public void setHotel4Star(double hotel4Star) { this.hotel4Star = hotel4Star; }

    public double getHotel5Star() { return hotel5Star; }
    public void setHotel5Star(double hotel5Star) { this.hotel5Star = hotel5Star; }

    public double getTransportBus() { return transportBus; }
    public void setTransportBus(double transportBus) { this.transportBus = transportBus; }

    public double getTransportTrain() { return transportTrain; }
    public void setTransportTrain(double transportTrain) { this.transportTrain = transportTrain; }

    public double getTransportSharedCab() { return transportSharedCab; }
    public void setTransportSharedCab(double transportSharedCab) { this.transportSharedCab = transportSharedCab; }

    public double getTransportPrivateCab() { return transportPrivateCab; }
    public void setTransportPrivateCab(double transportPrivateCab) { this.transportPrivateCab = transportPrivateCab; }

    public double getTransportFlight() { return transportFlight; }
    public void setTransportFlight(double transportFlight) { this.transportFlight = transportFlight; }

    public double getMealBreakfast() { return mealBreakfast; }
    public void setMealBreakfast(double mealBreakfast) { this.mealBreakfast = mealBreakfast; }

    public double getMealMap() { return mealMap; }
    public void setMealMap(double mealMap) { this.mealMap = mealMap; }

    public double getMealAp() { return mealAp; }
    public void setMealAp(double mealAp) { this.mealAp = mealAp; }

    public double getServiceCharge() { return serviceCharge; }
    public void setServiceCharge(double serviceCharge) { this.serviceCharge = serviceCharge; }
}
