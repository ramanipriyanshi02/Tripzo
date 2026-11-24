package com.holiday.model;

public class Package {
    private int packageId;
    private String packageName;
    private String destination;
    private String duration;
    private double price;
    private String hotel;
    private String transport;
    private String description;

    public Package() {}

    public Package(int packageId, String packageName, String destination, String duration, double price, String hotel, String transport, String description) {
        this.packageId = packageId;
        this.packageName = packageName;
        this.destination = destination;
        this.duration = duration;
        this.price = price;
        this.hotel = hotel;
        this.transport = transport;
        this.description = description;
    }

    public Package(String packageName, String destination, String duration, double price, String hotel, String transport, String description) {
        this.packageName = packageName;
        this.destination = destination;
        this.duration = duration;
        this.price = price;
        this.hotel = hotel;
        this.transport = transport;
        this.description = description;
    }

    public int getPackageId() { return packageId; }
    public void setPackageId(int packageId) { this.packageId = packageId; }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getHotel() { return hotel; }
    public void setHotel(String hotel) { this.hotel = hotel; }

    public String getTransport() { return transport; }
    public void setTransport(String transport) { this.transport = transport; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return packageId + " - " + packageName + " (" + destination + " @ ₹" + price + ")";
    }
}
