package common;

import java.util.Random;

public class ComputerPlayer {
    private Ship[] ships;
    private final int[][] board;

    public ComputerPlayer() {
        board = new int[10][10];
    }

    public void placeShip(Ship s) {
        for (int i = 0; i < s.getSize(); i++) {
            int cx = s.getX() + (s.isVertical() ? i : 0);
            int cy = s.getY() + (s.isVertical() ? 0 : i);
            board[cx][cy] = s.getSize();
        }
    }

    public boolean isValidPlacement(Ship s) {
        int x = s.getX();
        int y = s.getY();
        int size = s.getSize();
        boolean vertical = s.isVertical();

        if (vertical) {
            if (x + size > 10) return false;
        } else {
            if (y + size > 10) return false;
        }

        for (int i = 0; i < size; i++) {
            int cx = x + (vertical ? i : 0);
            int cy = y + (vertical ? 0 : i);

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int nx = cx + dx;
                    int ny = cy + dy;
                    if (nx >= 0 && nx < 10 && ny >= 0 && ny < 10) {
                        if (board[nx][ny] == 1) {
                            return false;
                        } else if (board[nx][ny] == 2) {
                            return false;
                        } else if (board[nx][ny] == 3) {
                            return false;
                        } else if (board[nx][ny] == 4) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public void generateBoard() {
        Random rnd = new Random();

        int[] sizes = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};
        int shipIndex = 0;

        ships = new Ship[10];

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

    public int[][] getBoard() {
        return board;
    }
}
