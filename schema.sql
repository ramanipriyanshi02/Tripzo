-- MySQL Schema for Holiday Package Booking System (Tripzo)
-- Database: holiday_booking

CREATE DATABASE IF NOT EXISTS holiday_booking;
USE holiday_booking;

-- 1. Admin Table
CREATE TABLE IF NOT EXISTS admin (
    admin_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);

-- Insert Default Admin Account if not exists
INSERT INTO admin (username, password)
SELECT 'admin', 'admin123'
WHERE NOT EXISTS (SELECT 1 FROM admin WHERE username = 'admin');

-- 2. Packages Table
CREATE TABLE IF NOT EXISTS packages (
    package_id INT PRIMARY KEY AUTO_INCREMENT,
    package_name VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    duration VARCHAR(50) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    hotel VARCHAR(100),
    transport VARCHAR(100),
    description TEXT
);

-- 3. Customers Table
CREATE TABLE IF NOT EXISTS customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    email VARCHAR(100),
    address VARCHAR(255)
);

-- 4. Bookings Table
CREATE TABLE IF NOT EXISTS bookings (
    booking_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    package_id INT NULL,
    booking_date DATE DEFAULT NULL,
    travel_date DATE NOT NULL,
    travelers INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    booking_status VARCHAR(30) DEFAULT 'Confirmed',
    offer_id INT DEFAULT NULL,
    original_amount DECIMAL(10,2) DEFAULT NULL,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (package_id) REFERENCES packages(package_id) ON DELETE CASCADE
);

-- 5. Payments Table
CREATE TABLE IF NOT EXISTS payments (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    booking_id INT NOT NULL,
    payment_method VARCHAR(30) NOT NULL,
    payment_status VARCHAR(30) DEFAULT 'Pending',
    payment_date DATE,
    amount DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE
);

-- 6. Custom Trips Table
CREATE TABLE IF NOT EXISTS custom_trips (
    trip_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    destination VARCHAR(100) NOT NULL,
    travel_date DATE NOT NULL,
    return_date DATE,
    travelers INT NOT NULL DEFAULT 1,
    duration VARCHAR(50),
    hotel VARCHAR(100),
    hotel_category VARCHAR(50),
    transport VARCHAR(100),
    meal_plan VARCHAR(100),
    activities TEXT,
    additional_services TEXT,
    additional_req TEXT,
    hotel_cost DECIMAL(10,2) DEFAULT 0.00,
    transport_cost DECIMAL(10,2) DEFAULT 0.00,
    meal_cost DECIMAL(10,2) DEFAULT 0.00,
    activity_cost DECIMAL(10,2) DEFAULT 0.00,
    additional_services_cost DECIMAL(10,2) DEFAULT 0.00,
    service_charge DECIMAL(10,2) DEFAULT 0.00,
    discount DECIMAL(10,2) DEFAULT 0.00,
    final_price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    per_person_cost DECIMAL(10,2) DEFAULT 0.00,
    budget DECIMAL(10,2) DEFAULT 0.00,
    status VARCHAR(30) DEFAULT 'Draft',
    booking_id INT DEFAULT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
);

-- 7. Destination Pricing Table
CREATE TABLE IF NOT EXISTS destination_pricing (
    pricing_id INT PRIMARY KEY AUTO_INCREMENT,
    destination VARCHAR(100) NOT NULL UNIQUE,
    base_cost DECIMAL(10,2) NOT NULL DEFAULT 1500.00,
    hotel_3_star DECIMAL(10,2) DEFAULT 2000.00,
    hotel_4_star DECIMAL(10,2) DEFAULT 4000.00,
    hotel_5_star DECIMAL(10,2) DEFAULT 7500.00,
    transport_bus DECIMAL(10,2) DEFAULT 1000.00,
    transport_train DECIMAL(10,2) DEFAULT 1500.00,
    transport_shared_cab DECIMAL(10,2) DEFAULT 3000.00,
    transport_private_cab DECIMAL(10,2) DEFAULT 6000.00,
    transport_flight DECIMAL(10,2) DEFAULT 12000.00,
    meal_breakfast DECIMAL(10,2) DEFAULT 300.00,
    meal_map DECIMAL(10,2) DEFAULT 800.00,
    meal_ap DECIMAL(10,2) DEFAULT 1200.00,
    service_charge DECIMAL(10,2) DEFAULT 2000.00
);

-- 8. Activities Table
CREATE TABLE IF NOT EXISTS activities (
    activity_id INT PRIMARY KEY AUTO_INCREMENT,
    activity_name VARCHAR(100) NOT NULL UNIQUE,
    price_per_person DECIMAL(10,2) NOT NULL DEFAULT 1000.00
);

