import java.awt.*;

public class Model {
    private Viewer viewer;
    private Cell[][] cellAreaComputer;

    public Model(Viewer viewer) {
        this.viewer = viewer;
        initializationComputerArea();
    }

    public void doAction(int x, int y) {
        out: for (int i = 0; i < cellAreaComputer.length; i++) {
            for (int j = 0; j < cellAreaComputer[i].length; j++) {
                Cell cell = cellAreaComputer[i][j];
                if (cell.contains(x - 10, y - 10)) {
                    System.out.println(cell);
                    break out;
                }
            }
        }
    }

    public void initializationComputerArea() {
        int start = 50;
        int x = start;
        int y = start;
        int width = 50;
        int height = 50;
        int offset = 10;

        cellAreaComputer = new Cell[10][10];

        for(int i = 0; i < cellAreaComputer.length; i++) {
            for(int j = 0; j < cellAreaComputer[i].length; j++) {
                cellAreaComputer[i][j] = new Cell((i + 1), (j + 1), x, y, width, height);
                x = x + width + offset;
            }
            x = start;
            y = y + height + offset;
        }
    }

    public Cell[][] getCallAreaComputer() {
        return cellAreaComputer;
    }

}
