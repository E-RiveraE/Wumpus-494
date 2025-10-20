import java.util.ArrayList;
import java.util.List;

public class Agent {

    private int xPOS;
    private int yPOS;
    private boolean isAlive = true;
    private KnowledgeBase kb;

    public Agent (){
        this.xPOS = 1;
        this.yPOS = 1;
        this.kb = new KnowledgeBase();
        // Mark starting position as safe
        kb.markSafe(xPOS, yPOS);
    }

    public int getxPOS() {
        return xPOS;
    }

    public int getyPOS() {
        return yPOS;
    }

    public boolean isAlive(){
        return isAlive;
    }

    public void AgentMoveto(int xPOS, int yPOS){
        this.xPOS = xPOS;
        this.yPOS = yPOS;
        System.out.println("Agent moved to (" + xPOS + "," + yPOS + ")");
    }

    public void kill(){
        this.isAlive = false;
        System.out.println("Agent has died");
    }

    public void updateKB (Gameboard gboard){
        //update currPOS
        kb.markVisited(xPOS, yPOS);
        kb.markSafe(xPOS, yPOS);

        boolean breezy = checkForBreeze(gboard);
        boolean stinky = checkForStink(gboard);

        if(breezy){
            kb.setBreeze(xPOS, yPOS, true);
            System.out.println("It's breezy here...");
        }

        if(stinky){
            kb.setStink(xPOS,yPOS, true);
            System.out.println("It's stinky here...");
        }

        if(!breezy && !stinky){
            System.out.println("This square is clear");
        }

        kb.deduce(xPOS, yPOS, breezy, stinky);
    }

