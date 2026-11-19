package com.holiday.gui;

import com.holiday.dao.BookingDAO;
import com.holiday.dao.CustomerDAO;
import com.holiday.dao.CustomTripDAO;
import com.holiday.dao.PricingDAO;
import com.holiday.gui.ui.CardPanel;
import com.holiday.gui.ui.ModernButton;
import com.holiday.gui.ui.ModernTable;
import com.holiday.gui.ui.StatusBadge;
import com.holiday.gui.ui.Theme;
import com.holiday.model.Booking;
import com.holiday.model.Customer;
import com.holiday.model.CustomTrip;
import com.holiday.model.DestinationPricing;
import com.holiday.util.SoundManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class CustomTripPanel extends JPanel {

    private MainFrame mainFrame;
    private CustomerDAO customerDAO;
    private CustomTripDAO customTripDAO;
    private PricingDAO pricingDAO;
    private BookingDAO bookingDAO;

    // Form Fields
    private JTextField txtTripId;
    private JComboBox<Customer> comboCustomer;
    private JTextField txtDestination;
    private JTextField txtTravelDate;
    private JTextField txtReturnDate;
    private JSpinner spinnerTravelers;
    private JSpinner spinnerDays;

    private JComboBox<String> comboHotelCategory;
    private JTextField txtHotel;
    private JComboBox<String> comboTransport;
    private JComboBox<String> comboMealPlan;

    // Activities Checkboxes
    private JCheckBox cbSightseeing;
    private JCheckBox cbAdventure;
    private JCheckBox cbWaterSports;
    private JCheckBox cbCityTour;
    private JCheckBox cbMuseumVisit;
    private JCheckBox cbTrekking;
    private JCheckBox cbLocalExperience;

    // Additional Services Checkboxes
    private JCheckBox cbPickup;
    private JCheckBox cbGuide;
    private JCheckBox cbInsurance;
    private JTextArea txtAdditionalReq;

    // Budget & Discount Fields
    private JTextField txtBudget;
    private JTextField txtDiscount;
    private JComboBox<String> comboStatus;

    // Calculated Costs Breakdown Fields
    private JTextField txtHotelCost;
    private JTextField txtTransportCost;
    private JTextField txtMealCost;
    private JTextField txtActivityCost;
    private JTextField txtServicesCost;
    private JTextField txtServiceCharge;

    // Prominent Header & Price Labels
    private JLabel lblPerPersonCost;
    private JLabel lblTotalTravelers;
    private JLabel lblFinalPriceVal;
    private JLabel lblBudgetStatus;
    private JLabel lblOptimizationSuggest;
    private JPanel pnlOptimization;

    // Search & Directory Table
    private JTextField txtSearch;
    private JTable tripTable;
    private DefaultTableModel tableModel;

    public CustomTripPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.customerDAO = new CustomerDAO();
        this.customTripDAO = new CustomTripDAO();
        this.pricingDAO = new PricingDAO();
        this.bookingDAO = new BookingDAO();

        setLayout(new BorderLayout(0, 15));
        setOpaque(false);

        // Header Banner
        CardPanel headerPanel = new CardPanel();
        headerPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("✨ SMART CUSTOM TRIP PLANNER & DYNAMIC QUOTATION");
        titleLabel.setFont(Theme.FONT_HEADER);
        titleLabel.setForeground(Theme.TEXT_MAIN);

        JLabel subLabel = new JLabel("Create personalized holiday itineraries with transparent live price calculations, budget checks, and direct booking conversion.");
        subLabel.setFont(Theme.FONT_SUBTITLE);
        subLabel.setForeground(Theme.TEXT_MUTED);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subLabel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Main Center Grid: Left Multi-section Form + Right Estimated Price & Summary
        JPanel centerGrid = new JPanel(new GridLayout(1, 2, 15, 0));
        centerGrid.setOpaque(false);

        // Left Form Scroll Pane
        JPanel leftForm = createFormSections();
        JScrollPane formScroll = new JScrollPane(leftForm);
        formScroll.setBorder(BorderFactory.createEmptyBorder());
        formScroll.getViewport().setBackground(Theme.BACKGROUND);
        centerGrid.add(formScroll);

        // Right Column: Dynamic Price Card + Actions + Directory Table
        JPanel rightCol = createRightColumn();
        centerGrid.add(rightCol);

        add(centerGrid, BorderLayout.CENTER);

        loadCustomers();
        calculatePrices();
        loadTripTableData();
    }

    private JPanel createFormSections() {
        JPanel formContainer = new JPanel();
        formContainer.setLayout(new javax.swing.BoxLayout(formContainer, javax.swing.BoxLayout.Y_AXIS));
        formContainer.setOpaque(false);

        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { calculatePrices(); }
            public void removeUpdate(DocumentEvent e) { calculatePrices(); }
            public void changedUpdate(DocumentEvent e) { calculatePrices(); }
        };

        // 1. TRIP & CUSTOMER DETAILS
        CardPanel cardTrip = new CardPanel(Theme.PRIMARY);
        cardTrip.add(createSectionHeader("1. CUSTOMER & TRAVEL DATES"), BorderLayout.NORTH);

        JPanel pnlTrip = new JPanel(new GridBagLayout());
        pnlTrip.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        txtTripId = new JTextField();
        txtTripId.setEditable(false);
        txtTripId.setFont(Theme.FONT_REGULAR);
        txtTripId.setBackground(new Color(0xEA, 0xEF, 0xEF));
        txtTripId.setForeground(Theme.PRIMARY);
        addFormField(pnlTrip, "Trip ID (Auto):", txtTripId, gbc, row++);

        comboCustomer = new JComboBox<>();
        comboCustomer.setFont(Theme.FONT_REGULAR);
        addFormField(pnlTrip, "Customer *:", comboCustomer, gbc, row++);

        txtDestination = createStyledTextField("Jaipur", dl);
        addFormField(pnlTrip, "Destination *:", txtDestination, gbc, row++);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long tomorrow = System.currentTimeMillis() + 86400000L;
        long fiveDays = System.currentTimeMillis() + 86400000L * 6;

        txtTravelDate = createStyledTextField(sdf.format(new Date(tomorrow)), dl);
        addFormField(pnlTrip, "Travel Date (yyyy-MM-dd) *:", txtTravelDate, gbc, row++);

        txtReturnDate = createStyledTextField(sdf.format(new Date(fiveDays)), dl);
        addFormField(pnlTrip, "Return Date (yyyy-MM-dd):", txtReturnDate, gbc, row++);

        spinnerTravelers = new JSpinner(new SpinnerNumberModel(4, 1, 100, 1));
        spinnerTravelers.setFont(Theme.FONT_REGULAR);
        spinnerTravelers.addChangeListener((ChangeEvent e) -> calculatePrices());
        addFormField(pnlTrip, "No. of Travelers *:", spinnerTravelers, gbc, row++);

        spinnerDays = new JSpinner(new SpinnerNumberModel(5, 1, 60, 1));
        spinnerDays.setFont(Theme.FONT_REGULAR);
        spinnerDays.addChangeListener((ChangeEvent e) -> calculatePrices());
        addFormField(pnlTrip, "Trip Duration (Days) *:", spinnerDays, gbc, row++);

        cardTrip.add(pnlTrip, BorderLayout.CENTER);
        formContainer.add(cardTrip);
        formContainer.add(javax.swing.Box.createVerticalStrut(10));

        // 2. ACCOMMODATION, TRANSPORT & MEALS
        CardPanel cardStay = new CardPanel(Theme.SECONDARY);
        cardStay.add(createSectionHeader("2. ACCOMMODATION, TRANSPORT & MEALS"), BorderLayout.NORTH);

        JPanel pnlStay = new JPanel(new GridBagLayout());
        pnlStay.setOpaque(false);
        row = 0;

        comboHotelCategory = new JComboBox<>(new String[]{"No Hotel", "3 Star Deluxe", "4 Star Luxury", "5 Star Premium", "Custom Hotel"});
        comboHotelCategory.setSelectedIndex(2);
        comboHotelCategory.setFont(Theme.FONT_REGULAR);
        comboHotelCategory.addActionListener(e -> calculatePrices());
        addFormField(pnlStay, "Hotel Category:", comboHotelCategory, gbc, row++);

        txtHotel = createStyledTextField("Royal Heritage Palace", dl);
        addFormField(pnlStay, "Hotel Name:", txtHotel, gbc, row++);

        comboTransport = new JComboBox<>(new String[]{"Bus", "Train", "Shared Cab", "Private Cab", "Flight"});
        comboTransport.setSelectedIndex(3);
        comboTransport.setFont(Theme.FONT_REGULAR);
        comboTransport.addActionListener(e -> calculatePrices());
        addFormField(pnlStay, "Transport Mode:", comboTransport, gbc, row++);

        comboMealPlan = new JComboBox<>(new String[]{"No Meals", "Breakfast Only", "Breakfast + Dinner (MAP)", "All Meals (AP)"});
        comboMealPlan.setSelectedIndex(2);
        comboMealPlan.setFont(Theme.FONT_REGULAR);
        comboMealPlan.addActionListener(e -> calculatePrices());
        addFormField(pnlStay, "Meal Plan:", comboMealPlan, gbc, row++);

        cardStay.add(pnlStay, BorderLayout.CENTER);
        formContainer.add(cardStay);
        formContainer.add(javax.swing.Box.createVerticalStrut(10));

        // 3. ACTIVITIES & ADDITIONAL SERVICES
        CardPanel cardAct = new CardPanel(Theme.ACCENT);
        cardAct.add(createSectionHeader("3. ACTIVITIES & ADDITIONAL SERVICES"), BorderLayout.NORTH);

        JPanel pnlAct = new JPanel(new GridLayout(4, 2, 5, 5));
        pnlAct.setOpaque(false);

        cbSightseeing = createCheckBox("Sightseeing (₹1,000/p)", true);
        cbAdventure = createCheckBox("Adventure (₹2,500/p)", false);
        cbWaterSports = createCheckBox("Water Sports (₹3,000/p)", false);
        cbCityTour = createCheckBox("City Tour (₹1,500/p)", true);
        cbMuseumVisit = createCheckBox("Museum Visit (₹800/p)", false);
        cbTrekking = createCheckBox("Trekking (₹2,000/p)", false);
        cbLocalExperience = createCheckBox("Local Experience (₹1,200/p)", false);

        pnlAct.add(cbSightseeing);
        pnlAct.add(cbCityTour);
        pnlAct.add(cbAdventure);
        pnlAct.add(cbWaterSports);
        pnlAct.add(cbMuseumVisit);
        pnlAct.add(cbTrekking);
        pnlAct.add(cbLocalExperience);
        pnlAct.add(new JLabel(""));

        // Additional Services Checkboxes
        JPanel pnlServices = new JPanel(new GridLayout(2, 2, 5, 5));
        pnlServices.setOpaque(false);
        pnlServices.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Theme.BORDER), "Additional Services", 0, 0, Theme.FONT_BOLD, Theme.PRIMARY));

        cbPickup = createCheckBox("Airport/Station Pickup (₹1,500)", true);
        cbGuide = createCheckBox("Private Tour Guide (₹2,000)", false);
        cbInsurance = createCheckBox("Travel Insurance (₹500/p)", false);

        pnlServices.add(cbPickup);
        pnlServices.add(cbGuide);
        pnlServices.add(cbInsurance);
        pnlServices.add(new JLabel(""));

        txtAdditionalReq = new JTextArea(2, 20);
        txtAdditionalReq.setFont(Theme.FONT_REGULAR);
        txtAdditionalReq.setBackground(Theme.BACKGROUND);
        txtAdditionalReq.setForeground(Theme.PRIMARY);
        txtAdditionalReq.setCaretColor(Theme.PRIMARY);
        txtAdditionalReq.setLineWrap(true);
        txtAdditionalReq.setWrapStyleWord(true);
        JScrollPane reqScroll = new JScrollPane(txtAdditionalReq);
        reqScroll.setPreferredSize(new Dimension(300, 45));

        JPanel pnlReq = new JPanel(new BorderLayout(0, 4));
        pnlReq.setOpaque(false);
        JLabel lblReq = new JLabel("Special Requests / Notes:");
        lblReq.setFont(Theme.FONT_LABEL);
        lblReq.setForeground(Theme.TEXT_MAIN);
        pnlReq.add(lblReq, BorderLayout.NORTH);
        pnlReq.add(reqScroll, BorderLayout.CENTER);

        JPanel actCombine = new JPanel(new BorderLayout(0, 8));
        actCombine.setOpaque(false);
        actCombine.add(pnlAct, BorderLayout.NORTH);
        actCombine.add(pnlServices, BorderLayout.CENTER);
        actCombine.add(pnlReq, BorderLayout.SOUTH);

        cardAct.add(actCombine, BorderLayout.CENTER);
        formContainer.add(cardAct);
        formContainer.add(javax.swing.Box.createVerticalStrut(10));

        // 4. BUDGET, DISCOUNT & STATUS
        CardPanel cardBudget = new CardPanel();
        cardBudget.add(createSectionHeader("4. BUDGET, DISCOUNT & STATUS"), BorderLayout.NORTH);

        JPanel pnlBudget = new JPanel(new GridBagLayout());
        pnlBudget.setOpaque(false);
        row = 0;

        txtBudget = createStyledTextField("70000", dl);
        addFormField(pnlBudget, "Customer Budget (₹):", txtBudget, gbc, row++);

        txtDiscount = createStyledTextField("2000", dl);
        addFormField(pnlBudget, "Special Discount (₹):", txtDiscount, gbc, row++);

        comboStatus = new JComboBox<>(new String[]{"Draft", "Quotation Generated", "Approved", "Rejected", "Converted to Booking", "Cancelled"});
        comboStatus.setFont(Theme.FONT_REGULAR);
        addFormField(pnlBudget, "Custom Trip Status:", comboStatus, gbc, row++);

        cardBudget.add(pnlBudget, BorderLayout.CENTER);
        formContainer.add(cardBudget);

        return formContainer;
    }

    private JPanel createRightColumn() {
        JPanel rightCol = new JPanel(new BorderLayout(0, 12));
        rightCol.setOpaque(false);

        // TOP: ESTIMATED TRIP COST CARD
        CardPanel priceCard = new CardPanel(Theme.ACCENT);
        priceCard.setLayout(new BorderLayout(0, 10));

        JLabel cardHeader = new JLabel("ESTIMATED TRIP COST", SwingConstants.CENTER);
        cardHeader.setFont(Theme.FONT_TITLE);
        cardHeader.setForeground(Theme.TEXT_MUTED);

        JPanel centerPricePnl = new JPanel();
        centerPricePnl.setLayout(new javax.swing.BoxLayout(centerPricePnl, javax.swing.BoxLayout.Y_AXIS));
        centerPricePnl.setOpaque(false);

        lblPerPersonCost = new JLabel("₹0.00 / Person", SwingConstants.CENTER);
        lblPerPersonCost.setFont(Theme.FONT_TITLE);
        lblPerPersonCost.setForeground(Theme.PRIMARY);
        lblPerPersonCost.setAlignmentX(CENTER_ALIGNMENT);

        lblTotalTravelers = new JLabel("4 Travelers • 5 Days", SwingConstants.CENTER);
        lblTotalTravelers.setFont(Theme.FONT_SUBTITLE);
        lblTotalTravelers.setForeground(Theme.TEXT_MUTED);
        lblTotalTravelers.setAlignmentX(CENTER_ALIGNMENT);

        JLabel finalHeader = new JLabel("FINAL PACKAGE PRICE", SwingConstants.CENTER);
        finalHeader.setFont(Theme.FONT_LABEL);
        finalHeader.setForeground(Theme.ACCENT);
        finalHeader.setAlignmentX(CENTER_ALIGNMENT);

        lblFinalPriceVal = new JLabel("₹0.00", SwingConstants.CENTER);
        lblFinalPriceVal.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 30));
        lblFinalPriceVal.setForeground(Theme.PRIMARY);
        lblFinalPriceVal.setAlignmentX(CENTER_ALIGNMENT);

        lblBudgetStatus = new JLabel("✓ Trip fits within customer's budget.", SwingConstants.CENTER);
        lblBudgetStatus.setFont(Theme.FONT_BOLD);
        lblBudgetStatus.setForeground(Theme.SUCCESS);
        lblBudgetStatus.setAlignmentX(CENTER_ALIGNMENT);

        centerPricePnl.add(cardHeader);
        centerPricePnl.add(javax.swing.Box.createVerticalStrut(4));
        centerPricePnl.add(lblPerPersonCost);
        centerPricePnl.add(lblTotalTravelers);
        centerPricePnl.add(javax.swing.Box.createVerticalStrut(10));
        centerPricePnl.add(finalHeader);
        centerPricePnl.add(lblFinalPriceVal);
        centerPricePnl.add(javax.swing.Box.createVerticalStrut(8));
        centerPricePnl.add(lblBudgetStatus);

        // ITEMIZATION BREAKDOWN DISCLOSURE PANEL
        JPanel pnlBreakdown = new JPanel(new GridLayout(3, 2, 6, 4));
        pnlBreakdown.setOpaque(false);
        pnlBreakdown.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Theme.BORDER), "Cost Breakdown", 0, 0, Theme.FONT_SMALL, Theme.TEXT_MUTED));

        txtHotelCost = createReadOnlyCost();
        txtTransportCost = createReadOnlyCost();
        txtMealCost = createReadOnlyCost();
        txtActivityCost = createReadOnlyCost();
        txtServicesCost = createReadOnlyCost();
        txtServiceCharge = createReadOnlyCost();

        pnlBreakdown.add(createCostRow("Hotel:", txtHotelCost));
        pnlBreakdown.add(createCostRow("Transport:", txtTransportCost));
        pnlBreakdown.add(createCostRow("Meals:", txtMealCost));
        pnlBreakdown.add(createCostRow("Activities:", txtActivityCost));
        pnlBreakdown.add(createCostRow("Services:", txtServicesCost));
        pnlBreakdown.add(createCostRow("Service Fee:", txtServiceCharge));

        priceCard.add(centerPricePnl, BorderLayout.NORTH);
        priceCard.add(pnlBreakdown, BorderLayout.CENTER);

        // COST OPTIMIZATION SUGGESTION BOX
        pnlOptimization = new JPanel(new BorderLayout());
        pnlOptimization.setBackground(new Color(0xFF, 0xF8, 0xE7));
        pnlOptimization.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.WARNING, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        lblOptimizationSuggest = new JLabel("💡 Cost Optimization: Switch to 3-Star & Shared Cab to save ₹8,000!");
        lblOptimizationSuggest.setFont(Theme.FONT_SMALL);
        lblOptimizationSuggest.setForeground(Theme.WARNING);
        pnlOptimization.add(lblOptimizationSuggest, BorderLayout.CENTER);
        pnlOptimization.setVisible(false);

        priceCard.add(pnlOptimization, BorderLayout.SOUTH);

        // ACTIONS BAR
        CardPanel actionsCard = new CardPanel();
        actionsCard.setLayout(new GridLayout(2, 3, 6, 6));

        ModernButton btnSaveTrip = new ModernButton("💾 Save Trip", ModernButton.Style.PRIMARY);
        btnSaveTrip.addActionListener(e -> saveCustomTripAction());

        ModernButton btnQuotation = new ModernButton("📄 Quotation", ModernButton.Style.SECONDARY);
        btnQuotation.addActionListener(e -> generateQuotationAction());

        ModernButton btnConvertBooking = new ModernButton("🔄 Convert", ModernButton.Style.SUCCESS);
        btnConvertBooking.addActionListener(e -> convertToBookingAction());

        ModernButton btnClear = new ModernButton("Clear", ModernButton.Style.OUTLINE);
        btnClear.addActionListener(e -> clearForm());

        ModernButton btnDelete = new ModernButton("Delete", ModernButton.Style.DANGER);
        btnDelete.addActionListener(e -> deleteTripAction());

        ModernButton btnRefresh = new ModernButton("Refresh", ModernButton.Style.OUTLINE);
        btnRefresh.addActionListener(e -> {
            loadCustomers();
            loadTripTableData();
        });

        actionsCard.add(btnSaveTrip);
        actionsCard.add(btnQuotation);
        actionsCard.add(btnConvertBooking);
        actionsCard.add(btnClear);
        actionsCard.add(btnDelete);
        actionsCard.add(btnRefresh);

        // CUSTOM TRIPS DIRECTORY TABLE CARD
        CardPanel tableCard = new CardPanel();
        tableCard.setLayout(new BorderLayout(0, 8));

        JPanel topTableBar = new JPanel(new BorderLayout(5, 0));
        topTableBar.setOpaque(false);

        JLabel tblTitle = new JLabel("📋 Saved Custom Trips");
        tblTitle.setFont(Theme.FONT_TITLE);
        tblTitle.setForeground(Theme.TEXT_MAIN);

        txtSearch = new JTextField(12);
        txtSearch.setFont(Theme.FONT_REGULAR);
        txtSearch.setBackground(Theme.BACKGROUND);
        txtSearch.setForeground(Theme.PRIMARY);
        txtSearch.setCaretColor(Theme.PRIMARY);
        txtSearch.setToolTipText("Search customer, destination, status...");
        txtSearch.addActionListener(e -> searchTripAction());

        ModernButton btnSearch = new ModernButton("Search", ModernButton.Style.PRIMARY);
        btnSearch.addActionListener(e -> searchTripAction());

        JPanel searchGroup = new JPanel(new BorderLayout(4, 0));
        searchGroup.setOpaque(false);
        searchGroup.add(txtSearch, BorderLayout.CENTER);
        searchGroup.add(btnSearch, BorderLayout.EAST);

        topTableBar.add(tblTitle, BorderLayout.WEST);
        topTableBar.add(searchGroup, BorderLayout.EAST);
        tableCard.add(topTableBar, BorderLayout.NORTH);

        String[] cols = {"ID", "Customer", "Destination", "Travel Date", "Days", "Travelers", "Total (₹)", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tripTable = ModernTable.createTable(tableModel);
        ModernTable.centerColumns(tripTable, 0, 3, 4, 5);
        tripTable.getColumnModel().getColumn(7).setCellRenderer(StatusBadge.getTableRenderer());

        tripTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tripTable.getSelectedRow() != -1) {
                populateFormFromTable(tripTable.getSelectedRow());
            }
        });

        JScrollPane scrollTable = ModernTable.createScrollPane(tripTable);
        scrollTable.setPreferredSize(new Dimension(getWidth(), 150));
        tableCard.add(scrollTable, BorderLayout.CENTER);

        JPanel topPnl = new JPanel(new BorderLayout(0, 10));
        topPnl.setOpaque(false);
        topPnl.add(priceCard, BorderLayout.NORTH);
        topPnl.add(actionsCard, BorderLayout.SOUTH);

        rightCol.add(topPnl, BorderLayout.NORTH);
        rightCol.add(tableCard, BorderLayout.CENTER);

        return rightCol;
    }

    private void calculatePrices() {
        String dest = txtDestination != null ? txtDestination.getText().trim() : "Jaipur";
        DestinationPricing dp = pricingDAO.getPricingForDestination(dest);

        int travelers = spinnerTravelers != null ? (Integer) spinnerTravelers.getValue() : 4;
        int days = spinnerDays != null ? (Integer) spinnerDays.getValue() : 5;

        int rooms = (int) Math.ceil(travelers / 2.0);
        String hotelCategory = comboHotelCategory != null ? (String) comboHotelCategory.getSelectedItem() : "4 Star Luxury";
        double hotelNightRate = 0.0;
        if ("3 Star Deluxe".equalsIgnoreCase(hotelCategory)) hotelNightRate = dp.getHotel3Star();
        else if ("4 Star Luxury".equalsIgnoreCase(hotelCategory)) hotelNightRate = dp.getHotel4Star();
        else if ("5 Star Premium".equalsIgnoreCase(hotelCategory)) hotelNightRate = dp.getHotel5Star();
        else if ("Custom Hotel".equalsIgnoreCase(hotelCategory)) hotelNightRate = dp.getHotel4Star();
        else hotelNightRate = 0.0;

        double hotelCost = hotelNightRate * rooms * days;

        String transport = comboTransport != null ? (String) comboTransport.getSelectedItem() : "Private Cab";
        double transportCost = 0.0;
        int vehicleCount = (int) Math.ceil(travelers / 4.0);
        if ("Bus".equalsIgnoreCase(transport)) transportCost = dp.getTransportBus() * travelers;
        else if ("Train".equalsIgnoreCase(transport)) transportCost = dp.getTransportTrain() * travelers;
        else if ("Shared Cab".equalsIgnoreCase(transport)) transportCost = dp.getTransportSharedCab() * vehicleCount;
        else if ("Private Cab".equalsIgnoreCase(transport)) transportCost = dp.getTransportPrivateCab() * vehicleCount;
        else if ("Flight".equalsIgnoreCase(transport)) transportCost = dp.getTransportFlight() * travelers;

        String mealPlan = comboMealPlan != null ? (String) comboMealPlan.getSelectedItem() : "Breakfast + Dinner (MAP)";
        double mealDayRate = 0.0;
        if ("Breakfast Only".equalsIgnoreCase(mealPlan)) mealDayRate = dp.getMealBreakfast();
        else if ("Breakfast + Dinner (MAP)".equalsIgnoreCase(mealPlan)) mealDayRate = dp.getMealMap();
        else if ("All Meals (AP)".equalsIgnoreCase(mealPlan)) mealDayRate = dp.getMealAp();

        double mealCost = mealDayRate * travelers * days;

        double actRatePerPerson = 0.0;
        if (cbSightseeing != null && cbSightseeing.isSelected()) actRatePerPerson += 1000.0;
        if (cbAdventure != null && cbAdventure.isSelected()) actRatePerPerson += 2500.0;
        if (cbWaterSports != null && cbWaterSports.isSelected()) actRatePerPerson += 3000.0;
        if (cbCityTour != null && cbCityTour.isSelected()) actRatePerPerson += 1500.0;
        if (cbMuseumVisit != null && cbMuseumVisit.isSelected()) actRatePerPerson += 800.0;
        if (cbTrekking != null && cbTrekking.isSelected()) actRatePerPerson += 2000.0;
        if (cbLocalExperience != null && cbLocalExperience.isSelected()) actRatePerPerson += 1200.0;

        double activityCost = actRatePerPerson * travelers;

        double servicesCost = 0.0;
        if (cbPickup != null && cbPickup.isSelected()) servicesCost += 1500.0;
        if (cbGuide != null && cbGuide.isSelected()) servicesCost += 2000.0;
        if (cbInsurance != null && cbInsurance.isSelected()) servicesCost += (500.0 * travelers);

        double serviceCharge = dp.getServiceCharge() > 0 ? dp.getServiceCharge() : 2000.0;
        double discount = parseVal(txtDiscount);

        double estSubtotal = dp.getBaseCost() + hotelCost + transportCost + mealCost + activityCost + servicesCost + serviceCharge;
        double finalPrice = Math.max(0, estSubtotal - discount);
        double perPerson = travelers > 0 ? finalPrice / travelers : finalPrice;
        double budget = parseVal(txtBudget);

        if (txtHotelCost != null) txtHotelCost.setText(String.format("₹%,.0f", hotelCost));
        if (txtTransportCost != null) txtTransportCost.setText(String.format("₹%,.0f", transportCost));
        if (txtMealCost != null) txtMealCost.setText(String.format("₹%,.0f", mealCost));
        if (txtActivityCost != null) txtActivityCost.setText(String.format("₹%,.0f", activityCost));
        if (txtServicesCost != null) txtServicesCost.setText(String.format("₹%,.0f", servicesCost));
        if (txtServiceCharge != null) txtServiceCharge.setText(String.format("₹%,.0f", serviceCharge));

        if (lblPerPersonCost != null) lblPerPersonCost.setText(String.format("₹%,.2f / Person", perPerson));
        if (lblTotalTravelers != null) lblTotalTravelers.setText(travelers + " Traveler(s) • " + days + " Day(s)");
        if (lblFinalPriceVal != null) lblFinalPriceVal.setText(String.format("₹%,.2f", finalPrice));

        if (lblBudgetStatus != null) {
            if (budget > 0) {
                if (finalPrice <= budget) {
                    lblBudgetStatus.setText("✓ Trip fits within customer's budget (₹" + String.format("%,.0f", budget) + ").");
                    lblBudgetStatus.setForeground(Theme.SUCCESS);
                    pnlOptimization.setVisible(false);
                } else {
                    double over = finalPrice - budget;
                    lblBudgetStatus.setText("⚠️ Estimated cost exceeds budget by ₹" + String.format("%,.0f", over) + "!");
                    lblBudgetStatus.setForeground(Theme.DANGER);

                    double optHotelSavings = (dp.getHotel4Star() - dp.getHotel3Star()) * rooms * days;
                    double optTransSavings = ("Private Cab".equalsIgnoreCase(transport)) ? (dp.getTransportPrivateCab() - dp.getTransportSharedCab()) * vehicleCount : 0;
                    double potentialSavings = Math.max(0, optHotelSavings + optTransSavings);

                    if (potentialSavings > 0) {
                        lblOptimizationSuggest.setText("💡 Cost Optimization: Switch to 3-Star & Shared Cab → Save ₹" + String.format("%,.0f", potentialSavings) + "!");
                        pnlOptimization.setVisible(true);
                    } else {
                        pnlOptimization.setVisible(false);
                    }
                }
            } else {
                lblBudgetStatus.setText("No budget limit specified.");
                lblBudgetStatus.setForeground(Theme.TEXT_MUTED);
                pnlOptimization.setVisible(false);
            }
        }
    }

    private CustomTrip buildTripFromForm() {
        Customer cust = (Customer) comboCustomer.getSelectedItem();
        if (cust == null) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Please select an existing Customer.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        String dest = txtDestination.getText().trim();
        if (dest.isEmpty()) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Destination is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        Date travelDate = parseDate(txtTravelDate.getText().trim(), true);
        if (travelDate == null) return null;

        Date returnDate = parseDate(txtReturnDate.getText().trim(), false);
        if (returnDate != null && returnDate.before(travelDate)) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Return Date cannot be before Travel Date!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        int travelers = (Integer) spinnerTravelers.getValue();
        int days = (Integer) spinnerDays.getValue();
        if (travelers <= 0 || days <= 0) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Travelers and Days must be greater than zero.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        String duration = days + " Days / " + Math.max(1, days - 1) + " Nights";
        String hotelCategory = (String) comboHotelCategory.getSelectedItem();
        String hotelName = txtHotel.getText().trim();
        String transport = (String) comboTransport.getSelectedItem();
        String mealPlan = (String) comboMealPlan.getSelectedItem();

        List<String> actList = new ArrayList<>();
        if (cbSightseeing.isSelected()) actList.add("Sightseeing");
        if (cbAdventure.isSelected()) actList.add("Adventure");
        if (cbWaterSports.isSelected()) actList.add("Water Sports");
        if (cbCityTour.isSelected()) actList.add("City Tour");
        if (cbMuseumVisit.isSelected()) actList.add("Museum Visit");
        if (cbTrekking.isSelected()) actList.add("Trekking");
        if (cbLocalExperience.isSelected()) actList.add("Local Experience");
        String activitiesStr = String.join(", ", actList);

        List<String> srvList = new ArrayList<>();
        if (cbPickup.isSelected()) srvList.add("Airport/Station Pickup");
        if (cbGuide.isSelected()) srvList.add("Tour Guide");
        if (cbInsurance.isSelected()) srvList.add("Travel Insurance");
        String servicesStr = String.join(", ", srvList);

        String reqs = txtAdditionalReq.getText().trim();

        DestinationPricing dp = pricingDAO.getPricingForDestination(dest);
        int rooms = (int) Math.ceil(travelers / 2.0);
        double hotelNightRate = 0.0;
        if ("3 Star Deluxe".equalsIgnoreCase(hotelCategory)) hotelNightRate = dp.getHotel3Star();
        else if ("4 Star Luxury".equalsIgnoreCase(hotelCategory)) hotelNightRate = dp.getHotel4Star();
        else if ("5 Star Premium".equalsIgnoreCase(hotelCategory)) hotelNightRate = dp.getHotel5Star();
        else if ("Custom Hotel".equalsIgnoreCase(hotelCategory)) hotelNightRate = dp.getHotel4Star();

        double hotelCost = hotelNightRate * rooms * days;

        double transportCost = 0.0;
        int vehicleCount = (int) Math.ceil(travelers / 4.0);
        if ("Bus".equalsIgnoreCase(transport)) transportCost = dp.getTransportBus() * travelers;
        else if ("Train".equalsIgnoreCase(transport)) transportCost = dp.getTransportTrain() * travelers;
        else if ("Shared Cab".equalsIgnoreCase(transport)) transportCost = dp.getTransportSharedCab() * vehicleCount;
        else if ("Private Cab".equalsIgnoreCase(transport)) transportCost = dp.getTransportPrivateCab() * vehicleCount;
        else if ("Flight".equalsIgnoreCase(transport)) transportCost = dp.getTransportFlight() * travelers;

        double mealDayRate = 0.0;
        if ("Breakfast Only".equalsIgnoreCase(mealPlan)) mealDayRate = dp.getMealBreakfast();
        else if ("Breakfast + Dinner (MAP)".equalsIgnoreCase(mealPlan)) mealDayRate = dp.getMealMap();
        else if ("All Meals (AP)".equalsIgnoreCase(mealPlan)) mealDayRate = dp.getMealAp();
        double mealCost = mealDayRate * travelers * days;

        double actRatePerPerson = 0.0;
        if (cbSightseeing.isSelected()) actRatePerPerson += 1000.0;
        if (cbAdventure.isSelected()) actRatePerPerson += 2500.0;
        if (cbWaterSports.isSelected()) actRatePerPerson += 3000.0;
        if (cbCityTour.isSelected()) actRatePerPerson += 1500.0;
        if (cbMuseumVisit.isSelected()) actRatePerPerson += 800.0;
        if (cbTrekking.isSelected()) actRatePerPerson += 2000.0;
        if (cbLocalExperience.isSelected()) actRatePerPerson += 1200.0;
        double activityCost = actRatePerPerson * travelers;

        double servicesCost = 0.0;
        if (cbPickup.isSelected()) servicesCost += 1500.0;
        if (cbGuide.isSelected()) servicesCost += 2000.0;
        if (cbInsurance.isSelected()) servicesCost += (500.0 * travelers);

        double serviceCharge = dp.getServiceCharge() > 0 ? dp.getServiceCharge() : 2000.0;
        double discount = parseVal(txtDiscount);

        double estSubtotal = dp.getBaseCost() + hotelCost + transportCost + mealCost + activityCost + servicesCost + serviceCharge;
        double finalPrice = Math.max(0, estSubtotal - discount);
        double perPerson = travelers > 0 ? finalPrice / travelers : finalPrice;
        double budget = parseVal(txtBudget);
        String status = (String) comboStatus.getSelectedItem();

        int tripId = !txtTripId.getText().isEmpty() ? Integer.parseInt(txtTripId.getText()) : 0;

        CustomTrip t = new CustomTrip(tripId, cust.getCustomerId(), dest, travelDate, returnDate, travelers, duration,
                hotelName, hotelCategory, transport, mealPlan, activitiesStr, servicesStr, reqs,
                hotelCost, transportCost, mealCost, activityCost, servicesCost, serviceCharge, discount,
                finalPrice, perPerson, budget, status, null);
        t.setCustomerName(cust.getName());

        return t;
    }

    private void saveCustomTripAction() {
        CustomTrip t = buildTripFromForm();
        if (t == null) return;

        if (t.getTripId() > 0) {
            if (customTripDAO.updateCustomTrip(t)) {
                SoundManager.playSuccessSound();
                JOptionPane.showMessageDialog(this, "Custom Trip #" + t.getTripId() + " updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTripTableData();
            } else {
                SoundManager.playErrorSound();
                JOptionPane.showMessageDialog(this, "Failed to update Custom Trip.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            if (customTripDAO.addCustomTrip(t)) {
                SoundManager.playSuccessSound();
                JOptionPane.showMessageDialog(this, "Custom Trip created successfully with Trip ID: #" + t.getTripId(), "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTripTableData();
            } else {
                SoundManager.playErrorSound();
                JOptionPane.showMessageDialog(this, "Failed to save Custom Trip.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void generateQuotationAction() {
        CustomTrip t = buildTripFromForm();
        if (t != null) {
            if (t.getTripId() == 0) {
                customTripDAO.addCustomTrip(t);
                t.setStatus("Quotation Generated");
                customTripDAO.updateTripStatus(t.getTripId(), "Quotation Generated", null);
                loadTripTableData();
            }
            QuotationDialog dialog = new QuotationDialog(mainFrame, t);
            dialog.setVisible(true);
        }
    }

    private void convertToBookingAction() {
        if (txtTripId.getText().isEmpty()) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Please select a saved Custom Trip from the directory table to convert into a Booking.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int tripId = Integer.parseInt(txtTripId.getText());
        CustomTrip t = customTripDAO.getCustomTripById(tripId);
        if (t == null) return;

        if ("Converted to Booking".equalsIgnoreCase(t.getStatus()) || (t.getBookingId() != null && t.getBookingId() > 0)) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "This Custom Trip has already been converted to Booking #" + t.getBookingId() + "!\nDuplicate conversion is prevented.", "Already Converted", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to convert Custom Trip #" + t.getTripId() + " (" + t.getDestination() + ") into an official Booking?\nFinal Amount: ₹" + String.format("%,.2f", t.getFinalPrice()),
            "Confirm Conversion to Booking",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            Booking b = new Booking(0, t.getCustomerId(), 0, t.getTravelDate(), t.getTravelers(), t.getFinalPrice(), "Confirmed");
            if (bookingDAO.addBooking(b)) {
                customTripDAO.updateTripStatus(t.getTripId(), "Converted to Booking", b.getBookingId());
                SoundManager.playSuccessSound();
                JOptionPane.showMessageDialog(this, "🎉 Custom Trip successfully converted to Booking #" + b.getBookingId() + "!\nStatus updated in system.", "Conversion Success", JOptionPane.INFORMATION_MESSAGE);
                loadTripTableData();
                mainFrame.showCard("BOOKINGS");
            } else {
                SoundManager.playErrorSound();
                JOptionPane.showMessageDialog(this, "Failed to create Booking record.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteTripAction() {
        if (txtTripId.getText().isEmpty()) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Please select a trip from the directory to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this Custom Trip record?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int tripId = Integer.parseInt(txtTripId.getText());
            if (customTripDAO.deleteCustomTrip(tripId)) {
                SoundManager.playSuccessSound();
                JOptionPane.showMessageDialog(this, "Custom Trip record deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTripTableData();
            } else {
                SoundManager.playErrorSound();
                JOptionPane.showMessageDialog(this, "Failed to delete Custom Trip.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadTripTableData() {
        tableModel.setRowCount(0);
        List<CustomTrip> list = customTripDAO.getAllCustomTrips();
        for (CustomTrip t : list) {
            tableModel.addRow(new Object[]{
                t.getTripId(),
                t.getCustomerName(),
                t.getDestination(),
                t.getTravelDate(),
                t.getDuration(),
                t.getTravelers(),
                "₹" + String.format("%.2f", t.getFinalPrice()),
                t.getStatus()
            });
        }
        clearForm();
    }

    private void searchTripAction() {
        String q = txtSearch.getText().trim();
        if (q.isEmpty()) {
            loadTripTableData();
            return;
        }
        tableModel.setRowCount(0);
        List<CustomTrip> list = customTripDAO.searchCustomTrips(q);
        for (CustomTrip t : list) {
            tableModel.addRow(new Object[]{
                t.getTripId(),
                t.getCustomerName(),
                t.getDestination(),
                t.getTravelDate(),
                t.getDuration(),
                t.getTravelers(),
                "₹" + String.format("%.2f", t.getFinalPrice()),
                t.getStatus()
            });
        }
    }

    private void populateFormFromTable(int row) {
        int tripId = (Integer) tableModel.getValueAt(row, 0);
        CustomTrip t = customTripDAO.getCustomTripById(tripId);
        if (t != null) {
            txtTripId.setText(String.valueOf(t.getTripId()));

            for (int i = 0; i < comboCustomer.getItemCount(); i++) {
                if (comboCustomer.getItemAt(i).getCustomerId() == t.getCustomerId()) {
                    comboCustomer.setSelectedIndex(i);
                    break;
                }
            }

            txtDestination.setText(t.getDestination());
            txtTravelDate.setText(t.getTravelDate().toString());
            if (t.getReturnDate() != null) txtReturnDate.setText(t.getReturnDate().toString());
            spinnerTravelers.setValue(t.getTravelers());

            try {
                int days = Integer.parseInt(t.getDuration().split(" ")[0]);
                spinnerDays.setValue(days);
            } catch (Exception ignored) {}

            comboHotelCategory.setSelectedItem(t.getHotelCategory());
            txtHotel.setText(t.getHotel());
            comboTransport.setSelectedItem(t.getTransport());
            comboMealPlan.setSelectedItem(t.getMealPlan());

            String actStr = t.getActivities() != null ? t.getActivities() : "";
            cbSightseeing.setSelected(actStr.contains("Sightseeing"));
            cbAdventure.setSelected(actStr.contains("Adventure"));
            cbWaterSports.setSelected(actStr.contains("Water Sports"));
            cbCityTour.setSelected(actStr.contains("City Tour"));
            cbMuseumVisit.setSelected(actStr.contains("Museum Visit"));
            cbTrekking.setSelected(actStr.contains("Trekking"));
            cbLocalExperience.setSelected(actStr.contains("Local Experience"));

            String srvStr = t.getAdditionalServices() != null ? t.getAdditionalServices() : "";
            cbPickup.setSelected(srvStr.contains("Pickup"));
            cbGuide.setSelected(srvStr.contains("Guide"));
            cbInsurance.setSelected(srvStr.contains("Insurance"));

            txtAdditionalReq.setText(t.getAdditionalReq());
            txtBudget.setText(String.format("%.0f", t.getBudget()));
            txtDiscount.setText(String.format("%.0f", t.getDiscount()));
            comboStatus.setSelectedItem(t.getStatus());

            calculatePrices();
        }
    }

    private void loadCustomers() {
        comboCustomer.removeAllItems();
        List<Customer> list = customerDAO.getAllCustomers();
        for (Customer c : list) {
            comboCustomer.addItem(c);
        }
    }

    private Date parseDate(String s, boolean showWarning) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            java.util.Date d = sdf.parse(s);
            return new Date(d.getTime());
        } catch (ParseException e) {
            if (showWarning) {
                SoundManager.playErrorSound();
                JOptionPane.showMessageDialog(this, "Invalid Date format! Please use yyyy-MM-dd format.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            }
            return null;
        }
    }

    private double parseVal(JTextField tf) {
        if (tf == null) return 0.0;
        try {
            return Double.parseDouble(tf.getText().trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    private void clearForm() {
        txtTripId.setText("");
        if (comboCustomer.getItemCount() > 0) comboCustomer.setSelectedIndex(0);
        txtDestination.setText("Jaipur");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long tomorrow = System.currentTimeMillis() + 86400000L;
        long fiveDays = System.currentTimeMillis() + 86400000L * 6;

        txtTravelDate.setText(sdf.format(new Date(tomorrow)));
        txtReturnDate.setText(sdf.format(new Date(fiveDays)));

        spinnerTravelers.setValue(4);
        spinnerDays.setValue(5);

        comboHotelCategory.setSelectedIndex(2);
        txtHotel.setText("Royal Heritage Palace");
        comboTransport.setSelectedIndex(3);
        comboMealPlan.setSelectedIndex(2);

        cbSightseeing.setSelected(true);
        cbCityTour.setSelected(true);
        cbAdventure.setSelected(false);
        cbWaterSports.setSelected(false);
        cbMuseumVisit.setSelected(false);
        cbTrekking.setSelected(false);
        cbLocalExperience.setSelected(false);

        cbPickup.setSelected(true);
        cbGuide.setSelected(false);
        cbInsurance.setSelected(false);

        txtAdditionalReq.setText("");
        txtBudget.setText("70000");
        txtDiscount.setText("2000");
        comboStatus.setSelectedIndex(0);
        txtSearch.setText("");
        tripTable.clearSelection();

        calculatePrices();
    }

    private JLabel createSectionHeader(String title) {
        JLabel lbl = new JLabel(title);
        lbl.setFont(Theme.FONT_TITLE);
        lbl.setForeground(Theme.PRIMARY);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        return lbl;
    }

    private JTextField createStyledTextField(String text, DocumentListener dl) {
        JTextField tf = new JTextField(text);
        tf.setFont(Theme.FONT_REGULAR);
        tf.setBackground(Theme.BACKGROUND);
        tf.setForeground(Theme.PRIMARY);
        tf.setCaretColor(Theme.PRIMARY);
        tf.setBorder(Theme.BORDER_INPUT);
        tf.getDocument().addDocumentListener(dl);
        return tf;
    }

    private void addFormField(JPanel pnl, String labelText, javax.swing.JComponent comp, GridBagConstraints gbc, int row) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.4;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(Theme.FONT_LABEL);
        lbl.setForeground(Theme.TEXT_MAIN);
        pnl.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 0.6;
        pnl.add(comp, gbc);
    }

    private JCheckBox createCheckBox(String text, boolean selected) {
        JCheckBox cb = new JCheckBox(text, selected);
        cb.setFont(Theme.FONT_REGULAR);
        cb.setForeground(Theme.TEXT_MAIN);
        cb.setOpaque(false);
        cb.addActionListener(e -> calculatePrices());
        return cb;
    }

    private JTextField createReadOnlyCost() {
        JTextField tf = new JTextField("₹0");
        tf.setEditable(false);
        tf.setFont(Theme.FONT_SMALL);
        tf.setBackground(new Color(0xEA, 0xEF, 0xEF));
        tf.setForeground(Theme.PRIMARY);
        tf.setHorizontalAlignment(JTextField.RIGHT);
        return tf;
    }

    private JPanel createCostRow(String label, JTextField tf) {
        JPanel p = new JPanel(new BorderLayout(4, 0));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(Theme.FONT_SMALL);
        l.setForeground(Theme.TEXT_MUTED);
        p.add(l, BorderLayout.WEST);
        p.add(tf, BorderLayout.CENTER);
        return p;
    }
}
