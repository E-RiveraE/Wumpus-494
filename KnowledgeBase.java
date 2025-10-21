import java.util.ArrayList;
import java.util.List;

public class KnowledgeBase {

    private BoardKnowledge[][] cells; // 2D array to store knowledge of each cell
    private List<Cords> maybePitLoc; // list of cords that might have a pit
    private List<Cords> maybeWumpusLoc; // list of cords that might have a wumpus

    /****** CONSTRUCTOR, CREATES 4x4 KB & LISTS FOR POS DANGERS ******/
    public KnowledgeBase() {
        cells = new BoardKnowledge[4][4]; // inits the 4x4 grid
        // loop to create a BoardKnowledge obj for each cell
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                this.cells[i][j] = new BoardKnowledge();
            }
        }
        // inits the lists for maybe pits/wumpus
        maybePitLoc = new ArrayList<>();
        maybeWumpusLoc = new ArrayList<>();
    }

    /*** GETS BoardKnowledge OBJ FROM GIVEN (X,Y) CORD ***/
    public BoardKnowledge getBK(int x, int y) {
        // converts (1-4) game cords to (0-3) array index
        int row = 4 - y;
        int col = x - 1;
        return cells[row][col];
    }

    /*** MARKS A CELL AS VISITED ***/
    public void markVisited(int x, int y) {
        getBK(x, y).setVisited(true);
    }

    /*** MARKS A CELL AS SAFE ***/
    public void markSafe(int x, int y) {
        // sets the cell's safe bool to T
        BoardKnowledge bk = getBK(x, y);
        bk.setSafe(true);

        // if a cell is safe, it can't have wumpus or pit, so remove from lists
        removeFromList(maybeWumpusLoc, x, y);
        removeFromList(maybePitLoc, x, y);
    }

    /*** SETS BREEZE STATUS FOR A CELL ***/
    public void setBreeze(int x, int y, boolean hasBreeze) {
        getBK(x, y).setBreezy(hasBreeze);
    }

    /*** SETS STINK STATUS FOR A CELL ***/
    public void setStink(int x, int y, boolean hasStink) {
        getBK(x, y).setStinky(hasStink);
    }

    /*** CORE LOGIC, MAKES DEDUCTIONS BASED ON PERCEPTS (BREEZE/STINK) ***/
    public void deduce(int x, int y, boolean breeze, boolean stink) {
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        /*** DEDUCTIONS FOR PITS ***/
        if (breeze) {
            // breeze means a pit is in an adj, unvisited, not-safe cell
            for (int[] direct : directions) {
                int adjX = x + direct[0];
                int adjY = y + direct[1];

                if (inBounds(adjX, adjY) && !getBK(adjX, adjY).isVisted()) {
                    if (!getBK(adjX, adjY).isSafe()) {
                        // adds that cell to the maybePit list
                        addToListIfNotPresent(maybePitLoc, adjX, adjY);
                    }
                }
            }
        } else {
            // no breeze means all adj cells are safe from pits
            for (int[] direct : directions) {
                int adjX = x + direct[0];
                int adjY = y + direct[1];
                if (inBounds(adjX, adjY)) {
                    markSafe(adjX, adjY);
                }
            }
        }

        /*** DEDUCTIONS FOR WUMPUS ***/
        if (stink) {
            // stink means wumpus is in an adj, unvisited, not-safe cell
            for (int[] direct : directions) {
                int adjX = x + direct[0];
                int adjY = y + direct[1];
                if (inBounds(adjX, adjY) && !getBK(adjX, adjY).isVisted()) {
                    if (!getBK(adjX, adjY).isSafe()) {
                        // adds cell to maybeWumpus list
                        addToListIfNotPresent(maybeWumpusLoc, adjX, adjY);
                    }
                }
            }
        } else {
            // no stink means wumpus isn't in any adj cells
            for (int[] direct : directions) {
                int adjX = x + direct[0];
                int adjY = y + direct[1];
                if (inBounds(adjX, adjY)) {
                    // so we can remove them from the maybeWumpus list
                    removeFromList(maybeWumpusLoc, adjX, adjY);
                }
            }
        }
    }

    /*** CALCS PROBABILITY OF A PIT AT (X,Y) ***/
    public double getPitProb(int x, int y) {
        BoardKnowledge bk = getBK(x, y);

        // if cell is visited or known safe, 0% chance
        if (bk.isVisted() || bk.isSafe()) {
            return 0.0;
        }

        // if not in the maybe list, 0% chance
        if (!isInList(maybePitLoc, x, y)) {
            return 0.0;
        }

        // counts how many other maybe-pit cells share a breeze source
        int competingCells = 0;
        for (Cords c : maybePitLoc) {
            // checks for shared breeze source
            if (dblStackedBreezeCell(x, y, c.getX(), c.getY())) {
                competingCells++;
            }
        }

        // prob is 1 / num of competing cells
        if (competingCells > 0) {
            return 1.0 / competingCells;
        }

        // default prob if in list but no other info
        return 0.5;
    }

    /*** CALCS PROBABILITY OF THE WUMPUS AT (X,Y) ***/
    public double getWumpusProb(int x, int y) {
        BoardKnowledge bk = getBK(x, y);

        // if cell is visited or known safe, 0% chance
        if (bk.isVisted() || bk.isSafe()) {
            return 0.0;
        }

        // checks if its a possible wumpus location
        if (isInList(maybeWumpusLoc, x, y)) {
            int count = maybeWumpusLoc.size();

            // prob is 1 / num of possible locations
            if (count > 0) {
                return 1.0 / count;
            } else {
                return 0.5; // default if something goes wrong
            }
        }
        // if not in maybe list, 0% chance
        return 0.0;
    }

    /*** CHECKS IF TWO CELLS SHARE A BREEZE-SOURCE CELL ***/
    private boolean dblStackedBreezeCell(int x1, int y1, int x2, int y2) {
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        // loops through neighbors of first cell (x1, y1)
        for (int[] dir : directions) {
            int adjX = x1 + dir[0];
            int adjY = y1 + dir[1];

            // if adj neighbor is breezy
            if (inBounds(adjX, adjY) && getBK(adjX, adjY).isBreezy()) {
                //check if it's ALSO a neighbor of the second cell (x2, y2)
                if (Math.abs(adjX - x2) + Math.abs(adjY - y2) == 1) {
                    return true; // they share a breeze source
                }
            }
        }
        return false;
    }

    /*** COUNTS UNVISTED ADJACENT CELLS ***/
    private int countUnvistedAdj(int x, int y) {
        int count = 0;
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        for (int[] dir : directions) {
            int adjX = x + dir[0];
            int adjY = y + dir[1];

            if (inBounds(adjX, adjY) && !getBK(adjX, adjY).isVisted()) {
                count++;
            }
        }
        return count;
    }

    /*** HELPER, CHECKS IF (X,Y) IS ON THE 4x4 BOARD ***/
    private boolean inBounds(int x, int y) {
        return x >= 1 && x <= 4 && y >= 1 && y <= 4;
    }

    /*** HELPER, ADDS CORD TO A LIST IF NOT ALREADY THERE ***/
    private void addToListIfNotPresent(List<Cords> list, int x, int y) {
        if (!isInList(list, x, y)) {
            list.add(new Cords(x, y));
        }
    }

    /*** HELPER, REMOVES A CORD FROM A LIST ***/
    private void removeFromList(List<Cords> list, int x, int y) {
        list.removeIf(c -> c.getX() == x && c.getY() == y);
    }

    /*** HELPER, CHECKS IF A CORD IS IN A LIST ***/
    private boolean isInList(List<Cords> list, int x, int y) {
        for (Cords c : list) {
            if (c.getX() == x && c.getY() == y) {
                return true;
            }
        }
        return false;
    }

    /*** PRINTS THE KNOWLEDGE BASE IN A READABLE FORMAT ***/
    public void printKB() {
        System.out.println("\n=== AGENT'S KNOWLEDGE BASE ===");
        // loops through board from top to bottom
        for (int y = 4; y >= 1; y--) {
            for (int x = 1; x <= 4; x++) {
                BoardKnowledge bk = getBK(x, y);
                System.out.print("(" + x + "," + y + "):");

                // Case 1: Visited
                if (bk.isVisted()) {
                    System.out.print(" VISITED");
                    if (bk.isBreezy()) System.out.print(" BREEZE");
                    if (bk.isStinky()) System.out.print(" STINKY");
                    // Case 2: Inferred Safe
                } else if (bk.isSafe()) {
                    System.out.print(" SAFE");
                    // Case 3: Unknown or Dangerous
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
                System.out.print("\t"); // adds space between cells
            }
            System.out.println(); // new line for next row
        }
        System.out.println("==============================\n");
    }
}