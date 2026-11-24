package com.holiday.gui;

import com.holiday.dao.BookingDAO;
import com.holiday.dao.PaymentDAO;
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class PaymentPanel extends JPanel {

    private MainFrame mainFrame;
    private PaymentDAO paymentDAO;
    private BookingDAO bookingDAO;

    // Form fields
    private JTextField txtPaymentId;
    private JComboBox<Booking> comboBooking;
    private JTextField txtBookingAmount;
    private JComboBox<String> comboMethod;
    private JComboBox<String> comboStatus;
    private JTextField txtPaymentDate;
    private JTextField txtAmount;

    private JTextField txtSearch;
    private JTable paymentTable;
    private DefaultTableModel tableModel;

    public PaymentPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.paymentDAO = new PaymentDAO();
        this.bookingDAO = new BookingDAO();

        setLayout(new BorderLayout(15, 0));
        setOpaque(false);

        // Left Form Card
        JPanel formCard = createFormCard();
        formCard.setPreferredSize(new Dimension(390, getHeight()));
        add(formCard, BorderLayout.WEST);

        // Right Table Card
        JPanel tableCard = createTableCard();
        add(tableCard, BorderLayout.CENTER);

        loadDropdownsAndData();
    }

    private JPanel createFormCard() {
        CardPanel card = new CardPanel(Theme.PRIMARY);
        card.setLayout(new BorderLayout(0, 10));

        JLabel title = new JLabel("💳 Payment & Settlement Form");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_MAIN);
        card.add(title, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        txtPaymentId = addFormField(fieldsPanel, "Payment ID (Auto):", gbc, row++);
        txtPaymentId.setEditable(false);
        txtPaymentId.setBackground(new Color(0xEA, 0xEF, 0xEF));

        // Booking Dropdown
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.35;
        JLabel lblBk = new JLabel("Booking *:");
        lblBk.setFont(Theme.FONT_LABEL);
        lblBk.setForeground(Theme.TEXT_MAIN);
        fieldsPanel.add(lblBk, gbc);

        gbc.gridx = 1; gbc.weightx = 0.65;
        comboBooking = new JComboBox<>();
        comboBooking.setFont(Theme.FONT_REGULAR);
        comboBooking.addActionListener(e -> updateBookingAmountDisplay());
        fieldsPanel.add(comboBooking, gbc);
        row++;

        // Booking Amount Display
        txtBookingAmount = addFormField(fieldsPanel, "Total Booking Amount:", gbc, row++);
        txtBookingAmount.setEditable(false);
        txtBookingAmount.setBackground(new Color(0xEA, 0xEF, 0xEF));

        // Payment Method
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        JLabel lblMeth = new JLabel("Payment Method *:");
        lblMeth.setFont(Theme.FONT_LABEL);
        lblMeth.setForeground(Theme.TEXT_MAIN);
        fieldsPanel.add(lblMeth, gbc);

        gbc.gridx = 1; gbc.weightx = 0.65;
        comboMethod = new JComboBox<>(new String[]{"Cash", "Card", "UPI", "Bank Transfer"});
        comboMethod.setFont(Theme.FONT_REGULAR);
        fieldsPanel.add(comboMethod, gbc);
        row++;

        // Payment Status
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        JLabel lblStat = new JLabel("Payment Status *:");
        lblStat.setFont(Theme.FONT_LABEL);
        lblStat.setForeground(Theme.TEXT_MAIN);
        fieldsPanel.add(lblStat, gbc);

        gbc.gridx = 1; gbc.weightx = 0.65;
        comboStatus = new JComboBox<>(new String[]{"Paid", "Pending", "Partial"});
        comboStatus.setFont(Theme.FONT_REGULAR);
        fieldsPanel.add(comboStatus, gbc);
        row++;

        // Payment Date
        txtPaymentDate = addFormField(fieldsPanel, "Payment Date (yyyy-MM-dd):", gbc, row++);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        txtPaymentDate.setText(sdf.format(new Date(System.currentTimeMillis())));

        // Payment Amount
        txtAmount = addFormField(fieldsPanel, "Paid Amount (₹) *:", gbc, row++);
        txtAmount.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 13));

        card.add(fieldsPanel, BorderLayout.CENTER);

        // Buttons Panel
        JPanel btnPanel = new JPanel(new GridLayout(3, 3, 6, 6));
        btnPanel.setOpaque(false);

        ModernButton btnAdd = new ModernButton("Add Payment", ModernButton.Style.PRIMARY);
        ModernButton btnUpdate = new ModernButton("Update", ModernButton.Style.SUCCESS);
        ModernButton btnDelete = new ModernButton("Delete", ModernButton.Style.DANGER);
        ModernButton btnReceipt = new ModernButton("Receipt", ModernButton.Style.SECONDARY);
        ModernButton btnClear = new ModernButton("Clear", ModernButton.Style.OUTLINE);
        ModernButton btnRefresh = new ModernButton("Refresh", ModernButton.Style.OUTLINE);

        btnAdd.addActionListener(e -> addPaymentAction());
        btnUpdate.addActionListener(e -> updatePaymentAction());
        btnDelete.addActionListener(e -> deletePaymentAction());
        btnReceipt.addActionListener(e -> viewReceiptAction());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> loadDropdownsAndData());

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
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

        // Top Search Bar
        JPanel topBar = new JPanel(new BorderLayout(10, 0));
        topBar.setOpaque(false);

        JLabel lblTitle = new JLabel("📑 Financial Payment Records");
        lblTitle.setFont(Theme.FONT_TITLE);
        lblTitle.setForeground(Theme.TEXT_MAIN);

        JPanel searchGroup = new JPanel(new BorderLayout(5, 0));
        searchGroup.setOpaque(false);

        txtSearch = new JTextField(16);
        txtSearch.setFont(Theme.FONT_REGULAR);
        txtSearch.setBackground(Theme.BACKGROUND);
        txtSearch.setForeground(Theme.PRIMARY);
        txtSearch.setCaretColor(Theme.PRIMARY);
        txtSearch.setToolTipText("Search ID, booking, customer...");

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
        String[] columns = {"Payment ID", "Booking ID", "Customer Name", "Method", "Status", "Date", "Amount"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        paymentTable = ModernTable.createTable(tableModel);
        ModernTable.centerColumns(paymentTable, 0, 1, 3, 5);
        paymentTable.getColumnModel().getColumn(4).setCellRenderer(StatusBadge.getTableRenderer());

        paymentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && paymentTable.getSelectedRow() != -1) {
                populateFormFromTable(paymentTable.getSelectedRow());
            }
        });

        JScrollPane scrollPane = ModernTable.createScrollPane(paymentTable);
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
        comboBooking.removeAllItems();
        List<Booking> bookings = bookingDAO.getAllBookings();
        for (Booking b : bookings) {
            comboBooking.addItem(b);
        }
        updateBookingAmountDisplay();
        loadPaymentTableData();
    }

    private void updateBookingAmountDisplay() {
        Booking selectedBooking = (Booking) comboBooking.getSelectedItem();
        if (selectedBooking != null) {
            txtBookingAmount.setText(String.format("%.2f", selectedBooking.getTotalAmount()));
            txtAmount.setText(String.format("%.2f", selectedBooking.getTotalAmount()));
        } else {
            txtBookingAmount.setText("0.00");
            txtAmount.setText("0.00");
        }
    }

    private void loadPaymentTableData() {
        tableModel.setRowCount(0);
        List<Payment> list = paymentDAO.getAllPayments();
        for (Payment p : list) {
            tableModel.addRow(new Object[]{
                p.getPaymentId(),
                p.getBookingId(),
                p.getCustomerName(),
                p.getPaymentMethod(),
                p.getPaymentStatus(),
                p.getPaymentDate(),
                "₹" + String.format("%.2f", p.getAmount())
            });
        }
        clearForm();
    }

    private void searchAction() {
        String q = txtSearch.getText().trim();
        if (q.isEmpty()) {
            loadPaymentTableData();
            return;
        }
        tableModel.setRowCount(0);
        List<Payment> list = paymentDAO.searchPayments(q);
        for (Payment p : list) {
            tableModel.addRow(new Object[]{
                p.getPaymentId(),
                p.getBookingId(),
                p.getCustomerName(),
                p.getPaymentMethod(),
                p.getPaymentStatus(),
                p.getPaymentDate(),
                "₹" + String.format("%.2f", p.getAmount())
            });
        }
    }

    private void populateFormFromTable(int row) {
        int paymentId = (Integer) tableModel.getValueAt(row, 0);
        int bookingId = (Integer) tableModel.getValueAt(row, 1);

        txtPaymentId.setText(String.valueOf(paymentId));

        for (int i = 0; i < comboBooking.getItemCount(); i++) {
            if (comboBooking.getItemAt(i).getBookingId() == bookingId) {
                comboBooking.setSelectedIndex(i);
                break;
            }
        }

        comboMethod.setSelectedItem(tableModel.getValueAt(row, 3).toString());
        comboStatus.setSelectedItem(tableModel.getValueAt(row, 4).toString());
        txtPaymentDate.setText(tableModel.getValueAt(row, 5).toString());

        String amountStr = tableModel.getValueAt(row, 6).toString().replace("₹", "").trim();
        txtAmount.setText(amountStr);
    }

    private void addPaymentAction() {
        Booking booking = (Booking) comboBooking.getSelectedItem();
        if (booking == null) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Please select an existing Booking.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String method = (String) comboMethod.getSelectedItem();
            String status = (String) comboStatus.getSelectedItem();
            Date date = parseDate(txtPaymentDate.getText().trim());
            double amount = Double.parseDouble(txtAmount.getText().trim());

            if (date == null) return;

            Payment p = new Payment(0, booking.getBookingId(), method, status, date, amount);
            if (paymentDAO.addPayment(p)) {
                SoundManager.playSuccessSound();
                JOptionPane.showMessageDialog(this, "Payment record added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadPaymentTableData();
            } else {
                SoundManager.playErrorSound();
                JOptionPane.showMessageDialog(this, "Failed to add payment. Verify booking exists.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric Payment Amount.", "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updatePaymentAction() {
        if (txtPaymentId.getText().isEmpty()) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Please select a payment from the table to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Booking booking = (Booking) comboBooking.getSelectedItem();
        if (booking == null) return;

        try {
            int paymentId = Integer.parseInt(txtPaymentId.getText());
            String method = (String) comboMethod.getSelectedItem();
            String status = (String) comboStatus.getSelectedItem();
            Date date = parseDate(txtPaymentDate.getText().trim());
            double amount = Double.parseDouble(txtAmount.getText().trim());

            if (date == null) return;

            Payment p = new Payment(paymentId, booking.getBookingId(), method, status, date, amount);
            if (paymentDAO.updatePayment(p)) {
                SoundManager.playSuccessSound();
                JOptionPane.showMessageDialog(this, "Payment updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadPaymentTableData();
            } else {
                SoundManager.playErrorSound();
                JOptionPane.showMessageDialog(this, "Failed to update payment.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric Amount.", "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deletePaymentAction() {
        if (txtPaymentId.getText().isEmpty()) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Please select a payment from the table to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this payment record?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int paymentId = Integer.parseInt(txtPaymentId.getText());
            if (paymentDAO.deletePayment(paymentId)) {
                SoundManager.playSuccessSound();
                JOptionPane.showMessageDialog(this, "Payment record deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadPaymentTableData();
            } else {
                SoundManager.playErrorSound();
                JOptionPane.showMessageDialog(this, "Failed to delete payment.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewReceiptAction() {
        Booking booking = (Booking) comboBooking.getSelectedItem();
        if (booking == null) return;

        Payment p = null;
        if (!txtPaymentId.getText().isEmpty()) {
            p = paymentDAO.getPaymentByBookingId(booking.getBookingId());
        }

        ReceiptDialog dialog = new ReceiptDialog(mainFrame, booking, p);
        dialog.setVisible(true);
    }

    private Date parseDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            java.util.Date d = sdf.parse(dateStr);
            return new Date(d.getTime());
        } catch (ParseException e) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Invalid Date format! Please use yyyy-MM-dd format.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private void clearForm() {
        txtPaymentId.setText("");
        if (comboBooking.getItemCount() > 0) comboBooking.setSelectedIndex(0);
        comboMethod.setSelectedIndex(0);
        comboStatus.setSelectedIndex(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        txtPaymentDate.setText(sdf.format(new Date(System.currentTimeMillis())));
        txtSearch.setText("");
        paymentTable.clearSelection();
        updateBookingAmountDisplay();
    }
}
