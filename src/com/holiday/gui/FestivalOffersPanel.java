package com.holiday.gui;

import com.holiday.dao.FestivalOfferDAO;
import com.holiday.gui.ui.CardPanel;
import com.holiday.gui.ui.ModernButton;
import com.holiday.gui.ui.StatusBadge;
import com.holiday.gui.ui.Theme;
import com.holiday.model.FestivalOffer;
import com.holiday.util.FestivalImageUtil;
import com.holiday.util.SoundManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.sql.Date;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class FestivalOffersPanel extends JPanel {

    private MainFrame mainFrame;
    private FestivalOfferDAO festivalOfferDAO;

    // Stat Metric Labels
    private JLabel lblTotalOffers;
    private JLabel lblActiveOffers;
    private JLabel lblScheduledOffers;
    private JLabel lblExpiredOffers;
    private JLabel lblTotalDiscounts;
    private JLabel lblOfferBookingsSummary;

    // Controls
    private JTextField txtSearch;
    private JComboBox<String> comboFilterStatus;
    private JPanel cardsContainer;
    private JScrollPane scrollPane;

    private List<FestivalOffer> allOffers = new ArrayList<>();

    public FestivalOffersPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.festivalOfferDAO = new FestivalOfferDAO();

        setLayout(new BorderLayout(0, 12));
        setOpaque(false);

        // Root container scrollable
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);

        // 1. TOP HERO BANNER
        JPanel heroBanner = createHeroBanner();
        mainContent.add(heroBanner);
        mainContent.add(Box.createVerticalStrut(14));

        // 2. STATISTICS METRIC CARDS
        JPanel statsPanel = createStatsPanel();
        mainContent.add(statsPanel);
        mainContent.add(Box.createVerticalStrut(14));

        // 3. SEARCH & FILTER TOOLBAR
        JPanel filterToolbar = createFilterToolbar();
        mainContent.add(filterToolbar);
        mainContent.add(Box.createVerticalStrut(12));

        // 4. OFFERS CARDS GRID
        cardsContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 16));
        cardsContainer.setOpaque(false);

        mainContent.add(cardsContainer);

        scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        add(scrollPane, BorderLayout.CENTER);

        loadData();
    }

    private JPanel createHeroBanner() {
        JPanel hero = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Draw background hero image smoothly
                ImageIcon heroIcon = FestivalImageUtil.getFestivalImage("src/com/holiday/assets/festivals/festival_hero_banner.jpg", "Hero", w, h, 14);
                if (heroIcon != null && heroIcon.getImage() != null) {
                    g2.drawImage(heroIcon.getImage(), 0, 0, w, h, null);
                } else {
                    g2.setColor(Theme.PRIMARY);
                    g2.fillRoundRect(0, 0, w, h, 14, 14);
                }

                // Semi-translucent gradient scrim for text readability
                java.awt.GradientPaint scrim = new java.awt.GradientPaint(
                        0, 0, new Color(18, 48, 58, 220),
                        w, 0, new Color(18, 48, 58, 140)
                );
                g2.setPaint(scrim);
                g2.fillRoundRect(0, 0, w, h, 14, 14);

                // Golden subtle top-left accent line
                g2.setColor(Theme.ACCENT);
                g2.fillRoundRect(0, 0, w, 4, 14, 14);

                g2.dispose();
            }
        };

        hero.setPreferredSize(new Dimension(1000, 145));
        hero.setMinimumSize(new Dimension(800, 135));
        hero.setBorder(BorderFactory.createEmptyBorder(18, 25, 18, 25));

        // Left Text Block
        JPanel textBlock = new JPanel(new GridLayout(2, 1, 0, 4));
        textBlock.setOpaque(false);

        JLabel titleLbl = new JLabel("🎉 FESTIVAL SPECIAL OFFERS & PROMOTIONS");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLbl.setForeground(Color.WHITE);

        JLabel subtitleLbl = new JLabel("Celebrate Diwali, Navratri, Christmas & Summer holidays with limited-time package discounts & seasonal savings.");
        subtitleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLbl.setForeground(new Color(230, 245, 248));

        textBlock.add(titleLbl);
        textBlock.add(subtitleLbl);

        // Right Button Block
        JPanel actionBlock = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 20));
        actionBlock.setOpaque(false);

        ModernButton btnCreateOffer = new ModernButton("➕ Create New Offer", ModernButton.Style.PRIMARY);
        btnCreateOffer.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCreateOffer.setPreferredSize(new Dimension(200, 44));
        btnCreateOffer.addActionListener(e -> openCreateOfferDialog());

        ModernButton btnRefresh = new ModernButton("🔄 Refresh", ModernButton.Style.OUTLINE);
        btnRefresh.setPreferredSize(new Dimension(110, 44));
        btnRefresh.addActionListener(e -> loadData());

        actionBlock.add(btnCreateOffer);
        actionBlock.add(btnRefresh);

        hero.add(textBlock, BorderLayout.WEST);
        hero.add(actionBlock, BorderLayout.EAST);

        return hero;
    }

    private JPanel createStatsPanel() {
        JPanel statsGrid = new JPanel(new GridLayout(1, 5, 14, 0));
        statsGrid.setOpaque(false);
        statsGrid.setPreferredSize(new Dimension(1000, 100));

        lblTotalOffers = new JLabel("0");
        lblActiveOffers = new JLabel("0");
        lblScheduledOffers = new JLabel("0");
        lblExpiredOffers = new JLabel("0");
        lblTotalDiscounts = new JLabel("₹0");

        statsGrid.add(createStatCard("TOTAL OFFERS", lblTotalOffers, "🎁", Theme.PRIMARY, "Campaigns created"));
        statsGrid.add(createStatCard("ACTIVE OFFERS", lblActiveOffers, "🔥", Theme.SUCCESS, "Live discounts now"));
        statsGrid.add(createStatCard("SCHEDULED", lblScheduledOffers, "📅", Theme.WARNING, "Upcoming festivals"));
        statsGrid.add(createStatCard("EXPIRED / PAUSED", lblExpiredOffers, "⏸️", Theme.TEXT_MUTED, "Inactive campaigns"));
        statsGrid.add(createStatCard("DISCOUNTS GIVEN", lblTotalDiscounts, "💰", Theme.ACCENT, "Total customer savings"));

        return statsGrid;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, String icon, Color accent, String subtext) {
        CardPanel card = new CardPanel(accent);
        card.setLayout(new BorderLayout(0, 2));
        card.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        titleLbl.setForeground(Theme.TEXT_MUTED);

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));

        topRow.add(titleLbl, BorderLayout.WEST);
        topRow.add(iconLbl, BorderLayout.EAST);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(Theme.TEXT_MAIN);

        JLabel subLabel = new JLabel(subtext);
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        subLabel.setForeground(Theme.TEXT_MUTED);

        card.add(topRow, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(subLabel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createFilterToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout(15, 0));
        toolbar.setOpaque(false);

        // Search Group
        JPanel leftGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftGroup.setOpaque(false);

        JLabel lblSearch = new JLabel("🔍 Search:");
        lblSearch.setFont(Theme.FONT_LABEL);
        lblSearch.setForeground(Theme.TEXT_MAIN);

        txtSearch = new JTextField(20);
        txtSearch.setFont(Theme.FONT_REGULAR);
        txtSearch.setBorder(Theme.BORDER_INPUT);
        txtSearch.setToolTipText("Filter by offer name, festival, or package...");
        txtSearch.addActionListener(e -> filterOffers());

        ModernButton btnSearch = new ModernButton("Search", ModernButton.Style.SECONDARY);
        btnSearch.addActionListener(e -> filterOffers());

        leftGroup.add(lblSearch);
        leftGroup.add(txtSearch);
        leftGroup.add(btnSearch);

        // Filter Group
        JPanel rightGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightGroup.setOpaque(false);

        JLabel lblFilter = new JLabel("Status Filter:");
        lblFilter.setFont(Theme.FONT_LABEL);
        lblFilter.setForeground(Theme.TEXT_MAIN);

        comboFilterStatus = new JComboBox<>(new String[]{"All Statuses", "Active", "Scheduled", "Paused", "Expired"});
        comboFilterStatus.setFont(Theme.FONT_REGULAR);
        comboFilterStatus.addActionListener(e -> filterOffers());

        lblOfferBookingsSummary = new JLabel("Offers applied in bookings: 0");
        lblOfferBookingsSummary.setFont(Theme.FONT_BADGE);
        lblOfferBookingsSummary.setForeground(Theme.PRIMARY);

        rightGroup.add(lblOfferBookingsSummary);
        rightGroup.add(Box.createHorizontalStrut(10));
        rightGroup.add(lblFilter);
        rightGroup.add(comboFilterStatus);

        toolbar.add(leftGroup, BorderLayout.WEST);
        toolbar.add(rightGroup, BorderLayout.EAST);

        return toolbar;
    }

    public void loadData() {
        // 1. Fetch real statistics
        Map<String, Object> stats = festivalOfferDAO.getOfferStatistics();
        lblTotalOffers.setText(String.valueOf(stats.get("totalOffers")));
        lblActiveOffers.setText(String.valueOf(stats.get("activeOffers")));
        lblScheduledOffers.setText(String.valueOf(stats.get("scheduledOffers")));
        lblExpiredOffers.setText(String.valueOf(stats.get("expiredOffers")));

        double discounts = (Double) stats.get("totalDiscountsGiven");
        NumberFormat curFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"));
        lblTotalDiscounts.setText(curFormat.format(discounts));

        int offerBookings = (Integer) stats.get("totalOfferBookings");
        double offerRevenue = (Double) stats.get("totalOfferBookingValue");
        lblOfferBookingsSummary.setText("📊 Applied in " + offerBookings + " Bookings (" + curFormat.format(offerRevenue) + ")");

        // 2. Fetch all offers
        allOffers = festivalOfferDAO.getAllOffers();
        filterOffers();
    }

    private void filterOffers() {
        String query = txtSearch.getText().trim().toLowerCase();
        String statusFilter = (String) comboFilterStatus.getSelectedItem();

        cardsContainer.removeAll();

        int matchedCount = 0;
        for (FestivalOffer offer : allOffers) {
            // Filter by status
            if (statusFilter != null && !"All Statuses".equalsIgnoreCase(statusFilter)) {
                if (!statusFilter.equalsIgnoreCase(offer.getStatus())) {
                    continue;
                }
            }

            // Filter by search query
            if (!query.isEmpty()) {
                String fullSearchable = (offer.getOfferName() + " " + offer.getFestivalName() + " "
                        + (offer.getDescription() != null ? offer.getDescription() : "") + " "
                        + (offer.getApplicablePackageName() != null ? offer.getApplicablePackageName() : "")).toLowerCase();
                if (!fullSearchable.contains(query)) {
                    continue;
                }
            }

            JPanel card = createOfferCard(offer);
            cardsContainer.add(card);
            matchedCount++;
        }

        if (matchedCount == 0) {
            cardsContainer.add(createEmptyState());
        }

        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    private JPanel createOfferCard(FestivalOffer offer) {
        CardPanel card = new CardPanel();
        card.setLayout(new BorderLayout(0, 8));
        card.setPreferredSize(new Dimension(360, 480));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1),
                BorderFactory.createEmptyBorder(0, 0, 10, 0)
        ));

        // 1. TOP IMAGE BANNER WITH OVERLAYS
        JPanel imageBannerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Draw actual scaled festival banner
                ImageIcon icon = FestivalImageUtil.getFestivalImage(offer.getImagePath(), offer.getFestivalName(), w, h, 10);
                if (icon != null && icon.getImage() != null) {
                    g2.drawImage(icon.getImage(), 0, 0, w, h, null);
                }

                // Dark top gradient strip for badges
                java.awt.GradientPaint topVignette = new java.awt.GradientPaint(
                        0, 0, new Color(0, 0, 0, 120),
                        0, 40, new Color(0, 0, 0, 0)
                );
                g2.setPaint(topVignette);
                g2.fillRect(0, 0, w, 40);

                g2.dispose();
            }
        };
        imageBannerPanel.setPreferredSize(new Dimension(360, 175));
        imageBannerPanel.setLayout(new BorderLayout());
        imageBannerPanel.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        // Floating Badges on top of image
        JPanel topBadgeRow = new JPanel(new BorderLayout());
        topBadgeRow.setOpaque(false);

        // Discount Badge (e.g. 20% OFF or ₹2,000 OFF)
        JLabel discBadge = new JLabel(" " + offer.getDiscountBadgeText() + " ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.ACCENT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        discBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        discBadge.setForeground(Color.WHITE);
        discBadge.setHorizontalAlignment(SwingConstants.CENTER);
        discBadge.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        // Status Badge
        StatusBadge statusBadge = new StatusBadge(offer.getStatus());

        topBadgeRow.add(discBadge, BorderLayout.WEST);
        topBadgeRow.add(statusBadge, BorderLayout.EAST);
        imageBannerPanel.add(topBadgeRow, BorderLayout.NORTH);

        // Bottom Festival Tag over Image
        JPanel bottomTagRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bottomTagRow.setOpaque(false);
        String emoji = FestivalImageUtil.getFestivalEmoji(offer.getFestivalName());
        JLabel festivalTag = new JLabel(" " + emoji + " " + offer.getFestivalName().toUpperCase() + " SPECIAL ");
        festivalTag.setFont(new Font("Segoe UI", Font.BOLD, 10));
        festivalTag.setForeground(Color.WHITE);
        festivalTag.setOpaque(true);
        festivalTag.setBackground(new Color(22, 74, 91, 210));
        festivalTag.setBorder(BorderFactory.createEmptyBorder(3, 7, 3, 7));
        bottomTagRow.add(festivalTag);
        imageBannerPanel.add(bottomTagRow, BorderLayout.SOUTH);

        card.add(imageBannerPanel, BorderLayout.NORTH);

        // 2. CARD BODY CONTENT
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(4, 14, 4, 14));

        // Offer Title
        JLabel titleLbl = new JLabel(offer.getOfferName());
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLbl.setForeground(Theme.TEXT_MAIN);
        body.add(titleLbl);
        body.add(Box.createVerticalStrut(4));

        // Short Description
        String desc = offer.getDescription();
        if (desc == null || desc.trim().isEmpty()) {
            desc = "Special festival promotion offering attractive package discounts for travelers.";
        }
        JLabel descLbl = new JLabel("<html><body style='width: 300px; color: #4A6B78; font-size: 10px;'>" + desc + "</body></html>");
        descLbl.setFont(Theme.FONT_SMALL);
        body.add(descLbl);
        body.add(Box.createVerticalStrut(8));

        // Details Grid (Applicable package, dates, min booking, usage)
        JPanel detailsGrid = new JPanel(new GridLayout(4, 1, 0, 4));
        detailsGrid.setBackground(new Color(0xF4, 0xF8, 0xF8));
        detailsGrid.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));

        // 1. Applicable Package
        String pkgName = offer.getApplicablePackageId() != null && offer.getApplicablePackageName() != null
                ? "🌴 " + offer.getApplicablePackageName()
                : "🌟 All Holiday Packages";
        JLabel pkgLbl = new JLabel(pkgName);
        pkgLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        pkgLbl.setForeground(Theme.PRIMARY);

        // 2. Validity Dates
        JLabel dateLbl = new JLabel("📅 " + offer.getFormattedDateRange());
        dateLbl.setFont(Theme.FONT_SMALL);
        dateLbl.setForeground(Theme.TEXT_MAIN);

        // 3. Financial Info (Min Booking & Max Cap)
        String minBk = offer.getMinBookingAmount() > 0 ? "₹" + String.format("%.0f", offer.getMinBookingAmount()) : "None";
        String cap = offer.getMaxDiscount() != null && offer.getMaxDiscount() > 0 ? " | Cap: ₹" + String.format("%.0f", offer.getMaxDiscount()) : "";
        JLabel finLbl = new JLabel("💰 Min Booking: " + minBk + cap);
        finLbl.setFont(Theme.FONT_SMALL);
        finLbl.setForeground(Theme.TEXT_MAIN);

        // 4. Usage Info
        String usageStr = offer.getUsageLimit() != null && offer.getUsageLimit() > 0
                ? "👥 Used " + offer.getTimesUsed() + " / " + offer.getUsageLimit() + " times"
                : "👥 Used " + offer.getTimesUsed() + " times (Unlimited)";
        JLabel usageLbl = new JLabel(usageStr);
        usageLbl.setFont(Theme.FONT_SMALL);
        usageLbl.setForeground(offer.getUsageLimit() != null && offer.getTimesUsed() >= offer.getUsageLimit() ? Theme.DANGER : Theme.TEXT_MUTED);

        detailsGrid.add(pkgLbl);
        detailsGrid.add(dateLbl);
        detailsGrid.add(finLbl);
        detailsGrid.add(usageLbl);

        body.add(detailsGrid);
        card.add(body, BorderLayout.CENTER);

        // 3. ADMIN ACTION BUTTONS
        JPanel actionRow = new JPanel(new GridLayout(1, 3, 6, 0));
        actionRow.setOpaque(false);
        actionRow.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));

        ModernButton btnEdit = new ModernButton("✏️ Edit", ModernButton.Style.OUTLINE);
        btnEdit.setFont(Theme.FONT_SMALL);
        btnEdit.addActionListener(e -> openEditOfferDialog(offer));

        // Toggle Status Button (Pause or Activate)
        boolean isActive = "Active".equalsIgnoreCase(offer.getStatus());
        ModernButton btnToggle = new ModernButton(isActive ? "⏸️ Pause" : "▶️ Activate",
                isActive ? ModernButton.Style.OUTLINE : ModernButton.Style.SUCCESS);
        btnToggle.setFont(Theme.FONT_SMALL);
        btnToggle.addActionListener(e -> toggleOfferStatus(offer));

        ModernButton btnDelete = new ModernButton("🗑️ Delete", ModernButton.Style.DANGER);
        btnDelete.setFont(Theme.FONT_SMALL);
        btnDelete.addActionListener(e -> deleteOfferAction(offer));

        actionRow.add(btnEdit);
        actionRow.add(btnToggle);
        actionRow.add(btnDelete);

        card.add(actionRow, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createEmptyState() {
        JPanel emptyPanel = new JPanel(new BorderLayout(0, 10));
        emptyPanel.setOpaque(false);
        emptyPanel.setPreferredSize(new Dimension(800, 200));

        JLabel iconLbl = new JLabel("🎁", SwingConstants.CENTER);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));

        JLabel titleLbl = new JLabel("No Festival Offers Found", SwingConstants.CENTER);
        titleLbl.setFont(Theme.FONT_TITLE);
        titleLbl.setForeground(Theme.TEXT_MAIN);

        JLabel subLbl = new JLabel("No offers match your current search or filter criteria. Create a new campaign to get started!", SwingConstants.CENTER);
        subLbl.setFont(Theme.FONT_SUBTITLE);
        subLbl.setForeground(Theme.TEXT_MUTED);

        JPanel centerText = new JPanel(new GridLayout(2, 1));
        centerText.setOpaque(false);
        centerText.add(titleLbl);
        centerText.add(subLbl);

        emptyPanel.add(iconLbl, BorderLayout.NORTH);
        emptyPanel.add(centerText, BorderLayout.CENTER);

        return emptyPanel;
    }

    private void openCreateOfferDialog() {
        SoundManager.playClickSound();
        FestivalOfferDialog dialog = new FestivalOfferDialog(mainFrame, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadData();
        }
    }

    private void openEditOfferDialog(FestivalOffer offer) {
        SoundManager.playClickSound();
        FestivalOfferDialog dialog = new FestivalOfferDialog(mainFrame, offer);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadData();
        }
    }

    private void toggleOfferStatus(FestivalOffer offer) {
        SoundManager.playClickSound();
        String current = offer.getStatus();
        String nextStatus = "Active".equalsIgnoreCase(current) ? "Paused" : "Active";

        if (festivalOfferDAO.updateOfferStatus(offer.getOfferId(), nextStatus)) {
            SoundManager.playSuccessSound();
            offer.setStatus(nextStatus);
            loadData();
        } else {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Failed to update offer status.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteOfferAction(FestivalOffer offer) {
        SoundManager.playClickSound();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete festival offer: '" + offer.getOfferName() + "'?\nExisting bookings with this offer applied will retain their discount records.",
                "Confirm Delete Festival Offer",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (festivalOfferDAO.deleteOffer(offer.getOfferId())) {
                SoundManager.playSuccessSound();
                JOptionPane.showMessageDialog(this, "Festival offer deleted successfully.", "Offer Deleted", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                SoundManager.playErrorSound();
                JOptionPane.showMessageDialog(this, "Failed to delete festival offer.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
