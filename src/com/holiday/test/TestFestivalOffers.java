package com.holiday.test;

import com.holiday.dao.BookingDAO;
import com.holiday.dao.CustomerDAO;
import com.holiday.dao.FestivalOfferDAO;
import com.holiday.dao.PackageDAO;
import com.holiday.model.Booking;
import com.holiday.model.Customer;
import com.holiday.model.FestivalOffer;
import com.holiday.model.Package;
import com.holiday.util.FestivalImageUtil;
import com.holiday.util.ReceiptPdfExporter;
import java.io.File;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;

public class TestFestivalOffers {

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println(" FESTIVAL SPECIAL OFFERS & DISCOUNTS - TEST SUITE");
        System.out.println("==========================================================");

        int passed = 0;
        int failed = 0;

        FestivalOfferDAO offerDAO = new FestivalOfferDAO();
        BookingDAO bookingDAO = new BookingDAO();
        CustomerDAO customerDAO = new CustomerDAO();
        PackageDAO packageDAO = new PackageDAO();

        // ----------------------------------------------------
        // TEST 1: Offline Image Loading
        // ----------------------------------------------------
        System.out.print("\n[TEST 1] Bundled Festival Images Loading: ");
        try {
            ImageIcon diwali = FestivalImageUtil.getFestivalImage("src/com/holiday/assets/festivals/diwali_banner.jpg", "Diwali", 360, 160, 12);
            ImageIcon navratri = FestivalImageUtil.getFestivalImage("src/com/holiday/assets/festivals/navratri_banner.jpg", "Navratri", 360, 160, 12);
            ImageIcon christmas = FestivalImageUtil.getFestivalImage("src/com/holiday/assets/festivals/christmas_banner.jpg", "Christmas", 360, 160, 12);
            ImageIcon hero = FestivalImageUtil.getFestivalImage("src/com/holiday/assets/festivals/festival_hero_banner.jpg", "Hero", 800, 140, 14);

            if (diwali != null && navratri != null && christmas != null && hero != null
                    && diwali.getIconWidth() == 360 && hero.getIconWidth() == 800) {
                System.out.println("PASSED (Successfully loaded & scaled bundled festival assets)");
                passed++;
            } else {
                System.out.println("FAILED (Images not loaded properly)");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("FAILED with exception: " + e.getMessage());
            failed++;
        }

        // ----------------------------------------------------
        // TEST 2: Graceful Missing Image Fallback
        // ----------------------------------------------------
        System.out.print("[TEST 2] Missing Image Graceful Fallback: ");
        try {
            ImageIcon fallback = FestivalImageUtil.getFestivalImage("non_existent_folder/missing.jpg", "Pongal", 360, 160, 12);
            if (fallback != null && fallback.getIconWidth() == 360 && fallback.getIconHeight() == 160) {
                System.out.println("PASSED (Fallback gradient generated safely without crash)");
                passed++;
            } else {
                System.out.println("FAILED");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("FAILED with exception: " + e.getMessage());
            failed++;
        }

        // ----------------------------------------------------
        // TEST 3: Festival Offer Model Calculation Logic
        // ----------------------------------------------------
        System.out.print("[TEST 3] Discount Calculations & Eligibility: ");
        try {
            Date today = new Date(System.currentTimeMillis());
            Date start = new Date(System.currentTimeMillis() - 86400000L);
            Date end = new Date(System.currentTimeMillis() + 86400000L * 30);

            // A. Percentage Offer (20% with max cap 5000, min booking 15000)
            FestivalOffer pctOffer = new FestivalOffer(0, "Test Diwali", "Diwali", "Desc", "path", "Percentage", 20.0, 5000.0, 15000.0, start, end, null, "Active", 100);

            // 15,000 -> 20% = 3,000
            double d1 = pctOffer.calculateDiscount(15000.0);
            // 35,000 -> 20% = 7,000, capped at 5,000
            double d2 = pctOffer.calculateDiscount(35000.0);
            // 10,000 -> below min booking 15,000 -> 0
            double d3 = pctOffer.calculateDiscount(10000.0);

            // B. Fixed Offer (₹2,000 off, min booking 10000)
            FestivalOffer fixedOffer = new FestivalOffer(0, "Test Xmas", "Christmas", "Desc", "path", "Fixed Amount", 2000.0, null, 10000.0, start, end, null, "Active", 50);
            double d4 = fixedOffer.calculateDiscount(12000.0); // should be 2000

            if (Math.abs(d1 - 3000.0) < 0.01 && Math.abs(d2 - 5000.0) < 0.01 && Math.abs(d3 - 0.0) < 0.01 && Math.abs(d4 - 2000.0) < 0.01) {
                System.out.println("PASSED (20% on 15k=3k, 20% on 35k capped at 5k, min amount check=0, fixed 2k=2k)");
                passed++;
            } else {
                System.out.println("FAILED (d1=" + d1 + ", d2=" + d2 + ", d3=" + d3 + ", d4=" + d4 + ")");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("FAILED with exception: " + e.getMessage());
            failed++;
        }

        // ----------------------------------------------------
        // TEST 4: FestivalOfferDAO CRUD Operations
        // ----------------------------------------------------
        System.out.print("[TEST 4] FestivalOfferDAO CRUD Operations: ");
        int createdOfferId = 0;
        try {
            Date now = new Date(System.currentTimeMillis());
            Date start = new Date(System.currentTimeMillis() - 86400000L);
            Date end = new Date(System.currentTimeMillis() + 86400000L * 60);

            FestivalOffer newOffer = new FestivalOffer(0, "Test Eid Mubarak Savings", "Eid", "Celebration offer for family travel",
                    "src/com/holiday/assets/festivals/festival_hero_banner.jpg", "Percentage", 18.0, 4000.0, 12000.0,
                    start, end, null, "Active", 50);

            boolean added = offerDAO.addOffer(newOffer);
            createdOfferId = newOffer.getOfferId();

            FestivalOffer fetched = offerDAO.getOfferById(createdOfferId);
            boolean updated = false;
            boolean statusToggled = false;
            if (fetched != null) {
                fetched.setDescription("Updated celebration description");
                updated = offerDAO.updateOffer(fetched);
                statusToggled = offerDAO.updateOfferStatus(createdOfferId, "Paused");
            }

            if (added && createdOfferId > 0 && fetched != null && updated && statusToggled) {
                System.out.println("PASSED (Created #" + createdOfferId + ", Retrieved, Updated, Status Toggled to Paused)");
                passed++;
            } else {
                System.out.println("FAILED");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("FAILED with exception: " + e.getMessage());
            failed++;
        }

        // ----------------------------------------------------
        // TEST 5: Active Offers & Real Database Statistics
        // ----------------------------------------------------
        System.out.print("[TEST 5] Active Offers Retrieval & Database Statistics: ");
        try {
            Date today = new Date(System.currentTimeMillis());
            List<FestivalOffer> activeOffers = offerDAO.getActiveOffers(today);
            Map<String, Object> stats = offerDAO.getOfferStatistics();

            int total = (Integer) stats.get("totalOffers");
            int active = (Integer) stats.get("activeOffers");

            if (activeOffers != null && !activeOffers.isEmpty() && total > 0 && active > 0) {
                System.out.println("PASSED (Active offers=" + activeOffers.size() + ", Total in DB=" + total + ", Active in DB=" + active + ")");
                passed++;
            } else {
                System.out.println("FAILED (activeOffers count=" + (activeOffers != null ? activeOffers.size() : "null") + ", stats=" + stats + ")");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("FAILED with exception: " + e.getMessage());
            failed++;
        }

        // ----------------------------------------------------
        // TEST 6: Booking System Integration with Festival Offer
        // ----------------------------------------------------
        System.out.print("[TEST 6] Booking Integration & Database Discount Persistence: ");
        int testBookingId = 0;
        try {
            List<Customer> custs = customerDAO.getAllCustomers();
            Customer cust = custs.get(0);
            List<Package> pkgs = packageDAO.getAllPackages();
            Package pkg = pkgs.get(0);

            Date travelDate = new Date(System.currentTimeMillis() + 86400000L * 5);
            List<FestivalOffer> eligible = offerDAO.getEligibleOffersForBooking(pkg.getPackageId(), pkg.getPrice() * 2, travelDate);

            FestivalOffer selectedOffer = eligible.isEmpty() ? offerDAO.getAllOffers().get(0) : eligible.get(0);
            int initialTimesUsed = selectedOffer.getTimesUsed();

            int travelers = 2;
            double originalAmount = pkg.getPrice() * travelers;
            double discount = selectedOffer.calculateDiscount(originalAmount);
            double finalAmount = originalAmount - discount;

            Booking b = new Booking(0, cust.getCustomerId(), pkg.getPackageId(), travelDate, travelers,
                    finalAmount, "Confirmed", selectedOffer.getOfferId(), originalAmount, discount);

            boolean bkAdded = bookingDAO.addBooking(b);
            testBookingId = b.getBookingId();

            Booking refetched = bookingDAO.getBookingById(testBookingId);
            FestivalOffer refetchedOffer = offerDAO.getOfferById(selectedOffer.getOfferId());

            boolean discountMatches = refetched != null && Math.abs(refetched.getDiscountAmount() - discount) < 0.01;
            boolean originalMatches = refetched != null && Math.abs(refetched.getOriginalAmount() - originalAmount) < 0.01;
            boolean finalMatches = refetched != null && Math.abs(refetched.getTotalAmount() - finalAmount) < 0.01;
            boolean timesUsedIncremented = refetchedOffer != null && refetchedOffer.getTimesUsed() == initialTimesUsed + 1;

            if (bkAdded && testBookingId > 0 && discountMatches && originalMatches && finalMatches && timesUsedIncremented) {
                System.out.println("PASSED (Booking #" + testBookingId + " saved with Orig: ₹" + originalAmount + ", Disc: ₹" + discount + ", Final: ₹" + finalAmount + ", Offer Usage Incremented)");
                passed++;
            } else {
                System.out.println("FAILED (bkAdded=" + bkAdded + ", discMatch=" + discountMatches + ", origMatch=" + originalMatches + ", timesUsed=" + (refetchedOffer != null ? refetchedOffer.getTimesUsed() : "null"));
                failed++;
            }
        } catch (Exception e) {
            System.out.println("FAILED with exception: " + e.getMessage());
            failed++;
        }

        // ----------------------------------------------------
        // TEST 7: PDF Receipt Generation with Discount Breakdown
        // ----------------------------------------------------
        System.out.print("[TEST 7] PDF Receipt Generation with Festival Discount: ");
        try {
            if (testBookingId > 0) {
                Booking b = bookingDAO.getBookingById(testBookingId);
                File pdf = ReceiptPdfExporter.exportBookingReceiptPdf(b, null, new File("receipts/Test_Festival_Receipt_" + testBookingId + ".pdf"));
                if (pdf.exists() && pdf.length() > 0) {
                    System.out.println("PASSED (PDF generated at: " + pdf.getName() + ", size=" + pdf.length() + " bytes)");
                    passed++;
                } else {
                    System.out.println("FAILED (PDF file not created)");
                    failed++;
                }
            } else {
                System.out.println("SKIPPED (no booking)");
            }
        } catch (Exception e) {
            System.out.println("FAILED with exception: " + e.getMessage());
            failed++;
        }

        // Clean up temporary test offer & booking
        if (testBookingId > 0) {
            bookingDAO.deleteBooking(testBookingId);
        }
        if (createdOfferId > 0) {
            offerDAO.deleteOffer(createdOfferId);
        }

        System.out.println("\n==========================================================");
        System.out.println(" FESTIVAL OFFERS TEST RESULTS: " + passed + " Passed, " + failed + " Failed.");
        System.out.println("==========================================================");

        if (failed == 0) {
            System.exit(0);
        } else {
            System.exit(1);
        }
    }
}
