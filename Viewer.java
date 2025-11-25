import javax.swing.JFrame;

public class Viewer {

    public Viewer() {
        Controller controller = new Controller(this);
        Model model = controller.getModel();
        Canvas canvas = new Canvas(model);

        JFrame frame = new JFrame("Sea Battle MVC Pattern");

        frame.setSize(1300, 800);
        frame.setLocation(100, 50);
        frame.add("Center", canvas);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