-- 9. Festival Special Offers Table
CREATE TABLE IF NOT EXISTS festival_offers (
    offer_id INT PRIMARY KEY AUTO_INCREMENT,
    offer_name VARCHAR(100) NOT NULL,
    festival_name VARCHAR(60) NOT NULL,
    description TEXT,
    image_path VARCHAR(255),
    discount_type VARCHAR(20) NOT NULL,
    discount_value DECIMAL(10,2) NOT NULL,
    max_discount DECIMAL(10,2) DEFAULT NULL,
    min_booking_amount DECIMAL(10,2) DEFAULT 0.00,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    applicable_package_id INT DEFAULT NULL,
    status VARCHAR(20) DEFAULT 'Active',
    usage_limit INT DEFAULT NULL,
    times_used INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (applicable_package_id) REFERENCES packages(package_id) ON DELETE SET NULL
);

-- Insert 12 Attractive Holiday Packages
INSERT INTO packages (package_name, destination, duration, price, hotel, transport, description)
SELECT 'Goa Beach Escape', 'Goa', '4 Days / 3 Nights', 15000.00, 'Sea View Resort & Spa', 'AC Luxury Coach', 'Bask on the sun-kissed beaches of North and South Goa, enjoy thrilling water sports at Baga Beach, explore historic forts, and experience vibrant coastal nightlife.'
WHERE NOT EXISTS (SELECT 1 FROM packages WHERE package_name = 'Goa Beach Escape');

INSERT INTO packages (package_name, destination, duration, price, hotel, transport, description)
SELECT 'Kashmir Paradise', 'Kashmir', '6 Days / 5 Nights', 25000.00, 'Dal Lake Heritage Houseboat & Luxury Resort', 'Private AC Sedan', 'Experience heaven on earth with a shikara ride on Dal Lake, scenic pony rides in Gulmarg meadows, snow vistas in Sonmarg, and beautiful Mughal Gardens in Srinagar.'
WHERE NOT EXISTS (SELECT 1 FROM packages WHERE package_name = 'Kashmir Paradise');

INSERT INTO packages (package_name, destination, duration, price, hotel, transport, description)
SELECT 'Manali Mountain Adventure', 'Manali', '5 Days / 4 Nights', 18000.00, 'Snow Crest Mountain Chalet', 'AC Volvo Semi-Sleeper', 'Immerse yourself in the majestic Himalayas with paragliding at Solang Valley, Rohtang Pass snow viewpoints, Old Manali cafe hopping, and Hadimba Temple visits.'
WHERE NOT EXISTS (SELECT 1 FROM packages WHERE package_name = 'Manali Mountain Adventure');

INSERT INTO packages (package_name, destination, duration, price, hotel, transport, description)
SELECT 'Kerala Backwater Retreat', 'Kerala', '5 Days / 4 Nights', 22000.00, 'Alleppey Grand Houseboat & Munnar Tea Estate', 'AC Private Cab', 'Drift along serene emerald backwaters in a traditional houseboat, witness sprawling misty tea plantations in Munnar, and enjoy authentic Ayurvedic wellness sessions.'
WHERE NOT EXISTS (SELECT 1 FROM packages WHERE package_name = 'Kerala Backwater Retreat');

INSERT INTO packages (package_name, destination, duration, price, hotel, transport, description)
SELECT 'Rajasthan Royal Heritage', 'Jaipur & Udaipur', '6 Days / 5 Nights', 28000.00, 'Haveli Palace & Lakeview Heritage Hotel', 'AC Tourist Cab', 'Live like royalty exploring Amber Fort, Hawa Mahal, Lake Pichola sunset boat cruise, City Palace, and rich Rajasthani cultural folk performances.'
WHERE NOT EXISTS (SELECT 1 FROM packages WHERE package_name = 'Rajasthan Royal Heritage');

INSERT INTO packages (package_name, destination, duration, price, hotel, transport, description)
SELECT 'Andaman Island Escape', 'Andaman', '6 Days / 5 Nights', 32000.00, 'Coral Cove Beachfront Resort', 'AC Cab & Inter-Island Cruise', 'Discover turquoise waters, world-renowned Radhanagar Beach on Havelock Island, coral reef snorkeling at Elephant Beach, and Cellular Jail light & sound show.'
WHERE NOT EXISTS (SELECT 1 FROM packages WHERE package_name = 'Andaman Island Escape');

INSERT INTO packages (package_name, destination, duration, price, hotel, transport, description)
SELECT 'Rishikesh Adventure Camp', 'Rishikesh', '3 Days / 2 Nights', 9000.00, 'Riverside Eco Adventure Camp', 'AC Tempo Traveller', 'Exhilarating Grade III river rafting on the Ganges, cliff jumping, peaceful evening Ganga Aarti at Triveni Ghat, bonfire nights, and riverside yoga.'
WHERE NOT EXISTS (SELECT 1 FROM packages WHERE package_name = 'Rishikesh Adventure Camp');

INSERT INTO packages (package_name, destination, duration, price, hotel, transport, description)
SELECT 'Darjeeling Tea Garden Tour', 'Darjeeling', '5 Days / 4 Nights', 20000.00, 'Cedar Inn & Hilltop Colonial Resort', 'Private AC SUV', 'Gaze at sunrise over Mt. Kanchenjunga from Tiger Hill, ride the UNESCO Himalayan Toy Train, and tour century-old organic tea gardens with tea-tasting sessions.'
WHERE NOT EXISTS (SELECT 1 FROM packages WHERE package_name = 'Darjeeling Tea Garden Tour');

