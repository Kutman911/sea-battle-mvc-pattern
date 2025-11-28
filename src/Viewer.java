import javax.swing.JFrame;

public class Viewer {

    private Canvas canvas;
    private JFrame frame;
    private Model model;

    public Viewer() {

        Controller controller = new Controller(this);
        model = controller.getModel();

        canvas = new Canvas(model);
        canvas.addMouseListener(controller);
        canvas.addMouseMotionListener(controller);
        model.setCanvas(canvas);

        frame = new JFrame("Sea Battle MVC Pattern");
        frame.setSize(1500, 900);
        // frame.setLocation(400, 50);
        frame.add("Center", canvas);
        frame.setLocationRelativeTo(null);
        // frame.setVisible(true);



    }

    public void update() {
        canvas.repaint();
    }

    public void setVisibleFrame(){
        model.showLevelStartWindow(model.getCurrentLevel());
        frame.setVisible(true);
    }

}
