import javax.swing.JFrame;
import javax.swing.WindowConstants;
public class Viewer {
    public Viewer() {

        Controller controller = new Controller(this);
        Model model = controller.getModel();
        Canvas canvas = new Canvas(model);
        JFrame frame = new JFrame("Sea Battle ");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1300, 800);
        frame.setLocation(100, 50);
        frame.add(canvas);
        frame.setVisible(true);
    }
}
