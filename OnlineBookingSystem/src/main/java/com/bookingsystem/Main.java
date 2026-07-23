package com.bookingsystem;

import com.bookingsystem.gui.MainWindow;
import com.bookingsystem.gui.ThemeManager;

import javax.swing.*;

/**
 * Application entry point. Sets up the system look and feel, applies the
 * previously saved (or default) theme, and shows the main window on the
 * Swing event dispatch thread.
 */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fall back to the cross-platform default look and feel.
        }

        ThemeManager.getInstance().applyTheme(ThemeManager.getInstance().getCurrentTheme());

        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}