INSERT INTO packages (package_name, destination, duration, price, hotel, transport, description)
SELECT 'Shimla Snow Experience', 'Shimla', '4 Days / 3 Nights', 16000.00, 'Pineview Heritage Hotel & Spa', 'AC Deluxe Coach', 'Stroll through British colonial charm along the Ridge and Mall Road, take a fun excursion to Kufri snow slopes, and visit Jakhoo Temple amidst towering cedar forests.'
WHERE NOT EXISTS (SELECT 1 FROM packages WHERE package_name = 'Shimla Snow Experience');

INSERT INTO packages (package_name, destination, duration, price, hotel, transport, description)
SELECT 'Dubai Luxury Experience', 'Dubai', '5 Days / 4 Nights', 55000.00, 'Grand 5-Star Marina Hotel & Spa', 'Luxury Airport & City Transfers', 'Glamorous desert safari with dune bashing and BBQ dinner, breathtaking views from Burj Khalifa 124th floor, Marina Dhow dinner cruise, and Dubai Mall shopping.'
WHERE NOT EXISTS (SELECT 1 FROM packages WHERE package_name = 'Dubai Luxury Experience');

INSERT INTO packages (package_name, destination, duration, price, hotel, transport, description)
SELECT 'Sikkim Himalayan Explorer', 'Sikkim', '6 Days / 5 Nights', 26000.00, 'Mount Himalayan Boutique Resort', '4WD Mountain SUV', 'Journey to sacred Tsomgo Lake and Baba Mandir, witness panoramic snow peaks from Gangtok ropeway, and visit ancient Rumtek Monastery in pristine mountain air.'
WHERE NOT EXISTS (SELECT 1 FROM packages WHERE package_name = 'Sikkim Himalayan Explorer');

INSERT INTO packages (package_name, destination, duration, price, hotel, transport, description)
SELECT 'Meghalaya Nature Discovery', 'Meghalaya', '5 Days / 4 Nights', 21000.00, 'Cherrapunji Cloud Nine Nature Resort', 'Private AC Cab', 'Marvel at the UNESCO Double Decker Living Root Bridge, crystal-clear waters of Umngot River in Dawki, Nohkalikai Falls, and mystical limestone caves of Mawsmai.'
WHERE NOT EXISTS (SELECT 1 FROM packages WHERE package_name = 'Meghalaya Nature Discovery');

-- Insert Sample Customers
INSERT INTO customers (name, phone, email, address)
SELECT 'Rahul Sharma', '9876543210', 'rahul.sharma@example.com', 'Mumbai, Maharashtra'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE phone = '9876543210');

INSERT INTO customers (name, phone, email, address)
SELECT 'Priya Verma', '9812345678', 'priya.v@example.com', 'New Delhi, Delhi'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE phone = '9812345678');

INSERT INTO customers (name, phone, email, address)
SELECT 'Amit Patel', '9988776655', 'amit.patel@example.com', 'Ahmedabad, Gujarat'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE phone = '9988776655');

INSERT INTO customers (name, phone, email, address)
SELECT 'Sneha Reddy', '9845012345', 'sneha.reddy@example.com', 'Bengaluru, Karnataka'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE phone = '9845012345');

INSERT INTO customers (name, phone, email, address)
SELECT 'Vikram Malhotra', '9820123456', 'vikram.m@example.com', 'Pune, Maharashtra'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE phone = '9820123456');

INSERT INTO customers (name, phone, email, address)
SELECT 'Ananya Iyer', '9840123456', 'ananya.iyer@example.com', 'Chennai, Tamil Nadu'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE phone = '9840123456');

INSERT INTO customers (name, phone, email, address)
SELECT 'Rohan Das', '9830123456', 'rohan.das@example.com', 'Kolkata, West Bengal'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE phone = '9830123456');

INSERT INTO customers (name, phone, email, address)
SELECT 'Neha Kapoor', '9811122334', 'neha.kapoor@example.com', 'Chandigarh, Punjab'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE phone = '9811122334');

INSERT INTO customers (name, phone, email, address)
SELECT 'Rajesh Nair', '9447123456', 'rajesh.nair@example.com', 'Kochi, Kerala'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE phone = '9447123456');

INSERT INTO customers (name, phone, email, address)
SELECT 'Pooja Joshi', '9822334455', 'pooja.joshi@example.com', 'Jaipur, Rajasthan'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE phone = '9822334455');

-- Insert 10 Realistic Sample Bookings Connected to Valid Customers & Packages
INSERT INTO bookings (customer_id, package_id, travel_date, travelers, total_amount, booking_status, booking_date, original_amount, discount_amount)
SELECT c.customer_id, p.package_id, '2026-11-25', 2, 30000.00, 'Confirmed', '2026-11-10', 30000.00, 0.00
FROM customers c, packages p
WHERE c.phone = '9876543210' AND p.package_name = 'Goa Beach Escape'
AND NOT EXISTS (SELECT 1 FROM bookings b WHERE b.customer_id = c.customer_id AND b.package_id = p.package_id AND b.travel_date = '2026-11-25');

