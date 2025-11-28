public class Ship {
    private int x;
    private int y;
    private boolean vertical;
    private int size;

    public Ship(int x, int y, boolean vertical, int size) {
        this.x = x;
        this.y = y;
        this.vertical = vertical;
        this.size = size;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isVertical() {
        return vertical;
    }

    public int getSize() {
        return size;
    }
}
