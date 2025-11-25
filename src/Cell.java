import com.sun.jdi.PrimitiveValue;

import java.awt.*;

public class Cell extends Rectangle {
    private int indexX;
    private int indexY;

    private CellState state;

   public Cell(int indexX, int indexY, int x, int y, int width, int height) {
       super(x, y, width, height);
       this.indexX = indexX;
       this.indexY = indexY;
       this.state = CellState.WATER;
   }

    public CellState getState() {
        return state;
    }

    public void setState(CellState state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", state=" + state +
                '}';
    }
}
