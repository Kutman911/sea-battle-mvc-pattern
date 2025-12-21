package common;

import javax.swing.*;
import java.awt.*;

public class SplashWindow {

    private JWindow window;

    public SplashWindow() {
        window = new JWindow();
        window.setAlwaysOnTop(true);
        ImageIcon icon = new ImageIcon(getClass().getResource("/resources/images/splash.jpg"));
        Image scaledImage = icon.getImage().getScaledInstance(1500, 900, Image.SCALE_DEFAULT);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel label = new JLabel(scaledIcon);

        window.add(label);
        window.setSize(1500, 900);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    public void fadeOutAndClose(Runnable onFinished) {
        Timer timer = new Timer(20, null); // ~50 FPS
        timer.addActionListener(new java.awt.event.ActionListener() {
            float opacity = 1.0f;
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                opacity -= 0.05f; // Скорость исчезновения
                if (opacity <= 0) {
                    opacity = 0;
                    timer.stop();
                    window.dispose();
                    if (onFinished != null) onFinished.run();
                }
                try {
                    window.setOpacity(opacity);
                } catch (Exception ex) {
                    timer.stop();
                    window.dispose();
                    if (onFinished != null) onFinished.run();
                }
            }
        });
        timer.start();
    }

    public void closeWindow() {
        window.dispose();
    }
}

