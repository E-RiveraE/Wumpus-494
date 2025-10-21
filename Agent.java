import java.util.ArrayList;
import java.util.List;

public class Agent {

    private int xPOS;  // row Position
    private int yPOS; // col PPosition
    private boolean isAlive = true; // live/dead bool (initialized to T)
    private KnowledgeBase kb; //agents KnowledgeBase

    /****** Constructor for agent, initalPOS is (1,1), sets Alive to T, creates KB ******/
    public Agent (){
        this.xPOS = 1;
        this.yPOS = 1;
        this.kb = new KnowledgeBase();
        kb.markSafe(xPOS, yPOS); //marks starting POS as SAFE
    }

    /*** GETTERS FOR AGENT ****/
    public int getxPOS() {
        return xPOS;
    }

    public int getyPOS() {
        return yPOS;
    }

    public boolean isAlive(){
        return isAlive;
    }

    /*** MOVES AGENT TO GIVEN (X,Y) CORD ***/
    public void AgentMoveto(int xPOS, int yPOS){
        this.xPOS = xPOS;
        this.yPOS = yPOS;
        System.out.println("Agent moved to (" + xPOS + "," + yPOS + ")");
    }

    /*** SETS isAlive TO FALSE IF AGENT DIES ***/
    public void kill(){
        this.isAlive = false;
        System.out.println("Agent has died");
    }


    public void updateKB (Gameboard gboard){
        //update currPOS marks as SAFE and VISITED
        kb.markVisited(xPOS, yPOS);
        kb.markSafe(xPOS, yPOS);

        //checks for warning flags (stinky (w) breezy (p)
        boolean breezy = checkForBreeze(gboard);
        boolean stinky = checkForStink(gboard);

        //if breezy is T, sets currCell to breezy and prints warning
        if(breezy){
            kb.setBreeze(xPOS, yPOS, true);
            System.out.println("It's breezy here...");
        }

        //if stinky is T, sets currCell to stinky and prints warning
        if(stinky){
            kb.setStink(xPOS,yPOS, true);
            System.out.println("It's stinky here...");
        }

        //if breezy $$ stinky F, currCell is clear
        if(!breezy && !stinky){
            System.out.println("This cell is clear");
        }

        //calls deduce after to make deductions about adj cells
        kb.deduce(xPOS, yPOS, breezy, stinky);
    }

    /*** CHECKS ADJ CELLS FOR BREEZE ***/
    private boolean checkForBreeze(Gameboard gboard){
        //defines directions (Right,  Left,   Down,   Up)
        int[][] directions = {{0,1}, {0,-1}, {1,0}, {-1,0}};

        //enhanced for-loop goes through directions
        for (int[] dir : directions) {
            int adjX = xPOS + dir[0];
            int adjY = yPOS + dir[1];

            //while looping, checks if adj cell is InBounds
            if(inBounds(adjX, adjY)) {
                Cords adj = gboard.getChamber(adjX, adjY);
                //return T if has pit gets T
                if(adj.hasPit()) {
                    return true;
                }
            }
        }
        return false;
    }

    /*** CHECKS ADJ CELLS FOR STINK ***/
    private boolean checkForStink(Gameboard gboard){
        //defines directions (Right,  Left,   Down,   Up)
        int[][] directions = {{0,1}, {0,-1}, {1,0}, {-1,0}};

        //enhanced for-loop goes through directions
        for (int[] dir : directions) {
            int adjX = xPOS + dir[0];
            int adjY = yPOS + dir[1];

            //while looping, checks if adj cell is InBounds
            if(inBounds(adjX, adjY)) {
                Cords adj = gboard.getChamber(adjX, adjY);
                //return T if has pit gets T
                if(adj.hasWumpus()) {
                    return true;
                }
            }
        }
        return false;
    }

    /*** CHECKS IF CELL IS inBounds ***/
    private boolean inBounds(int x, int y){
        return x >= 1 && x <= 4 && y >= 1 && y <= 4;
    }



