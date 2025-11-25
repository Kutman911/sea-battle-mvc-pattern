import javax.swing.JPanel;
import java.awt.*;

public class Canvas extends JPanel {

    private Model model;

    public Canvas(Model model) {
        this.model = model;
        setBackground(new Color(30, 30, 30));
    }

    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D graphics2D = (Graphics2D) g;

        Cell[][] cellAreaComputer = model.getCallAreaComputer();

        for(int i = 0; i < cellAreaComputer.length; i++) {
            for(int j = 0; j < cellAreaComputer[i].length; j++) {
                graphics2D.setColor(Color.YELLOW);
                graphics2D.draw(cellAreaComputer[i][j]);

            }
        }
    }
}
