import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Random;

public class ComputerLogic {

    private static final int BOARD_SIZE = 10;

    private enum Mode {
        HUNT,
        TARGET
    }

    private Mode mode;
    private boolean[][] tried;
    private List<int[]> hitsCurrentShip;
    private Deque<int[]> targetQueue;

    public ComputerLogic() {
        mode = Mode.HUNT;
        tried = new boolean[BOARD_SIZE][BOARD_SIZE];
        hitsCurrentShip = new ArrayList<>();
        targetQueue = new ArrayDeque<>();
    }

    public void reset() {
        mode = Mode.HUNT;
        hitsCurrentShip.clear();
        targetQueue.clear();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                tried[row][col] = false;
            }
        }
    }

    public int[] getNextShot() {
        Random rnd = new Random();
        while (!targetQueue.isEmpty()) {
            int[] p = targetQueue.pollFirst();
            int row = p[0];
            int col = p[1];
            if (!tried[row][col]) {
                return p;
            }
        }

        if (mode == Mode.TARGET) {
            mode = Mode.HUNT;
            hitsCurrentShip.clear();
        }
        List<int[]> candidates = new ArrayList<>();

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (!tried[row][col]) {
                    candidates.add(new int[]{row, col});
                }
            }
        }
        if (candidates.isEmpty()) {
            return null;
        }

        int idx = rnd.nextInt(candidates.size());
        return candidates.get(idx);
    }

    public void onShotResult(int row, int col, boolean isHit, int[][] desktopPlayer, boolean sunk) {

        tried[row][col] = true;

        if (!isHit) {
            return;
        }

        hitsCurrentShip.add(new int[]{row, col});

        if (sunk) {
            hitsCurrentShip.clear();
            targetQueue.clear();
            mode = Mode.HUNT;
            return;
        }

        mode = Mode.TARGET;
        rebuildTargetQueue();
    }


    private void rebuildTargetQueue() {
        targetQueue.clear();

        if (hitsCurrentShip.isEmpty()) {
            return;
        }

        if (hitsCurrentShip.size() == 1) {
            int[] h = hitsCurrentShip.get(0);
            int row = h[0];
            int col = h[1];

            addTarget(row - 1, col);
            addTarget(row + 1, col);
            addTarget(row, col - 1);
            addTarget(row, col + 1);
        } else {
            int[] h0 = hitsCurrentShip.get(0);
            int[] h1 = hitsCurrentShip.get(1);

            boolean vertical = (h0[0] != h1[0]);

            if (vertical) {
                int col = h0[1];
                int minRow = h0[0];
                int maxRow = h0[0];

                for (int[] h : hitsCurrentShip) {
                    if (h[0] < minRow) {
                        minRow = h[0];
                    }
                    if (h[0] > maxRow) {
                        maxRow = h[0];
                    }
                }

                addTarget(minRow - 1, col);
                addTarget(maxRow + 1, col);
            } else {
                int row = h0[0];
                int minCol = h0[1];
                int maxCol = h0[1];

                for (int[] h : hitsCurrentShip) {
                    if (h[1] < minCol) {
                        minCol = h[1];
                    }
                    if (h[1] > maxCol) {
                        maxCol = h[1];
                    }
                }

                addTarget(row, minCol - 1);
                addTarget(row, maxCol + 1);
            }
        }
    }

    private void addTarget(int row, int col) {
        if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) return;
        if (tried[row][col]) {
            return;
        }

        for (int[] p : targetQueue) {
            if (p[0] == row && p[1] == col) {
                return;
            }
        }

        targetQueue.addLast(new int[]{row, col});
    }

}