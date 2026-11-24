package com.holiday.gui.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class CardPanel extends JPanel {

    private Color accentColor = null;
    private int cornerRadius = 12;

    public CardPanel() {
        this(null);
    }

    public CardPanel(Color accentColor) {
        this.accentColor = accentColor;
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    }

    public CardPanel(int padding) {
        this.accentColor = null;
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
    }

    public void setAccentColor(Color accentColor) {
        this.accentColor = accentColor;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Dark card surface background
        g2.setColor(Theme.CARD);
        g2.fillRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);

        // Border line
        g2.setColor(Theme.BORDER);
        g2.drawRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);

        // Optional top accent strip
        if (accentColor != null) {
            g2.setColor(accentColor);
            g2.fillRoundRect(0, 0, width - 1, 5, cornerRadius, cornerRadius);
            g2.fillRect(0, 3, width - 1, 2);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
