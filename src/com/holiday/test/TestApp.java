package com.holiday.test;

import com.holiday.dao.*;
import com.holiday.model.*;
import java.sql.Date;
import java.util.List;
import java.util.Map;

public class TestApp {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("     TRIPZO DESKTOP APPLICATION - TEST SUITE     ");
        System.out.println("=================================================");

        int passed = 0;
        int failed = 0;

        // 1. Test Admin Authentication
        System.out.print("\n[TEST 1] Admin Authentication: ");
        AdminDAO adminDAO = new AdminDAO();
        Admin admin = adminDAO.authenticate("admin", "admin123");
        if (admin != null && "admin".equalsIgnoreCase(admin.getUsername())) {
            System.out.println("PASSED (Admin authenticated)");
            passed++;
        } else {
            System.out.println("FAILED (Admin login failed)");
            failed++;
        }

        // 2. Test Package DAO
        System.out.print("[TEST 2] Package DAO Operations: ");
        PackageDAO packageDAO = new PackageDAO();
        com.holiday.model.Package testPkg = new com.holiday.model.Package(0, "Test Kashmir Delight", "Srinagar", "5 Days / 4 Nights", 35000.0, "Dal Lake Houseboat", "Cab", "Scenic Kashmir Valley tour.");
        boolean pkgAdded = packageDAO.addPackage(testPkg);
        List<com.holiday.model.Package> packages = packageDAO.getAllPackages();
        if (pkgAdded && testPkg.getPackageId() > 0) {
            packageDAO.deletePackage(testPkg.getPackageId());
        }
        if (packages != null) {
            System.out.println("PASSED (Fetched " + packages.size() + " packages)");
            passed++;
        } else {
            System.out.println("FAILED");
            failed++;
        }

        // 3. Test Customer DAO
        System.out.print("[TEST 3] Customer DAO Operations: ");
        CustomerDAO customerDAO = new CustomerDAO();
        Customer testCust = new Customer(0, "Test User", "9998887770", "test@example.com", "Test City");
        boolean custAdded = customerDAO.addCustomer(testCust);
        List<Customer> customers = customerDAO.getAllCustomers();
        if (custAdded && testCust.getCustomerId() > 0) {
            customerDAO.deleteCustomer(testCust.getCustomerId());
        }
        if (customers != null) {
            System.out.println("PASSED (Fetched " + customers.size() + " customers)");
            passed++;
        } else {
            System.out.println("FAILED");
            failed++;
        }

        // 4. Test Booking DAO
        System.out.print("[TEST 4] Booking DAO Operations: ");
        BookingDAO bookingDAO = new BookingDAO();
        List<Booking> bookings = bookingDAO.getAllBookings();
        Map<String, Object> stats = bookingDAO.getDashboardStats();
        if (bookings != null && stats != null) {
            System.out.println("PASSED (Fetched " + bookings.size() + " bookings, Stats: " + stats.toString() + ")");
            passed++;
        } else {
            System.out.println("FAILED");
            failed++;
        }

        // 5. Test Payment DAO
        System.out.print("[TEST 5] Payment DAO Operations: ");
        PaymentDAO paymentDAO = new PaymentDAO();
        List<Payment> payments = paymentDAO.getAllPayments();
        if (payments != null) {
            System.out.println("PASSED (Fetched " + payments.size() + " payments)");
            passed++;
        } else {
            System.out.println("FAILED");
            failed++;
        }

        System.out.println("\n=================================================");
        System.out.println(" TEST RESULTS: " + passed + " Passed, " + failed + " Failed.");
        System.out.println("=================================================");

        if (failed == 0) {
            System.exit(0);
        } else {
            System.exit(1);
        }
    }
}