    private boolean checkForBreeze(Gameboard gboard){
        int[][] directions = {{0,1}, {0,-1}, {1,0}, {-1,0}};

        for (int[] dir : directions) {
            int adjX = xPOS + dir[0];
            int adjY = yPOS + dir[1];

            if(inBounds(adjX, adjY)) {
                Cords adj = gboard.getChamber(adjX, adjY);
                if(adj.hasPit()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkForStink(Gameboard gboard){
        int[][] directions = {{0,1}, {0,-1}, {1,0}, {-1,0}};

        for (int[] dir : directions) {
            int adjX = xPOS + dir[0];
            int adjY = yPOS + dir[1];

            if(inBounds(adjX, adjY)) {
                Cords adj = gboard.getChamber(adjX, adjY);
                if(adj.hasWumpus()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean inBounds(int x, int y){
        return x >= 1 && x <= 4 && y >= 1 && y <= 4;
    }

    /** next safest move (backtracks)*/
    public Cords chooseBestMove() {

        //Safe adj cells
        List<Cords> safeAdjacentMoves = getSafeAdjacentMoves();
        if (!safeAdjacentMoves.isEmpty()) {
            System.out.println("Found " + safeAdjacentMoves.size() + " safe adj moves");
            return safeAdjacentMoves.get(0);
        }

        //Unknown cells with 0 danger
        List<Cords> unknownMoves = getUnknownMoves();
        List<Cords> zeroDangerMoves = new ArrayList<>();
        for (Cords move : unknownMoves) {
            double pitProb = kb.getPitProb(move.getX(), move.getY());
            double wumpusProb = kb.getWumpusProb(move.getX(), move.getY());
            if (pitProb == 0.0 && wumpusProb == 0.0) {
                zeroDangerMoves.add(move);
            }
        }

        if (!zeroDangerMoves.isEmpty()) {
            System.out.println("Found " + zeroDangerMoves.size() + " unknown cells with 0% danger");
            return zeroDangerMoves.get(0);
        }

        // backtrack to any safe visited cell
        List<Cords> safeVisitedCells = getAllSafeVisitedCells();
        if (!safeVisitedCells.isEmpty()) {
            System.out.println("All adj cells dangerous. BACKTRACKING to safe cell...");
            // Find the closest safe cell
            Cords closest = findClosestCell(safeVisitedCells);
            System.out.println("Backtracking to (" + closest.getX() + "," + closest.getY() + ")");
            return closest;
        }

        //If risk unavoidable, choose the lowest danger prob
        if (!unknownMoves.isEmpty()) {
            System.out.println("No safe moves available. Taking calculated risk...");
            return chooseLeastDangerous(unknownMoves);
        }

        // Priority 5: No moves available
        System.out.println("No available moves!");
        return null;
    }

    /**Get adj cells that are safe and unvisited*/
    private List<Cords> getSafeAdjacentMoves() {
        List<Cords> safeMoves = new ArrayList<>();
        int[][] directions = {{0,1}, {0,-1}, {1,0}, {-1,0}};

        for (int[] dir : directions) {
            int adjX = xPOS + dir[0];
            int adjY = yPOS + dir[1];

            if (inBounds(adjX, adjY)) {
                BoardKnowledge bk = kb.getBK(adjX, adjY);
                // Safe and not yet visited
                if (bk.isSafe() && !bk.isVisted()) {
                    safeMoves.add(new Cords(adjX, adjY));
                }
            }
        }

        return safeMoves;
    }

    /** Get all adj cells that are unknown */
    private List<Cords> getUnknownMoves() {
        List<Cords> unknownMoves = new ArrayList<>();
        int[][] directions = {{0,1}, {0,-1}, {1,0}, {-1,0}};

        for (int[] dir : directions) {
            int adjX = xPOS + dir[0];
            int adjY = yPOS + dir[1];

            if (inBounds(adjX, adjY)) {
                BoardKnowledge bk = kb.getBK(adjX, adjY);
                // Not visited and not marked safe
                if (!bk.isVisted() && !bk.isSafe()) {
                    unknownMoves.add(new Cords(adjX, adjY));
                }
            }
        }

        return unknownMoves;
    }

    /** Get ALL safe visited cells in entire grid (for backtracking)     */
    private List<Cords> getAllSafeVisitedCells() {
        List<Cords> safeCells = new ArrayList<>();

        for (int x = 1; x <= 4; x++) {
            for (int y = 1; y <= 4; y++) {
                // Skip current position
                if (x == xPOS && y == yPOS) continue;

                BoardKnowledge bk = kb.getBK(x, y);
                // Find cells that are visited and safe
                if (bk.isVisted() && bk.isSafe()) {
                    safeCells.add(new Cords(x, y));
                }
            }
        }

        return safeCells;
    }

    /** Find closest cell from list   */
    private Cords findClosestCell(List<Cords> cells) {
        Cords closest = cells.get(0);
        int minDistance = Integer.MAX_VALUE;

        for (Cords cell : cells) {
            int distance = Math.abs(cell.getX() - xPOS) + Math.abs(cell.getY() - yPOS);
            if (distance < minDistance) {
                minDistance = distance;
                closest = cell;
            }
        }

        return closest;
    }

    /** From a list of unknown moves, choose the one with lowest danger probability     */
    private Cords chooseLeastDangerous(List<Cords> moves) {
        Cords bestMove = null;
        double lowestDanger = Double.MAX_VALUE;

        for (Cords move : moves) {
            double pitProb = kb.getPitProb(move.getX(), move.getY());
            double wumpusProb = kb.getWumpusProb(move.getX(), move.getY());
            double totalDanger = pitProb + wumpusProb;

            System.out.println("    (" + move.getX() + "," + move.getY() +
                    ") danger: " + String.format("%.2f", totalDanger) +
                    " (Pit: " + String.format("%.2f", pitProb) +
                    ", Wumpus: " + String.format("%.2f", wumpusProb) + ")");

            if (totalDanger < lowestDanger) {
                lowestDanger = totalDanger;
                bestMove = move;
            }
        }

        if (bestMove != null) {
            System.out.println("Choosing (" + bestMove.getX() + "," +
                    bestMove.getY() + ") with lowest danger: " +
                    String.format("%.2f", lowestDanger));
        }

        return bestMove;
    }

    public void query(int x, int y){
        //Checks if query given is inBounds of the gboard
        if(!inBounds(x, y)){
            System.out.println("Cords out of bounds. Cords must be (1-4) ");
            return;
        }

        System.out.println("\n**** Query for (" + x + "," + y + ") ****");

        BoardKnowledge bk = kb.getBK(x,y);

        if(bk.isVisted()) {
            System.out.println("Status: SAFE (visited)");
            if (bk.isBreezy()) {
                System.out.println("  Breezy: YES");
            } else {
                System.out.println("  Breezy: NO");
            }
            if (bk.isStinky()) {
                System.out.println("  Stinky: YES");
            } else {
                System.out.println("  Stinky: NO");
            }
            System.out.println("  Pit prob: 0.0");
            System.out.println("  Wumpus : 0.0");
        } else if (bk.isSafe()) {
            System.out.println("Status: SAFE (inferred)");
            System.out.println("  Pit prob: 0.0");
            System.out.println("  Wumpus : 0.0");
        } else {
            // Calculate probabilities
            double pitProb = kb.getPitProb(x, y);
            double wumpusProb = kb.getWumpusProb(x, y);

            if (pitProb == 0.0 && wumpusProb == 0.0) {
                System.out.println("Status: UNKNOWN");
            } else {
                System.out.println("Status: POSSIBLY DANGEROUS");
            }

            System.out.println("  Pit prob: " + pitProb);
            System.out.println("  Wumpus prob: " + wumpusProb);
        }
    }

    public void printAgentKB() {
        kb.printKB();
    }

    public String toString() {
        return "Current POS (" + xPOS + "," + yPOS + ')';
    }
}