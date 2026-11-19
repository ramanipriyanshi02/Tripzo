package com.holiday.gui;

import com.holiday.dao.BookingDAO;
import com.holiday.dao.CustomerDAO;
import com.holiday.dao.FestivalOfferDAO;
import com.holiday.dao.PackageDAO;
import com.holiday.dao.PaymentDAO;
import com.holiday.gui.ui.CardPanel;
import com.holiday.gui.ui.ModernButton;
import com.holiday.gui.ui.ModernTable;
import com.holiday.gui.ui.StatusBadge;
import com.holiday.gui.ui.Theme;
import com.holiday.model.Booking;
import com.holiday.model.Customer;
import com.holiday.model.FestivalOffer;
import com.holiday.model.Package;
import com.holiday.model.Payment;
import com.holiday.util.SoundManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;

public class BookingPanel extends JPanel {

    private MainFrame mainFrame;
    private BookingDAO bookingDAO;
    private CustomerDAO customerDAO;
    private PackageDAO packageDAO;
    private PaymentDAO paymentDAO;
    private FestivalOfferDAO festivalOfferDAO;

    // Form fields
    private JTextField txtBookingId;
    private JComboBox<Customer> comboCustomer;
    private JComboBox<Package> comboPackage;
    private JTextField txtPackagePrice;
    private JTextField txtTravelDate;
    private JSpinner spinnerTravelers;
    private JComboBox<Object> comboFestivalOffer;
    private JTextField txtOriginalAmount;
    private JTextField txtDiscountAmount;
    private JTextField txtTotalAmount;
    private JComboBox<String> comboStatus;

    private JTextField txtSearch;
    private JTable bookingTable;
    private DefaultTableModel tableModel;

    private boolean isUpdatingOffers = false;

    public BookingPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.bookingDAO = new BookingDAO();
        this.customerDAO = new CustomerDAO();
        this.packageDAO = new PackageDAO();
        this.paymentDAO = new PaymentDAO();
        this.festivalOfferDAO = new FestivalOfferDAO();

        setLayout(new BorderLayout(15, 0));
        setOpaque(false);

        // Left Form Card
        JPanel formCard = createFormCard();
        formCard.setPreferredSize(new Dimension(430, getHeight()));
        add(formCard, BorderLayout.WEST);

        // Right Table Card
        JPanel tableCard = createTableCard();
        add(tableCard, BorderLayout.CENTER);

