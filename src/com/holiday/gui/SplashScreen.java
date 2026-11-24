package com.holiday.gui;

import com.holiday.gui.ui.Theme;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class SplashScreen extends JWindow {

    private JProgressBar progressBar;
    private JLabel statusLabel;

    public SplashScreen() {
        setSize(620, 380);
        setLocationRelativeTo(null);

        // Deep Teal background panel with subtle minimalist curves
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(Theme.PRIMARY);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Subtle background accents
                g2d.setColor(new Color(255, 255, 255, 12));
                g2d.fillOval(getWidth() - 140, -40, 260, 260);
                g2d.setColor(new Color(244, 162, 97, 25)); // Subtle Warm Orange accent glow
                g2d.fillOval(-60, getHeight() - 120, 220, 220);
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createLineBorder(Theme.ACCENT, 2));

        // Center Branding
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(null);

        JLabel iconLabel = new JLabel("✈️", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 52));
        iconLabel.setBounds(0, 35, 620, 60);
        centerPanel.add(iconLabel);

        JLabel titleLabel = new JLabel("TRIPZO", SwingConstants.CENTER);
        titleLabel.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 100, 620, 40);
        centerPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Travel & Holiday Package Booking System", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font(Theme.FONT_FAMILY, Font.PLAIN, 14));
        subtitleLabel.setForeground(Theme.ACCENT);
        subtitleLabel.setBounds(0, 142, 620, 25);
        centerPanel.add(subtitleLabel);

        // Bottom Progress Section
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new BorderLayout(0, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 45, 35, 45));

        statusLabel = new JLabel("Initializing travel management platform...", SwingConstants.CENTER);
        statusLabel.setFont(new Font(Theme.FONT_FAMILY, Font.ITALIC, 12));
        statusLabel.setForeground(new Color(0xED, 0xF2, 0xF7));

        progressBar = new JProgressBar();
        progressBar.setPreferredSize(new Dimension(530, 8));
        progressBar.setForeground(Theme.ACCENT); // Sand Gold progress
        progressBar.setBackground(new Color(0x1D, 0x5A, 0x6E));
        progressBar.setBorderPainted(false);

        bottomPanel.add(statusLabel, BorderLayout.NORTH);
        bottomPanel.add(progressBar, BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    public void startSplash() {
        setVisible(true);

        Timer timer = new Timer(25, null);
        timer.addActionListener(e -> {
            int value = progressBar.getValue();
            if (value < 100) {
                value += 2;
                progressBar.setValue(value);
                if (value == 25) {
                    statusLabel.setText("Connecting to MySQL Database...");
                } else if (value == 55) {
                    statusLabel.setText("Loading Package & Customer Modules...");
                } else if (value == 85) {
                    statusLabel.setText("Preparing Admin Dashboard...");
                }
            } else {
                timer.stop();
                dispose();
                SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
            }
        });
        timer.start();
    }
}
