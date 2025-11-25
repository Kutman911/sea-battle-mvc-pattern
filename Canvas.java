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
        int[][] cellAreaComputer = model.getCellAreaComputer();

        int startPosition = 50;
        int x = startPosition;
        int y = startPosition;
        int width = 50;
        int height = 50;
        int offset = 10;

        for(int i = 0; i < cellAreaComputer.length; i++) {
            for(int j = 0; j < cellAreaComputer[i].length; j++) {
                g.setColor(Color.YELLOW);
                g.drawRect(x, y, width, height);
                x = x + width + offset;
            }
            x = startPosition;
            y = y + height + offset;
        }
    }
}
