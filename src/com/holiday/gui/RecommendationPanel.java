package com.holiday.gui;

import com.holiday.dao.PackageDAO;
import com.holiday.gui.ui.CardPanel;
import com.holiday.gui.ui.ModernButton;
import com.holiday.gui.ui.Theme;
import com.holiday.model.Package;
import com.holiday.util.BudgetRecommendationEngine;
import com.holiday.util.BudgetRecommendationEngine.RecommendationResult;
import com.holiday.util.DestinationImageCache;
import com.holiday.util.SoundManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

public class RecommendationPanel extends JPanel {

    private MainFrame mainFrame;
    private BudgetRecommendationEngine engine;

    // Filters
    private JSlider sliderBudget;
    private JLabel lblBudgetValue;
    private JSpinner spinTravelers;
    private JSpinner spinDays;
    private JComboBox<String> comboHotelPref;
    private JComboBox<String> comboTransportPref;
    private JComboBox<String> comboMealPlan;

    // Containers
    private JPanel resultsGrid;
    private JPanel comparisonPanel;
    private List<Package> selectedForCompare = new ArrayList<>();

    public RecommendationPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.engine = new BudgetRecommendationEngine();

        setLayout(new BorderLayout(0, 15));
        setOpaque(false);

        // Header
        CardPanel header = new CardPanel();
        header.setLayout(new BorderLayout());

        JLabel lblTitle = new JLabel("⭐ Smart Destination Pricing & Budget Recommendation Engine");
        lblTitle.setFont(Theme.FONT_HEADER);
        lblTitle.setForeground(Theme.TEXT_MAIN);

        JLabel lblSub = new JLabel("Ranks best-value holiday packages using real destination_pricing cost matrices & preferences.");
        lblSub.setFont(Theme.FONT_SUBTITLE);
        lblSub.setForeground(Theme.TEXT_MUTED);

        header.add(lblTitle, BorderLayout.NORTH);
        header.add(lblSub, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // Top Filter Bar Card
        CardPanel filterCard = createFilterCard();

        // Center Split Container
        JPanel centerContent = new JPanel(new BorderLayout(0, 15));
        centerContent.setOpaque(false);
        centerContent.add(filterCard, BorderLayout.NORTH);

        resultsGrid = new JPanel(new GridLayout(0, 2, 15, 15));
        resultsGrid.setOpaque(false);

        JScrollPane scrollResults = new JScrollPane(resultsGrid);
        scrollResults.setBorder(BorderFactory.createEmptyBorder());
        scrollResults.getViewport().setBackground(Theme.BACKGROUND);
        centerContent.add(scrollResults, BorderLayout.CENTER);

        // Bottom Comparison Panel
        comparisonPanel = createComparisonPanel();
        centerContent.add(comparisonPanel, BorderLayout.SOUTH);

        add(centerContent, BorderLayout.CENTER);

        runRecommendationSearch();
    }

