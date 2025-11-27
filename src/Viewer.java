import javax.swing.JFrame;

public class Viewer {

    private Canvas canvas;
    private JFrame frame;

    public Viewer() {

        Controller controller = new Controller(this);
        Model model = controller.getModel();

        canvas = new Canvas(model);
        canvas.addMouseListener(controller);

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

        frame.setVisible(true);
    }

}
