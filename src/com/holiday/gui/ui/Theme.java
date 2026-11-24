package com.holiday.gui.ui;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

public class Theme {

    // --- NEW COASTAL TRAVEL COLOR PALETTE ---
    public static final Color PRIMARY = new Color(0x16, 0x4A, 0x5B);       // Primary Deep Ocean #164A5B
    public static final Color SECONDARY = new Color(0xA8, 0xDA, 0xDC);     // Light Aqua #A8DADC
    public static final Color ACCENT = new Color(0xE0, 0x9F, 0x3E);        // Accent Sand Gold #E09F3E
    public static final Color BACKGROUND = new Color(0xF7, 0xF3, 0xEA);    // Main Warm Cream Background #F7F3EA
    public static final Color CARD = new Color(0xFF, 0xFF, 0xFF);          // White Card Surface #FFFFFF
    public static final Color SUCCESS = new Color(0x2E, 0x7D, 0x32);       // Confirmed / Paid Green
    public static final Color WARNING = new Color(0xD9, 0x77, 0x06);       // Pending Amber
    public static final Color DANGER = new Color(0xDC, 0x26, 0x26);        // Cancelled Red
    public static final Color TEXT_MAIN = new Color(0x16, 0x4A, 0x5B);     // Deep Ocean Primary Text #164A5B
    public static final Color TEXT_MUTED = new Color(0x4A, 0x6B, 0x78);    // Soft Neutral Dark Slate-Teal Text
    public static final Color BORDER = new Color(0xD2, 0xDC, 0xDD);        // Soft Light Border
    public static final Color HOVER_BG = new Color(0xBE, 0xE3, 0xE5);      // Hover Light Aqua Tint

    // --- ANALYTICS CHART PALETTE ---
    public static final Color CHART_1 = new Color(0x16, 0x4A, 0x5B);       // #164A5B
    public static final Color CHART_2 = new Color(0xE0, 0x9F, 0x3E);       // #E09F3E
    public static final Color CHART_3 = new Color(0xA8, 0xDA, 0xDC);       // #A8DADC
    public static final Color CHART_4 = new Color(0x6F, 0xA8, 0xB8);       // #6FA8B8
    public static final Color CHART_5 = new Color(0xD9, 0xC6, 0xA5);       // #D9C6A5

    // --- TYPOGRAPHY (Segoe UI / Inter) ---
    public static final String FONT_FAMILY = "Segoe UI";

    public static final Font FONT_HEADER = new Font(FONT_FAMILY, Font.BOLD, 22);
    public static final Font FONT_TITLE = new Font(FONT_FAMILY, Font.BOLD, 16);
    public static final Font FONT_SUBTITLE = new Font(FONT_FAMILY, Font.PLAIN, 13);
    public static final Font FONT_LABEL = new Font(FONT_FAMILY, Font.BOLD, 12);
    public static final Font FONT_REGULAR = new Font(FONT_FAMILY, Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font(FONT_FAMILY, Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font(FONT_FAMILY, Font.PLAIN, 11);
    public static final Font FONT_BADGE = new Font(FONT_FAMILY, Font.BOLD, 11);

    // --- BORDERS & SPACING ---
    public static final Border BORDER_CARD = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(BORDER, 1),
        BorderFactory.createEmptyBorder(15, 15, 15, 15)
    );

    public static final Border BORDER_INPUT = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(BORDER, 1),
        BorderFactory.createEmptyBorder(6, 10, 6, 10)
    );

    public static final Border BORDER_INPUT_FOCUS = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(PRIMARY, 1),
        BorderFactory.createEmptyBorder(6, 10, 6, 10)
    );
}
