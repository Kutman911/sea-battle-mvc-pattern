public class Move {

    public enum Who {
        PLAYER, COMPUTER
    }

    private final Who who;
    private final int row;
    private final int col;
    private final boolean hit;
    private final boolean sunk;

    public Move(Who who, int row, int col, boolean hit, boolean sunk) {
        this.who = who;
        this.row = row;
        this.col = col;
        this.hit = hit;
        this.sunk = sunk;
    }

    public Who getWho() { return who; }
    public int getRow() { return row; }
    public int getCol() { return col; }
    public boolean isHit() { return hit; }
    public boolean isSunk() { return sunk; }

    public String toDisplayString() {
        char column = (char) ('A' + col);
        int rowNum = row + 1;

        String result =
                sunk ? "☠ SUNK" :
                        hit  ? "💥 HIT" :
                                "• MISS";

        return String.format("%s → %c%d  %s",
                who == Who.PLAYER ? "Player" : "Computer",
                column,
                rowNum,
                result
        );
    }
}
