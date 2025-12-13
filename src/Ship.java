public class Ship {
    private int x;
    private int y;
    private boolean vertical;
    private int size;
    private boolean isPlaced;
    private boolean isDragging;

    public Ship(int x, int y, boolean vertical, int size) {
        this.x = x;
        this.y = y;
        this.vertical = vertical;
        this.size = size;
        this.isPlaced = false;
        this.isDragging = false;
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

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isPlaced() {
        return isPlaced;
    }

    public void setPlaced(boolean placed) {
        isPlaced = placed;
    }

    public boolean isDragging() {
        return isDragging;
    }

    public void setDragging(boolean dragging) {
        isDragging = dragging;
    }

    public void rotate() {
        this.vertical = !this.vertical;
    }
}