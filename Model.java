import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Model {
    // Cell states
    public static final int EMPTY = 0;
    public static final int SHIP = 1;
    public static final int HIT = 2;
    public static final int MISS = 3;

    private final int size = 10;

    // Boards
    private int[][] playerBoard;
    private int[][] computerBoard;

    private boolean playerTurn = true;
    private boolean gameOver = false;
    private String statusText = "Ваш ход: стреляйте по правому полю";

    public Model(Viewer viewer) {
        playerBoard = new int[size][size];
        computerBoard = new int[size][size];

        // Place ships for both sides
        placeAllShipsRandomly(playerBoard);
        placeAllShipsRandomly(computerBoard);
    }

    public int[][] getPlayerBoard() {
        return playerBoard;
    }

    public int[][] getComputerBoard() {
        return computerBoard;
    }

    public boolean isPlayerTurn() {
        return playerTurn;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public String getStatusText() {
        return statusText;
    }

    // Player shoots at computer
    public ShotResult playerShoot(int x, int y) {
        if (gameOver || !playerTurn) return ShotResult.IGNORED;
        ShotResult res = shootAt(computerBoard, x, y);
        if (res == ShotResult.REPEAT || res == ShotResult.IGNORED) return res;
        if (checkAllShipsSunk(computerBoard)) {
            gameOver = true;
            statusText = "Вы победили!";
            return res;
        }
        if (res == ShotResult.MISS) {
            playerTurn = false;
            statusText = "Промах. Ход компьютера";
            // Computer makes a move immediately (simple random AI)
            computerAutoMove();
        } else {
            statusText = "Попадание! Стреляйте ещё";
        }
        return res;
    }

    private void computerAutoMove() {
        if (gameOver) return;
        Random rnd = new Random();
        while (true) {
            int x = rnd.nextInt(size);
            int y = rnd.nextInt(size);
            ShotResult res = shootAt(playerBoard, x, y);
            if (res == ShotResult.REPEAT || res == ShotResult.IGNORED) continue;
            if (checkAllShipsSunk(playerBoard)) {
                gameOver = true;
                statusText = "Компьютер победил";
                playerTurn = false;
                return;
            }
            if (res == ShotResult.MISS) {
                statusText = "Компьютер промахнулся. Ваш ход";
                playerTurn = true;
                return;
            } else {
                // Hit: computer shoots again
                statusText = "Компьютер попал и стреляет ещё";
            }
        }
    }

    private ShotResult shootAt(int[][] board, int x, int y) {
        if (x < 0 || x >= size || y < 0 || y >= size) return ShotResult.IGNORED;
        int cell = board[y][x];
        if (cell == HIT || cell == MISS) return ShotResult.REPEAT;
        if (cell == SHIP) {
            board[y][x] = HIT;
            return ShotResult.HIT;
        } else {
            board[y][x] = MISS;
            return ShotResult.MISS;
        }
    }

    private boolean checkAllShipsSunk(int[][] board) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == SHIP) return false;
            }
        }
        return true;
    }

    public enum ShotResult { HIT, MISS, REPEAT, IGNORED }

    // Ship placement logic (classic set: 1x4, 2x3, 3x2, 4x1)
    private void placeAllShipsRandomly(int[][] board) {
        int[] ships = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};
        Random rnd = new Random();
        for (int len : ships) {
            boolean placed = false;
            int attempts = 0;
            while (!placed && attempts < 1000) {
                attempts++;
                boolean horizontal = rnd.nextBoolean();
                int x = rnd.nextInt(size);
                int y = rnd.nextInt(size);
                if (canPlace(board, x, y, len, horizontal)) {
                    placeShip(board, x, y, len, horizontal);
                    placed = true;
                }
            }
            if (!placed) {
                // Fallback: restart placement to avoid dead-end
                clearBoard(board);
                // restart from beginning
                placeAllShipsRandomly(board);
                return;
            }
        }
    }

    private void clearBoard(int[][] board) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = EMPTY;
            }
        }
    }

    private boolean canPlace(int[][] board, int x, int y, int length, boolean horizontal) {
        int dx = horizontal ? 1 : 0;
        int dy = horizontal ? 0 : 1;
        int endX = x + dx * (length - 1);
        int endY = y + dy * (length - 1);
        if (endX < 0 || endX >= size || endY < 0 || endY >= size) return false;

        for (int k = 0; k < length; k++) {
            int cx = x + dx * k;
            int cy = y + dy * k;
            if (!isCellAvailable(board, cx, cy)) return false;
        }
        return true;
    }

    private void placeShip(int[][] board, int x, int y, int length, boolean horizontal) {
        int dx = horizontal ? 1 : 0;
        int dy = horizontal ? 0 : 1;
        for (int k = 0; k < length; k++) {
            int cx = x + dx * k;
            int cy = y + dy * k;
            board[cy][cx] = SHIP;
        }
    }

    // Cell must be empty and all 8-neighbors must be empty (no adjacency)
    private boolean isCellAvailable(int[][] board, int x, int y) {
        if (board[y][x] != EMPTY) return false;
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;
                if (nx < 0 || ny < 0 || nx >= size || ny >= size) continue;
                if (board[ny][nx] != EMPTY) return false;
            }
        }
        return true;
    }
}