        loadDropdownsAndData();
    }

    private JPanel createFormCard() {
        CardPanel card = new CardPanel(Theme.PRIMARY);
        card.setLayout(new BorderLayout(0, 8));

        JLabel title = new JLabel("📅 Create / Edit Booking");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_MAIN);
        card.add(title, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        // Booking ID
        txtBookingId = addFormField(fieldsPanel, "Booking ID (Auto):", gbc, row++);
        txtBookingId.setEditable(false);
        txtBookingId.setBackground(new Color(0xEA, 0xEF, 0xEF));

        // Customer Dropdown
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.35;
        JLabel lblCust = new JLabel("Customer *:");
        lblCust.setFont(Theme.FONT_LABEL);
        lblCust.setForeground(Theme.TEXT_MAIN);
        fieldsPanel.add(lblCust, gbc);

        gbc.gridx = 1; gbc.weightx = 0.65;
        comboCustomer = new JComboBox<>();
        comboCustomer.setFont(Theme.FONT_REGULAR);
        fieldsPanel.add(comboCustomer, gbc);
        row++;

        // Package Dropdown
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        JLabel lblPkg = new JLabel("Holiday Package *:");
        lblPkg.setFont(Theme.FONT_LABEL);
        lblPkg.setForeground(Theme.TEXT_MAIN);
        fieldsPanel.add(lblPkg, gbc);

        gbc.gridx = 1; gbc.weightx = 0.65;
        comboPackage = new JComboBox<>();
        comboPackage.setFont(Theme.FONT_REGULAR);
        comboPackage.addActionListener(e -> updatePriceAndTotal());
        fieldsPanel.add(comboPackage, gbc);
        row++;

        // Package Price Display
        txtPackagePrice = addFormField(fieldsPanel, "Package Price (₹):", gbc, row++);
        txtPackagePrice.setEditable(false);
        txtPackagePrice.setBackground(new Color(0xEA, 0xEF, 0xEF));

        // Travelers Spinner
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        JLabel lblTrav = new JLabel("No. of Travelers *:");
        lblTrav.setFont(Theme.FONT_LABEL);
        lblTrav.setForeground(Theme.TEXT_MAIN);
        fieldsPanel.add(lblTrav, gbc);

        gbc.gridx = 1; gbc.weightx = 0.65;
        spinnerTravelers = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        spinnerTravelers.setFont(Theme.FONT_REGULAR);
        spinnerTravelers.addChangeListener((ChangeEvent e) -> updatePriceAndTotal());
        fieldsPanel.add(spinnerTravelers, gbc);
        row++;

        // Travel Date
        txtTravelDate = addFormField(fieldsPanel, "Travel Date (yyyy-MM-dd) *:", gbc, row++);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        txtTravelDate.setText(sdf.format(new Date(System.currentTimeMillis() + 86400000L)));
        txtTravelDate.addActionListener(e -> updatePriceAndTotal());

        // Festival Offer Selection Dropdown
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        JLabel lblOffer = new JLabel("Festival Offer:");
        lblOffer.setFont(Theme.FONT_LABEL);
        lblOffer.setForeground(Theme.TEXT_MAIN);
        fieldsPanel.add(lblOffer, gbc);

        gbc.gridx = 1; gbc.weightx = 0.65;
        comboFestivalOffer = new JComboBox<>();
        comboFestivalOffer.setFont(Theme.FONT_REGULAR);
        comboFestivalOffer.addActionListener(e -> {
            if (!isUpdatingOffers) {
                calculateTotalsOnly();
            }
        });
        fieldsPanel.add(comboFestivalOffer, gbc);
        row++;

        // Original Amount Display
        txtOriginalAmount = addFormField(fieldsPanel, "Original Total (₹):", gbc, row++);
        txtOriginalAmount.setEditable(false);
        txtOriginalAmount.setBackground(new Color(0xEA, 0xEF, 0xEF));

        // Discount Amount Display
        txtDiscountAmount = addFormField(fieldsPanel, "Festival Discount (₹):", gbc, row++);
        txtDiscountAmount.setEditable(false);
        txtDiscountAmount.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 12));
        txtDiscountAmount.setForeground(Theme.SUCCESS);
        txtDiscountAmount.setBackground(new Color(0xEA, 0xEF, 0xEF));

        // Final Payable Total Amount Display
        txtTotalAmount = addFormField(fieldsPanel, "Final Payable (₹):", gbc, row++);
        txtTotalAmount.setEditable(false);
        txtTotalAmount.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 14));
        txtTotalAmount.setForeground(Theme.PRIMARY);
        txtTotalAmount.setBackground(new Color(0xEA, 0xEF, 0xEF));

        // Booking Status
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        JLabel lblStat = new JLabel("Booking Status:");
        lblStat.setFont(Theme.FONT_LABEL);
        lblStat.setForeground(Theme.TEXT_MAIN);
        fieldsPanel.add(lblStat, gbc);

        gbc.gridx = 1; gbc.weightx = 0.65;
        comboStatus = new JComboBox<>(new String[]{"Confirmed", "Pending", "Completed", "Cancelled"});
        comboStatus.setFont(Theme.FONT_REGULAR);
        fieldsPanel.add(comboStatus, gbc);
        row++;

        JScrollPane formScroll = new JScrollPane(fieldsPanel);
        formScroll.setBorder(BorderFactory.createEmptyBorder());
        formScroll.setOpaque(false);
        formScroll.getViewport().setOpaque(false);
        card.add(formScroll, BorderLayout.CENTER);

        // Action Buttons
        JPanel btnPanel = new JPanel(new GridLayout(3, 3, 6, 6));
        btnPanel.setOpaque(false);

        ModernButton btnCreate = new ModernButton("Create", ModernButton.Style.PRIMARY);
        ModernButton btnUpdate = new ModernButton("Update", ModernButton.Style.SUCCESS);
        ModernButton btnCancel = new ModernButton("Cancel Trip", ModernButton.Style.ACCENT_ORANGE);
        ModernButton btnDelete = new ModernButton("Delete", ModernButton.Style.DANGER);
        ModernButton btnReceipt = new ModernButton("Receipt", ModernButton.Style.SECONDARY);
        ModernButton btnClear = new ModernButton("Clear", ModernButton.Style.OUTLINE);
        ModernButton btnRefresh = new ModernButton("Refresh", ModernButton.Style.OUTLINE);

        btnCreate.addActionListener(e -> createBookingAction());
        btnUpdate.addActionListener(e -> updateBookingAction());
        btnCancel.addActionListener(e -> cancelBookingAction());
        btnDelete.addActionListener(e -> deleteBookingAction());
        btnReceipt.addActionListener(e -> viewReceiptAction());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> loadDropdownsAndData());

        btnPanel.add(btnCreate);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnCancel);
        btnPanel.add(btnDelete);
        btnPanel.add(btnReceipt);
        btnPanel.add(btnClear);
        btnPanel.add(btnRefresh);

        card.add(btnPanel, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createTableCard() {
        CardPanel card = new CardPanel();
        card.setLayout(new BorderLayout(0, 10));

        // Top Bar
        JPanel topBar = new JPanel(new BorderLayout(10, 0));
        topBar.setOpaque(false);

        JLabel lblTitle = new JLabel("📖 All Customer Bookings");
        lblTitle.setFont(Theme.FONT_TITLE);
        lblTitle.setForeground(Theme.TEXT_MAIN);

        JPanel searchGroup = new JPanel(new BorderLayout(5, 0));
        searchGroup.setOpaque(false);

        txtSearch = new JTextField(16);
        txtSearch.setFont(Theme.FONT_REGULAR);
        txtSearch.setBackground(Theme.BACKGROUND);
        txtSearch.setForeground(Theme.PRIMARY);
        txtSearch.setCaretColor(Theme.PRIMARY);
        txtSearch.setToolTipText("Search ID, customer, package, or offer...");

        ModernButton btnSearch = new ModernButton("Search", ModernButton.Style.PRIMARY);
        btnSearch.addActionListener(e -> searchAction());
        txtSearch.addActionListener(e -> searchAction());

        JLabel searchIcon = new JLabel("🔍 ");
        searchIcon.setForeground(Theme.TEXT_MUTED);

        searchGroup.add(searchIcon, BorderLayout.WEST);
        searchGroup.add(txtSearch, BorderLayout.CENTER);
        searchGroup.add(btnSearch, BorderLayout.EAST);

        topBar.add(lblTitle, BorderLayout.WEST);
        topBar.add(searchGroup, BorderLayout.EAST);
        card.add(topBar, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Customer", "Package", "Destination", "Travel Date", "Travelers", "Final Total", "Offer Applied", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        bookingTable = ModernTable.createTable(tableModel);
        ModernTable.centerColumns(bookingTable, 0, 4, 5, 6, 7);
        bookingTable.getColumnModel().getColumn(8).setCellRenderer(StatusBadge.getTableRenderer());

        bookingTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && bookingTable.getSelectedRow() != -1) {
                populateFormFromTable(bookingTable.getSelectedRow());
            }
        });

        JScrollPane scrollPane = ModernTable.createScrollPane(bookingTable);
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    private JTextField addFormField(JPanel panel, String labelText, GridBagConstraints gbc, int row) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.35;
        JLabel label = new JLabel(labelText);
        label.setFont(Theme.FONT_LABEL);
        label.setForeground(Theme.TEXT_MAIN);
        panel.add(label, gbc);

        gbc.gridx = 1; gbc.weightx = 0.65;
        JTextField field = new JTextField();
        field.setFont(Theme.FONT_REGULAR);
        field.setBackground(Theme.BACKGROUND);
        field.setForeground(Theme.PRIMARY);
        field.setCaretColor(Theme.PRIMARY);
        field.setBorder(Theme.BORDER_INPUT);
        panel.add(field, gbc);

        return field;
    }

    public void loadDropdownsAndData() {
        // Load Customers into Combo
        comboCustomer.removeAllItems();
        List<Customer> customers = customerDAO.getAllCustomers();
        for (Customer c : customers) {
            comboCustomer.addItem(c);
        }

        // Load Packages into Combo
        comboPackage.removeAllItems();
        List<Package> packages = packageDAO.getAllPackages();
        for (Package p : packages) {
            comboPackage.addItem(p);
        }

        updatePriceAndTotal();
        loadBookingTableData();
    }

    private void updatePriceAndTotal() {
        Package selectedPackage = (Package) comboPackage.getSelectedItem();
        if (selectedPackage == null) {
            txtPackagePrice.setText("0.00");
            txtOriginalAmount.setText("0.00");
            txtDiscountAmount.setText("₹0.00");
            txtTotalAmount.setText("0.00");
            return;
        }

        double price = selectedPackage.getPrice();
        txtPackagePrice.setText(String.format("%.2f", price));

        int travelers = (Integer) spinnerTravelers.getValue();
        double originalTotal = price * travelers;
        txtOriginalAmount.setText(String.format("%.2f", originalTotal));

        Date travelDate = parseTravelDateSilent(txtTravelDate.getText().trim());
        if (travelDate == null) {
            travelDate = new Date(System.currentTimeMillis() + 86400000L);
        }

        // Fetch eligible festival offers for this package, amount, and date
        isUpdatingOffers = true;
        Object previousSelected = comboFestivalOffer.getSelectedItem();
        comboFestivalOffer.removeAllItems();

        comboFestivalOffer.addItem("🌟 Auto-Apply Best Active Offer");
        comboFestivalOffer.addItem("None (No Festival Discount)");

        List<FestivalOffer> eligibleOffers = festivalOfferDAO.getEligibleOffersForBooking(selectedPackage.getPackageId(), originalTotal, travelDate);
        for (FestivalOffer off : eligibleOffers) {
            comboFestivalOffer.addItem(off);
        }

        // Restore previous selection if still eligible
        if (previousSelected instanceof FestivalOffer) {
            FestivalOffer prev = (FestivalOffer) previousSelected;
            for (int i = 2; i < comboFestivalOffer.getItemCount(); i++) {
                FestivalOffer current = (FestivalOffer) comboFestivalOffer.getItemAt(i);
                if (current.getOfferId() == prev.getOfferId()) {
                    comboFestivalOffer.setSelectedIndex(i);
                    break;
                }
            }
        } else if ("None (No Festival Discount)".equals(previousSelected)) {
            comboFestivalOffer.setSelectedIndex(1);
        } else {
            comboFestivalOffer.setSelectedIndex(0); // Auto-apply
        }
        isUpdatingOffers = false;

        calculateTotalsOnly();
    }

    private void calculateTotalsOnly() {
        Package selectedPackage = (Package) comboPackage.getSelectedItem();
        if (selectedPackage == null) return;

        double price = selectedPackage.getPrice();
        int travelers = (Integer) spinnerTravelers.getValue();
        double originalTotal = price * travelers;
        txtOriginalAmount.setText(String.format("%.2f", originalTotal));

        Date travelDate = parseTravelDateSilent(txtTravelDate.getText().trim());
        if (travelDate == null) travelDate = new Date(System.currentTimeMillis() + 86400000L);

        Object selected = comboFestivalOffer.getSelectedItem();
        FestivalOffer offerToApply = null;

        if ("🌟 Auto-Apply Best Active Offer".equals(selected)) {
            // Find eligible offer giving max discount
            List<FestivalOffer> eligible = festivalOfferDAO.getEligibleOffersForBooking(selectedPackage.getPackageId(), originalTotal, travelDate);
            double maxDisc = 0.0;
            for (FestivalOffer off : eligible) {
                double d = off.calculateDiscount(originalTotal);
                if (d > maxDisc) {
                    maxDisc = d;
                    offerToApply = off;
                }
            }
        } else if (selected instanceof FestivalOffer) {
            offerToApply = (FestivalOffer) selected;
        }

        double discount = 0.0;
        if (offerToApply != null && offerToApply.isEligible(selectedPackage.getPackageId(), originalTotal, travelDate)) {
            discount = offerToApply.calculateDiscount(originalTotal);
            txtDiscountAmount.setText(String.format("-₹%.2f (%s)", discount, offerToApply.getDiscountBadgeText()));
            txtDiscountAmount.setForeground(Theme.SUCCESS);
        } else {
            txtDiscountAmount.setText("₹0.00");
            txtDiscountAmount.setForeground(Theme.TEXT_MUTED);
        }

        BigDecimal origBD = BigDecimal.valueOf(originalTotal);
        BigDecimal discBD = BigDecimal.valueOf(discount);
        BigDecimal finalBD = origBD.subtract(discBD).setScale(2, RoundingMode.HALF_UP);

        txtTotalAmount.setText(String.format("%.2f", finalBD.doubleValue()));
    }

    private void loadBookingTableData() {
        tableModel.setRowCount(0);
        List<Booking> list = bookingDAO.getAllBookings();
        for (Booking b : list) {
            String offerStr = b.getDiscountAmount() > 0
                    ? (b.getOfferName() != null ? b.getOfferName() : "Festival Offer") + " (-₹" + String.format("%.0f", b.getDiscountAmount()) + ")"
                    : "—";

            tableModel.addRow(new Object[]{
                b.getBookingId(),
                b.getCustomerName(),
                b.getPackageName(),
                b.getDestination(),
                b.getTravelDate(),
                b.getTravelers(),
                "₹" + String.format("%.2f", b.getTotalAmount()),
                offerStr,
                b.getBookingStatus()
            });
        }
        clearForm();
    }

    private void searchAction() {
        String q = txtSearch.getText().trim();
        if (q.isEmpty()) {
            loadBookingTableData();
            return;
        }
        tableModel.setRowCount(0);
        List<Booking> list = bookingDAO.searchBookings(q);
        for (Booking b : list) {
            String offerStr = b.getDiscountAmount() > 0
                    ? (b.getOfferName() != null ? b.getOfferName() : "Festival Offer") + " (-₹" + String.format("%.0f", b.getDiscountAmount()) + ")"
                    : "—";

            tableModel.addRow(new Object[]{
                b.getBookingId(),
                b.getCustomerName(),
                b.getPackageName(),
                b.getDestination(),
                b.getTravelDate(),
                b.getTravelers(),
                "₹" + String.format("%.2f", b.getTotalAmount()),
                offerStr,
                b.getBookingStatus()
            });
        }
    }

    private void populateFormFromTable(int row) {
        int bookingId = (Integer) tableModel.getValueAt(row, 0);
        Booking b = bookingDAO.getBookingById(bookingId);
        if (b != null) {
            txtBookingId.setText(String.valueOf(b.getBookingId()));

            for (int i = 0; i < comboCustomer.getItemCount(); i++) {
                if (comboCustomer.getItemAt(i).getCustomerId() == b.getCustomerId()) {
                    comboCustomer.setSelectedIndex(i);
                    break;
                }
            }

            for (int i = 0; i < comboPackage.getItemCount(); i++) {
                if (comboPackage.getItemAt(i).getPackageId() == b.getPackageId()) {
                    comboPackage.setSelectedIndex(i);
                    break;
                }
            }

            spinnerTravelers.setValue(b.getTravelers());
            txtTravelDate.setText(b.getTravelDate().toString());
            comboStatus.setSelectedItem(b.getBookingStatus());

            // Set amounts
            double origAmt = b.getOriginalAmount() != null ? b.getOriginalAmount() : b.getTotalAmount();
            txtOriginalAmount.setText(String.format("%.2f", origAmt));
            if (b.getDiscountAmount() > 0) {
                txtDiscountAmount.setText(String.format("-₹%.2f (%s)", b.getDiscountAmount(), b.getOfferName() != null ? b.getOfferName() : "Offer"));
                txtDiscountAmount.setForeground(Theme.SUCCESS);
            } else {
                txtDiscountAmount.setText("₹0.00");
                txtDiscountAmount.setForeground(Theme.TEXT_MUTED);
            }
            txtTotalAmount.setText(String.format("%.2f", b.getTotalAmount()));

            // Find matching offer in combo or add as custom display
            if (b.getOfferId() != null && b.getOfferId() > 0) {
                FestivalOffer off = festivalOfferDAO.getOfferById(b.getOfferId());
                if (off != null) {
                    boolean found = false;
                    for (int i = 0; i < comboFestivalOffer.getItemCount(); i++) {
                        Object item = comboFestivalOffer.getItemAt(i);
                        if (item instanceof FestivalOffer && ((FestivalOffer) item).getOfferId() == off.getOfferId()) {
                            comboFestivalOffer.setSelectedIndex(i);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        comboFestivalOffer.addItem(off);
                        comboFestivalOffer.setSelectedItem(off);
                    }
                }
            } else {
                comboFestivalOffer.setSelectedIndex(1); // None
            }
        }
    }

    private void createBookingAction() {
        Customer cust = (Customer) comboCustomer.getSelectedItem();
        Package pkg = (Package) comboPackage.getSelectedItem();

        if (cust == null) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Please select a Customer. Add customer first if directory is empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (pkg == null) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Please select a Package.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Date travelDate = parseTravelDate(txtTravelDate.getText().trim());
        if (travelDate == null) return;

        int travelers = (Integer) spinnerTravelers.getValue();
        if (travelers <= 0) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Number of travelers must be greater than zero.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double originalAmount = pkg.getPrice() * travelers;

        // Resolve Festival Offer
        FestivalOffer appliedOffer = resolveSelectedOffer(pkg.getPackageId(), originalAmount, travelDate);
        double discount = 0.0;
        Integer offerId = null;

        if (appliedOffer != null) {
            // Re-validate offer before saving
            if (!appliedOffer.isEligible(pkg.getPackageId(), originalAmount, travelDate)) {
                SoundManager.playErrorSound();
                JOptionPane.showMessageDialog(this,
                        "Festival offer '" + appliedOffer.getOfferName() + "' is not eligible for this booking.\n"
                        + "Please check minimum booking amount (₹" + appliedOffer.getMinBookingAmount() + ") or valid dates.",
                        "Offer Not Applicable", JOptionPane.WARNING_MESSAGE);
                return;
            }
            discount = appliedOffer.calculateDiscount(originalAmount);
            offerId = appliedOffer.getOfferId();
        }

        double finalTotal = originalAmount - discount;
        String status = (String) comboStatus.getSelectedItem();

        Booking b = new Booking(0, cust.getCustomerId(), pkg.getPackageId(), travelDate, travelers, finalTotal, status, offerId, originalAmount, discount);
        if (bookingDAO.addBooking(b)) {
            SoundManager.playSuccessSound();
            String discountMsg = discount > 0 ? "\nApplied Festival Offer: " + appliedOffer.getOfferName() + " (Saved ₹" + String.format("%.2f", discount) + "!)" : "";
            JOptionPane.showMessageDialog(this,
                    "Booking created successfully with Booking ID: #" + b.getBookingId()
                    + "\nOriginal Amount: ₹" + String.format("%.2f", originalAmount)
                    + discountMsg
                    + "\nFinal Payable Amount: ₹" + String.format("%.2f", finalTotal),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            loadBookingTableData();
        } else {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Failed to create Booking.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBookingAction() {
        if (txtBookingId.getText().isEmpty()) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Please select a booking from the table to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Customer cust = (Customer) comboCustomer.getSelectedItem();
        Package pkg = (Package) comboPackage.getSelectedItem();
        Date travelDate = parseTravelDate(txtTravelDate.getText().trim());
        if (cust == null || pkg == null || travelDate == null) return;

        int id = Integer.parseInt(txtBookingId.getText());
        int travelers = (Integer) spinnerTravelers.getValue();
        double originalAmount = pkg.getPrice() * travelers;

        // Resolve Festival Offer
        FestivalOffer appliedOffer = resolveSelectedOffer(pkg.getPackageId(), originalAmount, travelDate);
        double discount = 0.0;
        Integer offerId = null;

        if (appliedOffer != null) {
            discount = appliedOffer.calculateDiscount(originalAmount);
            offerId = appliedOffer.getOfferId();
        }

        double finalTotal = originalAmount - discount;
        String status = (String) comboStatus.getSelectedItem();

        Booking b = new Booking(id, cust.getCustomerId(), pkg.getPackageId(), travelDate, travelers, finalTotal, status, offerId, originalAmount, discount);
        if (bookingDAO.updateBooking(b)) {
            SoundManager.playSuccessSound();
            JOptionPane.showMessageDialog(this, "Booking updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadBookingTableData();
        } else {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Failed to update Booking.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private FestivalOffer resolveSelectedOffer(int packageId, double originalAmount, Date travelDate) {
        Object sel = comboFestivalOffer.getSelectedItem();
        if ("None (No Festival Discount)".equals(sel)) {
            return null;
        }

        if ("🌟 Auto-Apply Best Active Offer".equals(sel)) {
            List<FestivalOffer> eligible = festivalOfferDAO.getEligibleOffersForBooking(packageId, originalAmount, travelDate);
            double maxDisc = 0.0;
            FestivalOffer best = null;
            for (FestivalOffer off : eligible) {
                double d = off.calculateDiscount(originalAmount);
                if (d > maxDisc) {
                    maxDisc = d;
                    best = off;
                }
            }
            return best;
        }

        if (sel instanceof FestivalOffer) {
            return (FestivalOffer) sel;
        }

        return null;
    }

    private void cancelBookingAction() {
        if (txtBookingId.getText().isEmpty()) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Please select a booking from the table to cancel.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(txtBookingId.getText());
        if (bookingDAO.cancelBooking(id)) {
            SoundManager.playSuccessSound();
            JOptionPane.showMessageDialog(this, "Booking status updated to Cancelled.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadBookingTableData();
        } else {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Failed to cancel Booking.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBookingAction() {
        if (txtBookingId.getText().isEmpty()) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Please select a booking from the table to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this booking record?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(txtBookingId.getText());
            if (bookingDAO.deleteBooking(id)) {
                SoundManager.playSuccessSound();
                JOptionPane.showMessageDialog(this, "Booking deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadBookingTableData();
            } else {
                SoundManager.playErrorSound();
                JOptionPane.showMessageDialog(this, "Failed to delete Booking.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewReceiptAction() {
        if (txtBookingId.getText().isEmpty()) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Please select a booking from the table to view its receipt.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookingId = Integer.parseInt(txtBookingId.getText());
        Booking b = bookingDAO.getBookingById(bookingId);
        Payment p = paymentDAO.getPaymentByBookingId(bookingId);

        if (b != null) {
            ReceiptDialog dialog = new ReceiptDialog(mainFrame, b, p);
            dialog.setVisible(true);
        }
    }

    private Date parseTravelDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            java.util.Date d = sdf.parse(dateStr);
            return new Date(d.getTime());
        } catch (ParseException e) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Invalid Date format! Please use yyyy-MM-dd format (e.g. 2026-09-15).", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private Date parseTravelDateSilent(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            java.util.Date d = sdf.parse(dateStr);
            return new Date(d.getTime());
        } catch (Exception e) {
            return null;
        }
    }

    private void clearForm() {
        txtBookingId.setText("");
        if (comboCustomer.getItemCount() > 0) comboCustomer.setSelectedIndex(0);
        if (comboPackage.getItemCount() > 0) comboPackage.setSelectedIndex(0);
        spinnerTravelers.setValue(1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        txtTravelDate.setText(sdf.format(new Date(System.currentTimeMillis() + 86400000L)));
        comboStatus.setSelectedIndex(0);
        txtSearch.setText("");
        bookingTable.clearSelection();
        updatePriceAndTotal();
    }
}
