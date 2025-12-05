import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.List;

public class Model {

    private Viewer viewer;
    private Cell cell;
    private int[][] desktopComputer;
    private int[][] desktopPlayer;
    private Canvas canvas;
    private List<Ship> playerShips;
    private int[][] arrayOfIndexes;
    private ComputerLogic computerLogic;

    private ComputerPlayer computerPlayer;

    private Ship[] ships;

    private LevelWindow levelWindow;

    public Model(Viewer viewer) {
        this.viewer = viewer;
        cell = new Cell();
        arrayOfIndexes = new int[2][20];
        computerPlayer = new ComputerPlayer();
        desktopComputer = computerPlayer.getBoard();
        initializationDesktopComputer();
        initializationPlayerShips();
        computerLogic = new ComputerLogic();
        computerLogic.reset();
    }

    public void doAction(int x, int y) {
        if (isSetupPhase()) {
            return;
        }

        if(canvas != null) {
            Point boardPos = canvas.getComputerBoardPosition();
            if(boardPos != null && square(x, y, boardPos.x, boardPos.y)) {
                Cell cell = getRowAndColumn(x - boardPos.x, y - boardPos.y, Coordinates.WIDTH, Coordinates.HEIGHT);

                if (desktopComputer[cell.getRow()][cell.getColumn()] == -1 ||
                        desktopComputer[cell.getRow()][cell.getColumn()] == -9) {
                    return;
                }

                if(desktopComputer[cell.getRow()][cell.getColumn()] == 0) {
                    desktopComputer[cell.getRow()][cell.getColumn()] = -1;
                } else {
                    desktopComputer[cell.getRow()][cell.getColumn()] = -9;
                }

                viewer.update();

                if(won()) {
                    if (levelWindow.getCurrentLevel() >= 3) {
                        viewer.showResult(true);
                        return;
                    }
                    levelWindow.showLevelCompletedWindow();
                }
                computerTurn();

                viewer.update();
                if (lost()) {
                    viewer.showResult(false);
                    return;
                }

            }
        }
    }

