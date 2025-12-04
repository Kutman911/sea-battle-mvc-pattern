import java.awt.*;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.List;

public class Model {

    private Viewer viewer;
    private Cell cell;
    private int[][] desktopComputer;
    private int[][] desktopPlayer;
    private int stepX;
    private int stepY;
    private Canvas canvas;
    private List<Ship> playerShips;
    private int[][] arrayOfIndexes;
    private ComputerLogic computerLogic;
    private ComputerPlayer computerPlayer;

    private Ship[] ships;

    private int currentLevel = 1;
    private int levelWindow = 0;

    public Model(Viewer viewer) {
        this.viewer = viewer;
        cell = new Cell();
        arrayOfIndexes = new int[2][20];
        computerPlayer = new ComputerPlayer();
        desktopComputer = computerPlayer.getBoard();
        initializationDesktopComputer();
        initializationPlayerShips();
        stepX = 0;
        stepY = 0;
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

                if (stepY < 10 && stepX < 10) {

                    if (desktopPlayer[stepY][stepX] > 0) {
                        desktopPlayer[stepY][stepX] = -9;
                    } else if (desktopPlayer[stepY][stepX] == 0) {
                        desktopPlayer[stepY][stepX] = -1;
                    }

                    do {
                        stepX = stepX + 1;
                        if (stepX >= 10) {
                            stepX = 0;
                            stepY = stepY + 1;
                        }
                    } while (stepY < 10 && (desktopPlayer[stepY][stepX] == -1 || desktopPlayer[stepY][stepX] == -9));
                }

                viewer.update();

                if(won()) {
                    if (currentLevel >= 3) {
                        viewer.showResult(true);
                        return;
                    }
                    showLevelCompletedWindow();
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

    private boolean square(int x, int y, int i, int y1) {
        if((Coordinates.X <= x) && (Coordinates.Y <= y) &&
                (x <= (Coordinates.X + (Coordinates.WIDTH * 10))) &&
                (y <= (Coordinates.Y + (Coordinates.HEIGHT * 10)))) {
            return true;
        } else {
            return false;
        }
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
        if (currentLevel == 1 && levelWindow == 0) {

        }
        return false;
    }

    public void startBattlePhase() {
        System.out.println("Setup phase complete. Starting battle!");
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getLevelWindow() {
        return levelWindow;
    }

    public void setLevelWindow(int state) {
        this.levelWindow = state;
        viewer.update();
    }

    public void nextLevel() {
        currentLevel = currentLevel + 1;

        if (currentLevel > 3) {
            viewer.showResult(true);

            levelWindow = 3;

            if (canvas != null) {
                canvas.startLevelWindowAnimationFadeIn();
            }

            viewer.update();
            return;
        }

        showLevelStartWindow(currentLevel);
    }

    public void showLevelStartWindow(int level) {
        currentLevel = level;
        levelWindow = 1;

        if (canvas != null) {
            canvas.startLevelWindowAnimationFadeIn();
        }

        viewer.update();

        new Timer().schedule(
                new TimerTask() {
                    public void run() {
                        if (canvas != null) {
                            canvas.startLevelWindowAnimationFadeOut();
                        }

                        new Timer().schedule(new TimerTask() {
                            public void run() {
                                levelWindow = 0;
                                viewer.update();
                            }
                        }, 400);
                    }
                },
                2400
        );
    }

    public void showLevelCompletedWindow() {
        levelWindow = 2;

        if (canvas != null) {
            canvas.startLevelWindowAnimationFadeIn();
        }

        viewer.update();

        new Timer().schedule(
                new TimerTask() {
                    public void run() {
                        if (canvas != null) {
                            canvas.startLevelWindowAnimationFadeOut();
                        }

                        new Timer().schedule(new TimerTask() {
                            public void run() {
                                nextLevel();
                            }
                        }, 400);
                    }
                },
                2400
        );
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

    private boolean isShipSunk(int row, int col) {
        if (desktopPlayer == null) {
            return false;
        }

        if (desktopPlayer[row][col] != -9) {
            return false;
        }

        int[][] dirs = {
                {-1, 0}, 
                {1, 0},
                {0, -1},
                {0, 1}
        };

        for (int d = 0; d < dirs.length; d++) {
            int dr = dirs[d][0];
            int dc = dirs[d][1];

            int r = row + dr;
            int c = col + dc;

            while (r >= 0 && r < 10 && c >= 0 && c < 10) {
                int val = desktopPlayer[r][c];

                if (val == 0 || val == -1) {
                    break;
                }

                if (val > 0) {
                    return false;
                }

                r += dr;
                c += dc;
            }
        }

        return true;
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

}