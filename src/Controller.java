import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Controller implements MouseListener {

    private Model model;

    public Controller(Viewer viewer) {
        model = new Model(viewer);
    }

    public Model getModel() {
        return model;
    }

    @Override
    public void mouseClicked(MouseEvent event) {

    }

    @Override
    public void mousePressed(MouseEvent event) {
        int x = event.getX();
        int y = event.getY();
        model.doAction(x, y);
    }

    @Override
    public void mouseReleased(MouseEvent event) {

    }

    @Override
    public void mouseEntered(MouseEvent event) {

    }

    @Override
    public void mouseExited(MouseEvent event) {

    }
}