    private CardPanel createFilterCard() {
        CardPanel card = new CardPanel(Theme.PRIMARY);
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. Budget Slider
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.15;
        JLabel lblB = new JLabel("Max Budget (₹):");
        lblB.setFont(Theme.FONT_LABEL);
        lblB.setForeground(Theme.TEXT_MAIN);
        card.add(lblB, gbc);

        gbc.gridx = 1; gbc.weightx = 0.35;
        sliderBudget = new JSlider(10000, 250000, 60000);
        sliderBudget.setOpaque(false);
        lblBudgetValue = new JLabel("₹60,000", SwingConstants.LEFT);
        lblBudgetValue.setFont(Theme.FONT_BOLD);
        lblBudgetValue.setForeground(Theme.ACCENT);

        sliderBudget.addChangeListener(e -> lblBudgetValue.setText(String.format("₹%,d", sliderBudget.getValue())));

        JPanel budgetPnl = new JPanel(new BorderLayout(10, 0));
        budgetPnl.setOpaque(false);
        budgetPnl.add(sliderBudget, BorderLayout.CENTER);
        budgetPnl.add(lblBudgetValue, BorderLayout.EAST);
        card.add(budgetPnl, gbc);

        // 2. Travelers & Duration Spinners
        gbc.gridx = 2; gbc.weightx = 0.15;
        JLabel lblTrav = new JLabel("Travelers & Days:");
        lblTrav.setFont(Theme.FONT_LABEL);
        lblTrav.setForeground(Theme.TEXT_MAIN);
        card.add(lblTrav, gbc);

        gbc.gridx = 3; gbc.weightx = 0.35;
        JPanel spinGroup = new JPanel(new GridLayout(1, 2, 8, 0));
        spinGroup.setOpaque(false);

        spinTravelers = new JSpinner(new SpinnerNumberModel(2, 1, 20, 1));
        spinDays = new JSpinner(new SpinnerNumberModel(4, 1, 30, 1));
        spinGroup.add(spinTravelers);
        spinGroup.add(spinDays);
        card.add(spinGroup, gbc);

        // 3. Hotel Category
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.15;
        JLabel lblHotel = new JLabel("Hotel Category:");
        lblHotel.setFont(Theme.FONT_LABEL);
        lblHotel.setForeground(Theme.TEXT_MAIN);
        card.add(lblHotel, gbc);

        gbc.gridx = 1; gbc.weightx = 0.35;
        comboHotelPref = new JComboBox<>(new String[]{"3 Star Standard", "4 Star Deluxe", "5 Star Luxury"});
        comboHotelPref.setFont(Theme.FONT_REGULAR);
        card.add(comboHotelPref, gbc);

        // 4. Transport Mode
        gbc.gridx = 2; gbc.weightx = 0.15;
        JLabel lblTrans = new JLabel("Transport Mode:");
        lblTrans.setFont(Theme.FONT_LABEL);
        lblTrans.setForeground(Theme.TEXT_MAIN);
        card.add(lblTrans, gbc);

        gbc.gridx = 3; gbc.weightx = 0.35;
        comboTransportPref = new JComboBox<>(new String[]{"Shared Cab", "Private Cab", "Flight", "Express Train", "AC Bus"});
        comboTransportPref.setFont(Theme.FONT_REGULAR);
        card.add(comboTransportPref, gbc);

        // 5. Meal Plan
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.15;
        JLabel lblMeal = new JLabel("Meal Plan:");
        lblMeal.setFont(Theme.FONT_LABEL);
        lblMeal.setForeground(Theme.TEXT_MAIN);
        card.add(lblMeal, gbc);

        gbc.gridx = 1; gbc.weightx = 0.35;
        comboMealPlan = new JComboBox<>(new String[]{"Breakfast Only", "MAP (Breakfast + Dinner)", "AP (All Meals Included)"});
        comboMealPlan.setFont(Theme.FONT_REGULAR);
        card.add(comboMealPlan, gbc);

        // 6. Action Button
        gbc.gridx = 2; gbc.gridwidth = 2; gbc.weightx = 0.5;
        ModernButton btnFind = new ModernButton("🔍 Calculate Budget Recommendations", ModernButton.Style.ACCENT_ORANGE);
        btnFind.addActionListener(e -> runRecommendationSearch());
        card.add(btnFind, gbc);

        return card;
    }

    private void runRecommendationSearch() {
        resultsGrid.removeAll();

        double maxBudget = sliderBudget.getValue();
        int travelers = (int) spinTravelers.getValue();
        int days = (int) spinDays.getValue();
        String hotel = (String) comboHotelPref.getSelectedItem();
        String transport = (String) comboTransportPref.getSelectedItem();
        String meal = (String) comboMealPlan.getSelectedItem();

        List<RecommendationResult> list = engine.getRecommendations(maxBudget, travelers, days, hotel, transport, meal);

        for (RecommendationResult res : list) {
            resultsGrid.add(createRecommendationCard(res));
        }

        resultsGrid.revalidate();
        resultsGrid.repaint();
    }

