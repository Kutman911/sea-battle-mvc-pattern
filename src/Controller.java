import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Controller implements MouseListener, MouseMotionListener {

    private Model model;
    private Ship selectedShip = null;
    private int offsetX, offsetY;

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
    public void mouseDragged(MouseEvent event) {
        if (selectedShip != null) {
            int newScreenX = event.getX() - offsetX;
            int newScreenY = event.getY() - offsetY;

            selectedShip.setX(newScreenX);
            selectedShip.setY(newScreenY);

            model.getViewer().update();
        }
    }

    @Override
    public void mouseEntered(MouseEvent event) {

    }

    @Override
    public void mouseExited(MouseEvent event) {

    }

    @Override
    public void mousePressed(MouseEvent event) {
        if (model.isSetupPhase()) {
            int px = event.getX();
            int py = event.getY();

            for (Ship ship : model.getPlayerShips()) {
                if (!ship.isPlaced() || ship.isDragging()) {
                    int shipScreenX = getPlayerBoardX() + ship.getX() * Coordinates.WIDTH;
                    int shipScreenY = getPlayerBoardY() + ship.getY() * Coordinates.HEIGHT;

                    int width = ship.isVertical() ? Coordinates.WIDTH : ship.getSize() * Coordinates.WIDTH;
                    int height = ship.isVertical() ? ship.getSize() * Coordinates.HEIGHT : Coordinates.HEIGHT;

                    if (px >= shipScreenX && px < shipScreenX + width &&
                            py >= shipScreenY && py < shipScreenY + height)
                    {
                        selectedShip = ship;
                        selectedShip.setDragging(true);
                        offsetX = px - shipScreenX;
                        offsetY = py - shipScreenY;

                        if (event.getButton() == MouseEvent.BUTTON3) {
                            selectedShip.rotate();
                            model.getViewer().update();
                        }

                        break;
                    }
                }
            }
        } else {
            model.doAction(event.getX(), event.getY());
        }
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        if (selectedShip != null) {
            int px = event.getX() - offsetX;
            int py = event.getY() - offsetY;

            int boardX = getPlayerBoardX();
            int boardY = getPlayerBoardY();

            int cellCol = (px - boardX) / Coordinates.WIDTH;
            int cellRow = (py - boardY) / Coordinates.HEIGHT;

            int tempX = selectedShip.getX();
            int tempY = selectedShip.getY();

            selectedShip.setX(cellCol);
            selectedShip.setY(cellRow);

            if (cellRow >= 0 && cellRow < 10 && cellCol >= 0 && cellCol < 10 && model.isValidPlacement(selectedShip)) {
                selectedShip.setPlaced(true);
            } else {
                selectedShip.setX(tempX);
                selectedShip.setY(tempY);
                selectedShip.setPlaced(false);
            }

            selectedShip.setDragging(false);
            selectedShip = null;
            model.getViewer().update();
        }
    }

    private int getPlayerBoardX() {
        if (model.getCanvas() != null) {
            return model.getCanvas().getPlayerBoardPosition().x;
        }
        return 0;
    }

    private int getPlayerBoardY() {
        if (model.getCanvas() != null) {
            return model.getCanvas().getPlayerBoardPosition().y;
        }
        return 0;
    }

    @Override
    public void mouseMoved(MouseEvent event) {

    }
}