INSERT INTO bookings (customer_id, package_id, travel_date, travelers, total_amount, booking_status, booking_date, original_amount, discount_amount)
SELECT c.customer_id, p.package_id, '2026-12-05', 2, 50000.00, 'Confirmed', '2026-11-12', 50000.00, 0.00
FROM customers c, packages p
WHERE c.phone = '9812345678' AND p.package_name = 'Kashmir Paradise'
AND NOT EXISTS (SELECT 1 FROM bookings b WHERE b.customer_id = c.customer_id AND b.package_id = p.package_id AND b.travel_date = '2026-12-05');

INSERT INTO bookings (customer_id, package_id, travel_date, travelers, total_amount, booking_status, booking_date, original_amount, discount_amount)
SELECT c.customer_id, p.package_id, '2026-12-15', 3, 54000.00, 'Pending', '2026-11-14', 54000.00, 0.00
FROM customers c, packages p
WHERE c.phone = '9988776655' AND p.package_name = 'Manali Mountain Adventure'
AND NOT EXISTS (SELECT 1 FROM bookings b WHERE b.customer_id = c.customer_id AND b.package_id = p.package_id AND b.travel_date = '2026-12-15');

INSERT INTO bookings (customer_id, package_id, travel_date, travelers, total_amount, booking_status, booking_date, original_amount, discount_amount)
SELECT c.customer_id, p.package_id, '2026-12-20', 2, 44000.00, 'Confirmed', '2026-11-15', 44000.00, 0.00
FROM customers c, packages p
WHERE c.phone = '9845012345' AND p.package_name = 'Kerala Backwater Retreat'
AND NOT EXISTS (SELECT 1 FROM bookings b WHERE b.customer_id = c.customer_id AND b.package_id = p.package_id AND b.travel_date = '2026-12-20');

INSERT INTO bookings (customer_id, package_id, travel_date, travelers, total_amount, booking_status, booking_date, original_amount, discount_amount)
SELECT c.customer_id, p.package_id, '2026-12-28', 4, 112000.00, 'Confirmed', '2026-11-16', 112000.00, 0.00
FROM customers c, packages p
WHERE c.phone = '9820123456' AND p.package_name = 'Rajasthan Royal Heritage'
AND NOT EXISTS (SELECT 1 FROM bookings b WHERE b.customer_id = c.customer_id AND b.package_id = p.package_id AND b.travel_date = '2026-12-28');

INSERT INTO bookings (customer_id, package_id, travel_date, travelers, total_amount, booking_status, booking_date, original_amount, discount_amount)
SELECT c.customer_id, p.package_id, '2027-01-05', 2, 64000.00, 'Confirmed', '2026-11-17', 64000.00, 0.00
FROM customers c, packages p
WHERE c.phone = '9840123456' AND p.package_name = 'Andaman Island Escape'
AND NOT EXISTS (SELECT 1 FROM bookings b WHERE b.customer_id = c.customer_id AND b.package_id = p.package_id AND b.travel_date = '2027-01-05');

INSERT INTO bookings (customer_id, package_id, travel_date, travelers, total_amount, booking_status, booking_date, original_amount, discount_amount)
SELECT c.customer_id, p.package_id, '2027-01-12', 4, 36000.00, 'Confirmed', '2026-11-17', 36000.00, 0.00
FROM customers c, packages p
WHERE c.phone = '9830123456' AND p.package_name = 'Rishikesh Adventure Camp'
AND NOT EXISTS (SELECT 1 FROM bookings b WHERE b.customer_id = c.customer_id AND b.package_id = p.package_id AND b.travel_date = '2027-01-12');

INSERT INTO bookings (customer_id, package_id, travel_date, travelers, total_amount, booking_status, booking_date, original_amount, discount_amount)
SELECT c.customer_id, p.package_id, '2027-01-20', 2, 40000.00, 'Pending', '2026-11-17', 40000.00, 0.00
FROM customers c, packages p
WHERE c.phone = '9811122334' AND p.package_name = 'Darjeeling Tea Garden Tour'
AND NOT EXISTS (SELECT 1 FROM bookings b WHERE b.customer_id = c.customer_id AND b.package_id = p.package_id AND b.travel_date = '2027-01-20');

INSERT INTO bookings (customer_id, package_id, travel_date, travelers, total_amount, booking_status, booking_date, original_amount, discount_amount)
SELECT c.customer_id, p.package_id, '2027-01-25', 3, 48000.00, 'Cancelled', '2026-11-17', 48000.00, 0.00
FROM customers c, packages p
WHERE c.phone = '9447123456' AND p.package_name = 'Shimla Snow Experience'
AND NOT EXISTS (SELECT 1 FROM bookings b WHERE b.customer_id = c.customer_id AND b.package_id = p.package_id AND b.travel_date = '2027-01-25');

