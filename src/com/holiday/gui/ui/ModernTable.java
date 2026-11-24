package com.holiday.gui.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class ModernTable {

    public static JTable createTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        styleTable(table);
        return table;
    }

    public static void styleTable(JTable table) {
        table.setFont(Theme.FONT_REGULAR);
        table.setRowHeight(34);
        table.setBackground(Theme.CARD);
        table.setForeground(Theme.TEXT_MAIN);
        table.setSelectionBackground(Theme.SECONDARY); // Light Aqua highlight
        table.setSelectionForeground(Theme.PRIMARY);   // Deep Ocean text
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = table.getTableHeader();
        header.setFont(Theme.FONT_BOLD);
        header.setBackground(Theme.PRIMARY); // Deep Ocean Header #164A5B
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 36));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER));

        // High-contrast alternating row cell renderer
        DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

                if (isSelected) {
                    c.setBackground(Theme.SECONDARY);
                    c.setForeground(Theme.PRIMARY);
                } else {
                    c.setBackground(row % 2 == 0 ? Theme.CARD : new Color(0xF4, 0xF8, 0xF8));
                    c.setForeground(Theme.TEXT_MAIN);
                }
                return c;
            }
        };
        table.setDefaultRenderer(Object.class, defaultRenderer);
    }

    public static JScrollPane createScrollPane(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Theme.CARD);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        return scrollPane;
    }

    public static JPanel createTablePanelWithEmptyState(JTable table, String emptyMessage) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JScrollPane scrollPane = createScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JLabel emptyLabel = new JLabel(emptyMessage, SwingConstants.CENTER);
        emptyLabel.setFont(Theme.FONT_SUBTITLE);
        emptyLabel.setForeground(Theme.TEXT_MUTED);

        // Dynamic empty check listener
        table.getModel().addTableModelListener(e -> {
            if (table.getModel().getRowCount() == 0) {
                scrollPane.setViewportView(emptyLabel);
            } else {
                scrollPane.setViewportView(table);
            }
        });

        if (table.getModel().getRowCount() == 0) {
            scrollPane.setViewportView(emptyLabel);
        }

        return panel;
    }

    public static void centerColumns(JTable table, int... columnIndices) {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                if (isSelected) {
                    c.setBackground(Theme.SECONDARY);
                    c.setForeground(Theme.PRIMARY);
                } else {
                    c.setBackground(row % 2 == 0 ? Theme.CARD : new Color(0xF4, 0xF8, 0xF8));
                    c.setForeground(Theme.TEXT_MAIN);
                }
                return c;
            }
        };
        for (int idx : columnIndices) {
            if (idx >= 0 && idx < table.getColumnCount()) {
                table.getColumnModel().getColumn(idx).setCellRenderer(centerRenderer);
            }
        }
    }
}
