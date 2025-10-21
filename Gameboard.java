/*** REPRESENTS THE ACTUAL GAMEBOARD, HOLDS TRUE LOCATIONS OF DANGERS/TREASURE ***/
public class Gameboard {

    private Cords[][] grid; // 2D array of Cords, represents the grid


    /****** CONSTRUCTOR, CREATES BOARD AND PLACES PITS, WUMPUS, AND PARADISE ******/
    public Gameboard(Cords pit1, Cords pit2, Cords wumpus, Cords paradise) {
        this.grid = new Cords[4][4]; // inits 4x4 grid

        // loop to create a Cords obj for each cell
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                this.grid[i][j] = new Cords(grid.length - i, j + 1);
            }
        }
        // gets the cell for pit1 and places it
        Cords pitSpot1 = getChamber(pit1.getX(), pit1.getY());
        pitSpot1.setHasPit(true);

        // gets the cell for pit2 and places it
        Cords pitSpot2 = getChamber(pit2.getX(), pit2.getY());
        pitSpot2.setHasPit(true);

        // gets the cell for the wumpus and places it
        Cords wumpusSpot = getChamber(wumpus.getX(), wumpus.getY());
        wumpusSpot.setHasWumpus(true);

        // gets the cell for paradise and places it
        Cords paradiseSpot = getChamber(paradise.getX(), paradise.getY());
        paradiseSpot.setParadise(true);
    }

    /*** EMPTY CONSTRUCTOR ***/
    public Gameboard() {

    }

    /*** GETS THE Cords OBJ AT A GIVEN (X,Y) ***/
    public Cords getChamber(int x, int y) {
        // converts (1-4) game cords to (0-3) array index
        int row = 4 - y;
        int col = x - 1;
        return grid[row][col];
    }

    /*** STRING REP OF THE GAMEBOARD ***/
    public String toString() {
        // uses a stringbuilder for efficiency
        StringBuilder stringBuilder = new StringBuilder();
        // loops through each row and col
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                // appends the cell's string rep and a tab for spacing
                stringBuilder.append(grid[i][j].toString());
                stringBuilder.append("\t");
            }
            stringBuilder.append("\n"); // adds newline after each row
        }
        // returns the final, complete string
        return stringBuilder.toString();
    }
}