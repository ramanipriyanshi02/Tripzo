package com.holiday.model;

public class Activity {
    private int activityId;
    private String activityName;
    private double pricePerPerson;

    public Activity() {}

    public Activity(int activityId, String activityName, double pricePerPerson) {
        this.activityId = activityId;
        this.activityName = activityName;
        this.pricePerPerson = pricePerPerson;
    }

    public Activity(String activityName, double pricePerPerson) {
        this.activityName = activityName;
        this.pricePerPerson = pricePerPerson;
    }

    public int getActivityId() { return activityId; }
    public void setActivityId(int activityId) { this.activityId = activityId; }

    public String getActivityName() { return activityName; }
    public void setActivityName(String activityName) { this.activityName = activityName; }

    public double getPricePerPerson() { return pricePerPerson; }
    public void setPricePerPerson(double pricePerPerson) { this.pricePerPerson = pricePerPerson; }

    @Override
    public String toString() {
        return activityName + " (₹" + String.format("%.0f", pricePerPerson) + "/person)";
    }
}
