package com.holiday.gui;

import com.holiday.dao.FestivalOfferDAO;
import com.holiday.dao.PackageDAO;
import com.holiday.gui.ui.ModernButton;
import com.holiday.gui.ui.Theme;
import com.holiday.model.FestivalOffer;
import com.holiday.model.Package;
import com.holiday.util.FestivalImageUtil;
import com.holiday.util.SoundManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FestivalOfferDialog extends JDialog {

    private FestivalOffer existingOffer;
    private FestivalOfferDAO festivalOfferDAO;
    private PackageDAO packageDAO;
    private boolean saved = false;

    // Fields
    private JTextField txtOfferId;
    private JTextField txtOfferName;
    private JComboBox<String> comboFestival;
    private JTextArea txtDescription;
    private JComboBox<String> comboImagePreset;
    private JTextField txtCustomImagePath;
    private JLabel lblImagePreview;
    private JComboBox<String> comboDiscountType;
    private JTextField txtDiscountValue;
    private JTextField txtMaxDiscount;
    private JTextField txtMinBookingAmount;
    private JTextField txtStartDate;
    private JTextField txtEndDate;
    private JComboBox<Object> comboPackage;
    private JComboBox<String> comboStatus;
    private JTextField txtUsageLimit;

    public FestivalOfferDialog(Frame owner, FestivalOffer offerToEdit) {
        super(owner, offerToEdit == null ? "Create New Festival Offer" : "Edit Festival Offer #" + offerToEdit.getOfferId(), true);
        this.existingOffer = offerToEdit;
        this.festivalOfferDAO = new FestivalOfferDAO();
        this.packageDAO = new PackageDAO();

        setSize(780, 780);
        setMinimumSize(new Dimension(720, 680));
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.BACKGROUND);

        initUI();
        if (existingOffer != null) {
            populateFields(existingOffer);
        } else {
            setDefaults();
        }
    }

    private void initUI() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.PRIMARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel(existingOffer == null ? "✨ Create New Festival Offer & Discount" : "✏️ Edit Festival Campaign #" + existingOffer.getOfferId());
        titleLabel.setFont(Theme.FONT_HEADER);
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Configure festival discounts, validity dates, travel packages & promotional images");
        subtitleLabel.setFont(Theme.FONT_SUBTITLE);
        subtitleLabel.setForeground(Theme.SECONDARY);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Center Form
        JPanel formContainer = new JPanel(new BorderLayout());
        formContainer.setOpaque(false);
        formContainer.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel fieldsGrid = new JPanel(new GridBagLayout());
        fieldsGrid.setBackground(Theme.CARD);
        fieldsGrid.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Row 0: Offer ID & Status
        addLabel(fieldsGrid, "Offer ID:", gbc, 0, row);
        txtOfferId = new JTextField("Auto-Generated");
        txtOfferId.setEditable(false);
        txtOfferId.setBackground(new Color(0xEA, 0xEF, 0xEF));
        txtOfferId.setFont(Theme.FONT_BOLD);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.35;
        fieldsGrid.add(txtOfferId, gbc);

        addLabel(fieldsGrid, "Status *:", gbc, 2, row);
        comboStatus = new JComboBox<>(new String[]{"Active", "Scheduled", "Paused", "Draft", "Expired"});
        comboStatus.setFont(Theme.FONT_REGULAR);
        gbc.gridx = 3; gbc.gridy = row; gbc.weightx = 0.35;
        fieldsGrid.add(comboStatus, gbc);
        row++;

        // Row 1: Offer Name
        addLabel(fieldsGrid, "Offer Name *:", gbc, 0, row);
        txtOfferName = new JTextField();
        txtOfferName.setFont(Theme.FONT_REGULAR);
        txtOfferName.setToolTipText("e.g. Diwali Dhamaka 20% Off");
        gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 3; gbc.weightx = 1.0;
        fieldsGrid.add(txtOfferName, gbc);
        gbc.gridwidth = 1;
        row++;

        // Row 2: Festival Name
        addLabel(fieldsGrid, "Festival / Occasion *:", gbc, 0, row);
        comboFestival = new JComboBox<>(new String[]{
            "Diwali", "Navratri", "Christmas", "New Year", "Holi", "Summer Vacation", "Winter Holiday", "Independence Day", "Custom Celebration"
        });
        comboFestival.setEditable(true);
        comboFestival.setFont(Theme.FONT_REGULAR);
        comboFestival.addActionListener(e -> updatePresetByFestival());
        gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 3;
        fieldsGrid.add(comboFestival, gbc);
        gbc.gridwidth = 1;
        row++;

        // Row 3: Description
        addLabel(fieldsGrid, "Description:", gbc, 0, row);
        txtDescription = new JTextArea(3, 20);
        txtDescription.setFont(Theme.FONT_REGULAR);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(txtDescription);
        descScroll.setBorder(Theme.BORDER_INPUT);
        gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 3;
        fieldsGrid.add(descScroll, gbc);
        gbc.gridwidth = 1;
        row++;

        // Row 4: Festival Banner Image Selection
        addLabel(fieldsGrid, "Festival Image *:", gbc, 0, row);
        comboImagePreset = new JComboBox<>();
        for (String label : FestivalImageUtil.PRESET_IMAGES.keySet()) {
            comboImagePreset.addItem(label);
        }
        comboImagePreset.setFont(Theme.FONT_REGULAR);
        comboImagePreset.addActionListener(e -> updateImagePreview());
        gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 2;
        fieldsGrid.add(comboImagePreset, gbc);

        ModernButton btnBrowseImage = new ModernButton("📁 Browse...", ModernButton.Style.OUTLINE);
        btnBrowseImage.addActionListener(e -> browseCustomImage());
        gbc.gridx = 3; gbc.gridy = row; gbc.gridwidth = 1;
        fieldsGrid.add(btnBrowseImage, gbc);
        row++;

        // Row 5: Image Preview & Path
        txtCustomImagePath = new JTextField();
        txtCustomImagePath.setVisible(false); // holds actual path

        addLabel(fieldsGrid, "Image Preview:", gbc, 0, row);
        lblImagePreview = new JLabel();
        lblImagePreview.setPreferredSize(new Dimension(320, 110));
        lblImagePreview.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));
        gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 3;
        fieldsGrid.add(lblImagePreview, gbc);
        gbc.gridwidth = 1;
        row++;

        // Row 6: Discount Type & Value
        addLabel(fieldsGrid, "Discount Type *:", gbc, 0, row);
        comboDiscountType = new JComboBox<>(new String[]{"Percentage", "Fixed Amount"});
        comboDiscountType.setFont(Theme.FONT_REGULAR);
        comboDiscountType.addActionListener(e -> {
            boolean isPct = "Percentage".equals(comboDiscountType.getSelectedItem());
            txtMaxDiscount.setEnabled(isPct);
        });
        gbc.gridx = 1; gbc.gridy = row;
        fieldsGrid.add(comboDiscountType, gbc);

        addLabel(fieldsGrid, "Discount Value *:", gbc, 2, row);
        txtDiscountValue = new JTextField("20");
        txtDiscountValue.setFont(Theme.FONT_BOLD);
        gbc.gridx = 3; gbc.gridy = row;
        fieldsGrid.add(txtDiscountValue, gbc);
        row++;

        // Row 7: Max Discount & Minimum Booking Amount
        addLabel(fieldsGrid, "Max Discount Cap (₹):", gbc, 0, row);
        txtMaxDiscount = new JTextField("5000");
        txtMaxDiscount.setFont(Theme.FONT_REGULAR);
        txtMaxDiscount.setToolTipText("Maximum discount ceiling for percentage offers. Leave empty or 0 for no limit.");
        gbc.gridx = 1; gbc.gridy = row;
        fieldsGrid.add(txtMaxDiscount, gbc);

        addLabel(fieldsGrid, "Min Booking (₹):", gbc, 2, row);
        txtMinBookingAmount = new JTextField("10000");
        txtMinBookingAmount.setFont(Theme.FONT_REGULAR);
        gbc.gridx = 3; gbc.gridy = row;
        fieldsGrid.add(txtMinBookingAmount, gbc);
        row++;

        // Row 8: Dates
        addLabel(fieldsGrid, "Start Date (yyyy-MM-dd) *:", gbc, 0, row);
        txtStartDate = new JTextField();
        txtStartDate.setFont(Theme.FONT_REGULAR);
        gbc.gridx = 1; gbc.gridy = row;
        fieldsGrid.add(txtStartDate, gbc);

        addLabel(fieldsGrid, "End Date (yyyy-MM-dd) *:", gbc, 2, row);
        txtEndDate = new JTextField();
        txtEndDate.setFont(Theme.FONT_REGULAR);
        gbc.gridx = 3; gbc.gridy = row;
        fieldsGrid.add(txtEndDate, gbc);
        row++;

        // Row 9: Applicable Package
        addLabel(fieldsGrid, "Applicable Package:", gbc, 0, row);
        comboPackage = new JComboBox<>();
        comboPackage.setFont(Theme.FONT_REGULAR);
        comboPackage.addItem("🌟 All Holiday Packages (Universal Offer)");
        List<Package> packages = packageDAO.getAllPackages();
        for (Package p : packages) {
            comboPackage.addItem(p);
        }
        gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 3;
        fieldsGrid.add(comboPackage, gbc);
        gbc.gridwidth = 1;
        row++;

        // Row 10: Usage Limit
        addLabel(fieldsGrid, "Usage Limit (Max Uses):", gbc, 0, row);
        txtUsageLimit = new JTextField("100");
        txtUsageLimit.setFont(Theme.FONT_REGULAR);
        txtUsageLimit.setToolTipText("Leave blank or 0 for unlimited uses");
        gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 3;
        fieldsGrid.add(txtUsageLimit, gbc);
        gbc.gridwidth = 1;
        row++;

        JScrollPane scrollForm = new JScrollPane(fieldsGrid);
        scrollForm.setBorder(BorderFactory.createEmptyBorder());
        scrollForm.getVerticalScrollBar().setUnitIncrement(16);
        formContainer.add(scrollForm, BorderLayout.CENTER);
        add(formContainer, BorderLayout.CENTER);

        // Bottom Actions
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        bottomBar.setBackground(Theme.BACKGROUND);
        bottomBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER));

        ModernButton btnSave = new ModernButton(existingOffer == null ? "💾 Save Festival Offer" : "💾 Update Offer", ModernButton.Style.PRIMARY);
        ModernButton btnReset = new ModernButton("🔄 Reset", ModernButton.Style.OUTLINE);
        ModernButton btnCancel = new ModernButton("❌ Cancel", ModernButton.Style.OUTLINE);

        btnSave.addActionListener(e -> saveAction());
        btnReset.addActionListener(e -> {
            if (existingOffer != null) populateFields(existingOffer);
            else setDefaults();
        });
        btnCancel.addActionListener(e -> dispose());

        bottomBar.add(btnReset);
        bottomBar.add(btnCancel);
        bottomBar.add(btnSave);
        add(bottomBar, BorderLayout.SOUTH);
    }

    private void addLabel(JPanel panel, String text, GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.weightx = 0.15;
        JLabel lbl = new JLabel(text);
        lbl.setFont(Theme.FONT_LABEL);
        lbl.setForeground(Theme.TEXT_MAIN);
        panel.add(lbl, gbc);
    }

    private void setDefaults() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long now = System.currentTimeMillis();
        txtStartDate.setText(sdf.format(new Date(now)));
        txtEndDate.setText(sdf.format(new Date(now + 30L * 86400000L))); // 30 days later

        txtOfferName.setText("Diwali Festive Bonanza");
        comboFestival.setSelectedItem("Diwali");
        txtDescription.setText("Celebrate the festival of lights with exclusive holiday discounts, luxury hotel stays, and festive sightseeing.");
        comboDiscountType.setSelectedItem("Percentage");
        txtDiscountValue.setText("20");
        txtMaxDiscount.setText("5000");
        txtMinBookingAmount.setText("15000");
        comboStatus.setSelectedItem("Active");
        txtUsageLimit.setText("100");
        updatePresetByFestival();
    }

    private void populateFields(FestivalOffer o) {
        txtOfferId.setText(String.valueOf(o.getOfferId()));
        txtOfferName.setText(o.getOfferName());
        comboFestival.setSelectedItem(o.getFestivalName());
        txtDescription.setText(o.getDescription());

        txtCustomImagePath.setText(o.getImagePath());
        // Select matching preset if available
        boolean foundPreset = false;
        for (Map.Entry<String, String> entry : FestivalImageUtil.PRESET_IMAGES.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(o.getImagePath())) {
                comboImagePreset.setSelectedItem(entry.getKey());
                foundPreset = true;
                break;
            }
        }
        if (!foundPreset) {
            comboImagePreset.setSelectedIndex(0);
        }

        comboDiscountType.setSelectedItem(o.getDiscountType());
        txtDiscountValue.setText(String.format("%.2f", o.getDiscountValue()));
        txtMaxDiscount.setText(o.getMaxDiscount() != null ? String.format("%.2f", o.getMaxDiscount()) : "");
        txtMinBookingAmount.setText(String.format("%.2f", o.getMinBookingAmount()));
        txtStartDate.setText(o.getStartDate() != null ? o.getStartDate().toString() : "");
        txtEndDate.setText(o.getEndDate() != null ? o.getEndDate().toString() : "");
        comboStatus.setSelectedItem(o.getStatus());
        txtUsageLimit.setText(o.getUsageLimit() != null ? String.valueOf(o.getUsageLimit()) : "");

        // Match package combo
        if (o.getApplicablePackageId() != null && o.getApplicablePackageId() > 0) {
            for (int i = 1; i < comboPackage.getItemCount(); i++) {
                Object item = comboPackage.getItemAt(i);
                if (item instanceof Package && ((Package) item).getPackageId() == o.getApplicablePackageId()) {
                    comboPackage.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            comboPackage.setSelectedIndex(0);
        }

        updateImagePreview();
    }

    private void updatePresetByFestival() {
        String festival = (String) comboFestival.getSelectedItem();
        if (festival == null) return;
        String f = festival.toLowerCase();

        for (Map.Entry<String, String> entry : FestivalImageUtil.PRESET_IMAGES.entrySet()) {
            if (entry.getKey().toLowerCase().contains(f) || entry.getValue().toLowerCase().contains(f)) {
                comboImagePreset.setSelectedItem(entry.getKey());
                txtCustomImagePath.setText(entry.getValue());
                break;
            }
        }
        updateImagePreview();
    }

    private void updateImagePreview() {
        String selectedPresetKey = (String) comboImagePreset.getSelectedItem();
        String path = FestivalImageUtil.PRESET_IMAGES.get(selectedPresetKey);
        if (txtCustomImagePath.getText() != null && !txtCustomImagePath.getText().trim().isEmpty() && !txtCustomImagePath.getText().equals(path)) {
            // custom path was browsed
            path = txtCustomImagePath.getText();
        } else if (path != null) {
            txtCustomImagePath.setText(path);
        }

        String fest = (String) comboFestival.getSelectedItem();
        lblImagePreview.setIcon(FestivalImageUtil.getFestivalImage(path, fest, 320, 110, 8));
    }

    private void browseCustomImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Festival Banner Image");
        chooser.setFileFilter(new FileNameExtensionFilter("Image Files (*.jpg, *.jpeg, *.png)", "jpg", "jpeg", "png"));
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            txtCustomImagePath.setText(selected.getAbsolutePath());
            lblImagePreview.setIcon(FestivalImageUtil.getFestivalImage(selected.getAbsolutePath(), (String) comboFestival.getSelectedItem(), 320, 110, 8));
        }
    }

    private void saveAction() {
        // Validations
        String name = txtOfferName.getText().trim();
        if (name.isEmpty()) {
            showError("Offer Name cannot be empty.");
            txtOfferName.requestFocus();
            return;
        }

        String festival = comboFestival.getSelectedItem() != null ? comboFestival.getSelectedItem().toString().trim() : "";
        if (festival.isEmpty()) {
            showError("Festival / Occasion name cannot be empty.");
            return;
        }

        String discType = (String) comboDiscountType.getSelectedItem();
        double discVal;
        try {
            discVal = Double.parseDouble(txtDiscountValue.getText().trim());
            if (discVal <= 0) {
                showError("Discount value must be greater than zero.");
                txtDiscountValue.requestFocus();
                return;
            }
            if ("Percentage".equals(discType) && discVal > 100.0) {
                showError("Percentage discount cannot exceed 100%.");
                txtDiscountValue.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid numeric discount value.");
            txtDiscountValue.requestFocus();
            return;
        }

        Double maxDisc = null;
        if (!txtMaxDiscount.getText().trim().isEmpty()) {
            try {
                double md = Double.parseDouble(txtMaxDiscount.getText().trim());
                if (md < 0) {
                    showError("Maximum discount limit cannot be negative.");
                    return;
                }
                if (md > 0) maxDisc = md;
            } catch (NumberFormatException e) {
                showError("Please enter a valid number for Maximum Discount Cap.");
                return;
            }
        }

        double minBooking = 0.0;
        if (!txtMinBookingAmount.getText().trim().isEmpty()) {
            try {
                minBooking = Double.parseDouble(txtMinBookingAmount.getText().trim());
                if (minBooking < 0) {
                    showError("Minimum booking amount cannot be negative.");
                    return;
                }
            } catch (NumberFormatException e) {
                showError("Please enter a valid number for Minimum Booking Amount.");
                return;
            }
        }

        Date startDate = parseDate(txtStartDate.getText().trim(), "Start Date");
        if (startDate == null) return;

        Date endDate = parseDate(txtEndDate.getText().trim(), "End Date");
        if (endDate == null) return;

        if (startDate.after(endDate)) {
            showError("Start Date (" + startDate + ") cannot be after End Date (" + endDate + ").");
            return;
        }

        Integer usageLimit = null;
        if (!txtUsageLimit.getText().trim().isEmpty()) {
            try {
                int ul = Integer.parseInt(txtUsageLimit.getText().trim());
                if (ul < 0) {
                    showError("Usage limit cannot be negative.");
                    return;
                }
                if (ul > 0) usageLimit = ul;
            } catch (NumberFormatException e) {
                showError("Please enter a valid whole number for Usage Limit.");
                return;
            }
        }

        // Package selection
        Integer pkgId = null;
        Object selPkg = comboPackage.getSelectedItem();
        if (selPkg instanceof Package) {
            pkgId = ((Package) selPkg).getPackageId();
        }

        String imgPath = txtCustomImagePath.getText().trim();
        if (imgPath.isEmpty()) {
            String selectedPresetKey = (String) comboImagePreset.getSelectedItem();
            imgPath = FestivalImageUtil.PRESET_IMAGES.get(selectedPresetKey);
        }

        String desc = txtDescription.getText().trim();
        String status = (String) comboStatus.getSelectedItem();

        // Assemble Offer model
        FestivalOffer o = existingOffer != null ? existingOffer : new FestivalOffer();
        o.setOfferName(name);
        o.setFestivalName(festival);
        o.setDescription(desc);
        o.setImagePath(imgPath);
        o.setDiscountType(discType);
        o.setDiscountValue(discVal);
        o.setMaxDiscount(maxDisc);
        o.setMinBookingAmount(minBooking);
        o.setStartDate(startDate);
        o.setEndDate(endDate);
        o.setApplicablePackageId(pkgId);
        o.setStatus(status);
        o.setUsageLimit(usageLimit);

        boolean ok;
        if (existingOffer == null) {
            ok = festivalOfferDAO.addOffer(o);
        } else {
            ok = festivalOfferDAO.updateOffer(o);
        }

        if (ok) {
            SoundManager.playSuccessSound();
            JOptionPane.showMessageDialog(this,
                    "Festival Offer '" + o.getOfferName() + "' " + (existingOffer == null ? "created" : "updated") + " successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            saved = true;
            dispose();
        } else {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Failed to save Festival Offer in database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Date parseDate(String str, String fieldName) {
        if (str.isEmpty()) {
            showError(fieldName + " is required.");
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            java.util.Date d = sdf.parse(str);
            return new Date(d.getTime());
        } catch (ParseException e) {
            showError("Invalid " + fieldName + "! Please use yyyy-MM-dd format (e.g. 2026-10-25).");
            return null;
        }
    }

    private void showError(String msg) {
        SoundManager.playErrorSound();
        JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }

    public boolean isSaved() {
        return saved;
    }
}
