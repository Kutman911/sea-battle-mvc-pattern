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
        frame.setIconImage(new ImageIcon(Viewer.class.getResource("/images/appIcon.jpg")).getImage());
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
            model.getLevelWindow().showLevelStartWindow();
        });
    }

    public void showResult(boolean isWin) {
        if (audioPlayer != null) {
            audioPlayer.stop();
        }

        if (isWin) {
            audioPlayer.playSound("src/sounds/winSound.wav");
        } else {
            audioPlayer.playSound("src/sounds/loseSound.wav");
        }

        SwingUtilities.invokeLater(() -> {
            ResultDialog dialog = new ResultDialog(
                    frame,
                    isWin,
                    () -> {

                        if (isWin) {
                            if (model.getLevelWindow().getCurrentLevel() >= 3) {
                                model.getLevelWindow().resetToLevelOne();   // ← сброс только здесь!
                            }
                        } else {
                            model.getLevelWindow().resetToLevelOne();       // проиграл → всегда Level 1
                        }

                        model.resetGame();

                        SwingUtilities.invokeLater(() -> {
                            model.getLevelWindow().showLevelStartWindow();
                        });

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

    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }

}