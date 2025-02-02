package minesweeper;

public class Tile {

    protected boolean hidden = false;
    protected boolean isMine;

    protected int x;
    protected int y;

    protected static int size;

    public Tile(int x, int y, boolean isMine) {
        this.x = x;
        this.y = y;
        this.isMine = isMine;
    }
    
}