    /*** CORE LOGIC FOR THE AGENT, HAS 5 PRIORITIES TO DETERMINE NXT BEST MOVE ***/
    public Cords chooseBestMove() {

        /*** Prior 1: Move to a known safe, unvisited adj cell ***/
        List<Cords> safeAdjacentMoves = getSafeAdjacentMoves();
        if (!safeAdjacentMoves.isEmpty()) {
            System.out.println("Found " + safeAdjacentMoves.size() + " safe adj moves");
            return safeAdjacentMoves.get(0);
        }

        /*** Prior 2: Move to an unknown adj cell with 0% danger ***/
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

        /*** Prior 3: Backtrack to the closest known safe cell (if no safe moves are nearby) ***/
        List<Cords> safeVisitedCells = getAllSafeVisitedCells();
        if (!safeVisitedCells.isEmpty()) {
            System.out.println("All adj cells dangerous. BACKTRACKING to safe cell...");
            // Find the closest safe cell
            Cords closest = findClosestCell(safeVisitedCells);
            System.out.println("Backtracking to (" + closest.getX() + "," + closest.getY() + ")");
            return closest;
        }

        /*** Prior 4: Gamble on the least dangerous unknown cell ***/
        if (!unknownMoves.isEmpty()) {
            System.out.println("No safe moves available. Taking calculated risk...");
            return chooseLeastDangerous(unknownMoves);
        }

        /*** Prior 5: If no moves possible, return null ****/
        System.out.println("No available moves!");
        return null;
    }

    /*** GETS LIST OF ADJ CELLS KNOWN SAFE AND NOTVISTED ***/
    private List<Cords> getSafeAdjacentMoves() {
        List<Cords> safeMoves = new ArrayList<>();
        //defines directions (Right,  Left,   Down,   Up)
        int[][] directions = {{0,1}, {0,-1}, {1,0}, {-1,0}};

        //enhanced for-loop goes through directions
        for (int[] dir : directions) {
            int adjX = xPOS + dir[0];
            int adjY = yPOS + dir[1];

            //while looping, checks if adj cell is InBounds
            if (inBounds(adjX, adjY)) {
                BoardKnowledge bk = kb.getBK(adjX, adjY);
                //if a cell is marked safe and notVisted, it is safe
                if (bk.isSafe() && !bk.isVisted()) {
                    safeMoves.add(new Cords(adjX, adjY));
                }
            }
        }
        return safeMoves;
    }

    /*** GETS ALL ADJ CELLS THAT ARE UNKNOWN (notSafe & notVisited) ***/
    private List<Cords> getUnknownMoves() {
        List<Cords> unknownMoves = new ArrayList<>();
        //defines directions (Right,  Left,   Down,   Up)
        int[][] directions = {{0,1}, {0,-1}, {1,0}, {-1,0}};

        //enhanced for-loop goes through directions
        for (int[] dir : directions) {
            int adjX = xPOS + dir[0];
            int adjY = yPOS + dir[1];

            //while looping, checks if adj cell is InBounds
            if (inBounds(adjX, adjY)) {
                BoardKnowledge bk = kb.getBK(adjX, adjY);
                // unknown IF (notVisited && noeDeduced safe)
                if (!bk.isVisted() && !bk.isSafe()) {
                    unknownMoves.add(new Cords(adjX, adjY));
                }
            }
        }
        return unknownMoves;
    }

    /*** GETS LIST OF ALL SAFE CELLS (backtracker) ***/
    private List<Cords> getAllSafeVisitedCells() {
        List<Cords> safeCells = new ArrayList<>();

        for (int x = 1; x <= 4; x++) {
            for (int y = 1; y <= 4; y++) {
                // Skips currPos
                if (x == xPOS && y == yPOS) continue;

                BoardKnowledge bk = kb.getBK(x, y);
                // Find cells that are KNOWN visited and safe
                if (bk.isVisted() && bk.isSafe()) {
                    safeCells.add(new Cords(x, y));
                }
            }
        }

        return safeCells;
    }

