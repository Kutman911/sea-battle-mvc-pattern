import javax.swing.JPanel;
import java.awt.Color;

public class Canvas extends JPanel {

    private Model model;

    public Canvas(Model model) {
        this.model = model;
        setBackground(new Color(30, 30, 30));
    }
}