    public boolean lost() {
        if (isSetupPhase()) {
            return false;
        }

        for (Ship ship : playerShips) {
            if (ship.isPlaced()) {

                int dx = ship.isVertical() ? 0 : 1;
                int dy = ship.isVertical() ? 1 : 0;
                boolean isSunk = true;

                for (int k = 0; k < ship.getSize(); k++) {
                    int row = ship.getY() + dy * k;
                    int col = ship.getX() + dx * k;

                    if (row >= 0 && row < 10 && col >= 0 && col < 10) {
                        if (desktopPlayer[row][col] > 0) {
                            isSunk = false;
                            break;
                        }
                    }
                }

                if (!isSunk) {
                    return false;
                }
            }
        }

        return true;
    }


    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
        this.levelWindow = new LevelWindow(viewer, canvas);
    }

    public boolean won() {

        for(int index = 0; index < arrayOfIndexes[0].length; index++) {
            int i = arrayOfIndexes[0][index];
            int j = arrayOfIndexes[1][index];
            if(desktopComputer[i][j] != -9) {
                return false;
            }
        }
        return true;
    }
    public ComputerPlayer getComputerPlayer() {
        return computerPlayer;
    }


    private void initializationDesktopComputer() {
        int column = 0;

        for(int i = 0; i < desktopComputer.length; i++) {
            for(int j = 0; j < desktopComputer[i].length; j++) {
                int element = desktopComputer[i][j];
                if(element == 1) {
                    arrayOfIndexes[0][column] = i;
                    arrayOfIndexes[1][column] = j;
                    column = column + 1;
                }
            }
        }

        for(int i = 0; i < desktopComputer.length; i++) {
            for(int j = 0; j < desktopComputer[i].length; j++) {
                int element = desktopComputer[i][j];
                if(element == 2) {
                    arrayOfIndexes[0][column] = i;
                    arrayOfIndexes[1][column] = j;
                    column = column + 1;
                }
            }
        }

        for(int i = 0; i < desktopComputer.length; i++) {
            for(int j = 0; j < desktopComputer[i].length; j++) {
                int element = desktopComputer[i][j];
                if(element == 3) {
                    arrayOfIndexes[0][column] = i;
                    arrayOfIndexes[1][column] = j;
                    column = column + 1;
                }
            }
        }

        for(int i = 0; i < desktopComputer.length; i++) {
            for(int j = 0; j < desktopComputer[i].length; j++) {
                int element = desktopComputer[i][j];
                if(element == 4) {
                    arrayOfIndexes[0][column] = i;
                    arrayOfIndexes[1][column] = j;
                    column = column + 1;
                }
            }
        }
    }

    private boolean square(int mouseX, int mouseY, int boardX, int boardY) {
        int right  = boardX + Coordinates.WIDTH * 10;
        int bottom = boardY + Coordinates.HEIGHT * 10;

        return mouseX >= boardX && mouseX < right &&
                mouseY >= boardY && mouseY < bottom;
    }

    private Cell getRowAndColumn(int x, int y, int width, int height) {
        int row = y / height;
        int column = x / width;
        cell.setRow(row);
        cell.setColumn(column);
        return cell;
    }

    public int[][] getDesktopComputer() {
        return desktopComputer;
    }
    public int[][] getDesktopPlayer() {
        return desktopPlayer;
    }


    public int[][] getArrayOfIndexes() {
        return arrayOfIndexes;
    }

    public static boolean touches(Ship s, int col, int row) {
        int dx = s.isVertical() ? 0 : 1;
        int dy = s.isVertical() ? 1 : 0;

        for (int k = 0; k < s.getSize(); k++) {
            int sCol = s.getX() + dx * k;
            int sRow = s.getY() + dy * k;

            if (Math.abs(sCol - col) <= 1 && Math.abs(sRow - row) <= 1)
                return true;
        }
        return false;
    }

    private void initializationPlayerShips() {
        playerShips = new ArrayList<>();

        int x_offboard = 11;

        playerShips.add(new Ship(x_offboard, 0, false, 4));

        playerShips.add(new Ship(x_offboard, 2, false, 3));
        playerShips.add(new Ship(x_offboard + 4, 2, false, 3));

        playerShips.add(new Ship(x_offboard, 4, false, 2));
        playerShips.add(new Ship(x_offboard + 3, 4, false, 2));
        playerShips.add(new Ship(x_offboard + 6, 4, false, 2));

        playerShips.add(new Ship(x_offboard, 6, false, 1));
        playerShips.add(new Ship(x_offboard + 2, 6, false, 1));
        playerShips.add(new Ship(x_offboard + 4, 6, false, 1));
        playerShips.add(new Ship(x_offboard + 6, 6, false, 1));

        desktopPlayer = new int[10][10];
        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 10; j++) {
                desktopPlayer[i][j] = 0;
            }
        }
    }

    public List<Ship> getPlayerShips() {
        return playerShips;
    }

    public boolean isSetupPhase() {
        for (Ship ship : playerShips) {
            if (!ship.isPlaced()) {
                return true;
            }
        }
        if (levelWindow.getCurrentLevel() == 1 && levelWindow.getWindowState() == 0) {

        }
        return false;
    }

    public void startBattlePhase() {
        System.out.println("Setup phase complete. Starting battle!");
    }

    public void updateDesktopPlayer() {
        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 10; j++) {
                if (desktopPlayer[i][j] > 0) {
                    desktopPlayer[i][j] = 0;
                }
            }
        }

        for (Ship s : playerShips) {
            if (s.isPlaced()) {
                int shipValue = s.getSize();

                int dx = s.isVertical() ? 0 : 1;
                int dy = s.isVertical() ? 1 : 0;

                for (int k = 0; k < s.getSize(); k++) {
                    int col = s.getX() + dx * k;
                    int row = s.getY() + dy * k;

                    if (row >= 0 && row < 10 && col >= 0 && col < 10) {
                        if (desktopPlayer[row][col] != -9 && desktopPlayer[row][col] != -1) {
                            desktopPlayer[row][col] = shipValue;
                        }
                    }
                }
            }
        }
    }

    private boolean isShipSunk(int hitRow, int hitCol) {
        for (Ship s : playerShips) {
            if (!s.isPlaced()) continue;

            int dx = s.isVertical() ? 0 : 1;
            int dy = s.isVertical() ? 1 : 0;

            for (int k = 0; k < s.getSize(); k++) {
                int row = s.getY() + dy * k;
                int col = s.getX() + dx * k;

                if (row == hitRow && col == hitCol) {
                    for (int j = 0; j < s.getSize(); j++) {
                        int rr = s.getY() + dy * j;
                        int cc = s.getX() + dx * j;

                        if (desktopPlayer[rr][cc] != -9) {
                            return false;
                        }
                    }

                    return true;
                }
            }
        }

        return false;
    }

    private void computerTurn() {
        if (desktopPlayer == null) {
            return;
        }

        if (computerLogic == null) {
            computerLogic = new ComputerLogic();
            computerLogic.reset();
        }

        boolean canShoot = true;

        while (canShoot) {
            int[] shot = computerLogic.getNextShot(desktopPlayer);
            if (shot == null) {
                break;
            }

            int row = shot[0];
            int col = shot[1];

            int cellValue = desktopPlayer[row][col];
            boolean isHit = cellValue > 0;
            boolean sunk = false;

            if (!isHit) {
                desktopPlayer[row][col] = -1;
            } else {
                desktopPlayer[row][col] = -9;
                sunk = isShipSunk(row, col);
            }

            computerLogic.onShotResult(row, col, isHit, sunk, desktopPlayer);

            if (!isHit) {
                canShoot = false;
            }

        }
    }

    public boolean isValidPlacement(Ship ship) {
        int startX = ship.getX();
        int startY = ship.getY();
        int size = ship.getSize();
        boolean isVertical = ship.isVertical();

        if (startX < 0 || startY < 0) return false;
        if (isVertical) {
            if (startY + size > 10 || startX >= 10) return false;
        } else {
            if (startX + size > 10 || startY >= 10) return false;
        }

        for (Ship otherShip : playerShips) {
            if (otherShip != ship && otherShip.isPlaced()) {

                int dx = isVertical ? 0 : 1;
                int dy = isVertical ? 1 : 0;

                for (int k = 0; k < size; k++) {
                    int col = startX + dx * k;
                    int row = startY + dy * k;


                    if (touches(otherShip, col, row)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }


    public Viewer getViewer() {
        return viewer;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public Ship[] getShips() {
        return ships;
    }

    public LevelWindow getLevelWindow() {
        return levelWindow;
    }

}