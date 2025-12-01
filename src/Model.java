    import java.awt.*;
    import java.util.Random;
    import java.util.Timer;
    import java.util.TimerTask;
    import java.util.ArrayList;

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
        private int levelWindow = 0;
    
        public Model(Viewer viewer) {
            this.viewer = viewer;
            cell = new Cell();
            arrayOfIndexes = new int[2][20];
            initializationDesktopComputer();
            initializationPlayerShips();
            stepX = 0;
            stepY = 0;
        }

        public void doAction(int x, int y) {
            if (isSetupPhase()) return;

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
            int dx = s.isVertical() ? 0 : 1;
            int dy = s.isVertical() ? 1 : 0;

            for (int k = 0; k < s.getSize(); k++) {
                int col = s.getX() + dx * k;
                int row = s.getY() + dy * k;

                if (row < 0 || row >= 10 || col < 0 || col >= 10) return false;

                for (Ship other : playerShips) {
                    if (other == s || !other.isPlaced()) continue;

                    if (touches(other, col, row)) {
                        return false;
                    }
                }
            }
            return true;
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
            playerShips = new java.util.ArrayList<>();

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
    
        public java.util.List<Ship> getPlayerShips() {
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
                levelWindow = 3;

                if (canvas != null) {
                    canvas.startLevelWindowAnimation();
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
                canvas.startLevelWindowAnimation();
            }

            viewer.update();

            new Timer().schedule(
                    new TimerTask() {
                        public void run() {
                            if (canvas != null) {
                                canvas.startLevelWindowFadeOut();
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
                canvas.startLevelWindowAnimation();
            }

            viewer.update();

            new Timer().schedule(
                    new TimerTask() {
                        public void run() {
                            if (canvas != null) {
                                canvas.startLevelWindowFadeOut();
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
                            desktopPlayer[row][col] = shipValue;
                        }
                    }
                }
            }
        }

    
        public Viewer getViewer() {
            return viewer;
        }
    
        public Canvas getCanvas() {
            return canvas;
        }
    
    }