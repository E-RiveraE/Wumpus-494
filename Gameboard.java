public class Gameboard {

    private Cords[][] grid;


    public Gameboard(Cords pit1, Cords pit2, Cords wumpus, Cords paradise) {
        this.grid = new Cords[4][4];

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                this.grid[i][j] = new Cords(grid.length - i, j+1);
            }
        }
        Cords pitSpot1 = getChamber(pit1.getX(), pit1.getY());
        pitSpot1.setHasPit(true);

        Cords pitSpot2 = getChamber(pit2.getX(), pit2.getY());
        pitSpot2.setHasPit(true);

        Cords wumpusSpot = getChamber(wumpus.getX(), wumpus.getY());
        wumpusSpot.setHasWumpus(true);

        Cords paradiseSpot = getChamber(paradise.getX(), paradise.getY());
        paradiseSpot.setParadise(true);

    }

    public Gameboard() {

    }

    public Cords getChamber(int x, int y){
        int row = 4 - y;
        int col = x - 1;
        return grid[row][col];
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                stringBuilder.append(grid[i][j].toString());
                stringBuilder.append("\t"); // Add a tab for spacing
            }
            stringBuilder.append("\n"); // Add a newline after each row
        }
        // Return the final, complete string
        return stringBuilder.toString();
    }
}