package com.holiday.gui;

import com.holiday.gui.ui.ModernButton;
import com.holiday.gui.ui.Theme;
import com.holiday.model.CustomTrip;
import com.holiday.util.SoundManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.print.PrinterException;
import java.sql.Date;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class QuotationDialog extends JDialog {

    private JTextArea txtQuotation;
    private CustomTrip trip;

    public QuotationDialog(Frame owner, CustomTrip trip) {
        super(owner, "Custom Holiday Quotation #" + (trip.getTripId() > 0 ? trip.getTripId() : "DRAFT"), true);
        this.trip = trip;

        setSize(620, 740);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        // Receipt Display Text Area
        txtQuotation = new JTextArea();
        txtQuotation.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtQuotation.setEditable(false);
        txtQuotation.setMargin(new java.awt.Insets(20, 25, 20, 25));
        txtQuotation.setBackground(Theme.CARD);
        txtQuotation.setForeground(Theme.TEXT_MAIN);
        txtQuotation.setCaretColor(Color.WHITE);

        generateQuotationText();

        JScrollPane scrollPane = new JScrollPane(txtQuotation);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Actions Panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Theme.BACKGROUND);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        ModernButton btnPrint = new ModernButton("🖨️ Print Quotation", ModernButton.Style.PRIMARY);
        btnPrint.addActionListener(e -> printQuotation());

        ModernButton btnClose = new ModernButton("Close", ModernButton.Style.OUTLINE);
        btnClose.addActionListener(e -> dispose());

        bottomPanel.add(btnPrint);
        bottomPanel.add(btnClose);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void generateQuotationText() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDate = sdf.format(new Date(System.currentTimeMillis()));

        StringBuilder sb = new StringBuilder();
        sb.append("==============================================================\n");
        sb.append("                         ✈ TRIPZO                             \n");
        sb.append("             OFFICIAL CUSTOM TRIP QUOTATION                  \n");
        sb.append("==============================================================\n");
        sb.append("Quotation Number : #").append(trip.getTripId() > 0 ? trip.getTripId() : "DRAFT").append("\n");
        sb.append("Issue Date       : ").append(currentDate).append("\n");
        sb.append("Status           : ").append(trip.getStatus() != null ? trip.getStatus() : "Quotation Generated").append("\n");
        sb.append("--------------------------------------------------------------\n");
        sb.append("CUSTOMER DETAILS:\n");
        sb.append("  Customer Name  : ").append(trip.getCustomerName() != null ? trip.getCustomerName() : "Valued Customer").append("\n");
        sb.append("--------------------------------------------------------------\n");
        sb.append("TRIP & ITINERARY DETAILS:\n");
        sb.append("  Destination    : ").append(trip.getDestination()).append("\n");
        sb.append("  Travel Date    : ").append(trip.getTravelDate()).append("\n");
        if (trip.getReturnDate() != null) {
            sb.append("  Return Date    : ").append(trip.getReturnDate()).append("\n");
        }
        sb.append("  Duration       : ").append(trip.getDuration()).append("\n");
        sb.append("  No. Travelers  : ").append(trip.getTravelers()).append(" Person(s)\n");
        sb.append("--------------------------------------------------------------\n");
        sb.append("ACCOMMODATION & SERVICES:\n");
        sb.append("  Accommodation  : ").append(trip.getHotelCategory()).append(" (").append(trip.getHotel() != null && !trip.getHotel().isEmpty() ? trip.getHotel() : "Standard").append(")\n");
        sb.append("  Transport      : ").append(trip.getTransport()).append("\n");
        sb.append("  Meal Plan      : ").append(trip.getMealPlan()).append("\n");
        sb.append("  Activities     : ").append(trip.getActivities() != null && !trip.getActivities().isEmpty() ? trip.getActivities() : "None Selected").append("\n");
        sb.append("  Add. Services  : ").append(trip.getAdditionalServices() != null && !trip.getAdditionalServices().isEmpty() ? trip.getAdditionalServices() : "Standard Assistance").append("\n");
        if (trip.getAdditionalReq() != null && !trip.getAdditionalReq().isEmpty()) {
            sb.append("  Special Notes  : ").append(trip.getAdditionalReq()).append("\n");
        }
        sb.append("--------------------------------------------------------------\n");
        sb.append("TRANSPARENT COST BREAKDOWN:\n");
        sb.append(String.format("  Accommodation Charges : ₹%,12.2f\n", trip.getHotelCost()));
        sb.append(String.format("  Transport Charges     : ₹%,12.2f\n", trip.getTransportCost()));
        sb.append(String.format("  Meal Plan             : ₹%,12.2f\n", trip.getMealCost()));
        sb.append(String.format("  Activities & Excursion: ₹%,12.2f\n", trip.getActivityCost()));
        sb.append(String.format("  Additional Services   : ₹%,12.2f\n", trip.getAdditionalServicesCost()));
        sb.append(String.format("  Service Charge        : ₹%,12.2f\n", trip.getServiceCharge()));
        sb.append("  ----------------------------------------------------------\n");
        double estimatedSubtotal = trip.getHotelCost() + trip.getTransportCost() + trip.getMealCost() + trip.getActivityCost() + trip.getAdditionalServicesCost() + trip.getServiceCharge();
        sb.append(String.format("  Estimated Subtotal    : ₹%,12.2f\n", estimatedSubtotal));
        sb.append(String.format("  Special Discount      : -₹%,11.2f\n", trip.getDiscount()));
        sb.append("  ==========================================================\n");
        sb.append(String.format("  ESTIMATED TOTAL COST  : ₹%,12.2f\n", trip.getFinalPrice()));
        sb.append(String.format("  PER PERSON COST       : ₹%,12.2f / Person\n", trip.getPerPersonCost()));
        sb.append("--------------------------------------------------------------\n");

        if (trip.getBudget() > 0) {
            if (trip.getFinalPrice() <= trip.getBudget()) {
                sb.append("BUDGET STATUS : ✓ Within Customer Budget (Budget: ₹").append(String.format("%,.2f", trip.getBudget())).append(")\n");
            } else {
                double over = trip.getFinalPrice() - trip.getBudget();
                sb.append("BUDGET STATUS : ⚠️ Exceeds Customer Budget by ₹").append(String.format("%,.2f", over)).append("\n");
            }
            sb.append("--------------------------------------------------------------\n");
        }

        sb.append("TERMS & CONDITIONS:\n");
        sb.append(" - This is an estimated quotation and final charges may vary\n");
        sb.append("   based on availability, season, taxes and booking conditions.\n");
        sb.append(" - Quoted price is valid for 7 business days from date of issue.\n");
        sb.append("==============================================================\n");
        sb.append("              Thank you for choosing Tripzo!                  \n");
        sb.append("==============================================================\n");

        txtQuotation.setText(sb.toString());
        txtQuotation.setCaretPosition(0);
    }

    private void printQuotation() {
        try {
            boolean complete = txtQuotation.print(
                new MessageFormat("Holiday Booking System - Quotation #" + (trip.getTripId() > 0 ? trip.getTripId() : "DRAFT")),
                new MessageFormat("Page {0}"),
                true, null, null, true
            );
            if (complete) {
                SoundManager.playSuccessSound();
                JOptionPane.showMessageDialog(this, "Quotation printed successfully.", "Print Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (PrinterException e) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Printing Error: " + e.getMessage(), "Print Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
