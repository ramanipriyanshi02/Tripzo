package com.holiday.gui;

import com.holiday.dao.BookingDAO;
import com.holiday.dao.CustomerDAO;
import com.holiday.dao.PackageDAO;
import com.holiday.dao.PaymentDAO;
import com.holiday.db.DBConnection;
import com.holiday.gui.ui.CardPanel;
import com.holiday.gui.ui.ModernButton;
import com.holiday.gui.ui.ModernTable;
import com.holiday.gui.ui.StatusBadge;
import com.holiday.gui.ui.Theme;
import com.holiday.model.Booking;
import com.holiday.model.Payment;
import com.holiday.util.SoundManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.print.PrinterException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class ReportsPanel extends JPanel {

    private BookingDAO bookingDAO;
    private PaymentDAO paymentDAO;
    private com.holiday.dao.CustomTripDAO customTripDAO;

    private JTabbedPane tabbedPane;

    // Bookings Report
    private JTable bookingReportTable;
    private DefaultTableModel bookingModel;

    // Revenue Report
    private JTable revenueReportTable;
    private DefaultTableModel revenueModel;
    private JLabel lblTotalRevVal;
    private JLabel lblPaidRevVal;
    private JLabel lblPendingRevVal;

    // Custom Trips Report
    private JTable customTripReportTable;
    private DefaultTableModel customTripModel;
    private JLabel lblTotalCustomVal;
    private JLabel lblConvertedCustomVal;
    private JLabel lblPendingCustomVal;

    // Package Report
    private JTable packageReportTable;
    private DefaultTableModel packageModel;

    // Customer Report
    private JTable customerReportTable;
    private DefaultTableModel customerModel;

    public ReportsPanel() {
        bookingDAO = new BookingDAO();
        paymentDAO = new PaymentDAO();
        customTripDAO = new com.holiday.dao.CustomTripDAO();

        setLayout(new BorderLayout(0, 10));
        setOpaque(false);

        // Header Control Bar
        CardPanel topBar = new CardPanel();
        topBar.setLayout(new BorderLayout());

        JLabel title = new JLabel("📊 Operational Reports & Financial Auditing");
        title.setFont(Theme.FONT_HEADER);
        title.setForeground(Theme.TEXT_MAIN);

        JPanel rightBtns = new JPanel(new GridLayout(1, 2, 10, 0));
        rightBtns.setOpaque(false);

        ModernButton btnPrintReport = new ModernButton("🖨️ Print Report", ModernButton.Style.PRIMARY);
        btnPrintReport.addActionListener(e -> printCurrentTabReport());

        ModernButton btnRefresh = new ModernButton("🔄 Refresh Data", ModernButton.Style.SECONDARY);
        btnRefresh.addActionListener(e -> refreshReports());

        rightBtns.add(btnPrintReport);
        rightBtns.add(btnRefresh);

        topBar.add(title, BorderLayout.WEST);
        topBar.add(rightBtns, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // Tabbed Pane for Reports
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(Theme.FONT_BOLD);
        tabbedPane.setBackground(Theme.CARD);
        tabbedPane.setForeground(Theme.TEXT_MAIN);

        tabbedPane.addTab("📅 Bookings Activity", createBookingReportTab());
        tabbedPane.addTab("💰 Revenue Breakdown", createRevenueReportTab());
        tabbedPane.addTab("✨ Custom Trips", createCustomTripReportTab());
        tabbedPane.addTab("🌴 Package Sales", createPackageReportTab());
        tabbedPane.addTab("👥 Customer Spending", createCustomerReportTab());

        add(tabbedPane, BorderLayout.CENTER);

        refreshReports();
    }

    private JPanel createBookingReportTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        String[] cols = {
            "Booking ID", "Customer Name", "Package Name", "Destination", 
            "Travel Date", "Travelers", "Total Amount (₹)", "Status"
        };
        bookingModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        bookingReportTable = ModernTable.createTable(bookingModel);
        ModernTable.centerColumns(bookingReportTable, 0, 4, 5);
        bookingReportTable.getColumnModel().getColumn(7).setCellRenderer(StatusBadge.getTableRenderer());

        panel.add(ModernTable.createScrollPane(bookingReportTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRevenueReportTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        // 3-Card Summary metric strip
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        summaryPanel.setOpaque(false);

        lblTotalRevVal = new JLabel("₹0.00", SwingConstants.CENTER);
        lblPaidRevVal = new JLabel("₹0.00", SwingConstants.CENTER);
        lblPendingRevVal = new JLabel("₹0.00", SwingConstants.CENTER);

        summaryPanel.add(createMiniCard("TOTAL BOOKED REVENUE", lblTotalRevVal, Theme.PRIMARY));
        summaryPanel.add(createMiniCard("COLLECTED / PAID", lblPaidRevVal, Theme.SUCCESS));
        summaryPanel.add(createMiniCard("PENDING COLLECTIONS", lblPendingRevVal, Theme.WARNING));

        panel.add(summaryPanel, BorderLayout.NORTH);

        String[] cols = {"Payment ID", "Booking ID", "Customer", "Payment Method", "Status", "Date", "Paid Amount (₹)"};
        revenueModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        revenueReportTable = ModernTable.createTable(revenueModel);
        ModernTable.centerColumns(revenueReportTable, 0, 1, 5);
        revenueReportTable.getColumnModel().getColumn(4).setCellRenderer(StatusBadge.getTableRenderer());

        panel.add(ModernTable.createScrollPane(revenueReportTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCustomTripReportTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        summaryPanel.setOpaque(false);

        lblTotalCustomVal = new JLabel("₹0.00", SwingConstants.CENTER);
        lblConvertedCustomVal = new JLabel("₹0.00", SwingConstants.CENTER);
        lblPendingCustomVal = new JLabel("0 Trips", SwingConstants.CENTER);

        summaryPanel.add(createMiniCard("TOTAL ESTIMATED PIPELINE", lblTotalCustomVal, Theme.PRIMARY));
        summaryPanel.add(createMiniCard("CONVERTED BOOKING VALUE", lblConvertedCustomVal, Theme.SUCCESS));
        summaryPanel.add(createMiniCard("OPEN / QUOTED TRIPS", lblPendingCustomVal, Theme.ACCENT));

        panel.add(summaryPanel, BorderLayout.NORTH);

        String[] cols = {"Trip ID", "Customer Name", "Destination", "Travel Date", "Travelers", "Estimated Cost (₹)", "Status", "Booking Link"};
        customTripModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        customTripReportTable = ModernTable.createTable(customTripModel);
        ModernTable.centerColumns(customTripReportTable, 0, 3, 4, 7);
        customTripReportTable.getColumnModel().getColumn(6).setCellRenderer(StatusBadge.getTableRenderer());

        panel.add(ModernTable.createScrollPane(customTripReportTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPackageReportTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        String[] cols = {"Package ID", "Package Name", "Destination", "Unit Price", "Total Bookings Count", "Total Revenue Generated"};
        packageModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        packageReportTable = ModernTable.createTable(packageModel);
        ModernTable.centerColumns(packageReportTable, 0, 4);

        panel.add(ModernTable.createScrollPane(packageReportTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCustomerReportTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        String[] cols = {"Customer ID", "Customer Name", "Phone", "Email", "Total Bookings Made", "Total Spent (₹)"};
        customerModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        customerReportTable = ModernTable.createTable(customerModel);
        ModernTable.centerColumns(customerReportTable, 0, 4);

        panel.add(ModernTable.createScrollPane(customerReportTable), BorderLayout.CENTER);
        return panel;
    }

    public void refreshReports() {
        loadBookingReport();
        loadRevenueReport();
        loadCustomTripReport();
        loadPackageReport();
        loadCustomerReport();
    }

    private void loadBookingReport() {
        bookingModel.setRowCount(0);
        List<Booking> list = bookingDAO.getAllBookings();
        for (Booking b : list) {
            bookingModel.addRow(new Object[]{
                b.getBookingId(),
                b.getCustomerName(),
                b.getPackageName(),
                b.getDestination(),
                b.getTravelDate(),
                b.getTravelers(),
                "₹" + String.format("%.2f", b.getTotalAmount()),
                b.getBookingStatus()
            });
        }
    }

    private void loadRevenueReport() {
        revenueModel.setRowCount(0);
        List<Payment> list = paymentDAO.getAllPayments();

        double paidTotal = 0.0;
        double pendingTotal = 0.0;

        for (Payment p : list) {
            revenueModel.addRow(new Object[]{
                p.getPaymentId(),
                p.getBookingId(),
                p.getCustomerName(),
                p.getPaymentMethod(),
                p.getPaymentStatus(),
                p.getPaymentDate(),
                "₹" + String.format("%.2f", p.getAmount())
            });

            if ("Paid".equalsIgnoreCase(p.getPaymentStatus())) {
                paidTotal += p.getAmount();
            } else {
                pendingTotal += p.getAmount();
            }
        }

        // Total revenue booked across all non-cancelled bookings
        double totalRev = 0.0;
        List<Booking> bookings = bookingDAO.getAllBookings();
        for (Booking b : bookings) {
            if (!"Cancelled".equalsIgnoreCase(b.getBookingStatus())) {
                totalRev += b.getTotalAmount();
            }
        }

        lblTotalRevVal.setText("₹" + String.format("%.2f", totalRev));
        lblPaidRevVal.setText("₹" + String.format("%.2f", paidTotal));
        lblPendingRevVal.setText("₹" + String.format("%.2f", pendingTotal));
    }

    private void loadCustomTripReport() {
        customTripModel.setRowCount(0);
        List<com.holiday.model.CustomTrip> list = customTripDAO.getAllCustomTrips();

        double totalVal = 0.0;
        double convertedVal = 0.0;
        int pendingCount = 0;

        for (com.holiday.model.CustomTrip t : list) {
            customTripModel.addRow(new Object[]{
                t.getTripId(),
                t.getCustomerName(),
                t.getDestination(),
                t.getTravelDate(),
                t.getTravelers(),
                "₹" + String.format("%.2f", t.getFinalPrice()),
                t.getStatus(),
                t.getBookingId() != null ? "#" + t.getBookingId() : "-"
            });

            totalVal += t.getFinalPrice();
            if ("Converted to Booking".equalsIgnoreCase(t.getStatus())) {
                convertedVal += t.getFinalPrice();
            } else if (!"Cancelled".equalsIgnoreCase(t.getStatus()) && !"Rejected".equalsIgnoreCase(t.getStatus())) {
                pendingCount++;
            }
        }

        lblTotalCustomVal.setText("₹" + String.format("%.2f", totalVal));
        lblConvertedCustomVal.setText("₹" + String.format("%.2f", convertedVal));
        lblPendingCustomVal.setText(pendingCount + " Active Trip(s)");
    }

    private void loadPackageReport() {
        packageModel.setRowCount(0);
        String sql = "SELECT p.package_id, p.package_name, p.destination, p.price, "
                   + "COUNT(b.booking_id) AS total_bookings, "
                   + "COALESCE(SUM(b.total_amount), 0) AS total_revenue "
                   + "FROM packages p "
                   + "LEFT JOIN bookings b ON p.package_id = b.package_id "
                   + "GROUP BY p.package_id, p.package_name, p.destination, p.price "
                   + "ORDER BY total_bookings DESC";

        Connection conn = DBConnection.getConnection();
        if (conn == null) return;

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
              ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                packageModel.addRow(new Object[]{
                    rs.getInt("package_id"),
                    rs.getString("package_name"),
                    rs.getString("destination"),
                    "₹" + String.format("%.2f", rs.getDouble("price")),
                    rs.getInt("total_bookings"),
                    "₹" + String.format("%.2f", rs.getDouble("total_revenue"))
                });
            }
        } catch (SQLException e) {
            System.err.println("Package Report Error: " + e.getMessage());
        }
    }

    private void loadCustomerReport() {
        customerModel.setRowCount(0);
        String sql = "SELECT c.customer_id, c.name, c.phone, c.email, "
                   + "COUNT(b.booking_id) AS total_bookings, "
                   + "COALESCE(SUM(b.total_amount), 0) AS total_spent "
                   + "FROM customers c "
                   + "LEFT JOIN bookings b ON c.customer_id = b.customer_id "
                   + "GROUP BY c.customer_id, c.name, c.phone, c.email "
                   + "ORDER BY total_bookings DESC";

        Connection conn = DBConnection.getConnection();
        if (conn == null) return;

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
              ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                customerModel.addRow(new Object[]{
                    rs.getInt("customer_id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("email") != null ? rs.getString("email") : "-",
                    rs.getInt("total_bookings"),
                    "₹" + String.format("%.2f", rs.getDouble("total_spent"))
                });
            }
        } catch (SQLException e) {
            System.err.println("Customer Report Error: " + e.getMessage());
        }
    }

    private void printCurrentTabReport() {
        int index = tabbedPane.getSelectedIndex();
        JTable activeTable = null;
        String reportTitle = "";

        switch (index) {
            case 0: activeTable = bookingReportTable; reportTitle = "Bookings Activity Report"; break;
            case 1: activeTable = revenueReportTable; reportTitle = "Revenue Breakdown Report"; break;
            case 2: activeTable = customTripReportTable; reportTitle = "Custom Trips Report"; break;
            case 3: activeTable = packageReportTable; reportTitle = "Package Sales Report"; break;
            case 4: activeTable = customerReportTable; reportTitle = "Customer Spending Report"; break;
        }

        if (activeTable != null) {
            try {
                boolean complete = activeTable.print(
                    JTable.PrintMode.FIT_WIDTH,
                    new MessageFormat("Tripzo - " + reportTitle),
                    new MessageFormat("Page {0}"),
                    true, null, true
                );
                if (complete) {
                    SoundManager.playSuccessSound();
                    JOptionPane.showMessageDialog(this, reportTitle + " printing completed.", "Print Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (PrinterException e) {
                SoundManager.playErrorSound();
                JOptionPane.showMessageDialog(this, "Print Error: " + e.getMessage(), "Print Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private CardPanel createMiniCard(String title, JLabel valLabel, Color accent) {
        CardPanel card = new CardPanel(accent);
        card.setLayout(new BorderLayout(0, 5));

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(Theme.FONT_BADGE);
        titleLbl.setForeground(Theme.TEXT_MUTED);

        valLabel.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 17));
        valLabel.setForeground(accent);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valLabel, BorderLayout.CENTER);
        return card;
    }
}
