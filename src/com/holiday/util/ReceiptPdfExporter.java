package com.holiday.util;

import com.holiday.model.Booking;
import com.holiday.model.Payment;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReceiptPdfExporter {

    public static File exportBookingReceiptPdf(Booking booking, Payment payment, File targetFile) throws Exception {
        if (targetFile == null) {
            File dir = new File("receipts");
            if (!dir.exists()) dir.mkdirs();
            targetFile = new File(dir, "Receipt_Booking_" + booking.getBookingId() + ".pdf");
        }

        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(targetFile));
        document.open();

        // Fonts
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.WHITE);
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(168, 218, 220));
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, new Color(0x16, 0x4A, 0x5B));
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(0x16, 0x4A, 0x5B));
        Font boldCellFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, new Color(0x16, 0x4A, 0x5B));
        Font stampFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new Color(46, 125, 50));

        // 1. Header Banner
        PdfPTable headerTable = new PdfPTable(1);
        headerTable.setWidthPercentage(100);
        PdfPCell headerCell = new PdfPCell();
        headerCell.setBackgroundColor(new Color(0x16, 0x4A, 0x5B));
        headerCell.setPadding(18);
        headerCell.setBorder(PdfPCell.NO_BORDER);

        Paragraph p1 = new Paragraph("TRIPZO", titleFont);
        p1.setAlignment(Element.ALIGN_CENTER);
        Paragraph p2 = new Paragraph("Official Booking & Payment Billing Receipt", subtitleFont);
        p2.setAlignment(Element.ALIGN_CENTER);

        headerCell.addElement(p1);
        headerCell.addElement(p2);
        headerTable.addCell(headerCell);
        document.add(headerTable);

        document.add(new Paragraph(" "));

        // 2. Receipt Meta Info Grid
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDate = sdf.format(new Date());

        PdfPTable metaTable = new PdfPTable(2);
        metaTable.setWidthPercentage(100);
        metaTable.setWidths(new float[]{1, 1});

        PdfPCell leftMeta = new PdfPCell(new Phrase("Receipt No: REC-" + System.currentTimeMillis() % 1000000, boldCellFont));
        leftMeta.setBorder(PdfPCell.NO_BORDER);
        PdfPCell rightMeta = new PdfPCell(new Phrase("Generated Date: " + currentDate, cellFont));
        rightMeta.setHorizontalAlignment(Element.ALIGN_RIGHT);
        rightMeta.setBorder(PdfPCell.NO_BORDER);

        metaTable.addCell(leftMeta);
        metaTable.addCell(rightMeta);
        document.add(metaTable);

        document.add(new Paragraph(" "));

        // 3. Customer & Booking Overview Section
        document.add(new Paragraph("Customer & Booking Summary", sectionFont));
        document.add(new Paragraph(" "));

        PdfPTable overviewTable = new PdfPTable(4);
        overviewTable.setWidthPercentage(100);
        overviewTable.setWidths(new float[]{2, 3, 2, 3});

        addTableRow(overviewTable, "Booking ID:", "#" + booking.getBookingId(), "Booking Status:", booking.getBookingStatus(), boldCellFont, cellFont);
        addTableRow(overviewTable, "Customer Name:", booking.getCustomerName(), "Customer Phone:", booking.getCustomerPhone() != null ? booking.getCustomerPhone() : "N/A", boldCellFont, cellFont);
        addTableRow(overviewTable, "Package Name:", booking.getPackageName(), "Destination:", booking.getDestination(), boldCellFont, cellFont);
        addTableRow(overviewTable, "Travel Date:", booking.getTravelDate() != null ? booking.getTravelDate().toString() : "N/A", "No. Travelers:", booking.getTravelers() + " Person(s)", boldCellFont, cellFont);

        document.add(overviewTable);
        document.add(new Paragraph(" "));

        // 4. Itemized Pricing Table
        document.add(new Paragraph("Itemized Invoice Details", sectionFont));
        document.add(new Paragraph(" "));

        PdfPTable invoiceTable = new PdfPTable(4);
        invoiceTable.setWidthPercentage(100);
        invoiceTable.setWidths(new float[]{4, 2, 2, 2});

        // Header
        addInvoiceHeader(invoiceTable, "Description", headerFont);
        addInvoiceHeader(invoiceTable, "Unit Rate (₹)", headerFont);
        addInvoiceHeader(invoiceTable, "Quantity / Duration", headerFont);
        addInvoiceHeader(invoiceTable, "Total (₹)", headerFont);

        double pkgPrice = booking.getPackagePrice();
        int travelers = booking.getTravelers();
        double grandTotal = booking.getTotalAmount();
        double discount = booking.getDiscountAmount();
        double origTotal = booking.getOriginalAmount() != null ? booking.getOriginalAmount() : (grandTotal + discount);

        addInvoiceRow(invoiceTable, booking.getPackageName() + " (" + booking.getDestination() + ")", String.format("%.2f", pkgPrice), travelers + " Traveler(s)", String.format("%.2f", origTotal), cellFont);

        if (discount > 0) {
            String offerTitle = "Festival Offer Discount (" + (booking.getOfferName() != null ? booking.getOfferName() : "Festival Special") + ")";
            Font greenDiscountFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, new Color(46, 125, 50));
            addInvoiceRow(invoiceTable, offerTitle, "- " + String.format("%.2f", discount), "1 Campaign Applied", "- ₹" + String.format("%.2f", discount), greenDiscountFont);
        }

        document.add(invoiceTable);

        // Grand Total Block
        PdfPTable totalTable = new PdfPTable(2);
        totalTable.setWidthPercentage(100);
        totalTable.setWidths(new float[]{3, 1});

        PdfPCell totalLabelCell = new PdfPCell(new Phrase("FINAL PAYABLE AMOUNT:", boldCellFont));
        totalLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalLabelCell.setPadding(8);
        totalLabelCell.setBackgroundColor(new Color(241, 245, 249));

        PdfPCell totalValCell = new PdfPCell(new Phrase("₹" + String.format("%.2f", grandTotal), boldCellFont));
        totalValCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalValCell.setPadding(8);
        totalValCell.setBackgroundColor(new Color(241, 245, 249));

        totalTable.addCell(totalLabelCell);
        totalTable.addCell(totalValCell);
        document.add(totalTable);

        document.add(new Paragraph(" "));

        // 5. Payment Details & Status Stamp
        document.add(new Paragraph("Payment Settlement Status", sectionFont));
        document.add(new Paragraph(" "));

        PdfPTable paymentTable = new PdfPTable(2);
        paymentTable.setWidthPercentage(100);
        paymentTable.setWidths(new float[]{3, 2});

        String pMethod = payment != null ? payment.getPaymentMethod() : "N/A";
        String pStatus = payment != null ? payment.getPaymentStatus() : "Pending";
        String pDate = (payment != null && payment.getPaymentDate() != null) ? payment.getPaymentDate().toString() : "N/A";
        double pAmt = payment != null ? payment.getAmount() : 0.0;
        double remBal = Math.max(0.0, grandTotal - pAmt);

        StringBuilder sbP = new StringBuilder();
        sbP.append("Payment Method    : ").append(pMethod).append("\n");
        sbP.append("Payment Status    : ").append(pStatus).append("\n");
        sbP.append("Payment Date      : ").append(pDate).append("\n");
        sbP.append("Amount Received   : ₹").append(String.format("%.2f", pAmt)).append("\n");
        sbP.append("Remaining Balance : ₹").append(String.format("%.2f", remBal));

        PdfPCell pInfoCell = new PdfPCell(new Phrase(sbP.toString(), cellFont));
        pInfoCell.setPadding(10);
        pInfoCell.setBackgroundColor(new Color(248, 250, 252));

        String stampText = "Paid".equalsIgnoreCase(pStatus) || "Confirmed".equalsIgnoreCase(booking.getBookingStatus()) ? "✓ PAID & CONFIRMED" : "⏳ PAYMENT PENDING";
        Color stampColor = "Paid".equalsIgnoreCase(pStatus) || "Confirmed".equalsIgnoreCase(booking.getBookingStatus()) ? new Color(16, 185, 129) : new Color(245, 158, 11);
        Font activeStampFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, stampColor);

        PdfPCell stampCell = new PdfPCell(new Phrase(stampText, activeStampFont));
        stampCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        stampCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        stampCell.setPadding(10);
        stampCell.setBackgroundColor(new Color(248, 250, 252));

        paymentTable.addCell(pInfoCell);
        paymentTable.addCell(stampCell);
        document.add(paymentTable);

        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        // 6. Signature Block & Terms Footer
        PdfPTable footerTable = new PdfPTable(2);
        footerTable.setWidthPercentage(100);
        footerTable.setWidths(new float[]{2, 1});

        PdfPCell termsCell = new PdfPCell(new Phrase("Terms & Conditions:\n1. This is a computer-generated receipt.\n2. Cancellations are subject to company refund policies.\n3. Please produce this receipt during hotel check-in.", FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY)));
        termsCell.setBorder(PdfPCell.NO_BORDER);

        PdfPCell sigCell = new PdfPCell(new Phrase("Authorized Signatory\n\n_______________________\nTripzo Management System", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.DARK_GRAY)));
        sigCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        sigCell.setBorder(PdfPCell.NO_BORDER);

        footerTable.addCell(termsCell);
        footerTable.addCell(sigCell);
        document.add(footerTable);

        document.close();

        // Try to open automatically in desktop PDF reader
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(targetFile);
            }
        } catch (Exception e) {
            // Ignore desktop launch errors
        }

        return targetFile;
    }

    private static void addTableRow(PdfPTable table, String k1, String v1, String k2, String v2, Font fk, Font fv) {
        PdfPCell c1 = new PdfPCell(new Phrase(k1, fk));
        c1.setBackgroundColor(new Color(241, 245, 249));
        c1.setPadding(6);

        PdfPCell c2 = new PdfPCell(new Phrase(v1, fv));
        c2.setPadding(6);

        PdfPCell c3 = new PdfPCell(new Phrase(k2, fk));
        c3.setBackgroundColor(new Color(241, 245, 249));
        c3.setPadding(6);

        PdfPCell c4 = new PdfPCell(new Phrase(v2, fv));
        c4.setPadding(6);

        table.addCell(c1);
        table.addCell(c2);
        table.addCell(c3);
        table.addCell(c4);
    }

    private static void addInvoiceHeader(PdfPTable table, String title, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(title, font));
        cell.setBackgroundColor(new Color(0x16, 0x4A, 0x5B));
        cell.setPadding(8);
        table.addCell(cell);
    }

    private static void addInvoiceRow(PdfPTable table, String desc, String rate, String qty, String total, Font font) {
        table.addCell(new PdfPCell(new Phrase(desc, font)) {{ setPadding(6); }});
        table.addCell(new PdfPCell(new Phrase(rate, font)) {{ setPadding(6); setHorizontalAlignment(Element.ALIGN_RIGHT); }});
        table.addCell(new PdfPCell(new Phrase(qty, font)) {{ setPadding(6); setHorizontalAlignment(Element.ALIGN_CENTER); }});
        table.addCell(new PdfPCell(new Phrase(total, font)) {{ setPadding(6); setHorizontalAlignment(Element.ALIGN_RIGHT); }});
    }
}
