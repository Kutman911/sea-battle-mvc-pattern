import java.awt.*;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Model {

    private Viewer viewer;
    private Cell cell;
    private int[][] desktopComputer;
    private int[][] desktopPlayer;
    private int stepX;
    private int stepY;
    private Canvas canvas;
    private java.util.List<Ship> playerShips;
    private int[][] arrayOfIndexes;
//    0 - No window
//    1 - Level X
//    2 - Level X Completed
//    3 - Game Completed
    private int currentLevel = 1;
    private int levelWindow = 1;

    public Model(Viewer viewer) {
        this.viewer = viewer;
        cell = new Cell();
        arrayOfIndexes = new int[2][20];
        initializationDesktopComputer();
        initializationPlayerShips();
    }

    public void doAction(int x, int y) {
        if(canvas != null) {
            Point boardPos = canvas.getComputerBoardPosition();
            if(boardPos != null && square(x, y, boardPos.x, boardPos.y)) {
                Cell cell = getRowAndColumn(x - boardPos.x, y - boardPos.y, Coordinates.WIDTH, Coordinates.HEIGHT);
                if(desktopComputer[cell.getRow()][cell.getColumn()] == 0) {
                    desktopComputer[cell.getRow()][cell.getColumn()] = -1;
                } else if(
                        desktopComputer[cell.getRow()][cell.getColumn()] == 1 ||
                                desktopComputer[cell.getRow()][cell.getColumn()] == 2 ||
                                desktopComputer[cell.getRow()][cell.getColumn()] == 3 ||
                                desktopComputer[cell.getRow()][cell.getColumn()] == 4
                ) {
                    desktopComputer[cell.getRow()][cell.getColumn()] = -9;
                }

                desktopPlayer[stepY][stepX] = -1;

                stepX = stepX + 1;
                stepY = stepY + 1;

                viewer.update();
                if(won()) {
                    showLevelCompletedWindow();
                }
            }
        }
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

    private void initializationDesktopComputer() {
        desktopComputer = new int[][] {
                {1, 0, 0, 2, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 2, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 0, 2, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 2, 0, 0, 4, 4, 4, 4},
                {0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 0, 0, 3, 3, 3, 0, 3, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 3, 0},
                {2, 2, 0, 0, 0, 0, 0, 0, 3, 0}
        };

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

    private void printDesktopComputer() {
        for (int i = 0; i < desktopComputer.length; i++) {
            for (int j = 0; j < desktopComputer[i].length; j++) {
                int element = desktopComputer[i][j];
                System.out.print(element + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public boolean isValidPlacement(Ship s) {

        if (s.isVertical()) {
            if (s.getX() + s.getSize() > 10) return false;
        } else {
            if (s.getY() + s.getSize() > 10) return false;
        }

        for (int i = 0; i < s.getSize(); i++) {
            int cx = s.getX() + (s.isVertical() ? i : 0);
            int cy = s.getY() + (s.isVertical() ? 0 : i);

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int nx = cx + dx;
                    int ny = cy + dy;

                    if (nx >= 0 && nx < 10 && ny >= 0 && ny < 10) {
                        if (desktopComputer[nx][ny] == 1) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public void placeShip(Ship s) {
        for (int i = 0; i < s.getSize(); i++) {
            int cx = s.getX() + (s.isVertical() ? i : 0);
            int cy = s.getY() + (s.isVertical() ? 0 : i);
            desktopComputer[cx][cy] = 1;
        }
    }

    public void generateBoard() {
        Random rnd = new Random();

        int[] sizes = {4,3,3,2,2,2,1,1,1,1};
        int shipIndex = 0;
        Ship[] ships = new Ship[10];

        for (int size : sizes) {
            boolean placed = false;

            while (!placed) {
                boolean vertical = rnd.nextBoolean();
                int x = rnd.nextInt(10);
                int y = rnd.nextInt(10);

                Ship s = new Ship(x, y, vertical, size);

                if (isValidPlacement(s)) {
                    placeShip(s);
                    ships[shipIndex++] = s;
                    placed = true;
                }
            }
        }
    }


    private void initializationPlayerShips() {
        playerShips = new java.util.ArrayList<>();

        int x_offboard = 12;

        playerShips.add(new Ship(x_offboard, 0, false, 4));

        playerShips.add(new Ship(x_offboard, 2, false, 3));
        playerShips.add(new Ship(x_offboard, 4, false, 3));

        playerShips.add(new Ship(x_offboard, 6, false, 2));
        playerShips.add(new Ship(x_offboard, 7, false, 2));
        playerShips.add(new Ship(x_offboard, 8, false, 2));

        playerShips.add(new Ship(x_offboard, 9, false, 1));
        playerShips.add(new Ship(x_offboard + 1, 9, false, 1));
        playerShips.add(new Ship(x_offboard + 2, 9, false, 1));
        playerShips.add(new Ship(x_offboard + 3, 9, false, 1));

        desktopPlayer = new int[10][10];
        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 10; j++) {
                desktopPlayer[i][j] = 0;
            }
        }
    }

    public java.util.List<Ship> getPlayerShips() {
        return playerShips;
    }

    public boolean isSetupPhase() {
        for (Ship ship : playerShips) {
            if (!ship.isPlaced()) {
                return true;
            }
        }
        return false;
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
            levelWindow = 3;
            viewer.update();
            return;
        }

        showLevelStartWindow(currentLevel);
    }

    public void showLevelStartWindow(int level) {
        currentLevel = level;
        levelWindow = 1;
        viewer.update();

        new Timer().schedule(
                new TimerTask() {
                    public void run() {
                        levelWindow = 0;
                        viewer.update();
                    }
                },
                1800
        );
    }

    public void showLevelCompletedWindow() {
        levelWindow = 2;
        viewer.update();

        new Timer().schedule(
                new TimerTask() {
                    public void run() {
                        nextLevel();
                    }
                },
                1800
        );
    }

    public Viewer getViewer() {
        return viewer;
    }

    public Canvas getCanvas() {
        return canvas;
    }

}