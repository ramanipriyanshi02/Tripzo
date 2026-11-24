package com.holiday.gui;

import com.holiday.gui.ui.Theme;
import com.holiday.model.Admin;
import com.holiday.util.SoundManager;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class MainFrame extends JFrame {

    private Admin currentAdmin;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private Map<String, JButton> navButtons = new HashMap<>();

    // Section Panels
    private DashboardPanel dashboardPanel;
    private PackagePanel packagePanel;
    private FestivalOffersPanel festivalOffersPanel;
    private CustomerPanel customerPanel;
    private BookingPanel bookingPanel;
    private PaymentPanel paymentPanel;
    private CustomTripPanel customTripPanel;
    private RecommendationPanel recommendationPanel;
    private AnalyticsPanel analyticsPanel;
    private ReportsPanel reportsPanel;
    private SettingsPanel settingsPanel;

    public MainFrame(Admin admin) {
        this.currentAdmin = admin;

        setTitle("Tripzo");
        setSize(1300, 800);
        setMinimumSize(new Dimension(1024, 680));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Layout Container
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(Theme.BACKGROUND);

        // 1. Top Header Bar
        JPanel headerPanel = createHeaderPanel();
        rootPanel.add(headerPanel, BorderLayout.NORTH);

        // 2. Left Sidebar Navigation
        JPanel sidebarPanel = createSidebarPanel();
        rootPanel.add(sidebarPanel, BorderLayout.WEST);

        // 3. Center Content Panel (CardLayout)
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(Theme.BACKGROUND);
        cardPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Instantiate Section Panels
        dashboardPanel = new DashboardPanel(this);
        packagePanel = new PackagePanel();
        festivalOffersPanel = new FestivalOffersPanel(this);
        customerPanel = new CustomerPanel();
        bookingPanel = new BookingPanel(this);
        paymentPanel = new PaymentPanel(this);
        customTripPanel = new CustomTripPanel(this);
        recommendationPanel = new RecommendationPanel(this);
        analyticsPanel = new AnalyticsPanel();
        reportsPanel = new ReportsPanel();
        settingsPanel = new SettingsPanel(currentAdmin, this);

        // Add cards
        cardPanel.add(dashboardPanel, "DASHBOARD");
        cardPanel.add(packagePanel, "PACKAGES");
        cardPanel.add(festivalOffersPanel, "OFFERS");
        cardPanel.add(customerPanel, "CUSTOMERS");
        cardPanel.add(bookingPanel, "BOOKINGS");
        cardPanel.add(paymentPanel, "PAYMENTS");
        cardPanel.add(customTripPanel, "CUSTOM_TRIPS");
        cardPanel.add(recommendationPanel, "RECOMMENDATIONS");
        cardPanel.add(analyticsPanel, "ANALYTICS");
        cardPanel.add(reportsPanel, "REPORTS");
        cardPanel.add(settingsPanel, "SETTINGS");

        rootPanel.add(cardPanel, BorderLayout.CENTER);

        setContentPane(rootPanel);
        showCard("DASHBOARD");
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.PRIMARY); // Deep Ocean Header #164A5B
        header.setPreferredSize(new Dimension(getWidth(), 60));
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // App Title
        JLabel titleLabel = new JLabel("✈️ TRIPZO");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        // Right side info: Date & Welcome Admin
        JPanel rightHeader = new JPanel(new GridLayout(1, 2, 15, 0));
        rightHeader.setOpaque(false);

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        JLabel dateLabel = new JLabel(sdf.format(new Date(System.currentTimeMillis())), SwingConstants.RIGHT);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(Theme.SECONDARY); // Light Aqua

        JLabel userBadge = new JLabel("👤 Welcome, " + currentAdmin.getUsername(), SwingConstants.RIGHT);
        userBadge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        userBadge.setForeground(Theme.ACCENT); // Sand Gold

        rightHeader.add(dateLabel);
        rightHeader.add(userBadge);

        header.add(titleLabel, BorderLayout.WEST);
        header.add(rightHeader, BorderLayout.EAST);

        return header;
    }

    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(12, 1, 0, 6));
        sidebar.setBackground(Theme.PRIMARY); // Deep Ocean Sidebar #164A5B
        sidebar.setPreferredSize(new Dimension(230, getHeight()));
        sidebar.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        String[][] navItems = {
            {"DASHBOARD", "📊 Dashboard"},
            {"PACKAGES", "🌴 Packages"},
            {"OFFERS", "🎉 Festival Offers"},
            {"CUSTOMERS", "👥 Customers"},
            {"BOOKINGS", "📅 Bookings"},
            {"PAYMENTS", "💳 Payments"},
            {"CUSTOM_TRIPS", "✨ Custom Trips"},
            {"RECOMMENDATIONS", "⭐ Smart Budget"},
            {"ANALYTICS", "📈 Visual Analytics"},
            {"REPORTS", "📑 Reports"},
            {"SETTINGS", "⚙️ Settings"}
        };

        for (String[] item : navItems) {
            String key = item[0];
            String label = item[1];

            JButton btn = new JButton(label);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));

            btn.addActionListener(e -> showCard(key));
            navButtons.put(key, btn);
            sidebar.add(btn);
        }

        // Logout Button
        JButton btnLogout = new JButton("🚪 Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.setHorizontalAlignment(SwingConstants.LEFT);
        btnLogout.setForeground(new Color(255, 230, 230));
        btnLogout.setBackground(new Color(160, 40, 40));
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        btnLogout.addActionListener(e -> handleLogout());
        sidebar.add(btnLogout);

        return sidebar;
    }

    public void showCard(String cardKey) {
        SoundManager.playClickSound();

        // Highlight active nav button with Light Aqua #A8DADC
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            if (entry.getKey().equals(cardKey)) {
                entry.getValue().setBackground(Theme.SECONDARY); // Light Aqua
                entry.getValue().setForeground(Theme.PRIMARY);   // Deep Ocean
            } else {
                entry.getValue().setBackground(Theme.PRIMARY);   // Deep Ocean
                entry.getValue().setForeground(Theme.BACKGROUND); // Warm Cream text
            }
        }

        // Refresh panel data on display
        switch (cardKey) {
            case "DASHBOARD":
                dashboardPanel.refreshStats();
                break;
            case "PACKAGES":
                packagePanel.loadPackageData();
                break;
            case "OFFERS":
                festivalOffersPanel.loadData();
                break;
            case "CUSTOMERS":
                customerPanel.loadCustomerData();
                break;
            case "BOOKINGS":
                bookingPanel.loadDropdownsAndData();
                break;
            case "PAYMENTS":
                paymentPanel.loadDropdownsAndData();
                break;
            case "CUSTOM_TRIPS":
                break;
            case "REPORTS":
                reportsPanel.refreshReports();
                break;
            case "SETTINGS":
                settingsPanel.clearForm();
                break;
        }

        cardLayout.show(cardPanel, cardKey);
    }

    public void handleLogout() {
        SoundManager.playClickSound();
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to log out of Tripzo?",
            "Logout Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }

    public Admin getCurrentAdmin() { return currentAdmin; }
    public BookingPanel getBookingPanel() { return bookingPanel; }
    public PaymentPanel getPaymentPanel() { return paymentPanel; }
    public FestivalOffersPanel getFestivalOffersPanel() { return festivalOffersPanel; }
}
