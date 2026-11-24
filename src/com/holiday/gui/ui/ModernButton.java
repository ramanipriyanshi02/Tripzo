package com.holiday.gui.ui;

import com.holiday.util.SoundManager;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;

public class ModernButton extends JButton {

    public enum Style {
        PRIMARY,
        SECONDARY,
        ACCENT_ORANGE,
        SUCCESS,
        DANGER,
        OUTLINE
    }

    private Color normalBg;
    private Color hoverBg;
    private Color textColor;
    private boolean isHovered = false;
    private Style style;

    public ModernButton(String text, Style style) {
        super(text);
        this.style = style;
        setupStyle();

        setFont(Theme.FONT_BOLD);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    SoundManager.playClickSound();
                }
            }
        });
    }

    public ModernButton(String text) {
        this(text, Style.PRIMARY);
    }

    private void setupStyle() {
        switch (style) {
            case PRIMARY:
            case ACCENT_ORANGE:
                normalBg = Theme.ACCENT; // Sand Gold #E09F3E for main action buttons
                hoverBg = new Color(0xC7, 0x87, 0x2A);
                textColor = Color.WHITE;
                break;
            case SECONDARY:
                normalBg = Theme.PRIMARY; // Deep Ocean #164A5B
                hoverBg = new Color(0x10, 0x36, 0x43);
                textColor = Color.WHITE;
                break;
            case SUCCESS:
                normalBg = Theme.SUCCESS;
                hoverBg = Theme.SUCCESS.darker();
                textColor = Color.WHITE;
                break;
            case DANGER:
                normalBg = Theme.DANGER;
                hoverBg = Theme.DANGER.darker();
                textColor = Color.WHITE;
                break;
            case OUTLINE:
                normalBg = Theme.CARD; // White Card Background
                hoverBg = new Color(0xEA, 0xEF, 0xEF);
                textColor = Theme.PRIMARY; // Deep Ocean Text
                break;
        }
        setForeground(textColor);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        Color bg = isHovered ? hoverBg : normalBg;

        g2.setColor(bg);
        g2.fillRoundRect(0, 0, width, height, 10, 10);

        if (style == Style.OUTLINE) {
            g2.setColor(Theme.BORDER);
            g2.drawRoundRect(0, 0, width - 1, height - 1, 10, 10);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
