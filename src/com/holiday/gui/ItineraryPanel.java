package com.holiday.gui;

import com.holiday.dao.PackageDAO;
import com.holiday.gui.ui.CardPanel;
import com.holiday.gui.ui.Theme;
import com.holiday.model.Package;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

public class ItineraryPanel extends JPanel {

    private MainFrame mainFrame;
    private PackageDAO packageDAO;
    private JComboBox<Package> comboPackages;
    private JPanel timelineContainer;

    public ItineraryPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.packageDAO = new PackageDAO();

        setLayout(new BorderLayout(0, 15));
        setOpaque(false);

        // Header Bar Card
        CardPanel headerCard = new CardPanel();
        headerCard.setLayout(new BorderLayout(15, 0));

        JPanel titleBox = new JPanel(new GridLayout(2, 1));
        titleBox.setOpaque(false);

        JLabel lblTitle = new JLabel("🗺️ Visual Itinerary Planner");
        lblTitle.setFont(Theme.FONT_HEADER);
        lblTitle.setForeground(Theme.TEXT_MAIN);

        JLabel lblSub = new JLabel("Explore Day-wise travel schedules and excursion timelines.");
        lblSub.setFont(Theme.FONT_SUBTITLE);
        lblSub.setForeground(Theme.TEXT_MUTED);

        titleBox.add(lblTitle);
        titleBox.add(lblSub);

        // Package Selector
        JPanel selectorBox = new JPanel(new GridBagLayout());
        selectorBox.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 0, 5);

        JLabel lblSelect = new JLabel("Select Package:");
        lblSelect.setFont(Theme.FONT_LABEL);
        lblSelect.setForeground(Theme.TEXT_MAIN);

        comboPackages = new JComboBox<>();
        comboPackages.setFont(Theme.FONT_REGULAR);
        comboPackages.setPreferredSize(new Dimension(240, 36));
        comboPackages.addActionListener(e -> loadItineraryForSelectedPackage());

        selectorBox.add(lblSelect, gbc);
        selectorBox.add(comboPackages, gbc);

        headerCard.add(titleBox, BorderLayout.WEST);
        headerCard.add(selectorBox, BorderLayout.EAST);
        add(headerCard, BorderLayout.NORTH);

        // Timeline Scroll Panel
        timelineContainer = new JPanel();
        timelineContainer.setLayout(new javax.swing.BoxLayout(timelineContainer, javax.swing.BoxLayout.Y_AXIS));
        timelineContainer.setOpaque(false);

        JScrollPane timelineScroll = new JScrollPane(timelineContainer);
        timelineScroll.setBorder(BorderFactory.createEmptyBorder());
        timelineScroll.getViewport().setBackground(Theme.BACKGROUND);

        add(timelineScroll, BorderLayout.CENTER);

        loadPackageDropdown();
    }

    private void loadPackageDropdown() {
        comboPackages.removeAllItems();
        List<Package> list = packageDAO.getAllPackages();
        for (Package p : list) {
            comboPackages.addItem(p);
        }
        if (comboPackages.getItemCount() > 0) {
            comboPackages.setSelectedIndex(0);
        }
    }

    private void loadItineraryForSelectedPackage() {
        timelineContainer.removeAll();

        Package selected = (Package) comboPackages.getSelectedItem();
        if (selected == null) return;

        String pkgName = selected.getPackageName();
        String dest = selected.getDestination();

        // Sample Day-wise Timeline generated dynamically for the package
        timelineContainer.add(createTimelineCard("DAY 1 | ARRIVAL", "Arrival in " + dest, "• Airport / Station Pickup\n• Hotel Check-in & Welcome Drink\n• Evening Leisure & Local Sightseeing"));
        timelineContainer.add(createTimelineArrow());

        timelineContainer.add(createTimelineCard("DAY 2 | EXCURSION", "Excursions & Exploration in " + dest, "• Morning Breakfast at Hotel\n• Guided Sightseeing Tour\n• Photography & Local Market Shopping"));
        timelineContainer.add(createTimelineArrow());

        timelineContainer.add(createTimelineCard("DAY 3 | ADVENTURE", "Adventure & Scenic Highlights", "• Scenic Mountain / Nature Excursion\n• Special Activity & Cultural Evening Dinner"));
        timelineContainer.add(createTimelineArrow());

        timelineContainer.add(createTimelineCard("DAY 4 | DEPARTURE", "Departure & Transfer", "• Breakfast & Checkout\n• Souvenir Shopping\n• Departure Transfer to Airport"));

        timelineContainer.revalidate();
        timelineContainer.repaint();
    }

    private CardPanel createTimelineCard(String dayBadge, String titleStr, String descStr) {
        CardPanel card = new CardPanel(Theme.PRIMARY);
        card.setMaximumSize(new Dimension(800, 140));
        card.setLayout(new BorderLayout(15, 0));

        // Left Day Badge Panel
        JPanel leftPnl = new JPanel(new BorderLayout());
        leftPnl.setOpaque(false);
        leftPnl.setPreferredSize(new Dimension(140, 100));

        JLabel badge = new JLabel(dayBadge, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(Theme.FONT_BADGE);
        badge.setForeground(Color.WHITE);
        badge.setPreferredSize(new Dimension(130, 32));

        leftPnl.add(badge, BorderLayout.NORTH);
        card.add(leftPnl, BorderLayout.WEST);

        // Right Detail Panel
        JPanel rightPnl = new JPanel(new BorderLayout(0, 5));
        rightPnl.setOpaque(false);

        JLabel titleLbl = new JLabel(titleStr);
        titleLbl.setFont(Theme.FONT_TITLE);
        titleLbl.setForeground(Theme.PRIMARY);

        JLabel descLbl = new JLabel("<html>" + descStr.replaceAll("\n", "<br>") + "</html>");
        descLbl.setFont(Theme.FONT_REGULAR);
        descLbl.setForeground(Theme.TEXT_MAIN);

        rightPnl.add(titleLbl, BorderLayout.NORTH);
        rightPnl.add(descLbl, BorderLayout.CENTER);

        card.add(rightPnl, BorderLayout.CENTER);

        return card;
    }

    private JPanel createTimelineArrow() {
        JPanel arrowPnl = new JPanel(new BorderLayout());
        arrowPnl.setOpaque(false);
        arrowPnl.setMaximumSize(new Dimension(800, 30));

        JLabel arrow = new JLabel("↓", SwingConstants.CENTER);
        arrow.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 22));
        arrow.setForeground(Theme.ACCENT);

        arrowPnl.add(arrow, BorderLayout.CENTER);
        return arrowPnl;
    }
}
