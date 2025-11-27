public class Model {

    private Viewer viewer;
    private Cell cell;
    private int[][] desktopComputer;
    private int[][] desktopPlayer;
    private int[][] arrayOfIndexes;
    private int stepX;
    private int stepY;

    public Model(Viewer viewer) {
        this.viewer = viewer;
        cell = new Cell();
        arrayOfIndexes = new int[2][20];
        initializationDesktopComputer();
        initializationDesktopPlayer();
    }

    public void doAction(int x, int y) {
        if(square(x, y)) {
            Cell cell = getRowAndColumn(x - Coordinates.X, y - Coordinates.Y, Coordinates.WIDTH, Coordinates.HEIGHT);
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

            }
        }
    }

    private void initializationDesktopPlayer() {
        desktopPlayer = new int[][] {
                {1, 0, 0, 2, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 2, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 0, 2, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 2, 0, 0, 4, 4, 4, 4},
                {0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 0, 0, 3, 3, 3, 0, 3, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 3, 0},
                {2, 2, 0, -1, 0, 0, 0, 0, 3, 0}
        };
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

    private boolean square(int x, int y) {
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

}


