import javax.swing.*;

public class Viewer {

    private Canvas canvas;
    private JFrame frame;
    private Model model;
    private AudioPlayer audioPlayer;

    public Viewer() {

        audioPlayer = new AudioPlayer();
        audioPlayer.playBackgroundMusic("src/sounds/background_music.wav");

        Controller controller = new Controller(this);
        model = controller.getModel();

        canvas = new Canvas(model);
        canvas.addMouseListener(controller);
        canvas.addMouseMotionListener(controller);
        model.setCanvas(canvas);

        frame = new JFrame("Sea Battle MVC Pattern");
        frame.setSize(1500, 900);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // frame.setLocation(400, 50);
        frame.add("Center", canvas);
        frame.setLocationRelativeTo(null);
        // frame.setVisible(true);

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

}
