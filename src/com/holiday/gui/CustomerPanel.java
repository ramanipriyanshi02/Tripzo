package com.holiday.gui;

import com.holiday.dao.CustomerDAO;
import com.holiday.gui.ui.CardPanel;
import com.holiday.gui.ui.ModernButton;
import com.holiday.gui.ui.ModernTable;
import com.holiday.gui.ui.Theme;
import com.holiday.model.Customer;
import com.holiday.util.SoundManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class CustomerPanel extends JPanel {

    private CustomerDAO customerDAO;

    // Form fields
    private JTextField txtCustomerId;
    private JTextField txtName;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JTextField txtAddress;

    private JTextField txtSearch;
    private JTable customerTable;
    private DefaultTableModel tableModel;

    public CustomerPanel() {
        customerDAO = new CustomerDAO();
        setLayout(new BorderLayout(15, 0));
        setOpaque(false);

        // Left Form Card
        JPanel formCard = createFormCard();
        formCard.setPreferredSize(new Dimension(370, getHeight()));
        add(formCard, BorderLayout.WEST);

        // Right Table Card
        JPanel tableCard = createTableCard();
        add(tableCard, BorderLayout.CENTER);

        loadCustomerData();
    }

    private JPanel createFormCard() {
        CardPanel card = new CardPanel(Theme.PRIMARY);
        card.setLayout(new BorderLayout(0, 15));

        JLabel title = new JLabel("👥 Customer Profile Form");
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

        txtCustomerId = addFormField(fieldsPanel, "Customer ID (Auto):", gbc, row++);
        txtCustomerId.setEditable(false);
        txtCustomerId.setBackground(new Color(0xEA, 0xEF, 0xEF));

        txtName = addFormField(fieldsPanel, "Full Name *:", gbc, row++);
        txtPhone = addFormField(fieldsPanel, "Phone Number *:", gbc, row++);
        txtEmail = addFormField(fieldsPanel, "Email Address:", gbc, row++);
        txtAddress = addFormField(fieldsPanel, "City / Address:", gbc, row++);

        card.add(fieldsPanel, BorderLayout.CENTER);

        // Buttons Grid
        JPanel btnPanel = new JPanel(new GridLayout(2, 3, 8, 8));
        btnPanel.setOpaque(false);

        ModernButton btnAdd = new ModernButton("Add", ModernButton.Style.PRIMARY);
        ModernButton btnUpdate = new ModernButton("Update", ModernButton.Style.SUCCESS);
        ModernButton btnDelete = new ModernButton("Delete", ModernButton.Style.DANGER);
        ModernButton btnClear = new ModernButton("Clear", ModernButton.Style.OUTLINE);
        ModernButton btnRefresh = new ModernButton("Refresh", ModernButton.Style.SECONDARY);

        btnAdd.addActionListener(e -> addCustomerAction());
        btnUpdate.addActionListener(e -> updateCustomerAction());
        btnDelete.addActionListener(e -> deleteCustomerAction());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> loadCustomerData());

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
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

        JLabel lblTitle = new JLabel("📋 Registered Customer Directory");
        lblTitle.setFont(Theme.FONT_TITLE);
        lblTitle.setForeground(Theme.TEXT_MAIN);

        JPanel searchGroup = new JPanel(new BorderLayout(5, 0));
        searchGroup.setOpaque(false);

        txtSearch = new JTextField(18);
        txtSearch.setFont(Theme.FONT_REGULAR);
        txtSearch.setBackground(Theme.BACKGROUND);
        txtSearch.setForeground(Theme.PRIMARY);
        txtSearch.setCaretColor(Theme.PRIMARY);
        txtSearch.setToolTipText("Search name or phone number...");

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
        String[] columns = {"ID", "Name", "Phone", "Email", "Address"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        customerTable = ModernTable.createTable(tableModel);
        ModernTable.centerColumns(customerTable, 0, 2);

        customerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && customerTable.getSelectedRow() != -1) {
                populateFormFromTable(customerTable.getSelectedRow());
            }
        });

        JScrollPane scrollPane = ModernTable.createScrollPane(customerTable);
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

    public void loadCustomerData() {
        tableModel.setRowCount(0);
        List<Customer> list = customerDAO.getAllCustomers();
        for (Customer c : list) {
            tableModel.addRow(new Object[]{
                c.getCustomerId(),
                c.getName(),
                c.getPhone(),
                c.getEmail() != null ? c.getEmail() : "-",
                c.getAddress() != null ? c.getAddress() : "-"
            });
        }
        clearForm();
    }

    private void searchAction() {
        String q = txtSearch.getText().trim();
        if (q.isEmpty()) {
            loadCustomerData();
            return;
        }
        tableModel.setRowCount(0);
        List<Customer> list = customerDAO.searchCustomers(q);
        for (Customer c : list) {
            tableModel.addRow(new Object[]{
                c.getCustomerId(),
                c.getName(),
                c.getPhone(),
                c.getEmail(),
                c.getAddress()
            });
        }
    }

    private void populateFormFromTable(int row) {
        txtCustomerId.setText(tableModel.getValueAt(row, 0).toString());
        txtName.setText(tableModel.getValueAt(row, 1).toString());
        txtPhone.setText(tableModel.getValueAt(row, 2).toString());
        txtEmail.setText(tableModel.getValueAt(row, 3).toString());
        txtAddress.setText(tableModel.getValueAt(row, 4).toString());
    }

    private void addCustomerAction() {
        if (!validateInputs()) return;

        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        String address = txtAddress.getText().trim();

        Customer c = new Customer(name, phone, email, address);
        if (customerDAO.addCustomer(c)) {
            SoundManager.playSuccessSound();
            JOptionPane.showMessageDialog(this, "Customer added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadCustomerData();
        } else {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Failed to add Customer.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCustomerAction() {
        if (txtCustomerId.getText().isEmpty()) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Please select a customer from the table to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validateInputs()) return;

        int id = Integer.parseInt(txtCustomerId.getText());
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        String address = txtAddress.getText().trim();

        Customer c = new Customer(id, name, phone, email, address);
        if (customerDAO.updateCustomer(c)) {
            SoundManager.playSuccessSound();
            JOptionPane.showMessageDialog(this, "Customer information updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadCustomerData();
        } else {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Failed to update Customer.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCustomerAction() {
        if (txtCustomerId.getText().isEmpty()) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Please select a customer from the table to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this customer? This will also remove associated bookings.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(txtCustomerId.getText());
            if (customerDAO.deleteCustomer(id)) {
                SoundManager.playSuccessSound();
                JOptionPane.showMessageDialog(this, "Customer deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCustomerData();
            } else {
                SoundManager.playErrorSound();
                JOptionPane.showMessageDialog(this, "Failed to delete Customer.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateInputs() {
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();

        if (name.isEmpty()) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Customer Name cannot be empty!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (phone.isEmpty() || !phone.matches("\\d{10,15}")) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Please enter a valid Phone Number (10 to 15 digits).", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!email.isEmpty() && !Pattern.matches("^[A-Za-z0-9+_.-]+@(.+)$", email)) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Please enter a valid Email Address format.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void clearForm() {
        txtCustomerId.setText("");
        txtName.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtAddress.setText("");
        txtSearch.setText("");
        customerTable.clearSelection();
    }
}
