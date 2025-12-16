package common;

import javax.swing.JWindow;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.*;

public class SplashWindow {

    private JWindow window;

    public SplashWindow() {
        window = new JWindow();
        ImageIcon icon = new ImageIcon("src/images/splash.gif");
        Image scaledImage = icon.getImage().getScaledInstance(1500, 800, Image.SCALE_DEFAULT);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel label = new JLabel(scaledIcon);

        window.add(label);
        window.setSize(1500, 800);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    public void closeWindow() {
        window.dispose();
    }
}