INSERT INTO bookings (customer_id, package_id, travel_date, travelers, total_amount, booking_status, booking_date, original_amount, discount_amount)
SELECT c.customer_id, p.package_id, '2027-02-10', 2, 110000.00, 'Confirmed', '2026-11-17', 110000.00, 0.00
FROM customers c, packages p
WHERE c.phone = '9822334455' AND p.package_name = 'Dubai Luxury Experience'
AND NOT EXISTS (SELECT 1 FROM bookings b WHERE b.customer_id = c.customer_id AND b.package_id = p.package_id AND b.travel_date = '2027-02-10');

-- Insert Sample Payments Connected to Bookings
INSERT INTO payments (booking_id, payment_method, payment_status, payment_date, amount)
SELECT b.booking_id, 'UPI', 'Paid', '2026-11-10', 30000.00
FROM bookings b
JOIN customers c ON b.customer_id = c.customer_id
JOIN packages p ON b.package_id = p.package_id
WHERE c.phone = '9876543210' AND p.package_name = 'Goa Beach Escape' AND b.travel_date = '2026-11-25'
AND NOT EXISTS (SELECT 1 FROM payments WHERE booking_id = b.booking_id);

INSERT INTO payments (booking_id, payment_method, payment_status, payment_date, amount)
SELECT b.booking_id, 'Card', 'Paid', '2026-11-12', 50000.00
FROM bookings b
JOIN customers c ON b.customer_id = c.customer_id
JOIN packages p ON b.package_id = p.package_id
WHERE c.phone = '9812345678' AND p.package_name = 'Kashmir Paradise' AND b.travel_date = '2026-12-05'
AND NOT EXISTS (SELECT 1 FROM payments WHERE booking_id = b.booking_id);

INSERT INTO payments (booking_id, payment_method, payment_status, payment_date, amount)
SELECT b.booking_id, 'Bank Transfer', 'Pending', '2026-11-14', 54000.00
FROM bookings b
JOIN customers c ON b.customer_id = c.customer_id
JOIN packages p ON b.package_id = p.package_id
WHERE c.phone = '9988776655' AND p.package_name = 'Manali Mountain Adventure' AND b.travel_date = '2026-12-15'
AND NOT EXISTS (SELECT 1 FROM payments WHERE booking_id = b.booking_id);

INSERT INTO payments (booking_id, payment_method, payment_status, payment_date, amount)
SELECT b.booking_id, 'UPI', 'Paid', '2026-11-15', 44000.00
FROM bookings b
JOIN customers c ON b.customer_id = c.customer_id
JOIN packages p ON b.package_id = p.package_id
WHERE c.phone = '9845012345' AND p.package_name = 'Kerala Backwater Retreat' AND b.travel_date = '2026-12-20'
AND NOT EXISTS (SELECT 1 FROM payments WHERE booking_id = b.booking_id);

INSERT INTO payments (booking_id, payment_method, payment_status, payment_date, amount)
SELECT b.booking_id, 'Card', 'Paid', '2026-11-16', 112000.00
FROM bookings b
JOIN customers c ON b.customer_id = c.customer_id
JOIN packages p ON b.package_id = p.package_id
WHERE c.phone = '9820123456' AND p.package_name = 'Rajasthan Royal Heritage' AND b.travel_date = '2026-12-28'
AND NOT EXISTS (SELECT 1 FROM payments WHERE booking_id = b.booking_id);

INSERT INTO payments (booking_id, payment_method, payment_status, payment_date, amount)
SELECT b.booking_id, 'Card', 'Paid', '2026-11-17', 64000.00
FROM bookings b
JOIN customers c ON b.customer_id = c.customer_id
JOIN packages p ON b.package_id = p.package_id
WHERE c.phone = '9840123456' AND p.package_name = 'Andaman Island Escape' AND b.travel_date = '2027-01-05'
AND NOT EXISTS (SELECT 1 FROM payments WHERE booking_id = b.booking_id);

INSERT INTO payments (booking_id, payment_method, payment_status, payment_date, amount)
SELECT b.booking_id, 'UPI', 'Paid', '2026-11-17', 36000.00
FROM bookings b
JOIN customers c ON b.customer_id = c.customer_id
JOIN packages p ON b.package_id = p.package_id
WHERE c.phone = '9830123456' AND p.package_name = 'Rishikesh Adventure Camp' AND b.travel_date = '2027-01-12'
AND NOT EXISTS (SELECT 1 FROM payments WHERE booking_id = b.booking_id);

INSERT INTO payments (booking_id, payment_method, payment_status, payment_date, amount)
SELECT b.booking_id, 'Cash', 'Pending', '2026-11-17', 40000.00
FROM bookings b
JOIN customers c ON b.customer_id = c.customer_id
JOIN packages p ON b.package_id = p.package_id
WHERE c.phone = '9811122334' AND p.package_name = 'Darjeeling Tea Garden Tour' AND b.travel_date = '2027-01-20'
AND NOT EXISTS (SELECT 1 FROM payments WHERE booking_id = b.booking_id);

