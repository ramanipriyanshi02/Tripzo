package com.holiday.gui;

import com.holiday.dao.AdminDAO;
import com.holiday.db.DBConnection;
import com.holiday.gui.ui.CardPanel;
import com.holiday.gui.ui.ModernButton;
import com.holiday.gui.ui.Theme;
import com.holiday.model.Admin;
import com.holiday.util.SoundManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class SettingsPanel extends JPanel {

    private Admin currentAdmin;
    private MainFrame mainFrame;
    private AdminDAO adminDAO;

    // Password Fields
    private JPasswordField txtCurrentPassword;
    private JPasswordField txtNewPassword;
    private JPasswordField txtConfirmPassword;

    // DB Settings Fields
    private JTextField txtDbHost;
    private JTextField txtDbPort;
    private JTextField txtDbUser;
    private JPasswordField txtDbPassword;

    public SettingsPanel(Admin admin, MainFrame mainFrame) {
        this.currentAdmin = admin;
        this.mainFrame = mainFrame;
        this.adminDAO = new AdminDAO();

        setLayout(new GridLayout(1, 2, 20, 0));
        setOpaque(false);

        // Left Card: Admin Security Settings
        JPanel passCard = createPasswordCard();
        add(passCard);

        // Right Card: Database Connection Configuration & App Actions
        JPanel dbCard = createDatabaseCard();
        add(dbCard);
    }

    private JPanel createPasswordCard() {
        CardPanel card = new CardPanel(Theme.PRIMARY);
        card.setLayout(new BorderLayout(0, 15));

        JLabel title = new JLabel("🔒 Admin Password Settings");
        title.setFont(Theme.FONT_HEADER);
        title.setForeground(Theme.TEXT_MAIN);
        card.add(title, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        // Current Password
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.4;
        JLabel lblCurr = new JLabel("Current Password *:");
        lblCurr.setFont(Theme.FONT_LABEL);
        lblCurr.setForeground(Theme.TEXT_MAIN);
        fieldsPanel.add(lblCurr, gbc);

        gbc.gridx = 1; gbc.weightx = 0.6;
        txtCurrentPassword = new JPasswordField();
        txtCurrentPassword.setFont(Theme.FONT_REGULAR);
        txtCurrentPassword.setBackground(Theme.BACKGROUND);
        txtCurrentPassword.setForeground(Theme.PRIMARY);
        txtCurrentPassword.setCaretColor(Theme.PRIMARY);
        fieldsPanel.add(txtCurrentPassword, gbc);
        row++;

        // New Password
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.4;
        JLabel lblNew = new JLabel("New Password *:");
        lblNew.setFont(Theme.FONT_LABEL);
        lblNew.setForeground(Theme.TEXT_MAIN);
        fieldsPanel.add(lblNew, gbc);

        gbc.gridx = 1; gbc.weightx = 0.6;
        txtNewPassword = new JPasswordField();
        txtNewPassword.setFont(Theme.FONT_REGULAR);
        txtNewPassword.setBackground(Theme.BACKGROUND);
        txtNewPassword.setForeground(Theme.PRIMARY);
        txtNewPassword.setCaretColor(Theme.PRIMARY);
        fieldsPanel.add(txtNewPassword, gbc);
        row++;

        // Confirm Password
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.4;
        JLabel lblConf = new JLabel("Confirm New Password *:");
        lblConf.setFont(Theme.FONT_LABEL);
        lblConf.setForeground(Theme.TEXT_MAIN);
        fieldsPanel.add(lblConf, gbc);

        gbc.gridx = 1; gbc.weightx = 0.6;
        txtConfirmPassword = new JPasswordField();
        txtConfirmPassword.setFont(Theme.FONT_REGULAR);
        txtConfirmPassword.setBackground(Theme.BACKGROUND);
        txtConfirmPassword.setForeground(Theme.PRIMARY);
        txtConfirmPassword.setCaretColor(Theme.PRIMARY);
        fieldsPanel.add(txtConfirmPassword, gbc);
        row++;

        card.add(fieldsPanel, BorderLayout.CENTER);

        // Update Button
        ModernButton btnUpdatePassword = new ModernButton("🔑 Change Password", ModernButton.Style.PRIMARY);
        btnUpdatePassword.setPreferredSize(new Dimension(card.getWidth(), 42));
        btnUpdatePassword.addActionListener(e -> updatePasswordAction());

        card.add(btnUpdatePassword, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createDatabaseCard() {
        CardPanel card = new CardPanel(Theme.SECONDARY);
        card.setLayout(new BorderLayout(0, 15));

        JLabel title = new JLabel("⚙️ System & Database Configuration");
        title.setFont(Theme.FONT_HEADER);
        title.setForeground(Theme.TEXT_MAIN);
        card.add(title, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        txtDbHost = addFormField(fieldsPanel, "MySQL Host:", gbc, row++);
        txtDbHost.setText("localhost");

        txtDbPort = addFormField(fieldsPanel, "Port:", gbc, row++);
        txtDbPort.setText("3306");

        txtDbUser = addFormField(fieldsPanel, "Username:", gbc, row++);
        txtDbUser.setText(DBConnection.getDbUser());

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.4;
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(Theme.FONT_LABEL);
        lblPass.setForeground(Theme.TEXT_MAIN);
        fieldsPanel.add(lblPass, gbc);

        gbc.gridx = 1; gbc.weightx = 0.6;
        txtDbPassword = new JPasswordField(DBConnection.getDbPassword());
        txtDbPassword.setFont(Theme.FONT_REGULAR);
        txtDbPassword.setBackground(Theme.BACKGROUND);
        txtDbPassword.setForeground(Theme.PRIMARY);
        txtDbPassword.setCaretColor(Theme.PRIMARY);
        fieldsPanel.add(txtDbPassword, gbc);
        row++;

        card.add(fieldsPanel, BorderLayout.CENTER);

        // Actions Bottom Grid
        JPanel btnGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        btnGrid.setOpaque(false);

        ModernButton btnTestDb = new ModernButton("🔌 Test DB Connection", ModernButton.Style.SUCCESS);
        btnTestDb.addActionListener(e -> testDbConnection());

        ModernButton btnApplyDb = new ModernButton("💾 Apply DB Config", ModernButton.Style.SECONDARY);
        btnApplyDb.addActionListener(e -> applyDbConfig());

        ModernButton btnLogout = new ModernButton("🚪 Logout Admin", ModernButton.Style.ACCENT_ORANGE);
        btnLogout.addActionListener(e -> mainFrame.handleLogout());

        ModernButton btnExit = new ModernButton("❌ Exit System", ModernButton.Style.DANGER);
        btnExit.addActionListener(e -> System.exit(0));

        btnGrid.add(btnTestDb);
        btnGrid.add(btnApplyDb);
        btnGrid.add(btnLogout);
        btnGrid.add(btnExit);

        card.add(btnGrid, BorderLayout.SOUTH);

        return card;
    }

    private JTextField addFormField(JPanel panel, String labelText, GridBagConstraints gbc, int row) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.4;
        JLabel label = new JLabel(labelText);
        label.setFont(Theme.FONT_LABEL);
        label.setForeground(Theme.TEXT_MAIN);
        panel.add(label, gbc);

        gbc.gridx = 1; gbc.weightx = 0.6;
        JTextField field = new JTextField();
        field.setFont(Theme.FONT_REGULAR);
        field.setBackground(Theme.BACKGROUND);
        field.setForeground(Theme.PRIMARY);
        field.setCaretColor(Theme.PRIMARY);
        panel.add(field, gbc);

        return field;
    }

    private void updatePasswordAction() {
        String currPass = new String(txtCurrentPassword.getPassword()).trim();
        String newPass = new String(txtNewPassword.getPassword()).trim();
        String confPass = new String(txtConfirmPassword.getPassword()).trim();

        if (currPass.isEmpty() || newPass.isEmpty() || confPass.isEmpty()) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "All password fields are required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!currPass.equals(currentAdmin.getPassword())) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Current Password is incorrect!", "Authentication Error", JOptionPane.ERROR_MESSAGE);
            txtCurrentPassword.setText("");
            txtCurrentPassword.requestFocusInWindow();
            return;
        }

        if (!newPass.equals(confPass)) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "New Password and Confirm Password do not match!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtConfirmPassword.setText("");
            txtConfirmPassword.requestFocusInWindow();
            return;
        }

        if (adminDAO.updatePassword(currentAdmin.getAdminId(), newPass)) {
            currentAdmin.setPassword(newPass);
            SoundManager.playSuccessSound();
            JOptionPane.showMessageDialog(this, "Admin Password changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
        } else {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Failed to update Password in database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void testDbConnection() {
        if (DBConnection.testConnection()) {
            SoundManager.playSuccessSound();
            JOptionPane.showMessageDialog(this, "Connection to MySQL Database successful!", "DB Test Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Could not connect to MySQL database! Please check if MySQL service is running on 3306.", "DB Test Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyDbConfig() {
        String host = txtDbHost.getText().trim();
        String port = txtDbPort.getText().trim();
        String user = txtDbUser.getText().trim();
        String pass = new String(txtDbPassword.getPassword()).trim();

        DBConnection.setCredentials(host, port, user, pass);
        testDbConnection();
    }

    public void clearForm() {
        txtCurrentPassword.setText("");
        txtNewPassword.setText("");
        txtConfirmPassword.setText("");
    }
}
