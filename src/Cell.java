import com.sun.jdi.PrimitiveValue;

import java.awt.*;

public class Cell extends Rectangle {
    private int indexX;
    private int indexY;

   public Cell(int indexX, int indexY, int x, int y, int width, int height) {
       super(x, y, width, height);
       this.indexX = indexX;
       this.indexY = indexY;
   }


    @Override
    public String toString() {
        return "Cell{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
