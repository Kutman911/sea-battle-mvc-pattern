import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Controller implements MouseListener, MouseMotionListener {

    private Model model;
    private Ship selectedShip = null;
    private int offsetX, offsetY;
    private int initialGridX, initialGridY;

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

            if (event.getButton() == MouseEvent.BUTTON3) {
                for (Ship ship : model.getPlayerShips()) {
                    if (ship.isPlaced()) {
                        int shipScreenX = getPlayerBoardX() + ship.getX() * Coordinates.WIDTH;
                        int shipScreenY = getPlayerBoardY() + ship.getY() * Coordinates.HEIGHT;

                        int width = ship.isVertical() ? Coordinates.WIDTH : ship.getSize() * Coordinates.WIDTH;
                        int height = ship.isVertical() ? ship.getSize() * Coordinates.HEIGHT : Coordinates.HEIGHT;

                        if (px >= shipScreenX && px < shipScreenX + width &&
                                py >= shipScreenY && py < shipScreenY + height)
                        {
                            ship.rotate();
                            if (model.isValidPlacement(ship)) {
                                model.updateDesktopPlayer();
                            } else {
                                ship.rotate();
                            }
                            model.getViewer().update();
                            return;
                        }
                    }
                }
            }


            for (Ship ship : model.getPlayerShips()) {

                int shipScreenX, shipScreenY;

                if (ship.isPlaced()) {
                    shipScreenX = getPlayerBoardX() + ship.getX() * Coordinates.WIDTH;
                    shipScreenY = getPlayerBoardY() + ship.getY() * Coordinates.HEIGHT;
                } else {
                    Point offBoardPos = getScreenPositionFromGrid(ship.getX(), ship.getY());
                    shipScreenX = offBoardPos.x;
                    shipScreenY = offBoardPos.y;
                }

                int width = ship.isVertical() ? Coordinates.WIDTH : ship.getSize() * Coordinates.WIDTH;
                int height = ship.isVertical() ? ship.getSize() * Coordinates.HEIGHT : Coordinates.HEIGHT;

                if (px >= shipScreenX && px < shipScreenX + width &&
                        py >= shipScreenY && py < shipScreenY + height)
                {
                    selectedShip = ship;

                    initialGridX = selectedShip.getX();
                    initialGridY = selectedShip.getY();

                    selectedShip.setPlaced(false);
                    selectedShip.setDragging(true);

                    offsetX = px - shipScreenX;
                    offsetY = py - shipScreenY;

                    model.getViewer().update();
                    break;
                }
            }
        } else {
            model.doAction(event.getX(), event.getY());
        }
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        if (selectedShip != null) {

            int boardX = getPlayerBoardX();
            int boardY = getPlayerBoardY();

            int shipScreenX = selectedShip.getX();
            int shipScreenY = selectedShip.getY();

            int shipCenterX = shipScreenX + (selectedShip.isVertical() ? Coordinates.WIDTH : selectedShip.getSize() * Coordinates.WIDTH) / 2;
            int shipCenterY = shipScreenY + (selectedShip.isVertical() ? selectedShip.getSize() * Coordinates.HEIGHT : Coordinates.HEIGHT) / 2;


            int cellCol = (shipCenterX - boardX) / Coordinates.WIDTH;
            int cellRow = (shipCenterY - boardY) / Coordinates.HEIGHT;


            selectedShip.setX(cellCol);
            selectedShip.setY(cellRow);

            if (cellRow >= 0 && cellRow < 10 && cellCol >= 0 && cellCol < 10 && model.isValidPlacement(selectedShip)) {
                selectedShip.setPlaced(true);
                selectedShip.setDragging(false);

                model.updateDesktopPlayer();

                if (!model.isSetupPhase()) {
                    model.startBattlePhase();
                }
            } else {
                selectedShip.setX(initialGridX);
                selectedShip.setY(initialGridY);
                selectedShip.setPlaced(false);
            }

            selectedShip.setDragging(false);
            selectedShip = null;
            model.getViewer().update();
        }
    }

    private Point getScreenPositionFromGrid(int gridX, int gridY) {
        int boardX = getPlayerBoardX();
        int boardY = getPlayerBoardY();

        int screenX = boardX + gridX * Coordinates.WIDTH;
        int screenY = boardY + gridY * Coordinates.HEIGHT;
        return new Point(screenX, screenY);
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
