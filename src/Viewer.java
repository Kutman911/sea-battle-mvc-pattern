import javax.swing.*;
import java.awt.*;

public class Viewer {

    private Canvas canvas;
    private JFrame frame;
    private Model model;
    private AudioPlayer audioPlayer;
    private Controller controller;

    public Viewer() {

        audioPlayer = new AudioPlayer();
        audioPlayer.playBackgroundMusic("src/sounds/background_music.wav");

        controller = new Controller(this);
        model = controller.getModel();

        canvas = new Canvas(model);
        canvas.addMouseListener(controller);
        canvas.addMouseMotionListener(controller);
        model.setCanvas(canvas);

        frame = new JFrame("Sea Battle MVC Pattern");
        frame.setSize(1500, 900);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add("Center", canvas);
        frame.setLocationRelativeTo(null);

    }

    public void update() {
        canvas.repaint();
    }

    public void setVisibleFrame(){
        frame.setVisible(true);

        SwingUtilities.invokeLater(() -> {
            model.showLevelStartWindow(model.getCurrentLevel());
        });
    }

    public void showResult(boolean isWin) {
        if (audioPlayer != null) {
            audioPlayer.stop();
        }

        SwingUtilities.invokeLater(() -> {
            ResultDialog dialog = new ResultDialog(
                    (Frame) frame,
                    isWin,
                    () -> {
                        if (controller != null) {
                            controller.restartGame();
                        }
                        if (audioPlayer != null) {
                            audioPlayer.playBackgroundMusic("src/sounds/background_music.wav");
                        }
                    }
            );
            dialog.setVisible(true);
        });
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public Model getModel() {
        return model;
    }
}