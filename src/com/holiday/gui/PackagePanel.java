package com.holiday.gui;

import com.holiday.dao.PackageDAO;
import com.holiday.gui.ui.CardPanel;
import com.holiday.gui.ui.ModernButton;
import com.holiday.gui.ui.ModernTable;
import com.holiday.gui.ui.Theme;
import com.holiday.model.Package;
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
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class PackagePanel extends JPanel {

    private PackageDAO packageDAO;

    // Form fields
    private JTextField txtPackageId;
    private JTextField txtPackageName;
    private JTextField txtDestination;
    private JTextField txtDuration;
    private JTextField txtPrice;
    private JTextField txtHotel;
    private JTextField txtTransport;
    private JTextArea txtDescription;

    private JTextField txtSearch;
    private JTable packageTable;
    private DefaultTableModel tableModel;

    // Image Card Preview Panel
    private JLabel lblImagePreview;
    private JLabel lblPreviewTitle;

    public PackagePanel() {
        packageDAO = new PackageDAO();
        setLayout(new BorderLayout(15, 0));
        setOpaque(false);

        // 1. Left Form Card Panel
        JPanel formCard = createFormCard();
        formCard.setPreferredSize(new Dimension(390, getHeight()));
        add(formCard, BorderLayout.WEST);

        // 2. Right Table Card Panel
        JPanel tableCard = createTableCard();
        add(tableCard, BorderLayout.CENTER);

        loadPackageData();
    }

    private JPanel createFormCard() {
        CardPanel card = new CardPanel(Theme.PRIMARY);
        card.setLayout(new BorderLayout(0, 10));

        JLabel title = new JLabel("📦 Package Details Form");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_MAIN);
        card.add(title, BorderLayout.NORTH);

        // Scrollable Form Container
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        // Destination Image Live Preview Box
        lblImagePreview = new JLabel(DestinationImageCache.getDestinationImage("Default", 340, 100, 12));
        lblImagePreview.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        fieldsPanel.add(lblImagePreview, gbc);

        gbc.gridwidth = 1;

        txtPackageId = addFormField(fieldsPanel, "Package ID (Auto):", gbc, row++);
        txtPackageId.setEditable(false);
        txtPackageId.setBackground(new Color(0xEA, 0xEF, 0xEF));

        txtPackageName = addFormField(fieldsPanel, "Package Name *:", gbc, row++);
        txtDestination = addFormField(fieldsPanel, "Destination *:", gbc, row++);
        
        // Update image preview on destination typing
        txtDestination.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                updateImagePreview(txtDestination.getText().trim());
            }
        });

        txtDuration = addFormField(fieldsPanel, "Duration (Days/Nights) *:", gbc, row++);
        txtPrice = addFormField(fieldsPanel, "Price (₹) *:", gbc, row++);
        txtHotel = addFormField(fieldsPanel, "Hotel Name:", gbc, row++);
        txtTransport = addFormField(fieldsPanel, "Transport Mode:", gbc, row++);

        // Description TextArea
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        JLabel lblDesc = new JLabel("Description:");
        lblDesc.setFont(Theme.FONT_LABEL);
        lblDesc.setForeground(Theme.TEXT_MAIN);
        fieldsPanel.add(lblDesc, gbc);

        txtDescription = new JTextArea(3, 20);
        txtDescription.setFont(Theme.FONT_REGULAR);
        txtDescription.setBackground(Theme.BACKGROUND);
        txtDescription.setForeground(Theme.PRIMARY);
        txtDescription.setCaretColor(Theme.PRIMARY);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(txtDescription);
        descScroll.setPreferredSize(new Dimension(340, 55));
        descScroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));

        gbc.gridy = row++;
        fieldsPanel.add(descScroll, gbc);

        card.add(fieldsPanel, BorderLayout.CENTER);

        // Buttons Panel
        JPanel btnPanel = new JPanel(new GridLayout(2, 3, 8, 8));
        btnPanel.setOpaque(false);

        ModernButton btnAdd = new ModernButton("Add", ModernButton.Style.PRIMARY);
        ModernButton btnUpdate = new ModernButton("Update", ModernButton.Style.SUCCESS);
        ModernButton btnDelete = new ModernButton("Delete", ModernButton.Style.DANGER);
        ModernButton btnClear = new ModernButton("Clear", ModernButton.Style.OUTLINE);
        ModernButton btnRefresh = new ModernButton("Refresh", ModernButton.Style.SECONDARY);

        btnAdd.addActionListener(e -> addPackageAction());
        btnUpdate.addActionListener(e -> updatePackageAction());
        btnDelete.addActionListener(e -> deletePackageAction());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> loadPackageData());

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

        JLabel lblTitle = new JLabel("🌴 Holiday Packages Inventory");
        lblTitle.setFont(Theme.FONT_TITLE);
        lblTitle.setForeground(Theme.TEXT_MAIN);

        JPanel searchGroup = new JPanel(new BorderLayout(5, 0));
        searchGroup.setOpaque(false);

        txtSearch = new JTextField(18);
        txtSearch.setFont(Theme.FONT_REGULAR);
        txtSearch.setBackground(Theme.BACKGROUND);
        txtSearch.setForeground(Theme.PRIMARY);
        txtSearch.setCaretColor(Theme.PRIMARY);
        txtSearch.setToolTipText("Search package name or destination...");

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
        String[] columns = {"ID", "Package Name", "Destination", "Duration", "Price (₹)", "Hotel", "Transport", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        packageTable = ModernTable.createTable(tableModel);
        ModernTable.centerColumns(packageTable, 0, 3, 4);

        packageTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && packageTable.getSelectedRow() != -1) {
                populateFormFromTable(packageTable.getSelectedRow());
            }
        });

        JScrollPane scrollPane = ModernTable.createScrollPane(packageTable);
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

    public void loadPackageData() {
        tableModel.setRowCount(0);
        List<Package> list = packageDAO.getAllPackages();
        for (Package p : list) {
            tableModel.addRow(new Object[]{
                p.getPackageId(),
                p.getPackageName(),
                p.getDestination(),
                p.getDuration(),
                String.format("%.2f", p.getPrice()),
                p.getHotel() != null ? p.getHotel() : "-",
                p.getTransport() != null ? p.getTransport() : "-",
                p.getDescription() != null ? p.getDescription() : "-"
            });
        }
        clearForm();
    }

    private void updateImagePreview(String destination) {
        ImageIcon icon = DestinationImageCache.getDestinationImage(destination, 340, 100, 12);
        lblImagePreview.setIcon(icon);
    }

    private void searchAction() {
        String q = txtSearch.getText().trim();
        if (q.isEmpty()) {
            loadPackageData();
            return;
        }
        tableModel.setRowCount(0);
        List<Package> list = packageDAO.searchPackages(q);
        for (Package p : list) {
            tableModel.addRow(new Object[]{
                p.getPackageId(),
                p.getPackageName(),
                p.getDestination(),
                p.getDuration(),
                String.format("%.2f", p.getPrice()),
                p.getHotel(),
                p.getTransport(),
                p.getDescription()
            });
        }
    }

    private void populateFormFromTable(int row) {
        txtPackageId.setText(tableModel.getValueAt(row, 0).toString());
        txtPackageName.setText(tableModel.getValueAt(row, 1).toString());
        String dest = tableModel.getValueAt(row, 2).toString();
        txtDestination.setText(dest);
        txtDuration.setText(tableModel.getValueAt(row, 3).toString());
        txtPrice.setText(tableModel.getValueAt(row, 4).toString());
        txtHotel.setText(tableModel.getValueAt(row, 5).toString());
        txtTransport.setText(tableModel.getValueAt(row, 6).toString());
        txtDescription.setText(tableModel.getValueAt(row, 7).toString());

        updateImagePreview(dest);
    }

    private void addPackageAction() {
        if (!validateInputs()) return;

        try {
            String name = txtPackageName.getText().trim();
            String dest = txtDestination.getText().trim();
            String duration = txtDuration.getText().trim();
            double price = Double.parseDouble(txtPrice.getText().trim());
            String hotel = txtHotel.getText().trim();
            String transport = txtTransport.getText().trim();
            String desc = txtDescription.getText().trim();

            Package pkg = new Package(name, dest, duration, price, hotel, transport, desc);
            if (packageDAO.addPackage(pkg)) {
                SoundManager.playSuccessSound();
                JOptionPane.showMessageDialog(this, "Holiday Package added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadPackageData();
            } else {
                SoundManager.playErrorSound();
                JOptionPane.showMessageDialog(this, "Failed to add Package.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric Price.", "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updatePackageAction() {
        if (txtPackageId.getText().isEmpty()) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Please select a package from the table to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validateInputs()) return;

        try {
            int id = Integer.parseInt(txtPackageId.getText());
            String name = txtPackageName.getText().trim();
            String dest = txtDestination.getText().trim();
            String duration = txtDuration.getText().trim();
            double price = Double.parseDouble(txtPrice.getText().trim());
            String hotel = txtHotel.getText().trim();
            String transport = txtTransport.getText().trim();
            String desc = txtDescription.getText().trim();

            Package pkg = new Package(id, name, dest, duration, price, hotel, transport, desc);
            if (packageDAO.updatePackage(pkg)) {
                SoundManager.playSuccessSound();
                JOptionPane.showMessageDialog(this, "Package updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadPackageData();
            } else {
                SoundManager.playErrorSound();
                JOptionPane.showMessageDialog(this, "Failed to update Package.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric Price.", "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deletePackageAction() {
        if (txtPackageId.getText().isEmpty()) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Please select a package from the table to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this package?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(txtPackageId.getText());
            if (packageDAO.deletePackage(id)) {
                SoundManager.playSuccessSound();
                JOptionPane.showMessageDialog(this, "Package deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadPackageData();
            } else {
                SoundManager.playErrorSound();
                JOptionPane.showMessageDialog(this, "Failed to delete Package. Make sure it is not referenced in active bookings.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateInputs() {
        if (txtPackageName.getText().trim().isEmpty() ||
            txtDestination.getText().trim().isEmpty() ||
            txtDuration.getText().trim().isEmpty() ||
            txtPrice.getText().trim().isEmpty()) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Package Name, Destination, Duration, and Price are required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void clearForm() {
        txtPackageId.setText("");
        txtPackageName.setText("");
        txtDestination.setText("");
        txtDuration.setText("");
        txtPrice.setText("");
        txtHotel.setText("");
        txtTransport.setText("");
        txtDescription.setText("");
        txtSearch.setText("");
        packageTable.clearSelection();
        updateImagePreview("Default");
    }
}