INSERT INTO payments (booking_id, payment_method, payment_status, payment_date, amount)
SELECT b.booking_id, 'Card', 'Refunded', '2026-11-17', 48000.00
FROM bookings b
JOIN customers c ON b.customer_id = c.customer_id
JOIN packages p ON b.package_id = p.package_id
WHERE c.phone = '9447123456' AND p.package_name = 'Shimla Snow Experience' AND b.travel_date = '2027-01-25'
AND NOT EXISTS (SELECT 1 FROM payments WHERE booking_id = b.booking_id);

INSERT INTO payments (booking_id, payment_method, payment_status, payment_date, amount)
SELECT b.booking_id, 'Bank Transfer', 'Paid', '2026-11-17', 110000.00
FROM bookings b
JOIN customers c ON b.customer_id = c.customer_id
JOIN packages p ON b.package_id = p.package_id
WHERE c.phone = '9822334455' AND p.package_name = 'Dubai Luxury Experience' AND b.travel_date = '2027-02-10'
AND NOT EXISTS (SELECT 1 FROM payments WHERE booking_id = b.booking_id);

-- Insert Default Destination Rates
INSERT INTO destination_pricing (destination, base_cost, hotel_3_star, hotel_4_star, hotel_5_star, transport_bus, transport_train, transport_shared_cab, transport_private_cab, transport_flight, meal_breakfast, meal_map, meal_ap, service_charge)
SELECT 'Goa', 2000.00, 2500.00, 4500.00, 8500.00, 1200.00, 1800.00, 3500.00, 7000.00, 11000.00, 350.00, 900.00, 1400.00, 2500.00
WHERE NOT EXISTS (SELECT 1 FROM destination_pricing WHERE destination = 'Goa');

INSERT INTO destination_pricing (destination, base_cost, hotel_3_star, hotel_4_star, hotel_5_star, transport_bus, transport_train, transport_shared_cab, transport_private_cab, transport_flight, meal_breakfast, meal_map, meal_ap, service_charge)
SELECT 'Manali', 2500.00, 2200.00, 4200.00, 7800.00, 1500.00, 2000.00, 4000.00, 8000.00, 13000.00, 300.00, 850.00, 1300.00, 2500.00
WHERE NOT EXISTS (SELECT 1 FROM destination_pricing WHERE destination = 'Manali');

INSERT INTO destination_pricing (destination, base_cost, hotel_3_star, hotel_4_star, hotel_5_star, transport_bus, transport_train, transport_shared_cab, transport_private_cab, transport_flight, meal_breakfast, meal_map, meal_ap, service_charge)
SELECT 'Jaipur', 1800.00, 2000.00, 3800.00, 7000.00, 1000.00, 1400.00, 3000.00, 6000.00, 10000.00, 300.00, 800.00, 1200.00, 2000.00
WHERE NOT EXISTS (SELECT 1 FROM destination_pricing WHERE destination = 'Jaipur');

INSERT INTO destination_pricing (destination, base_cost, hotel_3_star, hotel_4_star, hotel_5_star, transport_bus, transport_train, transport_shared_cab, transport_private_cab, transport_flight, meal_breakfast, meal_map, meal_ap, service_charge)
SELECT 'Kerala', 3000.00, 2800.00, 5000.00, 9000.00, 1400.00, 2200.00, 4500.00, 8500.00, 14000.00, 400.00, 950.00, 1500.00, 3000.00
WHERE NOT EXISTS (SELECT 1 FROM destination_pricing WHERE destination = 'Kerala');

INSERT INTO destination_pricing (destination, base_cost, hotel_3_star, hotel_4_star, hotel_5_star, transport_bus, transport_train, transport_shared_cab, transport_private_cab, transport_flight, meal_breakfast, meal_map, meal_ap, service_charge)
SELECT 'Rajasthan', 2200.00, 2300.00, 4000.00, 7500.00, 1100.00, 1600.00, 3200.00, 6500.00, 10500.00, 320.00, 820.00, 1250.00, 2200.00
WHERE NOT EXISTS (SELECT 1 FROM destination_pricing WHERE destination = 'Rajasthan');

INSERT INTO destination_pricing (destination, base_cost, hotel_3_star, hotel_4_star, hotel_5_star, transport_bus, transport_train, transport_shared_cab, transport_private_cab, transport_flight, meal_breakfast, meal_map, meal_ap, service_charge)
SELECT 'Kashmir', 2800.00, 2600.00, 4800.00, 8800.00, 1600.00, 2200.00, 4200.00, 8200.00, 13500.00, 350.00, 900.00, 1400.00, 2600.00
WHERE NOT EXISTS (SELECT 1 FROM destination_pricing WHERE destination = 'Kashmir');

