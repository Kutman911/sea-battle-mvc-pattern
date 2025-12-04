import java.awt.Point;
import java.awt.event.MouseEvent;


public class ShipPlacementHandler {

    private final Model model;
    private final Viewer viewer;

    private Ship selectedShip = null;
    private int offsetX, offsetY;
    private int initialGridX, initialGridY;

    public ShipPlacementHandler(Model model, Viewer viewer) {
        this.model = model;
        this.viewer = viewer;
    }


    public void mousePressed(MouseEvent event) {
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
                        viewer.update();
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

                // Рассчитываем смещение курсора
                offsetX = px - shipScreenX;
                offsetY = py - shipScreenY;

                viewer.update();
                break;
            }
        }
    }


    public void mouseDragged(MouseEvent event) {
        if (selectedShip != null) {
            int newScreenX = event.getX() - offsetX;
            int newScreenY = event.getY() - offsetY;

            selectedShip.setX(newScreenX);
            selectedShip.setY(newScreenY);

            viewer.update();
        }
    }


    public void mouseReleased(MouseEvent event) {
        if (selectedShip != null) {

            int boardX = getPlayerBoardX();
            int boardY = getPlayerBoardY();

            int shipScreenX = selectedShip.getX();
            int shipScreenY = selectedShip.getY();

            int width = selectedShip.isVertical() ? Coordinates.WIDTH : selectedShip.getSize() * Coordinates.WIDTH;
            int height = selectedShip.isVertical() ? selectedShip.getSize() * Coordinates.HEIGHT : Coordinates.HEIGHT;

            int shipCenterX = shipScreenX + width / 2;
            int shipCenterY = shipScreenY + height / 2;

            int cellCol = (shipCenterX - boardX) / Coordinates.WIDTH;
            int cellRow = (shipCenterY - boardY) / Coordinates.HEIGHT;

            selectedShip.setX(cellCol);
            selectedShip.setY(cellRow);

            if (cellRow >= 0 && cellRow < 10 && cellCol >= 0 && cellCol < 10 && model.isValidPlacement(selectedShip)) {
                selectedShip.setPlaced(true);

                model.updateDesktopPlayer();
                viewer.getAudioPlayer().playSound("sea-battle-mvc-pattern/src/sounds/laughSound.wav");

                if (!model.isSetupPhase()) {
                    model.startBattlePhase();
                }
            } else {
                selectedShip.setX(initialGridX);
                selectedShip.setY(initialGridY);

                if (initialGridX >= 0 && initialGridX < 10 && initialGridY >= 0 && initialGridY < 10) {
                    selectedShip.setPlaced(true);
                } else {
                    selectedShip.setPlaced(false);
                }
            }

            selectedShip.setDragging(false);
            selectedShip = null;
            viewer.update();
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
}