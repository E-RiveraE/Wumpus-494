import java.util.ArrayList;
import java.util.List;

public class KnowledgeBase {

    private BoardKnowledge[][] cells;

    private List <Cords> maybePitLoc;

    private List <Cords> maybeWumpusLoc;

    public KnowledgeBase(){
        cells = new BoardKnowledge[4][4];
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                this.cells[i][j] = new BoardKnowledge();
            }
        }
        maybePitLoc = new ArrayList<>();
        maybeWumpusLoc = new ArrayList<>();
    }

    public BoardKnowledge getBK(int x, int y){
        int row = 4 - y;
        int col = x - 1;
        return cells[row][col];
    }

    public void markVisited(int x , int y){
        getBK(x, y).setVisited(true);
    }

    public void markSafe(int x, int y){

        //Mark square safe
        BoardKnowledge bk = getBK(x, y);
        bk.setSafe(true);

        //Remove from maybeList
        removeFromList(maybeWumpusLoc, x, y);
        removeFromList(maybePitLoc, x, y);
    }

    public void setBreeze(int x, int y, boolean hasBreeze){
        getBK(x, y).setBreezy(hasBreeze);
    }

    public void setStink(int x, int y, boolean hasStink){
        getBK(x, y).setStinky(hasStink);
    }

    public void deduce(int x, int y, boolean breeze, boolean stink) {
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        //breeze detected (PIT)
        if (breeze) {
            //Pit in one of the adj cells
            for (int[] direct : directions) {
                int adjX = x + direct[0];
                int adjY = y + direct[1];

                if (inBounds(adjX, adjY) && !getBK(adjX, adjY).isVisted()) {
                    if (!getBK(adjX, adjY).isSafe()) {
                        addToListIfNotPresent(maybePitLoc, adjX, adjY);
                    }
                }
            }
        } else {
            //if there's no breeze all adj cells are safe from pits
            for (int[] direct : directions) {
                int adjX = x + direct[0];
                int adjY = y + direct[1];

                if (inBounds(adjX, adjY)) {
                    markSafe(adjX, adjY);
                }
            }
        }


        //Stink detected (WUMPUS)
        if (stink) {
            //wumpus in one of the adj cells
            for (int[] direct : directions) {
                int adjX = x + direct[0];
                int adjY = y + direct[1];

                if (inBounds(adjX, adjY) && !getBK(adjX, adjY).isVisted()) {
                    if (!getBK(adjX, adjY).isSafe()) {
                        addToListIfNotPresent(maybeWumpusLoc, adjX, adjY);
                    }
                }
            }
        }
        //no wumpus
        else{
            for (int[] direct : directions) {
                int adjX = x + direct[0];
                int adjY = y + direct[1];

                if (inBounds(adjX,adjY)) {
                    //Mark safe from wumpus (maybe pit though)
                    removeFromList(maybeWumpusLoc, adjX, adjY);
                }
            }
        }
    }


    public double getPitProb(int x, int y) {
        BoardKnowledge bk = getBK(x, y);

        //Been there, no warnings (SAFE)
        if (bk.isVisted() || bk.isSafe()) {
            return 0.0;
        }

        //Check if this cell is maybe pit
        if (!isInList(maybePitLoc, x, y)) {
            return 0.0;  // Not in danger list = 0% chance
        }

        // Count num cells for the same breeze sources
        int competingCells = 0;
        for (Cords c : maybePitLoc) {
            if (dblStackedBreezeCell(x, y, c.getX(), c.getY())) {
                competingCells++;
            }
        }

        if (competingCells > 0) {
            return 1.0 / competingCells;
        }

        // Default: if in maybe list but no shared breezes, return 0.5
        return 0.5;
    }


    public double getWumpusProb(int x, int y){
        BoardKnowledge bk = getBK(x, y);

        //safe cell
        if(bk.isVisted() || bk.isSafe()){
            return 0.0;
        }

        if(isInList(maybeWumpusLoc, x, y)) {
            int count = maybeWumpusLoc.size();

            if (count > 0) {
                return 1.0 / count;
            } else {
                return 0.5;
            }
        }
        return 0.0;
    }

    private boolean dblStackedBreezeCell(int x1, int y1, int x2, int y2) {
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        for (int[] dir : directions) {
            int adjX = x1 + dir[0];
            int adjY = y1 + dir[1];

            if (inBounds(adjX, adjY) && getBK(adjX, adjY).isBreezy()) {
                if (Math.abs(adjX - x2) + Math.abs(adjY - y2) == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    private int countUnvistedAdj(int x, int y){
        int count = 0;
        int[][] directions = {{0,1}, {0,-1}, {1,0}, {-1,0}};

        for (int[] dir : directions){
            int adjX = x + dir[0];
            int adjY = y + dir[1];

            if (inBounds(adjX, adjY) && !getBK(adjX, adjY).isVisted()) {
                count++;
            }
        }
        return count;
    }

    private boolean inBounds(int x, int y){
        return x >= 1 && x <= 4 && y >= 1 && y <= 4;
    }

    private void addToListIfNotPresent(List <Cords> list, int x, int y){
        if(!isInList(list, x, y)){
            list.add(new Cords(x, y));
        }
    }


    private void removeFromList(List<Cords> list, int x, int y) {
        list.removeIf(c -> c.getX() == x && c.getY() == y);
    }

    private boolean isInList(List<Cords> list, int x, int y) {
        for (Cords c : list) {
            if (c.getX() == x && c.getY() == y) {
                return true;
            }
        }
        return false;
    }

    /** Print the kb in a readable format     */
    public void printKB() {
        System.out.println("\n=== AGENT'S KNOWLEDGE BASE ===");
        for (int y = 4; y >= 1; y--) {
            for (int x = 1; x <= 4; x++) {
                BoardKnowledge bk = getBK(x, y);
                System.out.print("(" + x + "," + y + "):");

                if (bk.isVisted()) {
                    System.out.print(" VISITED");
                    if (bk.isBreezy()) System.out.print(" BREEZE");
                    if (bk.isStinky()) System.out.print(" STINKY");
                } else if (bk.isSafe()) {
                    System.out.print(" SAFE");
                } else {
                    double pitProb = getPitProb(x, y);
                    double wumpusProb = getWumpusProb(x, y);

                    if (pitProb == 0.0 && wumpusProb == 0.0) {
                        System.out.print(" UNKNOWN");
                    } else {
                        System.out.print(" P:" + String.format("%.2f", pitProb));
                        System.out.print(" W:" + String.format("%.2f", wumpusProb));
                    }
                }
                System.out.print("\t");
            }
            System.out.println();
        }
        System.out.println("==============================\n");
    }
}