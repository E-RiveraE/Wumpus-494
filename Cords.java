public class Cords {

    private int x;
    private int y;
    private boolean hasPit = false;
    private boolean hasWumpus = false;
    private boolean isParadise = false;



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

    public void setHasPit(boolean val){
        this.hasPit = val;
    }

    public void setHasWumpus (boolean val){
        this.hasWumpus = val;
    }

    public void setParadise(boolean val){
        this.isParadise = val;
    }


    public boolean hasPit (){
        return this.hasPit;
    }

    public boolean hasWumpus (){
        return this.hasWumpus;
    }

    public boolean isParadise (){
        return this.isParadise;
    }

    @Override
    public String toString() {
        return "(" + "x=" + x + ", y=" + y + ')';
    }
}
