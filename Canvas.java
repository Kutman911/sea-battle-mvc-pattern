import javax.swing.*;
import java.awt.*;

public class Canvas extends JPanel {
    private Model model;
    public Canvas(Model model) {
        this.model = model;
        setBackground(new Color(35, 35, 35));
    }

    public void paint(Graphics g) {
        super.paint(g);
        int[][] cellAreaComputer = model.getCellAreaComputer();

        int start = 50;
        int x = start;
        int y = start;
        int width = 50;
        int height = 50;
        int offset = 10;
        for (int i = 0; i < cellAreaComputer.length; i++) {
            for (int j = 0; j < cellAreaComputer[i].length; j++) {
                g.setColor(Color.YELLOW);
                g.drawRect(x, y, width, height);
                x = x + offset + width;
            }
            x = start;
            y = y + offset + height;
        }
    }
}