INSERT INTO destination_pricing (destination, base_cost, hotel_3_star, hotel_4_star, hotel_5_star, transport_bus, transport_train, transport_shared_cab, transport_private_cab, transport_flight, meal_breakfast, meal_map, meal_ap, service_charge)
SELECT 'Andaman', 3200.00, 3000.00, 5500.00, 9500.00, 1500.00, 2500.00, 4800.00, 9000.00, 15000.00, 450.00, 1000.00, 1600.00, 3000.00
WHERE NOT EXISTS (SELECT 1 FROM destination_pricing WHERE destination = 'Andaman');

INSERT INTO destination_pricing (destination, base_cost, hotel_3_star, hotel_4_star, hotel_5_star, transport_bus, transport_train, transport_shared_cab, transport_private_cab, transport_flight, meal_breakfast, meal_map, meal_ap, service_charge)
SELECT 'Rishikesh', 1500.00, 1800.00, 3500.00, 6500.00, 900.00, 1200.00, 2800.00, 5500.00, 9500.00, 280.00, 750.00, 1100.00, 1800.00
WHERE NOT EXISTS (SELECT 1 FROM destination_pricing WHERE destination = 'Rishikesh');

INSERT INTO destination_pricing (destination, base_cost, hotel_3_star, hotel_4_star, hotel_5_star, transport_bus, transport_train, transport_shared_cab, transport_private_cab, transport_flight, meal_breakfast, meal_map, meal_ap, service_charge)
SELECT 'Darjeeling', 2200.00, 2400.00, 4400.00, 8000.00, 1300.00, 1800.00, 3800.00, 7200.00, 12000.00, 320.00, 850.00, 1300.00, 2200.00
WHERE NOT EXISTS (SELECT 1 FROM destination_pricing WHERE destination = 'Darjeeling');

INSERT INTO destination_pricing (destination, base_cost, hotel_3_star, hotel_4_star, hotel_5_star, transport_bus, transport_train, transport_shared_cab, transport_private_cab, transport_flight, meal_breakfast, meal_map, meal_ap, service_charge)
SELECT 'Shimla', 2000.00, 2200.00, 4100.00, 7600.00, 1200.00, 1700.00, 3500.00, 6800.00, 11500.00, 300.00, 800.00, 1250.00, 2000.00
WHERE NOT EXISTS (SELECT 1 FROM destination_pricing WHERE destination = 'Shimla');

INSERT INTO destination_pricing (destination, base_cost, hotel_3_star, hotel_4_star, hotel_5_star, transport_bus, transport_train, transport_shared_cab, transport_private_cab, transport_flight, meal_breakfast, meal_map, meal_ap, service_charge)
SELECT 'Dubai', 5000.00, 5500.00, 9500.00, 18000.00, 2500.00, 3500.00, 7000.00, 14000.00, 22000.00, 800.00, 1800.00, 2800.00, 5000.00
WHERE NOT EXISTS (SELECT 1 FROM destination_pricing WHERE destination = 'Dubai');

INSERT INTO destination_pricing (destination, base_cost, hotel_3_star, hotel_4_star, hotel_5_star, transport_bus, transport_train, transport_shared_cab, transport_private_cab, transport_flight, meal_breakfast, meal_map, meal_ap, service_charge)
SELECT 'Sikkim', 2400.00, 2500.00, 4600.00, 8200.00, 1400.00, 1900.00, 4000.00, 7500.00, 12500.00, 340.00, 880.00, 1350.00, 2400.00
WHERE NOT EXISTS (SELECT 1 FROM destination_pricing WHERE destination = 'Sikkim');

INSERT INTO destination_pricing (destination, base_cost, hotel_3_star, hotel_4_star, hotel_5_star, transport_bus, transport_train, transport_shared_cab, transport_private_cab, transport_flight, meal_breakfast, meal_map, meal_ap, service_charge)
SELECT 'Meghalaya', 2200.00, 2300.00, 4300.00, 7800.00, 1300.00, 1800.00, 3800.00, 7200.00, 12000.00, 320.00, 840.00, 1300.00, 2200.00
WHERE NOT EXISTS (SELECT 1 FROM destination_pricing WHERE destination = 'Meghalaya');

INSERT INTO destination_pricing (destination, base_cost, hotel_3_star, hotel_4_star, hotel_5_star, transport_bus, transport_train, transport_shared_cab, transport_private_cab, transport_flight, meal_breakfast, meal_map, meal_ap, service_charge)
SELECT 'Default', 1500.00, 2000.00, 3800.00, 6800.00, 1000.00, 1500.00, 3000.00, 6000.00, 10000.00, 300.00, 800.00, 1200.00, 2000.00
WHERE NOT EXISTS (SELECT 1 FROM destination_pricing WHERE destination = 'Default');

