import javax.swing.JWindow;
import javax.swing.JLabel;
import javax.swing.ImageIcon;

public class SplashWindow {

    private JWindow window;

    public SplashWindow() {
        window = new JWindow();
        ImageIcon icon = new ImageIcon("src/images/splash.jpg");
        JLabel label = new JLabel();
        label.setIcon(icon);
        window.add(label);
        window.setSize(1408, 768);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    public void closeWindow() {
        window.dispose();
    }
}

