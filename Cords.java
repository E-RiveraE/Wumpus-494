public class Cords {

    private int x;
    private int y;

    private boolean pit() ;
    Cords(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "(" + "x=" + x + ", y=" + y + ')';
    }
}
