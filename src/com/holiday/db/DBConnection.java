package com.holiday.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private static String dbHost = "localhost";
    private static String dbPort = "3306";
    private static String dbName = "holiday_booking";
    private static String dbUser = "root";
    private static String dbPassword = "priya@2328";

    private static Connection connection = null;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver class not found: " + e.getMessage());
        }
    }

    public static void setCredentials(String host, String port, String user, String password) {
        dbHost = host;
        dbPort = port;
        dbUser = user;
        dbPassword = password;
        closeConnection();
    }

    public static String getDbUser() {
        return dbUser;
    }

    public static String getDbPassword() {
        return dbPassword;
    }

    public static Connection getConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }

            // First connect without DB to create database if not present
            String baseUrl = "jdbc:mysql://" + dbHost + ":" + dbPort
                    + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            try (Connection sysConn = DriverManager.getConnection(baseUrl, dbUser, dbPassword);
                    Statement stmt = sysConn.createStatement()) {
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
            } catch (SQLException e) {
                // Ignore if database creation fails due to permissions, try direct connect
            }

            // Connect to holiday_booking database
            String dbUrl = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName
                    + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            // Initialize database tables if not existing
            initializeDatabase(connection);

            return connection;
        } catch (SQLException e) {
            System.err.println("Database Connection Error: " + e.getMessage());
            return null;
        }
    }

    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Synchronizes the AUTO_INCREMENT value for a table.
     * When the table is empty, the next ID starts from 1.
     * When records already exist, the next ID continues from MAX(id) + 1.
     * This ensures MySQL automatically generates subsequent IDs without gaps or resets of existing data.
     */
    public static void syncAutoIncrement(Connection conn, String tableName, String idColumn) {
        if (conn == null) return;
        try (Statement stmt = conn.createStatement()) {
            try (ResultSet rs = stmt.executeQuery("SELECT COALESCE(MAX(" + idColumn + "), 0) FROM " + tableName)) {
                if (rs.next()) {
                    int maxId = rs.getInt(1);
                    int nextId = maxId > 0 ? (maxId + 1) : 1;
                    stmt.executeUpdate("ALTER TABLE " + tableName + " AUTO_INCREMENT = " + nextId);
                }
            }
        } catch (SQLException e) {
            System.err.println("Note syncing auto-increment for " + tableName + ": " + e.getMessage());
        }
    }

    private static void initializeDatabase(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // 1. Admin Table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS admin ("
                    + "admin_id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "username VARCHAR(50) NOT NULL UNIQUE, "
                    + "password VARCHAR(100) NOT NULL"
                    + ")");

            // Default Admin Account
            stmt.executeUpdate("INSERT INTO admin (username, password) "
                    + "SELECT 'admin', 'admin123' WHERE NOT EXISTS (SELECT 1 FROM admin WHERE username = 'admin')");

            // 2. Packages Table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS packages ("
                    + "package_id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "package_name VARCHAR(100) NOT NULL, "
                    + "destination VARCHAR(100) NOT NULL, "
                    + "duration VARCHAR(50) NOT NULL, "
                    + "price DECIMAL(10,2) NOT NULL, "
                    + "hotel VARCHAR(100), "
                    + "transport VARCHAR(100), "
                    + "description TEXT"
                    + ")");

            // 3. Customers Table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS customers ("
                    + "customer_id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "name VARCHAR(100) NOT NULL, "
                    + "phone VARCHAR(15) NOT NULL, "
                    + "email VARCHAR(100), "
                    + "address VARCHAR(255)"
                    + ")");

            // 4. Bookings Table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS bookings ("
                    + "booking_id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "customer_id INT NOT NULL, "
                    + "package_id INT NULL, "
                    + "booking_date DATE DEFAULT NULL, "
                    + "travel_date DATE NOT NULL, "
                    + "travelers INT NOT NULL, "
                    + "total_amount DECIMAL(10,2) NOT NULL, "
                    + "booking_status VARCHAR(30) DEFAULT 'Confirmed', "
                    + "FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE, "
                    + "FOREIGN KEY (package_id) REFERENCES packages(package_id) ON DELETE CASCADE"
                    + ")");

            // 5. Payments Table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS payments ("
                    + "payment_id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "booking_id INT NOT NULL, "
                    + "payment_method VARCHAR(30) NOT NULL, "
                    + "payment_status VARCHAR(30) DEFAULT 'Pending', "
                    + "payment_date DATE, "
                    + "amount DECIMAL(10,2) NOT NULL, "
                    + "FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE"
                    + ")");

            // 6. Custom Trips Table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS custom_trips ("
                    + "trip_id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "customer_id INT NOT NULL, "
                    + "destination VARCHAR(100) NOT NULL, "
                    + "travel_date DATE NOT NULL, "
                    + "return_date DATE, "
                    + "travelers INT NOT NULL DEFAULT 1, "
                    + "duration VARCHAR(50), "
                    + "hotel VARCHAR(100), "
                    + "hotel_category VARCHAR(50), "
                    + "transport VARCHAR(100), "
                    + "meal_plan VARCHAR(100), "
                    + "activities TEXT, "
                    + "additional_services TEXT, "
                    + "additional_req TEXT, "
                    + "hotel_cost DECIMAL(10,2) DEFAULT 0.00, "
                    + "transport_cost DECIMAL(10,2) DEFAULT 0.00, "
                    + "meal_cost DECIMAL(10,2) DEFAULT 0.00, "
                    + "activity_cost DECIMAL(10,2) DEFAULT 0.00, "
                    + "additional_services_cost DECIMAL(10,2) DEFAULT 0.00, "
                    + "service_charge DECIMAL(10,2) DEFAULT 0.00, "
                    + "discount DECIMAL(10,2) DEFAULT 0.00, "
                    + "final_price DECIMAL(10,2) NOT NULL DEFAULT 0.00, "
                    + "per_person_cost DECIMAL(10,2) DEFAULT 0.00, "
                    + "budget DECIMAL(10,2) DEFAULT 0.00, "
                    + "status VARCHAR(30) DEFAULT 'Draft', "
                    + "booking_id INT DEFAULT NULL, "
                    + "FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE"
                    + ")");

            // Safe column additions for pre-existing tables
            String[] alters = {
                "ALTER TABLE custom_trips ADD COLUMN return_date DATE",
                "ALTER TABLE custom_trips ADD COLUMN additional_services TEXT",
                "ALTER TABLE custom_trips ADD COLUMN additional_services_cost DECIMAL(10,2) DEFAULT 0.00",
                "ALTER TABLE custom_trips ADD COLUMN per_person_cost DECIMAL(10,2) DEFAULT 0.00",
                "ALTER TABLE custom_trips ADD COLUMN budget DECIMAL(10,2) DEFAULT 0.00",
                "ALTER TABLE custom_trips ADD COLUMN booking_id INT DEFAULT NULL",
                "ALTER TABLE bookings MODIFY package_id INT NULL",
                "ALTER TABLE bookings ADD COLUMN booking_date DATE DEFAULT NULL",
                "ALTER TABLE bookings ADD COLUMN offer_id INT DEFAULT NULL",
                "ALTER TABLE bookings ADD COLUMN original_amount DECIMAL(10,2) DEFAULT NULL",
                "ALTER TABLE bookings ADD COLUMN discount_amount DECIMAL(10,2) DEFAULT 0.00"
            };
            for (String q : alters) {
                try (Statement alterStmt = conn.createStatement()) {
                    alterStmt.executeUpdate(q);
                } catch (SQLException ignored) {}
            }

            // 7. Destination Pricing Table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS destination_pricing ("
                    + "pricing_id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "destination VARCHAR(100) NOT NULL UNIQUE, "
                    + "base_cost DECIMAL(10,2) NOT NULL DEFAULT 1500.00, "
                    + "hotel_3_star DECIMAL(10,2) DEFAULT 2000.00, "
                    + "hotel_4_star DECIMAL(10,2) DEFAULT 4000.00, "
                    + "hotel_5_star DECIMAL(10,2) DEFAULT 7500.00, "
                    + "transport_bus DECIMAL(10,2) DEFAULT 1000.00, "
                    + "transport_train DECIMAL(10,2) DEFAULT 1500.00, "
                    + "transport_shared_cab DECIMAL(10,2) DEFAULT 3000.00, "
                    + "transport_private_cab DECIMAL(10,2) DEFAULT 6000.00, "
                    + "transport_flight DECIMAL(10,2) DEFAULT 12000.00, "
                    + "meal_breakfast DECIMAL(10,2) DEFAULT 300.00, "
                    + "meal_map DECIMAL(10,2) DEFAULT 800.00, "
                    + "meal_ap DECIMAL(10,2) DEFAULT 1200.00, "
                    + "service_charge DECIMAL(10,2) DEFAULT 2000.00"
                    + ")");

            // 8. Activities Table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS activities ("
                    + "activity_id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "activity_name VARCHAR(100) NOT NULL UNIQUE, "
                    + "price_per_person DECIMAL(10,2) NOT NULL DEFAULT 1000.00"
                    + ")");

            String[][] defaultActivities = {
                {"Sightseeing", "1000.00"},
                {"Adventure", "2500.00"},
                {"Water Sports", "3000.00"},
                {"City Tour", "1500.00"},
                {"Museum Visit", "800.00"},
                {"Trekking", "2000.00"},
                {"Local Experience", "1200.00"}
            };

            for (String[] act : defaultActivities) {
                stmt.executeUpdate("INSERT INTO activities (activity_name, price_per_person) "
                        + "SELECT '" + act[0] + "', " + act[1] + " WHERE NOT EXISTS (SELECT 1 FROM activities WHERE activity_name = '" + act[0] + "')");
            }

            // 9. Festival Special Offers Table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS festival_offers ("
                    + "offer_id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "offer_name VARCHAR(100) NOT NULL, "
                    + "festival_name VARCHAR(60) NOT NULL, "
                    + "description TEXT, "
                    + "image_path VARCHAR(255), "
                    + "discount_type VARCHAR(20) NOT NULL, "
                    + "discount_value DECIMAL(10,2) NOT NULL, "
                    + "max_discount DECIMAL(10,2) DEFAULT NULL, "
                    + "min_booking_amount DECIMAL(10,2) DEFAULT 0.00, "
                    + "start_date DATE NOT NULL, "
                    + "end_date DATE NOT NULL, "
                    + "applicable_package_id INT DEFAULT NULL, "
                    + "status VARCHAR(20) DEFAULT 'Active', "
                    + "usage_limit INT DEFAULT NULL, "
                    + "times_used INT DEFAULT 0, "
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                    + "FOREIGN KEY (applicable_package_id) REFERENCES packages(package_id) ON DELETE SET NULL"
                    + ")");

            try (Statement alterStmt = conn.createStatement()) {
                alterStmt.executeUpdate("ALTER TABLE bookings ADD CONSTRAINT fk_bookings_festival_offer FOREIGN KEY (offer_id) REFERENCES festival_offers(offer_id) ON DELETE SET NULL");
            } catch (SQLException ignored) {}

            // Seed Sample Festival Offers
            String[][] seedOffers = {
                {"Diwali Dhamaka", "Diwali", "Celebrate Diwali with grand savings on luxury royal palace tours and festive holidays across India.", "src/com/holiday/assets/festivals/diwali_banner.jpg", "Percentage", "20.00", "5000.00", "15000.00", "2026-10-01", "2026-11-30", "Active", "100"},
                {"Navratri Travel Special", "Navratri", "Dance into the festive season with family & friends! Special discounts on cultural heritage tours.", "src/com/holiday/assets/festivals/navratri_banner.jpg", "Percentage", "15.00", "3500.00", "10000.00", "2026-09-15", "2026-10-31", "Active", "80"},
                {"Christmas Holiday Escape", "Christmas", "Magical snowy getaways, cozy mountain chalets, and festive winter retreats with instant savings.", "src/com/holiday/assets/festivals/christmas_banner.jpg", "Fixed Amount", "2000.00", "0.00", "12000.00", "2026-12-01", "2026-12-31", "Active", "50"},
                {"New Year Grand Bash", "New Year", "Ring in the New Year under starlit fireworks at premier coastal and beach luxury resorts.", "src/com/holiday/assets/festivals/newyear_banner.jpg", "Percentage", "25.00", "8000.00", "20000.00", "2026-12-20", "2027-01-10", "Active", "50"},
                {"Holi Color Carnival", "Holi", "Experience the world's most vibrant festival with colorful cultural travel and joyful heritage trips.", "src/com/holiday/assets/festivals/holi_banner.jpg", "Percentage", "10.00", "2500.00", "8000.00", "2027-03-01", "2027-03-25", "Scheduled", "60"},
                {"Summer Vacation Bonanza", "Summer Vacation", "Escape the summer heat to pristine beaches, tranquil backwaters, and scenic hill stations.", "src/com/holiday/assets/festivals/summer_vacation.jpg", "Fixed Amount", "3500.00", "0.00", "25000.00", "2027-04-01", "2027-06-30", "Scheduled", "100"}
            };

            for (String[] o : seedOffers) {
                String maxDisc = "0.00".equals(o[6]) ? "NULL" : o[6];
                String seedSql = "INSERT INTO festival_offers (offer_name, festival_name, description, image_path, discount_type, discount_value, max_discount, min_booking_amount, start_date, end_date, status, usage_limit) "
                        + "SELECT '" + o[0] + "', '" + o[1] + "', '" + o[2].replace("'", "''") + "', '" + o[3] + "', '" + o[4] + "', " + o[5] + ", " + maxDisc + ", " + o[7] + ", '" + o[8] + "', '" + o[9] + "', '" + o[10] + "', " + o[11] + " "
                        + "WHERE NOT EXISTS (SELECT 1 FROM festival_offers WHERE offer_name = '" + o[0] + "')";
                stmt.executeUpdate(seedSql);
            }

            // Sync AUTO_INCREMENT for empty or existing tables before seed insertion
            syncAutoIncrement(conn, "packages", "package_id");
            syncAutoIncrement(conn, "customers", "customer_id");
            syncAutoIncrement(conn, "bookings", "booking_id");
            syncAutoIncrement(conn, "payments", "payment_id");
            syncAutoIncrement(conn, "custom_trips", "trip_id");

            // 10. Seed 12 Attractive, Realistic Holiday Packages
            String[][] packages12 = {
                {"Goa Beach Escape", "Goa", "4 Days / 3 Nights", "15000.00", "Sea View Resort & Spa", "AC Luxury Coach", "Bask on the sun-kissed beaches of North and South Goa, enjoy thrilling water sports at Baga Beach, explore historic forts, and experience vibrant coastal nightlife."},
                {"Kashmir Paradise", "Kashmir", "6 Days / 5 Nights", "25000.00", "Dal Lake Heritage Houseboat & Luxury Resort", "Private AC Sedan", "Experience heaven on earth with a shikara ride on Dal Lake, scenic pony rides in Gulmarg meadows, snow vistas in Sonmarg, and beautiful Mughal Gardens in Srinagar."},
                {"Manali Mountain Adventure", "Manali", "5 Days / 4 Nights", "18000.00", "Snow Crest Mountain Chalet", "AC Volvo Semi-Sleeper", "Immerse yourself in the majestic Himalayas with paragliding at Solang Valley, Rohtang Pass snow viewpoints, Old Manali cafe hopping, and Hadimba Temple visits."},
                {"Kerala Backwater Retreat", "Kerala", "5 Days / 4 Nights", "22000.00", "Alleppey Grand Houseboat & Munnar Tea Estate", "AC Private Cab", "Drift along serene emerald backwaters in a traditional houseboat, witness sprawling misty tea plantations in Munnar, and enjoy authentic Ayurvedic wellness sessions."},
                {"Rajasthan Royal Heritage", "Jaipur & Udaipur", "6 Days / 5 Nights", "28000.00", "Haveli Palace & Lakeview Heritage Hotel", "AC Tourist Cab", "Live like royalty exploring Amber Fort, Hawa Mahal, Lake Pichola sunset boat cruise, City Palace, and rich Rajasthani cultural folk performances."},
                {"Andaman Island Escape", "Andaman", "6 Days / 5 Nights", "32000.00", "Coral Cove Beachfront Resort", "AC Cab & Inter-Island Cruise", "Discover turquoise waters, world-renowned Radhanagar Beach on Havelock Island, coral reef snorkeling at Elephant Beach, and Cellular Jail light & sound show."},
                {"Rishikesh Adventure Camp", "Rishikesh", "3 Days / 2 Nights", "9000.00", "Riverside Eco Adventure Camp", "AC Tempo Traveller", "Exhilarating Grade III river rafting on the Ganges, cliff jumping, peaceful evening Ganga Aarti at Triveni Ghat, bonfire nights, and riverside yoga."},
                {"Darjeeling Tea Garden Tour", "Darjeeling", "5 Days / 4 Nights", "20000.00", "Cedar Inn & Hilltop Colonial Resort", "Private AC SUV", "Gaze at sunrise over Mt. Kanchenjunga from Tiger Hill, ride the UNESCO Himalayan Toy Train, and tour century-old organic tea gardens with tea-tasting sessions."},
                {"Shimla Snow Experience", "Shimla", "4 Days / 3 Nights", "16000.00", "Pineview Heritage Hotel & Spa", "AC Deluxe Coach", "Stroll through British colonial charm along the Ridge and Mall Road, take a fun excursion to Kufri snow slopes, and visit Jakhoo Temple amidst towering cedar forests."},
                {"Dubai Luxury Experience", "Dubai", "5 Days / 4 Nights", "55000.00", "Grand 5-Star Marina Hotel & Spa", "Luxury Airport & City Transfers", "Glamorous desert safari with dune bashing and BBQ dinner, breathtaking views from Burj Khalifa 124th floor, Marina Dhow dinner cruise, and Dubai Mall shopping."},
                {"Sikkim Himalayan Explorer", "Sikkim", "6 Days / 5 Nights", "26000.00", "Mount Himalayan Boutique Resort", "4WD Mountain SUV", "Journey to sacred Tsomgo Lake and Baba Mandir, witness panoramic snow peaks from Gangtok ropeway, and visit ancient Rumtek Monastery in pristine mountain air."},
                {"Meghalaya Nature Discovery", "Meghalaya", "5 Days / 4 Nights", "21000.00", "Cherrapunji Cloud Nine Nature Resort", "Private AC Cab", "Marvel at the UNESCO Double Decker Living Root Bridge, crystal-clear waters of Umngot River in Dawki, Nohkalikai Falls, and mystical limestone caves of Mawsmai."}
            };

            for (String[] p : packages12) {
                try (PreparedStatement checkStmt = conn.prepareStatement("SELECT package_id FROM packages WHERE package_name = ?")) {
                    checkStmt.setString(1, p[0]);
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (!rs.next()) {
                            try (PreparedStatement insertStmt = conn.prepareStatement(
                                    "INSERT INTO packages (package_name, destination, duration, price, hotel, transport, description) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                                insertStmt.setString(1, p[0]);
                                insertStmt.setString(2, p[1]);
                                insertStmt.setString(3, p[2]);
                                insertStmt.setDouble(4, Double.parseDouble(p[3]));
                                insertStmt.setString(5, p[4]);
                                insertStmt.setString(6, p[5]);
                                insertStmt.setString(7, p[6]);
                                insertStmt.executeUpdate();
                            }
                        } else {
                            try (PreparedStatement updateStmt = conn.prepareStatement(
                                    "UPDATE packages SET destination = ?, duration = ?, price = ?, hotel = ?, transport = ?, description = ? WHERE package_name = ?")) {
                                updateStmt.setString(1, p[1]);
                                updateStmt.setString(2, p[2]);
                                updateStmt.setDouble(3, Double.parseDouble(p[3]));
                                updateStmt.setString(4, p[4]);
                                updateStmt.setString(5, p[5]);
                                updateStmt.setString(6, p[6]);
                                updateStmt.setString(7, p[0]);
                                updateStmt.executeUpdate();
                            }
                        }
                    }
                }
            }

            // 11. Seed Realistic Customers
            String[][] customersData = {
                {"Rahul Sharma", "9876543210", "rahul.sharma@example.com", "Mumbai, Maharashtra"},
                {"Priya Verma", "9812345678", "priya.v@example.com", "New Delhi, Delhi"},
                {"Amit Patel", "9988776655", "amit.patel@example.com", "Ahmedabad, Gujarat"},
                {"Sneha Reddy", "9845012345", "sneha.reddy@example.com", "Bengaluru, Karnataka"},
                {"Vikram Malhotra", "9820123456", "vikram.m@example.com", "Pune, Maharashtra"},
                {"Ananya Iyer", "9840123456", "ananya.iyer@example.com", "Chennai, Tamil Nadu"},
                {"Rohan Das", "9830123456", "rohan.das@example.com", "Kolkata, West Bengal"},
                {"Neha Kapoor", "9811122334", "neha.kapoor@example.com", "Chandigarh, Punjab"},
                {"Rajesh Nair", "9447123456", "rajesh.nair@example.com", "Kochi, Kerala"},
                {"Pooja Joshi", "9822334455", "pooja.joshi@example.com", "Jaipur, Rajasthan"}
            };

            for (String[] c : customersData) {
                try (PreparedStatement checkCust = conn.prepareStatement("SELECT customer_id FROM customers WHERE phone = ?")) {
                    checkCust.setString(1, c[1]);
                    try (ResultSet rs = checkCust.executeQuery()) {
                        if (!rs.next()) {
                            try (PreparedStatement insCust = conn.prepareStatement(
                                    "INSERT INTO customers (name, phone, email, address) VALUES (?, ?, ?, ?)")) {
                                insCust.setString(1, c[0]);
                                insCust.setString(2, c[1]);
                                insCust.setString(3, c[2]);
                                insCust.setString(4, c[3]);
                                insCust.executeUpdate();
                            }
                        }
                    }
                }
            }

            // 12. Seed 10 Realistic Sample Bookings Connected to Valid Customers & Packages
            String[][] sampleBookingsData = {
                {"9876543210", "Goa Beach Escape", "2026-11-25", "2", "30000.00", "Confirmed", "2026-11-10"},
                {"9812345678", "Kashmir Paradise", "2026-12-05", "2", "50000.00", "Confirmed", "2026-11-12"},
                {"9988776655", "Manali Mountain Adventure", "2026-12-15", "3", "54000.00", "Pending", "2026-11-14"},
                {"9845012345", "Kerala Backwater Retreat", "2026-12-20", "2", "44000.00", "Confirmed", "2026-11-15"},
                {"9820123456", "Rajasthan Royal Heritage", "2026-12-28", "4", "112000.00", "Confirmed", "2026-11-16"},
                {"9840123456", "Andaman Island Escape", "2027-01-05", "2", "64000.00", "Confirmed", "2026-11-17"},
                {"9830123456", "Rishikesh Adventure Camp", "2027-01-12", "4", "36000.00", "Confirmed", "2026-11-17"},
                {"9811122334", "Darjeeling Tea Garden Tour", "2027-01-20", "2", "40000.00", "Pending", "2026-11-17"},
                {"9447123456", "Shimla Snow Experience", "2027-01-25", "3", "48000.00", "Cancelled", "2026-11-17"},
                {"9822334455", "Dubai Luxury Experience", "2027-02-10", "2", "110000.00", "Confirmed", "2026-11-17"}
            };

            for (String[] bk : sampleBookingsData) {
                String phone = bk[0];
                String pkgName = bk[1];
                Date travelDate = Date.valueOf(bk[2]);
                int travelers = Integer.parseInt(bk[3]);
                double totalAmount = Double.parseDouble(bk[4]);
                String status = bk[5];
                Date bookingDate = Date.valueOf(bk[6]);

                Integer custId = null;
                Integer pkgId = null;

                try (PreparedStatement s = conn.prepareStatement("SELECT customer_id FROM customers WHERE phone = ?")) {
                    s.setString(1, phone);
                    try (ResultSet rs = s.executeQuery()) {
                        if (rs.next()) custId = rs.getInt(1);
                    }
                }

                try (PreparedStatement s = conn.prepareStatement("SELECT package_id FROM packages WHERE package_name = ?")) {
                    s.setString(1, pkgName);
                    try (ResultSet rs = s.executeQuery()) {
                        if (rs.next()) pkgId = rs.getInt(1);
                    }
                }

                if (custId != null && pkgId != null) {
                    boolean exists = false;
                    try (PreparedStatement s = conn.prepareStatement(
                            "SELECT booking_id FROM bookings WHERE customer_id = ? AND package_id = ? AND travel_date = ?")) {
                        s.setInt(1, custId);
                        s.setInt(2, pkgId);
                        s.setDate(3, travelDate);
                        try (ResultSet rs = s.executeQuery()) {
                            if (rs.next()) exists = true;
                        }
                    }

                    if (!exists) {
                        try (PreparedStatement ins = conn.prepareStatement(
                                "INSERT INTO bookings (customer_id, package_id, travel_date, travelers, total_amount, booking_status, booking_date, original_amount, discount_amount) "
                                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                            ins.setInt(1, custId);
                            ins.setInt(2, pkgId);
                            ins.setDate(3, travelDate);
                            ins.setInt(4, travelers);
                            ins.setDouble(5, totalAmount);
                            ins.setString(6, status);
                            ins.setDate(7, bookingDate);
                            ins.setDouble(8, totalAmount);
                            ins.setDouble(9, 0.0);
                            ins.executeUpdate();
                        }
                    }
                }
            }

            // 13. Seed Related Sample Payments Connected to Bookings
            String[][] samplePaymentsData = {
                {"9876543210", "Goa Beach Escape", "2026-11-25", "UPI", "Paid", "2026-11-10", "30000.00"},
                {"9812345678", "Kashmir Paradise", "2026-12-05", "Card", "Paid", "2026-11-12", "50000.00"},
                {"9988776655", "Manali Mountain Adventure", "2026-12-15", "Bank Transfer", "Pending", "2026-11-14", "54000.00"},
                {"9845012345", "Kerala Backwater Retreat", "2026-12-20", "UPI", "Paid", "2026-11-15", "44000.00"},
                {"9820123456", "Rajasthan Royal Heritage", "2026-12-28", "Card", "Paid", "2026-11-16", "112000.00"},
                {"9840123456", "Andaman Island Escape", "2027-01-05", "Card", "Paid", "2026-11-17", "64000.00"},
                {"9830123456", "Rishikesh Adventure Camp", "2027-01-12", "UPI", "Paid", "2026-11-17", "36000.00"},
                {"9811122334", "Darjeeling Tea Garden Tour", "2027-01-20", "Cash", "Pending", "2026-11-17", "40000.00"},
                {"9447123456", "Shimla Snow Experience", "2027-01-25", "Card", "Refunded", "2026-11-17", "48000.00"},
                {"9822334455", "Dubai Luxury Experience", "2027-02-10", "Bank Transfer", "Paid", "2026-11-17", "110000.00"}
            };

            for (String[] pm : samplePaymentsData) {
                String phone = pm[0];
                String pkgName = pm[1];
                Date travelDate = Date.valueOf(pm[2]);
                String method = pm[3];
                String status = pm[4];
                Date payDate = Date.valueOf(pm[5]);
                double amount = Double.parseDouble(pm[6]);

                Integer bookingId = null;
                try (PreparedStatement s = conn.prepareStatement(
                        "SELECT b.booking_id FROM bookings b "
                                + "JOIN customers c ON b.customer_id = c.customer_id "
                                + "JOIN packages p ON b.package_id = p.package_id "
                                + "WHERE c.phone = ? AND p.package_name = ? AND b.travel_date = ?")) {
                    s.setString(1, phone);
                    s.setString(2, pkgName);
                    s.setDate(3, travelDate);
                    try (ResultSet rs = s.executeQuery()) {
                        if (rs.next()) bookingId = rs.getInt(1);
                    }
                }

                if (bookingId != null) {
                    boolean exists = false;
                    try (PreparedStatement s = conn.prepareStatement("SELECT payment_id FROM payments WHERE booking_id = ?")) {
                        s.setInt(1, bookingId);
                        try (ResultSet rs = s.executeQuery()) {
                            if (rs.next()) exists = true;
                        }
                    }

                    if (!exists) {
                        try (PreparedStatement ins = conn.prepareStatement(
                                "INSERT INTO payments (booking_id, payment_method, payment_status, payment_date, amount) VALUES (?, ?, ?, ?, ?)")) {
                            ins.setInt(1, bookingId);
                            ins.setString(2, method);
                            ins.setString(3, status);
                            ins.setDate(4, payDate);
                            ins.setDouble(5, amount);
                            ins.executeUpdate();
                        }
                    }
                }
            }

            // 14. Seed Destination Pricing for Custom Trips & Recommendation Engine
            String[][] destPricing = {
                {"Goa", "2000.00", "2500.00", "4500.00", "8500.00", "1200.00", "1800.00", "3500.00", "7000.00", "11000.00", "350.00", "900.00", "1400.00", "2500.00"},
                {"Manali", "2500.00", "2200.00", "4200.00", "7800.00", "1500.00", "2000.00", "4000.00", "8000.00", "13000.00", "300.00", "850.00", "1300.00", "2500.00"},
                {"Jaipur", "1800.00", "2000.00", "3800.00", "7000.00", "1000.00", "1400.00", "3000.00", "6000.00", "10000.00", "300.00", "800.00", "1200.00", "2000.00"},
                {"Kerala", "3000.00", "2800.00", "5000.00", "9000.00", "1400.00", "2200.00", "4500.00", "8500.00", "14000.00", "400.00", "950.00", "1500.00", "3000.00"},
                {"Rajasthan", "2200.00", "2300.00", "4000.00", "7500.00", "1100.00", "1600.00", "3200.00", "6500.00", "10500.00", "320.00", "820.00", "1250.00", "2200.00"},
                {"Kashmir", "2800.00", "2600.00", "4800.00", "8800.00", "1600.00", "2200.00", "4200.00", "8200.00", "13500.00", "350.00", "900.00", "1400.00", "2600.00"},
                {"Andaman", "3200.00", "3000.00", "5500.00", "9500.00", "1500.00", "2500.00", "4800.00", "9000.00", "15000.00", "450.00", "1000.00", "1600.00", "3000.00"},
                {"Rishikesh", "1500.00", "1800.00", "3500.00", "6500.00", "900.00", "1200.00", "2800.00", "5500.00", "9500.00", "280.00", "750.00", "1100.00", "1800.00"},
                {"Darjeeling", "2200.00", "2400.00", "4400.00", "8000.00", "1300.00", "1800.00", "3800.00", "7200.00", "12000.00", "320.00", "850.00", "1300.00", "2200.00"},
                {"Shimla", "2000.00", "2200.00", "4100.00", "7600.00", "1200.00", "1700.00", "3500.00", "6800.00", "11500.00", "300.00", "800.00", "1250.00", "2000.00"},
                {"Dubai", "5000.00", "5500.00", "9500.00", "18000.00", "2500.00", "3500.00", "7000.00", "14000.00", "22000.00", "800.00", "1800.00", "2800.00", "5000.00"},
                {"Sikkim", "2400.00", "2500.00", "4600.00", "8200.00", "1400.00", "1900.00", "4000.00", "7500.00", "12500.00", "340.00", "880.00", "1350.00", "2400.00"},
                {"Meghalaya", "2200.00", "2300.00", "4300.00", "7800.00", "1300.00", "1800.00", "3800.00", "7200.00", "12000.00", "320.00", "840.00", "1300.00", "2200.00"},
                {"Default", "1500.00", "2000.00", "3800.00", "6800.00", "1000.00", "1500.00", "3000.00", "6000.00", "10000.00", "300.00", "800.00", "1200.00", "2000.00"}
            };

            for (String[] dp : destPricing) {
                stmt.executeUpdate("INSERT INTO destination_pricing (destination, base_cost, hotel_3_star, hotel_4_star, hotel_5_star, transport_bus, transport_train, transport_shared_cab, transport_private_cab, transport_flight, meal_breakfast, meal_map, meal_ap, service_charge) "
                        + "SELECT '" + dp[0] + "', " + dp[1] + ", " + dp[2] + ", " + dp[3] + ", " + dp[4] + ", " + dp[5] + ", " + dp[6] + ", " + dp[7] + ", " + dp[8] + ", " + dp[9] + ", " + dp[10] + ", " + dp[11] + ", " + dp[12] + ", " + dp[13] + " "
                        + "WHERE NOT EXISTS (SELECT 1 FROM destination_pricing WHERE destination = '" + dp[0] + "')");
            }

            // Sync AUTO_INCREMENT again after seed data is in place
            syncAutoIncrement(conn, "packages", "package_id");
            syncAutoIncrement(conn, "customers", "customer_id");
            syncAutoIncrement(conn, "bookings", "booking_id");
            syncAutoIncrement(conn, "payments", "payment_id");
            syncAutoIncrement(conn, "custom_trips", "trip_id");

        } catch (SQLException e) {
            System.err.println("Table initialization note: " + e.getMessage());
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
