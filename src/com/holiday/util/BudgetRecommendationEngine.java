package com.holiday.util;

import com.holiday.dao.PackageDAO;
import com.holiday.dao.PricingDAO;
import com.holiday.model.DestinationPricing;
import com.holiday.model.Package;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BudgetRecommendationEngine {

    public static class RecommendationResult implements Comparable<RecommendationResult> {
        private Package pkg;
        private DestinationPricing pricing;
        private double calculatedTotalCost;
        private double budget;
        private double savings;
        private int matchScore; // 0 - 100%
        private String hotelTier;
        private String transportMode;
        private String mealPlan;
        private String matchReason;

        public RecommendationResult(Package pkg, DestinationPricing pricing, double calculatedTotalCost, double budget, int matchScore, String hotelTier, String transportMode, String mealPlan, String matchReason) {
            this.pkg = pkg;
            this.pricing = pricing;
            this.calculatedTotalCost = calculatedTotalCost;
            this.budget = budget;
            this.savings = budget - calculatedTotalCost;
            this.matchScore = Math.max(0, Math.min(100, matchScore));
            this.hotelTier = hotelTier;
            this.transportMode = transportMode;
            this.mealPlan = mealPlan;
            this.matchReason = matchReason;
        }

        public Package getPackage() { return pkg; }
        public DestinationPricing getPricing() { return pricing; }
        public double getCalculatedTotalCost() { return calculatedTotalCost; }
        public double getBudget() { return budget; }
        public double getSavings() { return savings; }
        public int getMatchScore() { return matchScore; }
        public String getHotelTier() { return hotelTier; }
        public String getTransportMode() { return transportMode; }
        public String getMealPlan() { return mealPlan; }
        public String getMatchReason() { return matchReason; }

        @Override
        public int compareTo(RecommendationResult o) {
            return Integer.compare(o.matchScore, this.matchScore); // Descending score
        }
    }

    private PricingDAO pricingDAO;
    private PackageDAO packageDAO;

    public BudgetRecommendationEngine() {
        this.pricingDAO = new PricingDAO();
        this.packageDAO = new PackageDAO();
    }

    public List<RecommendationResult> getRecommendations(double maxBudget, int travelers, int durationDays, String hotelCategory, String transportMode, String mealPlan) {
        List<RecommendationResult> results = new ArrayList<>();
        List<Package> packages = packageDAO.getAllPackages();

        if (travelers <= 0) travelers = 1;
        if (durationDays <= 0) durationDays = 3;

        for (Package pkg : packages) {
            DestinationPricing pricing = pricingDAO.getPricingForDestination(pkg.getDestination());
            
            // Calculate realistic itemized cost from destination_pricing table
            double hotelRatePerNight = getHotelRate(pricing, hotelCategory);
            double transportRatePerPerson = getTransportRate(pricing, transportMode);
            double mealRatePerDay = getMealRate(pricing, mealPlan);

            int nights = Math.max(1, durationDays - 1);
            double totalStayCost = hotelRatePerNight * nights * Math.ceil(travelers / 2.0); // 2 persons per room
            double totalTransportCost = transportRatePerPerson * travelers;
            double totalMealCost = mealRatePerDay * durationDays * travelers;
            double totalBaseCost = pricing.getBaseCost() * travelers;
            double serviceCharge = pricing.getServiceCharge();

            double calculatedTotal = totalBaseCost + totalStayCost + totalTransportCost + totalMealCost + serviceCharge;

            // Also consider existing package listed price
            double finalPackageCost = (pkg.getPrice() * travelers);
            double blendedTotal = (calculatedTotal * 0.4) + (finalPackageCost * 0.6);

            // Compute match score
            int score = 100;
            double diff = blendedTotal - maxBudget;
            if (diff > 0) {
                // Deduct score proportionately if over budget
                double overPercent = (diff / maxBudget) * 100.0;
                score -= (int) (overPercent * 1.5);
            } else {
                // Optimal budget range bonus
                double underPercent = (Math.abs(diff) / maxBudget) * 100.0;
                if (underPercent > 40) {
                    score -= 10; // Slightly lower score if budget is way higher than needed
                }
            }

            String matchReason;
            if (blendedTotal <= maxBudget) {
                matchReason = " Fits within budget with ₹" + String.format("%.0f", maxBudget - blendedTotal) + " savings!";
            } else if (score >= 50) {
                matchReason = " Slightly over budget (+₹" + String.format("%.0f", blendedTotal - maxBudget) + "). Upgrade option.";
            } else {
                matchReason = " Above budget limits. Premium package.";
            }

            results.add(new RecommendationResult(
                pkg, pricing, blendedTotal, maxBudget, score, hotelCategory, transportMode, mealPlan, matchReason
            ));
        }

        Collections.sort(results);
        return results;
    }

    private double getHotelRate(DestinationPricing p, String cat) {
        if (cat == null) return p.getHotel3Star();
        if (cat.contains("5") || cat.equalsIgnoreCase("5 Star") || cat.equalsIgnoreCase("Luxury")) return p.getHotel5Star();
        if (cat.contains("4") || cat.equalsIgnoreCase("4 Star") || cat.equalsIgnoreCase("Deluxe")) return p.getHotel4Star();
        return p.getHotel3Star();
    }

    private double getTransportRate(DestinationPricing p, String mode) {
        if (mode == null) return p.getTransportSharedCab();
        String m = mode.toLowerCase();
        if (m.contains("flight")) return p.getTransportFlight();
        if (m.contains("private") || m.contains("cab")) return p.getTransportPrivateCab();
        if (m.contains("train")) return p.getTransportTrain();
        if (m.contains("bus")) return p.getTransportBus();
        return p.getTransportSharedCab();
    }

    private double getMealRate(DestinationPricing p, String plan) {
        if (plan == null) return p.getMealBreakfast();
        String mp = plan.toLowerCase();
        if (mp.contains("ap") || mp.contains("all")) return p.getMealAp();
        if (mp.contains("map") || mp.contains("half")) return p.getMealMap();
        return p.getMealBreakfast();
    }
}