    private CardPanel createRecommendationCard(RecommendationResult res) {
        Package p = res.getPackage();
        CardPanel card = new CardPanel(res.getMatchScore() >= 80 ? Theme.SUCCESS : Theme.SECONDARY);
        card.setLayout(new BorderLayout(0, 10));

        // Top Header Image + Title
        JPanel topHeader = new JPanel(new BorderLayout(10, 0));
        topHeader.setOpaque(false);

        JLabel imgLabel = new JLabel(DestinationImageCache.getDestinationImage(p.getDestination(), 120, 80, 10));
        topHeader.add(imgLabel, BorderLayout.WEST);

        JPanel titleBox = new JPanel(new GridLayout(3, 1));
        titleBox.setOpaque(false);

        JLabel lblName = new JLabel(p.getPackageName());
        lblName.setFont(Theme.FONT_TITLE);
        lblName.setForeground(Theme.TEXT_MAIN);

        JLabel lblDest = new JLabel("📍 " + p.getDestination() + " • " + p.getDuration());
        lblDest.setFont(Theme.FONT_SMALL);
        lblDest.setForeground(Theme.TEXT_MUTED);

        JLabel lblMatchReason = new JLabel(res.getMatchReason());
        lblMatchReason.setFont(Theme.FONT_SMALL);
        lblMatchReason.setForeground(res.getSavings() >= 0 ? Theme.SUCCESS : Theme.WARNING);

        titleBox.add(lblName);
        titleBox.add(lblDest);
        titleBox.add(lblMatchReason);

        topHeader.add(titleBox, BorderLayout.CENTER);
        card.add(topHeader, BorderLayout.NORTH);

        // Center Details & Score Bar
        JPanel centerInfo = new JPanel(new GridLayout(3, 2, 5, 5));
        centerInfo.setOpaque(false);

        centerInfo.add(createDetailLabel("🏨 Hotel Tier:", res.getHotelTier()));
        centerInfo.add(createDetailLabel("🚌 Transport:", res.getTransportMode()));
        centerInfo.add(createDetailLabel("Estimated Cost:", String.format("₹%,.2f", res.getCalculatedTotalCost())));
        centerInfo.add(createDetailLabel("Budget Savings:", res.getSavings() >= 0 ? String.format("₹%,.2f", res.getSavings()) : "Over budget"));

        // Match score bar
        JPanel scoreBox = new JPanel(new BorderLayout(5, 0));
        scoreBox.setOpaque(false);

        JLabel lblScoreStr = new JLabel(res.getMatchScore() + "% Budget Match", SwingConstants.RIGHT);
        lblScoreStr.setFont(Theme.FONT_BADGE);
        lblScoreStr.setForeground(res.getMatchScore() >= 80 ? Theme.SUCCESS : Theme.ACCENT);

        JProgressBar pb = new JProgressBar(0, 100);
        pb.setValue(res.getMatchScore());
        pb.setPreferredSize(new Dimension(100, 12));
        pb.setForeground(res.getMatchScore() >= 80 ? Theme.SUCCESS : Theme.WARNING);
        pb.setBackground(Theme.BORDER);
        pb.setBorderPainted(false);

        scoreBox.add(lblScoreStr, BorderLayout.NORTH);
        scoreBox.add(pb, BorderLayout.SOUTH);

        centerInfo.add(createDetailLabel("⭐ Value Rank:", ""));
        centerInfo.add(scoreBox);

        card.add(centerInfo, BorderLayout.CENTER);

        // Bottom Actions
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 10, 0));
        btnRow.setOpaque(false);

        ModernButton btnBook = new ModernButton("Book Package", ModernButton.Style.SUCCESS);
        btnBook.addActionListener(e -> mainFrame.showCard("BOOKINGS"));

        ModernButton btnCompare = new ModernButton("+ Compare", ModernButton.Style.OUTLINE);
        btnCompare.addActionListener(e -> toggleCompare(p));

        btnRow.add(btnBook);
        btnRow.add(btnCompare);

        card.add(btnRow, BorderLayout.SOUTH);

        return card;
    }

    private JLabel createDetailLabel(String key, String val) {
        JLabel lbl = new JLabel("<html><span style='color:#94A3B8;'>" + key + "</span> <b style='color:#FFFFFF;'>" + val + "</b></html>");
        lbl.setFont(Theme.FONT_REGULAR);
        return lbl;
    }

    private JPanel createComparisonPanel() {
        CardPanel panel = new CardPanel();
        panel.setPreferredSize(new Dimension(getWidth(), 80));
        panel.setLayout(new BorderLayout());

        JLabel title = new JLabel("⚖ Package Comparison Slot (Select up to 3 packages)");
        title.setFont(Theme.FONT_BOLD);
        title.setForeground(Theme.TEXT_MUTED);
        panel.add(title, BorderLayout.WEST);

        ModernButton btnShowCompare = new ModernButton("Compare Selected Packages", ModernButton.Style.SECONDARY);
        btnShowCompare.addActionListener(e -> showComparisonDialog());
        panel.add(btnShowCompare, BorderLayout.EAST);

        return panel;
    }

    private void toggleCompare(Package p) {
        if (selectedForCompare.contains(p)) {
            selectedForCompare.remove(p);
            SoundManager.playClickSound();
            JOptionPane.showMessageDialog(this, p.getPackageName() + " removed from comparison list.");
        } else {
            if (selectedForCompare.size() >= 3) {
                SoundManager.playErrorSound();
                JOptionPane.showMessageDialog(this, "You can compare up to 3 packages at a time.", "Limit Reached", JOptionPane.WARNING_MESSAGE);
                return;
            }
            selectedForCompare.add(p);
            SoundManager.playSuccessSound();
            JOptionPane.showMessageDialog(this, p.getPackageName() + " added to comparison list!");
        }
    }

    private void showComparisonDialog() {
        if (selectedForCompare.isEmpty()) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Please click '+ Compare' on at least 1 package to view comparison.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder("=== PACKAGE COMPARISON SUMMARY ===\n\n");
        for (Package p : selectedForCompare) {
            sb.append("🌴 Package: ").append(p.getPackageName()).append("\n");
            sb.append("   Destination: ").append(p.getDestination()).append("\n");
            sb.append("   Duration   : ").append(p.getDuration()).append("\n");
            sb.append("   Price      : ₹").append(String.format("%.2f", p.getPrice())).append("\n");
            sb.append("   Hotel      : ").append(p.getHotel()).append("\n");
            sb.append("   Transport  : ").append(p.getTransport()).append("\n");
            sb.append("------------------------------------------\n");
        }

        SoundManager.playClickSound();
        JOptionPane.showMessageDialog(this, sb.toString(), "Package Comparison Tool", JOptionPane.INFORMATION_MESSAGE);
    }
}