    /*** FINDS CLOSEST CELL FROM LIST ***/
    private Cords findClosestCell(List<Cords> cells) {
        //starts at 0
        Cords closest = cells.get(0);
        int minDistance = Integer.MAX_VALUE;

        //enhanced for-loop to go through cells
        for (Cords cell : cells) {
            //while looping calcs dist |x1 - x2| + |y1 - y2|
            int distance = Math.abs(cell.getX() - xPOS) + Math.abs(cell.getY() - yPOS);

            //if cells is closer than closest alr found, update
            if (distance < minDistance) {
                minDistance = distance;
                closest = cell;
            }
        }
        return closest;
    }

    /*** CHOOSES LEAST DANGEROUS MOVE FROM LIST OF UNKNOWNS ***/
    private Cords chooseLeastDangerous(List<Cords> moves) {
        //starts bestMove as null and lowestDanger at MAX so first check passes
        Cords bestMove = null;
        double lowestDanger = Double.MAX_VALUE;

        //enhanced for-loop goes through moves
        for (Cords move : moves) {
            //gets pit and wumpus prob to calc totalDanger
            double pitProb = kb.getPitProb(move.getX(), move.getY());
            double wumpusProb = kb.getWumpusProb(move.getX(), move.getY());
            double totalDanger = pitProb + wumpusProb;

            //prints the danger stats for each potential move
            System.out.println("    (" + move.getX() + "," + move.getY() +
                    ") danger: " + String.format("%.2f", totalDanger) +
                    " (Pit: " + String.format("%.2f", pitProb) +
                    ", Wumpus: " + String.format("%.2f", wumpusProb) + ")");

            //if this move's danger is lower than currMove lowest, update it
            if (totalDanger < lowestDanger) {
                lowestDanger = totalDanger;
                bestMove = move;
            }
        }

        //prints the chosen move and its danger level
        if (bestMove != null) {
            System.out.println("Choosing (" + bestMove.getX() + "," +
                    bestMove.getY() + ") with lowest danger: " +
                    String.format("%.2f", lowestDanger));
        }
        return bestMove;
    }

    /*** PRINTS AGENT'S KNOWLEDGE ABOUT A GIVEN (X,Y) CELL ***/
    public void query(int x, int y){
        //Checks if query given is inBounds of the gboard
        if(!inBounds(x, y)){
            System.out.println("Cords out of bounds. Cords must be (1-4) ");
            return;
        }

        System.out.println("\n**** Query for (" + x + "," + y + ") ****");

        //gets the BoardKnowledge for the cell
        BoardKnowledge bk = kb.getBK(x,y);

        //Case 1: cell has been visited (we know everything)
        if(bk.isVisted()) {
            System.out.println("Status: SAFE (visited)");
            System.out.println("  Breezy: " + (bk.isBreezy() ? "YES" : "NO"));
            System.out.println("  Stinky: " + (bk.isStinky() ? "YES" : "NO"));
            System.out.println("  Pit prob: 0.0");
            System.out.println("  Wumpus : 0.0");
            //Case 2: cell is unvisited but inferred as safe
        } else if (bk.isSafe()) {
            System.out.println("Status: SAFE (inferred)");
            System.out.println("  Pit prob: 0.0");
            System.out.println("  Wumpus : 0.0");
            //Case 3: cell is unknown, so report calculated probs
        } else {
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

    /*** HELPER TO PRINT ENTIRE KNOWLEDGE BASE GRID ***/
    public void printAgentKB() {
        kb.printKB();
    }

    /*** STRING REP OF AGENT'S CURRENT STATE ***/
    public String toString() {
        return "Current POS (" + xPOS + "," + yPOS + ')';
    }
}