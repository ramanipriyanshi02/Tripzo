package com.holiday;

import com.formdev.flatlaf.FlatLightLaf;
import com.holiday.gui.SplashScreen;
import com.holiday.gui.ui.Theme;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Initialize FlatLaf Light Theme for modern desktop look & coastal aesthetic
        try {
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("ProgressBar.arc", 10);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("Panel.background", Theme.BACKGROUND);
            UIManager.put("Table.selectionBackground", Theme.SECONDARY);
            UIManager.put("Table.selectionForeground", Theme.PRIMARY);
        } catch (Exception e) {
            System.err.println("Note: FlatLaf initialization notice: " + e.getMessage());
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
        }

        // Launch Application via Animated Splash Screen
        SwingUtilities.invokeLater(() -> {
            SplashScreen splash = new SplashScreen();
            splash.startSplash();
        });
    }
}