-- Insert Default Activities
INSERT INTO activities (activity_name, price_per_person)
SELECT 'Sightseeing', 1000.00 WHERE NOT EXISTS (SELECT 1 FROM activities WHERE activity_name = 'Sightseeing');
INSERT INTO activities (activity_name, price_per_person)
SELECT 'Adventure', 2500.00 WHERE NOT EXISTS (SELECT 1 FROM activities WHERE activity_name = 'Adventure');
INSERT INTO activities (activity_name, price_per_person)
SELECT 'Water Sports', 3000.00 WHERE NOT EXISTS (SELECT 1 FROM activities WHERE activity_name = 'Water Sports');
INSERT INTO activities (activity_name, price_per_person)
SELECT 'City Tour', 1500.00 WHERE NOT EXISTS (SELECT 1 FROM activities WHERE activity_name = 'City Tour');
INSERT INTO activities (activity_name, price_per_person)
SELECT 'Museum Visit', 800.00 WHERE NOT EXISTS (SELECT 1 FROM activities WHERE activity_name = 'Museum Visit');
INSERT INTO activities (activity_name, price_per_person)
SELECT 'Trekking', 2000.00 WHERE NOT EXISTS (SELECT 1 FROM activities WHERE activity_name = 'Trekking');
INSERT INTO activities (activity_name, price_per_person)
SELECT 'Local Experience', 1200.00 WHERE NOT EXISTS (SELECT 1 FROM activities WHERE activity_name = 'Local Experience');

-- Seed Sample Festival Offers
INSERT INTO festival_offers (offer_name, festival_name, description, image_path, discount_type, discount_value, max_discount, min_booking_amount, start_date, end_date, status, usage_limit)
SELECT 'Diwali Dhamaka', 'Diwali', 'Celebrate Diwali with grand savings on luxury royal palace tours and festive holidays across India.', 'src/com/holiday/assets/festivals/diwali_banner.jpg', 'Percentage', 20.00, 5000.00, 15000.00, '2026-10-01', '2026-11-30', 'Active', 100
WHERE NOT EXISTS (SELECT 1 FROM festival_offers WHERE offer_name = 'Diwali Dhamaka');

INSERT INTO festival_offers (offer_name, festival_name, description, image_path, discount_type, discount_value, max_discount, min_booking_amount, start_date, end_date, status, usage_limit)
SELECT 'Navratri Travel Special', 'Navratri', 'Dance into the festive season with family & friends! Special discounts on cultural heritage tours.', 'src/com/holiday/assets/festivals/navratri_banner.jpg', 'Percentage', 15.00, 3500.00, 10000.00, '2026-09-15', '2026-10-31', 'Active', 80
WHERE NOT EXISTS (SELECT 1 FROM festival_offers WHERE offer_name = 'Navratri Travel Special');

INSERT INTO festival_offers (offer_name, festival_name, description, image_path, discount_type, discount_value, max_discount, min_booking_amount, start_date, end_date, status, usage_limit)
SELECT 'Christmas Holiday Escape', 'Christmas', 'Magical snowy getaways, cozy mountain chalets, and festive winter retreats with instant savings.', 'src/com/holiday/assets/festivals/christmas_banner.jpg', 'Fixed Amount', 2000.00, NULL, 12000.00, '2026-12-01', '2026-12-31', 'Active', 50
WHERE NOT EXISTS (SELECT 1 FROM festival_offers WHERE offer_name = 'Christmas Holiday Escape');

INSERT INTO festival_offers (offer_name, festival_name, description, image_path, discount_type, discount_value, max_discount, min_booking_amount, start_date, end_date, status, usage_limit)
SELECT 'New Year Grand Bash', 'New Year', 'Ring in the New Year under starlit fireworks at premier coastal and beach luxury resorts.', 'src/com/holiday/assets/festivals/newyear_banner.jpg', 'Percentage', 25.00, 8000.00, 20000.00, '2026-12-20', '2027-01-10', 'Active', 50
WHERE NOT EXISTS (SELECT 1 FROM festival_offers WHERE offer_name = 'New Year Grand Bash');

INSERT INTO festival_offers (offer_name, festival_name, description, image_path, discount_type, discount_value, max_discount, min_booking_amount, start_date, end_date, status, usage_limit)
SELECT 'Holi Color Carnival', 'Holi', 'Experience the world''s most vibrant festival with colorful cultural travel and joyful heritage trips.', 'src/com/holiday/assets/festivals/holi_banner.jpg', 'Percentage', 10.00, 2500.00, 8000.00, '2027-03-01', '2027-03-25', 'Scheduled', 60
WHERE NOT EXISTS (SELECT 1 FROM festival_offers WHERE offer_name = 'Holi Color Carnival');

INSERT INTO festival_offers (offer_name, festival_name, description, image_path, discount_type, discount_value, max_discount, min_booking_amount, start_date, end_date, status, usage_limit)
SELECT 'Summer Vacation Bonanza', 'Summer Vacation', 'Escape the summer heat to pristine beaches, tranquil backwaters, and scenic hill stations.', 'src/com/holiday/assets/festivals/summer_vacation.jpg', 'Fixed Amount', 3500.00, NULL, 25000.00, '2027-04-01', '2027-06-30', 'Scheduled', 100
WHERE NOT EXISTS (SELECT 1 FROM festival_offers WHERE offer_name = 'Summer Vacation Bonanza');
