package com.holiday.gui;

import com.holiday.dao.AdminDAO;
import com.holiday.gui.ui.ModernButton;
import com.holiday.gui.ui.Theme;
import com.holiday.model.Admin;
import com.holiday.util.SoundManager;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chkRemember;
    private ModernButton btnLogin;
    private ModernButton btnClear;
    private ModernButton btnExit;
    private AdminDAO adminDAO;

    public LoginFrame() {
        adminDAO = new AdminDAO();

        setTitle("Tripzo");
        setSize(960, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Root panel with Deep Ocean background
        JPanel rootPanel = new JPanel(new GridBagLayout());
        rootPanel.setBackground(Theme.PRIMARY);

        // Outer Container Card holding Left Hero + Right Form
        JPanel container = new JPanel(new GridLayout(1, 2, 0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Pure White Card Surface background
                g2.setColor(Theme.CARD);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                
                // Outer subtle border
                g2.setColor(Theme.BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
            }
        };
        container.setOpaque(false);
        container.setPreferredSize(new Dimension(880, 480));

        // LEFT SIDE: Travel Hero Panel
        TravelHeroPanel leftHero = new TravelHeroPanel();
        leftHero.setPreferredSize(new Dimension(440, 480));

        // RIGHT SIDE: Clean White Login Form Panel
        JPanel rightForm = new JPanel();
        rightForm.setOpaque(false);
        rightForm.setLayout(null);
        rightForm.setPreferredSize(new Dimension(440, 480));

        // 1. Brand Title Header
        JLabel lblBrandIcon = new JLabel("✈️", SwingConstants.LEFT);
        lblBrandIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        lblBrandIcon.setBounds(40, 30, 35, 35);
        rightForm.add(lblBrandIcon);

        JLabel lblBrandName = new JLabel("Tripzo", SwingConstants.LEFT);
        lblBrandName.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 26));
        lblBrandName.setForeground(Theme.PRIMARY); // Deep Ocean #164A5B
        lblBrandName.setBounds(78, 30, 200, 35);
        rightForm.add(lblBrandName);

        // 2. Welcome Section
        JLabel lblWelcome = new JLabel("Welcome Back", SwingConstants.LEFT);
        lblWelcome.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 20));
        lblWelcome.setForeground(Theme.PRIMARY);
        lblWelcome.setBounds(40, 75, 320, 28);
        rightForm.add(lblWelcome);

        JLabel lblSub = new JLabel("Please sign in to continue", SwingConstants.LEFT);
        lblSub.setFont(Theme.FONT_SUBTITLE);
        lblSub.setForeground(Theme.TEXT_MUTED); // Soft Teal #4A6B78
        lblSub.setBounds(40, 103, 320, 20);
        rightForm.add(lblSub);

        // 3. Username Field
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(Theme.FONT_LABEL);
        lblUser.setForeground(Theme.PRIMARY);
        lblUser.setBounds(40, 138, 320, 18);
        rightForm.add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setFont(Theme.FONT_REGULAR);
        txtUsername.setBackground(Theme.BACKGROUND); // Warm Cream #F7F3EA
        txtUsername.setForeground(Theme.PRIMARY);
        txtUsername.setCaretColor(Theme.PRIMARY);
        txtUsername.setBounds(40, 160, 340, 38);
        txtUsername.setBorder(Theme.BORDER_INPUT);
        rightForm.add(txtUsername);

        // 4. Password Field
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(Theme.FONT_LABEL);
        lblPass.setForeground(Theme.PRIMARY);
        lblPass.setBounds(40, 208, 320, 18);
        rightForm.add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setFont(Theme.FONT_REGULAR);
        txtPassword.setBackground(Theme.BACKGROUND);
        txtPassword.setForeground(Theme.PRIMARY);
        txtPassword.setCaretColor(Theme.PRIMARY);
        txtPassword.setBounds(40, 230, 340, 38);
        txtPassword.setBorder(Theme.BORDER_INPUT);
        rightForm.add(txtPassword);

        // 5. Remember Me & Forgot Password
        chkRemember = new JCheckBox("Remember Me");
        chkRemember.setFont(Theme.FONT_SMALL);
        chkRemember.setForeground(Theme.TEXT_MUTED);
        chkRemember.setOpaque(false);
        chkRemember.setFocusPainted(false);
        chkRemember.setBounds(37, 275, 140, 24);
        rightForm.add(chkRemember);

        JLabel lblForgot = new JLabel("Forgot Password?", SwingConstants.RIGHT);
        lblForgot.setFont(Theme.FONT_SMALL);
        lblForgot.setForeground(Theme.PRIMARY);
        lblForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblForgot.setBounds(230, 275, 150, 24);
        lblForgot.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(LoginFrame.this,
                    "Password Reset Notice:\n\nFor security reasons, please contact your Administrator to reset your Tripzo login credentials.",
                    "Forgot Password",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        rightForm.add(lblForgot);

        // 6. Prominent Sand Gold Login Button
        btnLogin = new ModernButton("LOGIN", ModernButton.Style.PRIMARY);
        btnLogin.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 14));
        btnLogin.setBounds(40, 312, 340, 42);
        rightForm.add(btnLogin);

        // 7. Secondary Action Buttons (Clear & Exit)
        btnClear = new ModernButton("Clear", ModernButton.Style.OUTLINE);
        btnClear.setBounds(40, 364, 162, 34);
        rightForm.add(btnClear);

        btnExit = new ModernButton("Exit", ModernButton.Style.DANGER);
        btnExit.setBounds(218, 364, 162, 34);
        rightForm.add(btnExit);

        // 8. Hint & Copyright Footer
        JLabel lblHint = new JLabel("Default Admin: admin / admin123", SwingConstants.CENTER);
        lblHint.setFont(Theme.FONT_SMALL);
        lblHint.setForeground(Theme.TEXT_MUTED);
        lblHint.setBounds(40, 412, 340, 18);
        rightForm.add(lblHint);

        JLabel lblCopyright = new JLabel("© 2026 Tripzo. All rights reserved.", SwingConstants.CENTER);
        lblCopyright.setFont(new Font(Theme.FONT_FAMILY, Font.PLAIN, 10));
        lblCopyright.setForeground(new Color(0x94, 0xA3, 0xB8));
        lblCopyright.setBounds(40, 435, 340, 16);
        rightForm.add(lblCopyright);

        container.add(leftHero);
        container.add(rightForm);

        rootPanel.add(container);
        setContentPane(rootPanel);

        // Event Handling
        btnLogin.addActionListener(e -> performLogin());
        txtPassword.addActionListener(e -> performLogin());
        txtUsername.addActionListener(e -> txtPassword.requestFocusInWindow());

        btnClear.addActionListener(e -> {
            txtUsername.setText("");
            txtPassword.setText("");
            txtUsername.requestFocusInWindow();
        });

        btnExit.addActionListener(e -> System.exit(0));
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this,
                "Please enter both Username and Password.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        Admin admin = adminDAO.authenticate(username, password);
        if (admin != null) {
            SoundManager.playSuccessSound();
            JOptionPane.showMessageDialog(this,
                "Welcome to Tripzo, " + admin.getUsername() + "!",
                "Login Successful",
                JOptionPane.INFORMATION_MESSAGE);

            dispose();
            SwingUtilities.invokeLater(() -> new MainFrame(admin).setVisible(true));
        } else {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this,
                "Invalid Username or Password! Please check your credentials.",
                "Login Failed",
                JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            txtPassword.requestFocusInWindow();
        }
    }

    /**
     * Custom Travel Hero Panel rendering scenic travel illustration background
     * (Mountains, Sky gradient, Clouds, Airplane with Flight Trail, Suitcase) in Coastal palette.
     */
    private static class TravelHeroPanel extends JPanel {
        private BufferedImage bgImage;

        public TravelHeroPanel() {
            setOpaque(false);
            loadBgImage();
        }

        private void loadBgImage() {
            try {
                // Try classpath resource first
                InputStream is = getClass().getResourceAsStream("/com/holiday/assets/travel_bg.jpg");
                if (is != null) {
                    bgImage = ImageIO.read(is);
                    is.close();
                } else {
                    // Try direct file path fallbacks
                    File[] candidateFiles = new File[]{
                        new File("src/com/holiday/assets/travel_bg.jpg"),
                        new File("bin/com/holiday/assets/travel_bg.jpg"),
                        new File("assets/travel_bg.jpg")
                    };
                    for (File f : candidateFiles) {
                        if (f.exists()) {
                            bgImage = ImageIO.read(f);
                            break;
                        }
                    }
                }
            } catch (Exception ignored) {
                bgImage = null;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            int width = getWidth();
            int height = getHeight();

            if (bgImage != null) {
                // Draw background image scaled to fit panel
                g2.drawImage(bgImage, 0, 0, width, height, null);
                
                // Subtle gradient overlay at bottom for smooth contrast
                GradientPaint bottomOverlay = new GradientPaint(
                    0, height * 0.6f, new Color(22, 74, 91, 0),
                    0, height, new Color(22, 74, 91, 90)
                );
                g2.setPaint(bottomOverlay);
                g2.fillRect(0, 0, width, height);
            } else {
                // Procedural Java 2D Vector Graphic Painting (Coastal Palette)
                // 1. Sky Gradient
                GradientPaint skyGradient = new GradientPaint(
                    0, 0, new Color(0x16, 0x4A, 0x5B), // Deep Ocean #164A5B
                    0, height * 0.75f, new Color(0xA8, 0xDA, 0xDC) // Light Aqua #A8DADC
                );
                g2.setPaint(skyGradient);
                g2.fillRect(0, 0, width, height);

                // 2. Sun Glow
                g2.setColor(new Color(0xE0, 0x9F, 0x3E, 140)); // Sand Gold #E09F3E
                g2.fillOval(width - 130, 50, 110, 110);
                g2.setColor(new Color(0xF7, 0xF3, 0xEA, 180));
                g2.fillOval(width - 115, 65, 80, 80);

                // 3. Clouds
                g2.setColor(new Color(255, 255, 255, 160));
                g2.fillOval(40, 60, 90, 30);
                g2.fillOval(65, 45, 60, 35);
                g2.fillOval(200, 90, 110, 35);
                g2.fillOval(235, 75, 70, 40);

                // 4. Layered Mountains
                // Background Mountain Range (Light Teal)
                g2.setColor(new Color(0x52, 0x95, 0xA4, 180));
                GeneralPath bgMtn = new GeneralPath();
                bgMtn.moveTo(0, height * 0.65f);
                bgMtn.lineTo(width * 0.25f, height * 0.40f);
                bgMtn.lineTo(width * 0.55f, height * 0.58f);
                bgMtn.lineTo(width * 0.85f, height * 0.38f);
                bgMtn.lineTo(width, height * 0.52f);
                bgMtn.lineTo(width, height);
                bgMtn.lineTo(0, height);
                bgMtn.closePath();
                g2.fill(bgMtn);

                // Midground Mountain Range (Ocean Teal)
                g2.setColor(new Color(0x27, 0x6E, 0x82, 220));
                GeneralPath midMtn = new GeneralPath();
                midMtn.moveTo(0, height * 0.72f);
                midMtn.lineTo(width * 0.35f, height * 0.48f);
                midMtn.lineTo(width * 0.65f, height * 0.68f);
                midMtn.lineTo(width, height * 0.46f);
                midMtn.lineTo(width, height);
                midMtn.lineTo(0, height);
                midMtn.closePath();
                g2.fill(midMtn);

                // Foreground Mountain Range (Deep Ocean #164A5B)
                g2.setColor(new Color(0x16, 0x4A, 0x5B));
                GeneralPath fgMtn = new GeneralPath();
                fgMtn.moveTo(0, height * 0.85f);
                fgMtn.lineTo(width * 0.20f, height * 0.55f);
                fgMtn.lineTo(width * 0.50f, height * 0.78f);
                fgMtn.lineTo(width * 0.80f, height * 0.60f);
                fgMtn.lineTo(width, height * 0.75f);
                fgMtn.lineTo(width, height);
                fgMtn.lineTo(0, height);
                fgMtn.closePath();
                g2.fill(fgMtn);

                // Snow caps / highlights
                g2.setColor(new Color(0xF7, 0xF3, 0xEA, 200));
                GeneralPath snow1 = new GeneralPath();
                snow1.moveTo(width * 0.20f, height * 0.55f);
                snow1.lineTo(width * 0.16f, height * 0.60f);
                snow1.lineTo(width * 0.24f, height * 0.61f);
                snow1.closePath();
                g2.fill(snow1);

                // 5. Airplane Flight Trail (Dotted Curve in Sand Gold #E09F3E)
                g2.setColor(new Color(0xE0, 0x9F, 0x3E));
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, new float[]{6.0f, 6.0f}, 0.0f));
                g2.drawArc(-30, 40, width * 2, height, 80, 50);

                // Flying Airplane Icon
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
                g2.drawString("✈️", (int) (width * 0.65f), (int) (height * 0.28f));

                // 6. Travel Suitcase / Luggage at Bottom Right
                int sx = (int) (width * 0.72f);
                int sy = (int) (height * 0.68f);
                g2.setColor(new Color(0xE0, 0x9F, 0x3E)); // Sand Gold Body
                g2.fillRoundRect(sx, sy, 70, 95, 12, 12);
                g2.setColor(new Color(0x16, 0x4A, 0x5B)); // Suitcase Trim & Straps
                g2.drawRoundRect(sx, sy, 70, 95, 12, 12);
                g2.fillRect(sx + 10, sy, 6, 95);
                g2.fillRect(sx + 54, sy, 6, 95);
                // Handle
                g2.setStroke(new BasicStroke(3.0f));
                g2.drawRoundRect(sx + 22, sy - 18, 26, 20, 6, 6);
                // Travel Stickers
                g2.setColor(new Color(0xA8, 0xDA, 0xDC));
                g2.fillOval(sx + 24, sy + 30, 22, 22);
                g2.setColor(new Color(0xFF, 0xFF, 0xFF));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 8));
                g2.drawString("TRIPZO", sx + 26, sy + 44);
            }

            // Left Hero Branding Text Overlay
            g2.setColor(new Color(255, 255, 255, 230));
            g2.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 22));
            g2.drawString("EXPLORE THE WORLD", 30, height - 90);

            g2.setColor(Theme.ACCENT); // Sand Gold #E09F3E
            g2.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 15));
            g2.drawString("Your Journey Begins with Tripzo", 30, height - 65);

            g2.setColor(new Color(0xF7, 0xF3, 0xEA, 200));
            g2.setFont(new Font(Theme.FONT_FAMILY, Font.PLAIN, 12));
            g2.drawString("Manage Packages, Bookings & Destinations", 30, height - 42);

            g2.dispose();
        }
    }
}
