import common.SplashWindow;
import viewer.Viewer;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            SplashWindow splashWindow = new SplashWindow();

            Viewer viewer = new Viewer();

            Timer t = new Timer(3000, e -> {
                ((Timer) e.getSource()).stop();
                splashWindow.fadeOutAndClose(null);

                viewer.showMainMenuFromCanvas();
            });

            t.setRepeats(false);
            t.start();
        });
    }
}