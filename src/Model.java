import java.awt.*;

public class Model {
    private Viewer viewer;
    private Cell[][] cellAreaComputer;

    public Model(Viewer viewer) {
        this.viewer = viewer;
        initializationComputerArea();
        placeTestShip();
    }

    private void placeTestShip() {
        if (cellAreaComputer.length > 2 && cellAreaComputer[0].length > 2) {
            cellAreaComputer[2][2].setState(CellState.SHIP);
        }
    }

    public void doAction(int x, int y) {
        out: for (int i = 0; i < cellAreaComputer.length; i++) {
            for (int j = 0; j < cellAreaComputer[i].length; j++) {
                Cell cell = cellAreaComputer[i][j];
                if (cell.contains(x - 10, y - 10)) {
                    System.out.println(cell);

                    if (cell.getState() == CellState.SHIP) {
                        cell.setState(CellState.HIT);
                        System.out.println("СТАТУС: ПОПАДАНИЕ!");
                    } else if (cell.getState() == CellState.WATER) {
                        cell.setState(CellState.MISS);
                        System.out.println("СТАТУС: ПРОМАХ!");
                    } else {
                        System.out.println("СТАТУС: УЖЕ АТАКОВАНО");
                    }

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
