package com.holiday.gui.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class StatusBadge extends JLabel {

    private Color bgColor;
    private Color fgColor;

    public StatusBadge(String status) {
        super(status, SwingConstants.CENTER);
        setFont(Theme.FONT_BADGE);
        setOpaque(false);
        setStatus(status);
        setPreferredSize(new Dimension(100, 24));
    }

    public void setStatus(String status) {
        setText(status != null ? status : "-");

        if (status == null) {
            bgColor = Theme.BORDER;
            fgColor = Theme.TEXT_MUTED;
            return;
        }

        String s = status.trim().toUpperCase();

        if (s.contains("INACTIVE") || s.contains("CANCEL") || s.contains("DELETE") || s.contains("FAILED") || s.contains("ERROR")) {
            bgColor = Theme.DANGER;
            fgColor = Color.WHITE;
        } else if (s.contains("CONFIRMED") || s.contains("PAID") || s.contains("COMPLETED") || s.contains("SUCCESS") || s.contains("ACTIVE")) {
            bgColor = Theme.SUCCESS;
            fgColor = Color.WHITE;
        } else if (s.contains("PENDING") || s.contains("PARTIAL") || s.contains("WAITING") || s.contains("EXPIRED")) {
            bgColor = Theme.WARNING;
            fgColor = Color.WHITE;
        } else if (s.contains("QUOTATION") || s.contains("CUSTOM")) {
            bgColor = Theme.ACCENT;
            fgColor = Color.WHITE;
        } else {
            bgColor = Theme.SECONDARY;
            fgColor = Theme.PRIMARY;
        }

        setForeground(fgColor);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(bgColor != null ? bgColor : Theme.PRIMARY);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

        g2.dispose();
        super.paintComponent(g);
    }

    // Static Cell Renderer helper for JTable
    public static DefaultTableCellRenderer getTableRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                String statusStr = value != null ? value.toString() : "";
                StatusBadge badge = new StatusBadge(statusStr);
                if (isSelected) {
                    badge.setOpaque(false);
                }
                return badge;
            }
        };
    }
}
