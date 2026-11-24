package com.holiday.gui;

import com.holiday.gui.ui.ModernButton;
import com.holiday.gui.ui.Theme;
import com.holiday.model.Booking;
import com.holiday.model.Payment;
import com.holiday.util.ReceiptPdfExporter;
import com.holiday.util.SoundManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.print.PrinterException;
import java.io.File;
import java.sql.Date;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ReceiptDialog extends JDialog {

    private JTextArea txtReceipt;
    private Booking booking;
    private Payment payment;

    public ReceiptDialog(Frame owner, Booking booking, Payment payment) {
        super(owner, "Booking & Payment Receipt", true);
        this.booking = booking;
        this.payment = payment;

        setSize(600, 720);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        // Receipt Display Text Area
        txtReceipt = new JTextArea();
        txtReceipt.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtReceipt.setEditable(false);
        txtReceipt.setMargin(new java.awt.Insets(18, 22, 18, 22));
        txtReceipt.setBackground(Theme.CARD);
        txtReceipt.setForeground(Theme.TEXT_MAIN);
        txtReceipt.setCaretColor(Color.WHITE);

        generateReceiptText();

        JScrollPane scrollPane = new JScrollPane(txtReceipt);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Actions Panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Theme.BACKGROUND);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        ModernButton btnExportPdf = new ModernButton("📄 Export PDF Receipt", ModernButton.Style.SUCCESS);
        btnExportPdf.addActionListener(e -> exportPdfReceipt());

        ModernButton btnPrint = new ModernButton("🖨️ Print Receipt", ModernButton.Style.PRIMARY);
        btnPrint.addActionListener(e -> printReceipt());

        ModernButton btnClose = new ModernButton("Close", ModernButton.Style.OUTLINE);
        btnClose.addActionListener(e -> dispose());

        bottomPanel.add(btnExportPdf);
        bottomPanel.add(btnPrint);
        bottomPanel.add(btnClose);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void generateReceiptText() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDate = sdf.format(new Date(System.currentTimeMillis()));

        StringBuilder sb = new StringBuilder();
        sb.append("========================================================\n");
        sb.append("                         TRIPZO                         \n");
        sb.append("             OFFICIAL BOOKING RECEIPT                 \n");
        sb.append("========================================================\n");
        sb.append("Receipt Date  : ").append(currentDate).append("\n");
        sb.append("Booking ID    : #").append(booking.getBookingId()).append("\n");
        sb.append("Booking Status: ").append(booking.getBookingStatus()).append("\n");
        sb.append("--------------------------------------------------------\n");
        sb.append("CUSTOMER DETAILS:\n");
        sb.append("  Customer Name : ").append(booking.getCustomerName()).append("\n");
        sb.append("  Phone Number  : ").append(booking.getCustomerPhone() != null ? booking.getCustomerPhone() : "N/A").append("\n");
        sb.append("--------------------------------------------------------\n");
        sb.append("PACKAGE DETAILS:\n");
        sb.append("  Package Name  : ").append(booking.getPackageName()).append("\n");
        sb.append("  Destination   : ").append(booking.getDestination()).append("\n");
        sb.append("  Duration      : ").append(booking.getDuration() != null ? booking.getDuration() : "N/A").append("\n");
        sb.append("  Unit Rate     : ₹").append(String.format("%.2f", booking.getPackagePrice())).append("\n");
        sb.append("--------------------------------------------------------\n");
        sb.append("BOOKING & FINANCIAL SUMMARY:\n");
        sb.append("  Travel Date     : ").append(booking.getTravelDate()).append("\n");
        sb.append("  No. Travelers   : ").append(booking.getTravelers()).append("\n");
        if (booking.getDiscountAmount() > 0) {
            double orig = booking.getOriginalAmount() != null ? booking.getOriginalAmount() : (booking.getTotalAmount() + booking.getDiscountAmount());
            sb.append("  Original Amount : ₹").append(String.format("%.2f", orig)).append("\n");
            sb.append("  Festival Offer  : ").append(booking.getOfferName() != null ? booking.getOfferName() : "Festival Special Offer").append("\n");
            sb.append("  Festival Discount: -₹").append(String.format("%.2f", booking.getDiscountAmount())).append("\n");
            sb.append("  Final Payable   : ₹").append(String.format("%.2f", booking.getTotalAmount())).append("\n");
        } else {
            sb.append("  Total Package   : ₹").append(String.format("%.2f", booking.getTotalAmount())).append("\n");
        }
        sb.append("--------------------------------------------------------\n");
        sb.append("PAYMENT & SETTLEMENT:\n");

        double amountPaid = 0.0;
        if (payment != null) {
            amountPaid = payment.getAmount();
            sb.append("  Payment ID      : #").append(payment.getPaymentId()).append("\n");
            sb.append("  Payment Method  : ").append(payment.getPaymentMethod()).append("\n");
            sb.append("  Payment Status  : ").append(payment.getPaymentStatus()).append("\n");
            sb.append("  Payment Date    : ").append(payment.getPaymentDate() != null ? payment.getPaymentDate() : "N/A").append("\n");
            sb.append("  Amount Paid     : ₹").append(String.format("%.2f", amountPaid)).append("\n");
        } else {
            sb.append("  Payment Status  : Pending (No Payment Recorded Yet)\n");
            sb.append("  Payment Method  : N/A\n");
            sb.append("  Amount Paid     : ₹0.00\n");
        }

        double remainingBalance = Math.max(0.0, booking.getTotalAmount() - amountPaid);
        sb.append("  Remaining Balance: ₹").append(String.format("%.2f", remainingBalance)).append("\n");

        sb.append("========================================================\n");
        sb.append("              Thank you for choosing Tripzo!            \n");
        sb.append("========================================================\n");

        txtReceipt.setText(sb.toString());
        txtReceipt.setCaretPosition(0);
    }

    private void exportPdfReceipt() {
        try {
            File pdfFile = ReceiptPdfExporter.exportBookingReceiptPdf(booking, payment, null);
            SoundManager.playSuccessSound();
            JOptionPane.showMessageDialog(this,
                "Vector PDF Receipt generated successfully!\nSaved to: " + pdfFile.getAbsolutePath(),
                "PDF Export Success",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this,
                "Failed to export PDF Receipt: " + e.getMessage(),
                "PDF Export Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void printReceipt() {
        try {
            boolean complete = txtReceipt.print(
                new MessageFormat("Holiday Booking System - Receipt #" + booking.getBookingId()),
                new MessageFormat("Page {0}"),
                true, null, null, true
            );
            if (complete) {
                SoundManager.playSuccessSound();
                JOptionPane.showMessageDialog(this, "Receipt printing initiated successfully.", "Print Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (PrinterException e) {
            SoundManager.playErrorSound();
            JOptionPane.showMessageDialog(this, "Printing Error: " + e.getMessage(), "Print Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
