
# Holiday Package Booking System (Java Swing + MySQL)

A modern, enterprise-grade Java Swing desktop application for managing holiday packages, customer bookings, payments, billing receipts, dynamic dashboards, and operational reports.

---

## 🌟 Key Features

- **Modern & Dark Modern GUI**: Custom visual dashboard, sidebar navigation, rounded glassmorphism cards, stat counters, and color-coded status badges.
- **Animated Splash Screen**: Professional progress indicator and modern startup animation.
- **Admin Authentication**: Secure login interface with database credential management & demo mode fallback.
- **Interactive Dashboard**:
  - Total Packages Count
  - Total Customers Count
  - Total Bookings Count
  - Total Revenue Calculation
  - Pending Payments Metric
  - Upcoming Trips Counter
- **Package Management**: Add, update, delete, search, and view holiday packages (Destination, Duration, Price, Hotel, Transport, Description).
- **Customer Management**: Register, update, search, and delete customer records with contact details.
- **Booking System**: Create new bookings, calculate totals dynamically, modify date/travelers, and cancel bookings.
- **Payment & Invoicing**: Track payment methods (UPI, Card, Cash, Bank Transfer), manage statuses (Paid, Pending, Refunded), and generate printable/exportable payment receipts.
- **Reports & Analytics**: Visual revenue analytics, top destinations breakdown, and data export options.
- **Settings & DB Config**: Real-time database credentials configuration, connectivity testing, and admin password updating.

---

## 📁 Directory Structure

```
Holidaypackage/
│
├── bin/                          # Compiled Java bytecode class files
├── lib/                          # External libraries & JDBC drivers
│   └── mysql-connector-j-8.3.0.jar
├── src/                          # Source code directory
│   └── com/holiday/
│       ├── db/                   # DBConnection manager & schema auto-initializer
│       ├── model/                # Data Transfer Objects (Admin, Package, Customer, Booking, Payment)
│       ├── dao/                  # Data Access Objects for CRUD persistence
│       ├── gui/                  # Swing UI frames, panels, custom renders & dialogs
│       ├── test/                 # Automated headless DAO test runner
│       └── Main.java             # Main application entry point
├── build.bat                     # Windows compilation script
├── run.bat                       # Windows execution script
├── test.bat                      # Windows automated unit test runner
└── schema.sql                    # SQL DDL & initial seed data script
```

---

## 🚀 Getting Started

### Prerequisites

- **Java Development Kit (JDK)**: Version 17 or higher.
- **MySQL Database Server**: Version 8.0+ (Optional: fallback mode allows running without MySQL active).

### Database Setup (MySQL)

1. Ensure MySQL server is running on `localhost:3306`.
2. Execution of `schema.sql` is optional since `DBConnection.java` automatically creates the `holiday_booking` database and table schemas on launch if they do not exist.
3. If importing manually:
   ```bash
   mysql -u root -p < schema.sql
   ```

### Default Credentials

| Field | Value |
|---|---|
| **Username** | `admin` |
| **Password** | `admin123` |

---

## 🛠️ How to Build and Run

### 1. Build the Application
Double-click `build.bat` or run in terminal:
```cmd
build.bat
```

### 2. Run the Application
Double-click `run.bat` or run in terminal:
```cmd
run.bat
```

### 3. Run Automated DAO Unit Tests
Double-click `test.bat` or run in terminal:
```cmd
test.bat
```

---

## 💻 Technical Architecture & Tech Stack

- **UI Framework**: Java Swing (Custom `Graphics2D` rendering, `CardLayout`, `GridBagLayout`, flat UI styling).
- **Data Access Pattern**: DAO (Data Access Object) Design Pattern.
- **Database Connection**: JDBC (`DriverManager`, `PreparedStatement`, transaction handling).
- **Driver**: MySQL Connector/J `8.3.0`.
