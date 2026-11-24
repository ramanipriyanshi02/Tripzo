package com.holiday.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class FestivalImageUtil {

    private static final Map<String, ImageIcon> iconCache = new HashMap<>();

    public static final Map<String, String> PRESET_IMAGES = new LinkedHashMap<>();
    static {
        PRESET_IMAGES.put("Diwali Glowing Lights & Heritage", "src/com/holiday/assets/festivals/diwali_banner.jpg");
        PRESET_IMAGES.put("Navratri Festive Colors & Garba", "src/com/holiday/assets/festivals/navratri_banner.jpg");
        PRESET_IMAGES.put("Christmas Winter Chalets & Snow", "src/com/holiday/assets/festivals/christmas_banner.jpg");
        PRESET_IMAGES.put("New Year Fireworks & Coastal Nights", "src/com/holiday/assets/festivals/newyear_banner.jpg");
        PRESET_IMAGES.put("Holi Vibrant Color Carnival", "src/com/holiday/assets/festivals/holi_banner.jpg");
        PRESET_IMAGES.put("Summer Vacation Family Escape", "src/com/holiday/assets/festivals/summer_vacation.jpg");
        PRESET_IMAGES.put("Festive Grand Hero Banner", "src/com/holiday/assets/festivals/festival_hero_banner.jpg");
    }

    /**
     * Loads a festival image, crops/scales it smoothly to target dimensions, and caches the result.
     */
    public static ImageIcon getFestivalImage(String imagePath, String festivalName, int width, int height, int cornerRadius) {
        String cacheKey = (imagePath != null ? imagePath : "null") + "|" + (festivalName != null ? festivalName : "null")
                + "|" + width + "x" + height + "_r" + cornerRadius;

        if (iconCache.containsKey(cacheKey)) {
            return iconCache.get(cacheKey);
        }

        BufferedImage rawImage = loadRawImage(imagePath, festivalName);
        BufferedImage scaledImage;

        if (rawImage != null) {
            scaledImage = scaleAndCropImage(rawImage, width, height, cornerRadius);
        } else {
            // Graceful fallback: render modern gradient banner with festival badge
            scaledImage = renderFallbackFestivalBanner(festivalName, width, height, cornerRadius);
        }

        ImageIcon icon = new ImageIcon(scaledImage);
        iconCache.put(cacheKey, icon);
        return icon;
    }

    /**
     * Resolves and loads an image via filesystem or classpath resource.
     */
    private static BufferedImage loadRawImage(String imagePath, String festivalName) {
        // 1. Try specified path if provided
        if (imagePath != null && !imagePath.trim().isEmpty()) {
            BufferedImage img = tryLoadFromFileOrResource(imagePath.trim());
            if (img != null) return img;
        }

        // 2. Try lookup by festival name keywords
        String presetFile = getPresetFilenameByFestival(festivalName);
        if (presetFile != null) {
            BufferedImage img = tryLoadFromFileOrResource(presetFile);
            if (img != null) return img;
        }

        // 3. Fallback to hero banner
        return tryLoadFromFileOrResource("src/com/holiday/assets/festivals/festival_hero_banner.jpg");
    }

    private static BufferedImage tryLoadFromFileOrResource(String path) {
        // A. Direct file path
        try {
            File f = new File(path);
            if (f.exists() && f.isFile()) {
                BufferedImage img = ImageIO.read(f);
                if (img != null) return img;
            }
        } catch (Exception ignored) {}

        // B. If path started with src/, try in bin/
        if (path.startsWith("src/")) {
            try {
                File f = new File(path.replace("src/", "bin/"));
                if (f.exists() && f.isFile()) {
                    BufferedImage img = ImageIO.read(f);
                    if (img != null) return img;
                }
            } catch (Exception ignored) {}
        }

        // C. If relative without src/, try src/... and bin/...
        String[] prefixes = {"src/com/holiday/assets/festivals/", "bin/com/holiday/assets/festivals/", "src/", "bin/"};
        for (String pfx : prefixes) {
            try {
                File f = new File(pfx + new File(path).getName());
                if (f.exists() && f.isFile()) {
                    BufferedImage img = ImageIO.read(f);
                    if (img != null) return img;
                }
            } catch (Exception ignored) {}
        }

        // D. Classpath resource lookup
        String resourcePath = path.startsWith("/") ? path : "/" + path;
        resourcePath = resourcePath.replace("/src/", "/");
        try (InputStream in = FestivalImageUtil.class.getResourceAsStream(resourcePath)) {
            if (in != null) {
                BufferedImage img = ImageIO.read(in);
                if (img != null) return img;
            }
        } catch (Exception ignored) {}

        // E. Classpath fallback with standard package location
        String fallbackRes = "/com/holiday/assets/festivals/" + new File(path).getName();
        try (InputStream in = FestivalImageUtil.class.getResourceAsStream(fallbackRes)) {
            if (in != null) {
                BufferedImage img = ImageIO.read(in);
                if (img != null) return img;
            }
        } catch (Exception ignored) {}

        return null;
    }

    private static String getPresetFilenameByFestival(String festivalName) {
        if (festivalName == null) return "src/com/holiday/assets/festivals/festival_hero_banner.jpg";
        String f = festivalName.toLowerCase().trim();

        if (f.contains("diwali") || f.contains("deepavali")) {
            return "src/com/holiday/assets/festivals/diwali_banner.jpg";
        } else if (f.contains("navratri") || f.contains("dussehra") || f.contains("garba")) {
            return "src/com/holiday/assets/festivals/navratri_banner.jpg";
        } else if (f.contains("christmas") || f.contains("xmas") || f.contains("winter")) {
            return "src/com/holiday/assets/festivals/christmas_banner.jpg";
        } else if (f.contains("new year") || f.contains("newyear") || f.contains("nye")) {
            return "src/com/holiday/assets/festivals/newyear_banner.jpg";
        } else if (f.contains("holi") || f.contains("color")) {
            return "src/com/holiday/assets/festivals/holi_banner.jpg";
        } else if (f.contains("summer") || f.contains("vacation") || f.contains("beach")) {
            return "src/com/holiday/assets/festivals/summer_vacation.jpg";
        } else {
            return "src/com/holiday/assets/festivals/festival_hero_banner.jpg";
        }
    }

    /**
     * Scale and center-crop the image to fill the target aspect ratio, with optional rounded corners.
     */
    private static BufferedImage scaleAndCropImage(BufferedImage src, int targetWidth, int targetHeight, int cornerRadius) {
        BufferedImage dest = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = dest.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        if (cornerRadius > 0) {
            g2.setClip(new RoundRectangle2D.Float(0, 0, targetWidth, targetHeight, cornerRadius, cornerRadius));
        }

        int srcW = src.getWidth();
        int srcH = src.getHeight();

        double scale = Math.max((double) targetWidth / srcW, (double) targetHeight / srcH);
        int scaledW = (int) Math.round(srcW * scale);
        int scaledH = (int) Math.round(srcH * scale);

        int x = (targetWidth - scaledW) / 2;
        int y = (targetHeight - scaledH) / 2;

        g2.drawImage(src, x, y, scaledW, scaledH, null);

        // Add subtle bottom vignette shadow for text legibility
        GradientPaint vignette = new GradientPaint(0, targetHeight - 40, new Color(0, 0, 0, 0),
                                                   0, targetHeight, new Color(0, 0, 0, 110));
        g2.setPaint(vignette);
        g2.fillRect(0, targetHeight - 40, targetWidth, 40);

        g2.dispose();
        return dest;
    }

    /**
     * Graceful fallback gradient banner if image file is missing.
     */
    private static BufferedImage renderFallbackFestivalBanner(String festivalName, int width, int height, int cornerRadius) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (cornerRadius > 0) {
            g2.setClip(new RoundRectangle2D.Float(0, 0, width, height, cornerRadius, cornerRadius));
        }

        Color[] colors = getFestivalThemeColors(festivalName);
        GradientPaint grad = new GradientPaint(0, 0, colors[0], width, height, colors[1]);
        g2.setPaint(grad);
        g2.fillRect(0, 0, width, height);

        // Decorative background geometric circles
        g2.setColor(new Color(255, 255, 255, 25));
        g2.fillOval(width - 80, -30, 160, 160);
        g2.fillOval(-40, height - 70, 140, 140);

        // Festival Emoji Icon
        String iconStr = getFestivalEmoji(festivalName);
        g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, Math.max(22, (int) (height * 0.35))));
        g2.setColor(new Color(255, 255, 255, 230));
        g2.drawString(iconStr, 18, (int) (height * 0.52));

        // Festival Name
        String name = festivalName != null && !festivalName.trim().isEmpty() ? festivalName.trim() : "Special Festival Offer";
        g2.setFont(new Font("Segoe UI", Font.BOLD, Math.max(13, (int) (height * 0.16))));
        g2.setColor(Color.WHITE);
        g2.drawString(name.toUpperCase(), 18, height - 16);

        g2.dispose();
        return img;
    }

    public static Color[] getFestivalThemeColors(String festival) {
        if (festival == null) return new Color[]{new Color(0x16, 0x4A, 0x5B), new Color(0x2A, 0x6F, 0x86)};
        String f = festival.toLowerCase();

        if (f.contains("diwali")) {
            return new Color[]{new Color(0x9A, 0x34, 0x12), new Color(0xEA, 0x58, 0x0C)}; // Rich Deep Amber Gold
        } else if (f.contains("navratri")) {
            return new Color[]{new Color(0x86, 0x19, 0x8F), new Color(0xC0, 0x26, 0xD3)}; // Festive Magenta-Purple
        } else if (f.contains("christmas")) {
            return new Color[]{new Color(0x14, 0x53, 0x2D), new Color(0x16, 0x65, 0x34)}; // Winter Pine Forest Green
        } else if (f.contains("new year")) {
            return new Color[]{new Color(0x1E, 0x1B, 0x4B), new Color(0x43, 0x38, 0xCA)}; // Midnight Starlight Indigo
        } else if (f.contains("holi")) {
            return new Color[]{new Color(0xBE, 0x12, 0x3C), new Color(0xE1, 0x1D, 0x48)}; // Vibrant Crimson Rose
        } else if (f.contains("summer")) {
            return new Color[]{new Color(0x02, 0x84, 0xC7), new Color(0x0D, 0x94, 0x88)}; // Ocean Turquoise
        } else {
            return new Color[]{new Color(0x16, 0x4A, 0x5B), new Color(0x3B, 0x7A, 0x8C)};
        }
    }

    public static String getFestivalEmoji(String festival) {
        if (festival == null) return "✨";
        String f = festival.toLowerCase();

        if (f.contains("diwali")) return "🪔";
        if (f.contains("navratri")) return "💃";
        if (f.contains("christmas")) return "🎄";
        if (f.contains("new year")) return "🎆";
        if (f.contains("holi")) return "🎨";
        if (f.contains("summer")) return "🏖️";
        return "🎉";
    }
}
