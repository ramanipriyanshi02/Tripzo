package com.holiday.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;

public class DestinationImageCache {

    private static final Map<String, ImageIcon> cache = new HashMap<>();

    public static ImageIcon getDestinationImage(String destination, int width, int height, int cornerRadius) {
        String key = (destination != null ? destination.toLowerCase().trim() : "default") + "_" + width + "x" + height + "_r" + cornerRadius;

        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        BufferedImage img = renderDestinationCard(destination, width, height, cornerRadius);
        ImageIcon icon = new ImageIcon(img);
        cache.put(key, icon);
        return icon;
    }

    private static BufferedImage renderDestinationCard(String destination, int width, int height, int cornerRadius) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Apply rounded rectangle clipping path
        g2.setClip(new RoundRectangle2D.Float(0, 0, width, height, cornerRadius, cornerRadius));

        // Get theme palette colors based on destination name
        Color[] colors = getDestinationColors(destination);
        GradientPaint gradient = new GradientPaint(0, 0, colors[0], width, height, colors[1]);
        g2.setPaint(gradient);
        g2.fillRect(0, 0, width, height);

        // Render subtle decorative background geometric shapes
        g2.setColor(new Color(255, 255, 255, 30));
        g2.fillOval(width - 60, -20, 120, 120);
        g2.fillOval(-30, height - 50, 100, 100);

        // Render destination icon & badge
        String iconStr = getDestinationEmoji(destination);
        g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, (int) (height * 0.32)));
        g2.setColor(new Color(255, 255, 255, 220));
        g2.drawString(iconStr, 15, (int) (height * 0.48));

        // Render destination name overlay
        String destName = destination != null && !destination.trim().isEmpty() ? destination.trim() : "Holiday Destination";
        g2.setFont(new Font("Segoe UI", Font.BOLD, Math.max(12, (int) (height * 0.16))));
        g2.setColor(Color.WHITE);

        FontMetrics fm = g2.getFontMetrics();
        int textX = 15;
        int textY = height - 14;
        g2.drawString(destName, textX, textY);

        // Render translucent bottom strip shadow
        GradientPaint shadowGradient = new GradientPaint(0, height - 35, new Color(0, 0, 0, 0), 0, height, new Color(0, 0, 0, 160));
        g2.setPaint(shadowGradient);
        g2.fillRect(0, height - 35, width, 35);

        // Re-draw text crisp over shadow
        g2.setColor(Color.WHITE);
        g2.drawString(destName, textX, textY);

        g2.dispose();
        return image;
    }

    private static Color[] getDestinationColors(String destination) {
        if (destination == null) return new Color[]{new Color(15, 76, 92), new Color(44, 122, 123)};
        String d = destination.toLowerCase();

        if (d.contains("goa") || d.contains("beach") || d.contains("coastal")) {
            return new Color[]{new Color(14, 165, 233), new Color(30, 58, 138)}; // Ocean Blue
        } else if (d.contains("andaman") || d.contains("island") || d.contains("maldives") || d.contains("bali")) {
            return new Color[]{new Color(6, 182, 212), new Color(14, 116, 144)}; // Cyan Lagoon
        } else if (d.contains("dubai")) {
            return new Color[]{new Color(217, 119, 6), new Color(120, 53, 15)}; // Luxury Desert Gold
        } else if (d.contains("rishikesh")) {
            return new Color[]{new Color(249, 115, 22), new Color(194, 65, 12)}; // Sunset Coral Adventure
        } else if (d.contains("meghalaya") || d.contains("kerala") || d.contains("backwater") || d.contains("darjeeling")) {
            return new Color[]{new Color(16, 185, 129), new Color(13, 148, 136)}; // Lush Green
        } else if (d.contains("manali") || d.contains("shimla") || d.contains("mountain") || d.contains("alps") || d.contains("kashmir") || d.contains("sikkim")) {
            return new Color[]{new Color(15, 118, 110), new Color(6, 78, 59)}; // Emerald Forest Green
        } else if (d.contains("rajasthan") || d.contains("desert") || d.contains("jaipur") || d.contains("heritage") || d.contains("udaipur")) {
            return new Color[]{new Color(245, 158, 11), new Color(180, 83, 9)}; // Warm Gold
        } else if (d.contains("paris") || d.contains("tokyo") || d.contains("york") || d.contains("swiss")) {
            return new Color[]{new Color(139, 92, 246), new Color(79, 70, 229)}; // Indigo Violet
        } else {
            return new Color[]{new Color(59, 130, 246), new Color(30, 64, 175)}; // Deep Blue
        }
    }

    private static String getDestinationEmoji(String destination) {
        if (destination == null) return "🌴";
        String d = destination.toLowerCase();

        if (d.contains("goa") || d.contains("beach")) {
            return "🏖️";
        } else if (d.contains("andaman") || d.contains("island") || d.contains("maldives") || d.contains("bali")) {
            return "🏝️";
        } else if (d.contains("dubai")) {
            return "🏙️";
        } else if (d.contains("rishikesh")) {
            return "🚣";
        } else if (d.contains("darjeeling")) {
            return "🫖";
        } else if (d.contains("meghalaya")) {
            return "🌿";
        } else if (d.contains("shimla")) {
            return "❄️";
        } else if (d.contains("manali") || d.contains("kashmir") || d.contains("sikkim") || d.contains("mountain") || d.contains("alps")) {
            return "🏔️";
        } else if (d.contains("kerala") || d.contains("backwater")) {
            return "🛶";
        } else if (d.contains("rajasthan") || d.contains("desert") || d.contains("jaipur") || d.contains("udaipur")) {
            return "🏰";
        } else if (d.contains("paris") || d.contains("tokyo") || d.contains("york") || d.contains("city")) {
            return "✈️";
        } else {
            return "🌴";
        }
    }
}
