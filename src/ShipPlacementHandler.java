import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Optional;

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
            Optional<Ship> clickedShip = findClickedShip(px, py);

            if (clickedShip.isPresent()) {
                Ship ship = clickedShip.get();
                ship.rotate();

                if (ship.isPlaced() && !model.isValidPlacement(ship)) {
                    ship.rotate();
                } else if (ship.isPlaced()) {
                    model.updateDesktopPlayer();
                }
                viewer.update();
                return;
            }
        }
        Optional<Ship> clickedShip = findClickedShip(px, py);

        if (clickedShip.isPresent()) {
            selectedShip = clickedShip.get();
            Point currentScreenPos = getShipScreenPosition(selectedShip);
            initialGridX = selectedShip.getX();
            initialGridY = selectedShip.getY();
            selectedShip.setPlaced(false);
            selectedShip.setDragging(true);
            offsetX = px - currentScreenPos.x;
            offsetY = py - currentScreenPos.y;
            selectedShip.setX(currentScreenPos.x);
            selectedShip.setY(currentScreenPos.y);

            viewer.update();
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
            selectedShip.setDragging(false);

            boolean snapped = snapShipToGrid(selectedShip);

            if (snapped && model.isValidPlacement(selectedShip)) {
                selectedShip.setPlaced(true);

                model.updateDesktopPlayer();

                if (viewer.getAudioPlayer() != null) {
                    viewer.getAudioPlayer().playSound("src/sounds/laughSound.wav");
                }

                if (!model.isSetupPhase()) {
                    model.startBattlePhase();
                }
            } else {
                selectedShip.setX(initialGridX);
                selectedShip.setY(initialGridY);

                if (initialGridX >= 0 && initialGridX < 10 && initialGridY >= 0 && initialGridY < 10) {
                    selectedShip.setPlaced(true);
                    model.updateDesktopPlayer();
                } else {
                    selectedShip.setPlaced(false);
                }
            }

            selectedShip = null;
            viewer.update();
        }
    }

    private Optional<Ship> findClickedShip(int mouseX, int mouseY) {
        for (Ship ship : model.getPlayerShips()) {
            Point screenPos = getShipScreenPosition(ship);

            int width = ship.isVertical() ? Coordinates.WIDTH : ship.getSize() * Coordinates.WIDTH;
            int height = ship.isVertical() ? ship.getSize() * Coordinates.HEIGHT : Coordinates.HEIGHT;

            if (mouseX >= screenPos.x && mouseX < screenPos.x + width &&
                    mouseY >= screenPos.y && mouseY < screenPos.y + height)
            {
                return Optional.of(ship);
            }
        }
        return Optional.empty();
    }

    private Point getShipScreenPosition(Ship ship) {
        if (ship.isPlaced()) {
            return getScreenPositionFromGrid(ship.getX(), ship.getY());
        }

        if (ship.isDragging()) {
            return new Point(ship.getX(), ship.getY());
        }

        return new Point(ship.getX(), ship.getY());
    }

    private Point getScreenPositionFromOffBoardGrid(int offBoardGridX, int offBoardGridY) {
        return getScreenPositionFromGrid(offBoardGridX, offBoardGridY);
    }

    private boolean snapShipToGrid(Ship ship) {
        int boardX = getPlayerBoardX();
        int boardY = getPlayerBoardY();
        int cellW = Coordinates.WIDTH;
        int cellH = Coordinates.HEIGHT;
        int boardSize = 10;
        int relX = ship.getX() - boardX;
        int relY = ship.getY() - boardY;
        int gridX = Math.round((float)relX / cellW);
        int gridY = Math.round((float)relY / cellH);

        if (gridX < 0 || gridX >= boardSize || gridY < 0 || gridY >= boardSize) {
            return false;
        }

        int size = ship.getSize();
        if (ship.isVertical()) {
            if (gridY + size > boardSize) {
                gridY = boardSize - size;
            }
        } else {
            if (gridX + size > boardSize) {
                gridX = boardX - size;
            }
        }

        gridX = Math.max(0, gridX);
        gridY = Math.max(0, gridY);
        ship.setX(gridX);
        ship.setY(gridY);

        return true;
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