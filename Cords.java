/*** DATA CLASS, REPRESENTS A SINGLE CELL/CORD ON THE BOARD ***/
public class Cords {

    // --- CELL STATE ---
    private int x; // row
    private int y; // col
    private boolean hasPit = false;     // T if this cell has a pit
    private boolean hasWumpus = false;  // T if this cell has the wumpus
    private boolean isParadise = false; // T if this cell is paradise


    /****** CONSTRUCTOR, SETS THE (X,Y) CORDS ******/
    Cords(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /*** GETTERS FOR X AND Y ***/
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /*** SETTERS FOR PIT, WUMPUS, AND PARADISE ***/
    public void setHasPit(boolean val) {
        this.hasPit = val;
    }

    public void setHasWumpus(boolean val) {
        this.hasWumpus = val;
    }

    public void setParadise(boolean val) {
        this.isParadise = val;
    }

    /*** BOOLEAN CHECKERS FOR PIT, WUMPUS, AND PARADISE ***/
    public boolean hasPit() {
        return this.hasPit;
    }

    public boolean hasWumpus() {
        return this.hasWumpus;
    }

    public boolean isParadise() {
        return this.isParadise;
    }

    /*** STRING REP OF THE CORD ***/
    @Override
    public String toString() {
        return "(" + "x=" + x + ", y=" + y + ')';
    }
}