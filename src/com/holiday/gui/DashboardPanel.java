package com.holiday.gui;

import com.holiday.dao.BookingDAO;
import com.holiday.dao.PackageDAO;
import com.holiday.gui.ui.CardPanel;
import com.holiday.gui.ui.ModernButton;
import com.holiday.gui.ui.ModernTable;
import com.holiday.gui.ui.StatusBadge;
import com.holiday.gui.ui.Theme;
import com.holiday.model.Booking;
import com.holiday.model.Package;
import com.holiday.util.DestinationImageCache;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class DashboardPanel extends JPanel {

    private MainFrame mainFrame;
    private BookingDAO bookingDAO;
    private PackageDAO packageDAO;
    private com.holiday.dao.CustomTripDAO customTripDAO;

    // Metric Labels
    private JLabel lblTotalPackages;
    private JLabel lblTotalCustomers;
    private JLabel lblTotalBookings;
    private JLabel lblTotalRevenue;
    private JLabel lblCustomTrips;
    private JLabel lblCustomEstimatedValue;
    private JLabel lblPendingPayments;
    private JLabel lblUpcomingTrips;

    private JTable recentBookingsTable;
    private DefaultTableModel tableModel;
    private JPanel popularPackagesGrid;

    public DashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.bookingDAO = new BookingDAO();
        this.packageDAO = new PackageDAO();
        this.customTripDAO = new com.holiday.dao.CustomTripDAO();

        setLayout(new BorderLayout(0, 15));
        setOpaque(false);

        // Scrollable Dashboard Container
        JPanel contentContainer = new JPanel();
        contentContainer.setLayout(new javax.swing.BoxLayout(contentContainer, javax.swing.BoxLayout.Y_AXIS));
        contentContainer.setOpaque(false);

        // 1. TOP WELCOME BANNER
        JPanel bannerPanel = new JPanel(new BorderLayout());
        bannerPanel.setOpaque(false);
        bannerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel greetingLabel = new JLabel("Good Day, Admin 👋");
        greetingLabel.setFont(Theme.FONT_HEADER);
        greetingLabel.setForeground(Theme.TEXT_MAIN);

        JLabel subtitleLabel = new JLabel("Real-time summary of your holiday bookings, package performance & revenue.");
        subtitleLabel.setFont(Theme.FONT_SUBTITLE);
        subtitleLabel.setForeground(Theme.TEXT_MUTED);

        bannerPanel.add(greetingLabel, BorderLayout.NORTH);
        bannerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        contentContainer.add(bannerPanel);
        contentContainer.add(javax.swing.Box.createVerticalStrut(10));

        // 2. METRIC CARDS GRID (8 High-Impact Cards: 2x4 Grid)
        JPanel cardsGrid = new JPanel(new GridLayout(2, 4, 15, 15));
        cardsGrid.setOpaque(false);
        cardsGrid.setPreferredSize(new Dimension(getWidth(), 230));

        lblTotalPackages = new JLabel("0");
        lblTotalCustomers = new JLabel("0");
        lblTotalBookings = new JLabel("0");
        lblTotalRevenue = new JLabel("₹0");
        lblCustomTrips = new JLabel("0");
        lblCustomEstimatedValue = new JLabel("₹0");
        lblPendingPayments = new JLabel("0");
        lblUpcomingTrips = new JLabel("0");

        cardsGrid.add(createMetricCard("TOTAL PACKAGES", lblTotalPackages, "🌴", Theme.PRIMARY, "Catalog packages"));
        cardsGrid.add(createMetricCard("TOTAL CUSTOMERS", lblTotalCustomers, "👥", Theme.SECONDARY, "Registered clients"));
        cardsGrid.add(createMetricCard("TOTAL BOOKINGS", lblTotalBookings, "📅", Theme.SUCCESS, "All-time bookings"));
        cardsGrid.add(createMetricCard("TOTAL REVENUE", lblTotalRevenue, "💰", Theme.PRIMARY, "Total collected revenue"));
        cardsGrid.add(createMetricCard("CUSTOM TRIPS", lblCustomTrips, "✨", Theme.SECONDARY, "Bespoke itineraries"));
        cardsGrid.add(createMetricCard("CUSTOM PIPELINE", lblCustomEstimatedValue, "💎", Theme.ACCENT, "Estimated custom value"));
        cardsGrid.add(createMetricCard("PENDING PAYMENTS", lblPendingPayments, "⚠️", Theme.WARNING, "Follow-up required"));
        cardsGrid.add(createMetricCard("UPCOMING TRIPS", lblUpcomingTrips, "✈️", Theme.ACCENT, "Scheduled departures"));

        contentContainer.add(cardsGrid);
        contentContainer.add(javax.swing.Box.createVerticalStrut(15));

        // 3. QUICK ACTION BAR
        CardPanel quickActionCard = new CardPanel();
        quickActionCard.setLayout(new BorderLayout(10, 0));

        JLabel qaTitle = new JLabel("Quick Actions:");
        qaTitle.setFont(Theme.FONT_BOLD);
        qaTitle.setForeground(Theme.TEXT_MAIN);
        quickActionCard.add(qaTitle, BorderLayout.WEST);

        JPanel qaBtnGroup = new JPanel(new GridLayout(1, 5, 10, 0));
        qaBtnGroup.setOpaque(false);

        ModernButton btnAddPkg = new ModernButton("+ Package", ModernButton.Style.OUTLINE);
        btnAddPkg.addActionListener(e -> mainFrame.showCard("PACKAGES"));

        ModernButton btnAddCust = new ModernButton("+ Customer", ModernButton.Style.OUTLINE);
        btnAddCust.addActionListener(e -> mainFrame.showCard("CUSTOMERS"));

        ModernButton btnCreateBk = new ModernButton("+ Create Booking", ModernButton.Style.PRIMARY);
        btnCreateBk.addActionListener(e -> mainFrame.showCard("BOOKINGS"));

        ModernButton btnCustomTrip = new ModernButton("✨ Custom Trip", ModernButton.Style.SECONDARY);
        btnCustomTrip.addActionListener(e -> mainFrame.showCard("CUSTOM_TRIPS"));

        ModernButton btnQuote = new ModernButton("📑 Reports", ModernButton.Style.OUTLINE);
        btnQuote.addActionListener(e -> mainFrame.showCard("REPORTS"));

        qaBtnGroup.add(btnAddPkg);
        qaBtnGroup.add(btnAddCust);
        qaBtnGroup.add(btnCreateBk);
        qaBtnGroup.add(btnCustomTrip);
        qaBtnGroup.add(btnQuote);

        quickActionCard.add(qaBtnGroup, BorderLayout.CENTER);
        contentContainer.add(quickActionCard);
        contentContainer.add(javax.swing.Box.createVerticalStrut(15));

        // 4. UPCOMING TRIPS TABLE CARD
        CardPanel upcomingCard = new CardPanel();
        upcomingCard.setLayout(new BorderLayout(0, 10));

        JLabel upcomingTitle = new JLabel("📋 Recent Bookings Activity");
        upcomingTitle.setFont(Theme.FONT_TITLE);
        upcomingTitle.setForeground(Theme.TEXT_MAIN);
        upcomingCard.add(upcomingTitle, BorderLayout.NORTH);

        String[] columns = {"Booking ID", "Customer Name", "Package Name", "Destination", "Travel Date", "Travelers", "Total Amount", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        recentBookingsTable = ModernTable.createTable(tableModel);
        recentBookingsTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {{ setHorizontalAlignment(CENTER); }});
        recentBookingsTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {{ setHorizontalAlignment(CENTER); }});
        recentBookingsTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {{ setHorizontalAlignment(CENTER); }});
        recentBookingsTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {{ setHorizontalAlignment(CENTER); }});
        recentBookingsTable.getColumnModel().getColumn(7).setCellRenderer(StatusBadge.getTableRenderer());

        JScrollPane scrollTable = ModernTable.createScrollPane(recentBookingsTable);
        scrollTable.setPreferredSize(new Dimension(getWidth(), 200));
        upcomingCard.add(scrollTable, BorderLayout.CENTER);

        contentContainer.add(upcomingCard);
        contentContainer.add(javax.swing.Box.createVerticalStrut(15));

        // 5. POPULAR PACKAGES SECTION WITH CACHED ROUNDED DESTINATION IMAGES
        CardPanel popCard = new CardPanel();
        popCard.setLayout(new BorderLayout(0, 12));

        JLabel popTitle = new JLabel("🌴 Popular Holiday Packages & Featured Destinations");
        popTitle.setFont(Theme.FONT_TITLE);
        popTitle.setForeground(Theme.TEXT_MAIN);
        popCard.add(popTitle, BorderLayout.NORTH);

        popularPackagesGrid = new JPanel(new GridLayout(1, 3, 15, 0));
        popularPackagesGrid.setOpaque(false);
        popCard.add(popularPackagesGrid, BorderLayout.CENTER);

        contentContainer.add(popCard);

        JScrollPane mainScroll = new JScrollPane(contentContainer);
        mainScroll.setBorder(BorderFactory.createEmptyBorder());
        mainScroll.getViewport().setBackground(Theme.BACKGROUND);
        add(mainScroll, BorderLayout.CENTER);

        refreshStats();
    }

    public void refreshStats() {
        Map<String, Object> stats = bookingDAO.getDashboardStats();
        lblTotalPackages.setText(String.valueOf(stats.get("totalPackages")));
        lblTotalCustomers.setText(String.valueOf(stats.get("totalCustomers")));
        lblTotalBookings.setText(String.valueOf(stats.get("totalBookings")));

        double rev = (double) stats.get("totalRevenue");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"));
        lblTotalRevenue.setText(currencyFormat.format(rev));

        lblPendingPayments.setText(String.valueOf(stats.get("pendingPayments")));
        lblUpcomingTrips.setText(String.valueOf(stats.get("upcomingTrips")));

        Map<String, Object> tripStats = customTripDAO.getCustomTripStats();
        lblCustomTrips.setText(String.valueOf(tripStats.getOrDefault("totalTrips", 0)));
        double tripVal = tripStats.get("totalEstimatedValue") != null ? (double) tripStats.get("totalEstimatedValue") : 0.0;
        lblCustomEstimatedValue.setText(currencyFormat.format(tripVal));

        // Populate recent bookings
        tableModel.setRowCount(0);
        List<Booking> bookings = bookingDAO.getAllBookings();
        int limit = Math.min(bookings.size(), 5);
        for (int i = 0; i < limit; i++) {
            Booking b = bookings.get(i);
            tableModel.addRow(new Object[]{
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

        // Populate popular package cards with cached destination images
        popularPackagesGrid.removeAll();
        List<Package> packages = packageDAO.getAllPackages();
        int pkgLimit = Math.min(packages.size(), 3);
        for (int i = 0; i < pkgLimit; i++) {
            Package p = packages.get(i);
            popularPackagesGrid.add(createPackageCard(p));
        }
        popularPackagesGrid.revalidate();
        popularPackagesGrid.repaint();
    }

    private CardPanel createMetricCard(String title, JLabel valueLabel, String iconStr, Color accentColor, String subText) {
        CardPanel card = new CardPanel(accentColor);
        card.setLayout(new BorderLayout(0, 5));

        JPanel topSub = new JPanel(new BorderLayout());
        topSub.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(Theme.FONT_SMALL);
        titleLbl.setForeground(Theme.TEXT_MUTED);

        JLabel iconLbl = new JLabel(iconStr, SwingConstants.RIGHT);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));

        topSub.add(titleLbl, BorderLayout.WEST);
        topSub.add(iconLbl, BorderLayout.EAST);

        valueLabel.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 20));
        valueLabel.setForeground(Theme.TEXT_MAIN);

        JLabel subLbl = new JLabel(subText);
        subLbl.setFont(Theme.FONT_SMALL);
        subLbl.setForeground(Theme.TEXT_MUTED);

        card.add(topSub, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(subLbl, BorderLayout.SOUTH);

        return card;
    }

    private CardPanel createPackageCard(Package p) {
        CardPanel card = new CardPanel(Theme.SECONDARY);
        card.setLayout(new BorderLayout(0, 8));

        // Dynamically Cached Destination Image with Rounded Borders
        ImageIcon destImg = DestinationImageCache.getDestinationImage(p.getDestination(), 280, 110, 12);
        JLabel imgLabel = new JLabel(destImg);
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(imgLabel, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 6));
        content.setOpaque(false);

        JLabel lblName = new JLabel(p.getPackageName());
        lblName.setFont(Theme.FONT_BOLD);
        lblName.setForeground(Theme.TEXT_MAIN);

        JLabel lblDest = new JLabel("📍 " + p.getDestination() + " • " + p.getDuration());
        lblDest.setFont(Theme.FONT_SMALL);
        lblDest.setForeground(Theme.TEXT_MUTED);

        JLabel lblPrice = new JLabel("₹" + String.format("%.2f", p.getPrice()));
        lblPrice.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 16));
        lblPrice.setForeground(Theme.ACCENT);

        JPanel details = new JPanel(new GridLayout(2, 1));
        details.setOpaque(false);
        details.add(lblDest);
        details.add(lblPrice);

        content.add(lblName, BorderLayout.NORTH);
        content.add(details, BorderLayout.CENTER);

        ModernButton btnDetail = new ModernButton("View Package", ModernButton.Style.OUTLINE);
        btnDetail.addActionListener(e -> mainFrame.showCard("PACKAGES"));

        card.add(content, BorderLayout.CENTER);
        card.add(btnDetail, BorderLayout.SOUTH);

        return card;
    }
}
