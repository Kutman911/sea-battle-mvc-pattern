package model;

import canvas.Canvas;
import common.*;
import viewer.Viewer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Model {

    private final Viewer viewer;
    private Cell cell;
    private int[][] desktopComputer;
    private int[][] desktopPlayer;
    private List<Ship> playerShips;
    private int[][] arrayOfIndexes;
    private ComputerLogic computerLogic;
    private ComputerPlayer computerPlayer;
    private LevelWindow levelWindow;
    private boolean playerTurn;
    private final List<Move> moveHistory = new ArrayList<>();
    private boolean battleStarted;

    private int totalComputerShipCells;
    private int destroyedComputerShipCells;

    private int totalPlayerShipCells;
    private int destroyedPlayerShipCells;


    public Model(Viewer viewer) {
        this.viewer = viewer;
        cell = new Cell();
        arrayOfIndexes = new int[2][20];
        computerPlayer = new ComputerPlayer();
        computerPlayer.generateBoard();
        desktopComputer = computerPlayer.getBoard();
        initializationDesktopComputer();
        initializationDesktopComputer();
        totalComputerShipCells = arrayOfIndexes[0].length;
        destroyedComputerShipCells = 0;
        initializationPlayerShips();
        totalPlayerShipCells = 0;
        for (Ship s : playerShips) {
            totalPlayerShipCells += s.getSize();
        }
        destroyedPlayerShipCells = 0;
        computerLogic = new ComputerLogic();
        computerLogic.reset();
        battleStarted = false;
    }

    public void doAction(int x, int y) {
        if (isSetupPhase() || !battleStarted) {
            return;
        }

        if(viewer.getCanvas() != null) {
            Point boardPos = viewer.getCanvas().getComputerBoardPosition();
            if (boardPos != null && square(x, y, boardPos.x, boardPos.y)) {
                Cell cell = getRowAndColumn(x - boardPos.x,y - boardPos.y, Coordinates.WIDTH, Coordinates.HEIGHT);

                int row = cell.getRow();
                int col = cell.getColumn();

                Boolean isHit = playerTurn(row, col);
                if (isHit == null) {
                    return;
                }
                viewer.update();

                if (won()) {
                    levelWindow.showLevelCompletedWindow();
                    if (viewer.getAudioPlayer() != null) {
                        viewer.getAudioPlayer().playSound("src/sounds/winSound.wav");
                    }

                    return;
                }
                if (!isHit) {
                    setPlayerTurn(false);
                    viewer.getCanvas().scheduleComputerTurn(viewer);
                    viewer.update();
                    if (lost()) {
                        viewer.getCanvas().showResult(false, viewer);
                    }
                }
            }

        }
    }

    private Boolean playerTurn(int row, int col) {
        if (desktopComputer[row][col] < 0) {
            return null;
        }
        boolean isHit = desktopComputer[row][col] > 0;
        boolean sunk = false;

        if (!isHit) {
            desktopComputer[row][col] = -1;

            if (viewer.getAudioPlayer() != null) {
                viewer.getAudioPlayer().playSound("src/sounds/waterSound.wav");
            }
            
        } else {
            desktopComputer[row][col] = -9;
            destroyedComputerShipCells++;
            sunk = markSunkIfComplete(desktopComputer, row, col);

            if (sunk) {
                if (viewer.getAudioPlayer() != null) {
                    viewer.getAudioPlayer().playSound("src/sounds/crashSound.wav");
                }
            } else {
                if (viewer.getAudioPlayer() != null) {
                    viewer.getAudioPlayer().playSound("src/sounds/shotSound.wav");
                }
            }
            // If the whole ship is destroyed, convert its parts to SUNK (-8)
//            markSunkIfComplete(desktopComputer, row, col);
        }

        addMove(new Move(
                Move.Who.PLAYER,
                row,
                col,
                isHit,
                sunk
        ));

        return isHit;

    }
    public void computerTurn() {
        boolean canShoot = true;

        while (canShoot) {
            int[] shot = computerLogic.getNextShot();
            if (shot == null) {
                break;
            }

            int row = shot[0];
            int col = shot[1];

            boolean isHit = desktopPlayer[row][col] > 0;
            boolean sunk = false;

            if (!isHit) {
                desktopPlayer[row][col] = -1;

                if (viewer.getAudioPlayer() != null) {
                    viewer.getAudioPlayer().playSound("src/sounds/waterSound.wav");
                }

            } else {
                desktopPlayer[row][col] = -9;
                destroyedPlayerShipCells++;

                sunk = markSunkIfComplete(desktopPlayer, row, col);

                if (viewer.getAudioPlayer() != null) {
                    viewer.getAudioPlayer().playSound(
                            sunk ? "src/sounds/crashSound.wav"
                                    : "src/sounds/shotSound.wav"
                    );
                }
            }

            addMove(new Move(
                    Move.Who.COMPUTER,
                    row,
                    col,
                    isHit,
                    sunk
            ));

            computerLogic.onShotResult(row, col, isHit, desktopPlayer, sunk);

            if (!isHit) {
                canShoot = false;
            }
        }

        setPlayerTurn(true);
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


    public void setCanvas(canvas.Canvas canvas) {
        this.levelWindow = new LevelWindow(viewer, canvas, this);
    }

    public boolean won() {

        for(int index = 0; index < arrayOfIndexes[0].length; index++) {
            int i = arrayOfIndexes[0][index];
            int j = arrayOfIndexes[1][index];
            int v = desktopComputer[i][j];
            if(!(v == -9 || v == -8)) {
                return false;
            }
        }
        return true;
    }

    private boolean markSunkIfComplete(int[][] board, int row, int col) {
        final int SIZE = 10;

        boolean horizontal = false;
        boolean vertical = false;

        if (col - 1 >= 0) {
            int v = board[row][col - 1];
            if (v > 0 || v == -9 || v == -8) horizontal = true;
        }
        if (col + 1 < SIZE) {
            int v = board[row][col + 1];
            if (v > 0 || v == -9 || v == -8) horizontal = true;
        }
        if (row - 1 >= 0) {
            int v = board[row - 1][col];
            if (v > 0 || v == -9 || v == -8) vertical = true;
        }
        if (row + 1 < SIZE) {
            int v = board[row + 1][col];
            if (v > 0 || v == -9 || v == -8) vertical = true;
        }

        boolean useHorizontal = horizontal && !vertical ? true : (!horizontal && vertical ? false : true);

        java.util.List<int[]> cells = new java.util.ArrayList<>();
        cells.add(new int[]{row, col});

        if (useHorizontal) {
            int c = col - 1;
            while (c >= 0) {
                int v = board[row][c];
                if (v > 0 || v == -9 || v == -8) {
                    cells.add(new int[]{row, c});
                    c--;
                } else {
                    break;
                }
            }
            c = col + 1;
            while (c < SIZE) {
                int v = board[row][c];
                if (v > 0 || v == -9 || v == -8) {
                    cells.add(new int[]{row, c});
                    c++;
                } else {
                    break;
                }
            }
        } else {
            int r = row - 1;
            while (r >= 0) {
                int v = board[r][col];
                if (v > 0 || v == -9 || v == -8) {
                    cells.add(new int[]{r, col});
                    r--;
                } else {
                    break;
                }
            }
            r = row + 1;
            while (r < SIZE) {
                int v = board[r][col];
                if (v > 0 || v == -9 || v == -8) {
                    cells.add(new int[]{r, col});
                    r++;
                } else {
                    break;
                }
            }
        }

        boolean anyPositive = false;
        boolean anyHit = false;
        for (int[] p : cells) {
            int v = board[p[0]][p[1]];
            if (v > 0) anyPositive = true;
            if (v == -9) anyHit = true;
        }

        if (anyPositive) {
            return false;
        }

        if (anyHit) {
            for (int[] p : cells) {
                if (board[p[0]][p[1]] == -9) {
                    board[p[0]][p[1]] = -8;
                }
            }
        }
        for (int[] p : cells) {
            int r = p[0];
            int c = p[1];

            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {

                    int nr = r + dr;
                    int nc = c + dc;

                    if (nr < 0 || nr >= 10 || nc < 0 || nc >= 10)
                        continue;

                    int value = board[nr][nc];

                    if (value == 0) {
                        board[nr][nc] = -1;
                    }
                }
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
        return !battleStarted;
    }


    public boolean areAllShipsPlaced() {
        for (Ship ship : playerShips) {
            if (!ship.isPlaced()) {
                return false;
            }
        }
        return true;
    }

    public void startBattlePhase() {
        if (!areAllShipsPlaced()) {
            return;
        }
        System.out.println("Setup phase complete. Starting battle!");
        setPlayerTurn(true);
        battleStarted = true;

        viewer.showHistoryPanel();
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

    /**
     * Randomly place all player's ships on the board.
     * Can be called multiple times and also works if some ships are already placed.
     * Only works during setup phase (before the battle starts).
     */
    public void randomizePlayerShips() {
        if (!isSetupPhase()) {
            return;
        }

        // Clear current placements
        for (Ship s : playerShips) {
            s.setPlaced(false);
        }

        // Place ships from largest to smallest to improve success chance
        List<Ship> shipsToPlace = new ArrayList<>(playerShips);
        shipsToPlace.sort((a, b) -> Integer.compare(b.getSize(), a.getSize()));

        Random rand = new Random();
        for (Ship ship : shipsToPlace) {
            boolean placed = false;
            // Try a reasonable number of times for each ship
            for (int attempt = 0; attempt < 2000 && !placed; attempt++) {
                boolean vertical = rand.nextBoolean();
                int maxX = vertical ? 9 : 10 - ship.getSize();
                int maxY = vertical ? 10 - ship.getSize() : 9;
                int x = rand.nextInt(Math.max(1, maxX + 1));
                int y = rand.nextInt(Math.max(1, maxY + 1));

                // Temporarily set properties to validate
                ship.setX(x);
                ship.setY(y);
                if (ship.isVertical() != vertical) {
                    ship.rotate();
                }

                if (isValidPlacement(ship)) {
                    ship.setPlaced(true);
                    placed = true;
                }
            }

            // Fallback: if for some reason not placed, reset off-board state
            if (!placed) {
                int x_offboard = 11;
                ship.setX(x_offboard);
                ship.setY(0);
                ship.setPlaced(false);
            }
        }

        updateDesktopPlayer();
        viewer.update();
    }

    public Canvas getCanvas() {
        return viewer.getCanvas();
    }

    public LevelWindow getLevelWindow() {
        return levelWindow;
    }

    public void resetGame() {
        computerPlayer = new ComputerPlayer();
        computerPlayer.generateBoard();
        desktopComputer = computerPlayer.getBoard();
        initializationDesktopComputer();

        destroyedComputerShipCells = 0;
        destroyedPlayerShipCells = 0;

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                desktopPlayer[i][j] = 0;
            }
        }

        initializationPlayerShips();

        computerLogic = new ComputerLogic();
        computerLogic.reset();
        battleStarted = false;
        playerTurn = false;
        moveHistory.clear();
        viewer.clearHistory();

        viewer.update();

        viewer.getCanvas().revalidate();
        viewer.getCanvas().repaint();
        viewer.hideHistoryPanel();
    }

    public boolean isPlayerTurn() {
        return playerTurn;
    }

    public void setPlayerTurn(boolean playerTurn) {
        this.playerTurn = playerTurn;
    }

    public List<Move> getMoveHistory() {
        return moveHistory;
    }

    public void addMove(Move move) {
        moveHistory.add(move);
        viewer.addHistoryMove(move);
    }



    public boolean isBattleStarted() {
        return battleStarted;
    }

    public int getDestroyedComputerShips() { return destroyedComputerShipCells; }
    public int getTotalComputerShipCells() { return totalComputerShipCells; }

    public int getDestroyedPlayerShips() { return destroyedPlayerShipCells; }
    public int getTotalPlayerShipCells() { return totalPlayerShipCells; }

    public Viewer getViewer() {
        return viewer;
    }
}